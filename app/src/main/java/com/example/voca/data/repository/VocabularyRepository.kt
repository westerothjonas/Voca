package com.example.voca.data.repository

import com.example.voca.data.model.GardenState
import com.example.voca.data.model.UserProfile
import com.example.voca.data.model.VocabPackage
import com.example.voca.data.model.VocabularyItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class VocabularyRepository(
    private val firestore: FirebaseFirestore? = try { FirebaseFirestore.getInstance() } catch (e: Exception) { null },
    private val auth: FirebaseAuth? = try { FirebaseAuth.getInstance() } catch (e: Exception) { null }
) {
    private val useLocalFallback = firestore == null

    // In-memory local fallback storage per userId
    private val localItemsMap = MutableStateFlow<Map<String, List<VocabularyItem>>>(emptyMap())
    private val localPackagesMap = MutableStateFlow<Map<String, List<VocabPackage>>>(emptyMap())
    private val localGardenMap = MutableStateFlow<Map<String, GardenState>>(emptyMap())
    private val localProfileMap = MutableStateFlow<Map<String, UserProfile>>(emptyMap())

    fun getVocabularyItems(userId: String): Flow<List<VocabularyItem>> {
        if (useLocalFallback || firestore == null) {
            return localItemsMap.map { map -> map[userId] ?: emptyList() }
        }
        return callbackFlow {
            val firestoreInstance = firestore
            if (firestoreInstance == null) {
                trySend(emptyList())
                close()
                return@callbackFlow
            }
            val listener = firestoreInstance.collection("users")
                .document(userId)
                .collection("vocabulary")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        trySend(localItemsMap.value[userId] ?: emptyList())
                        return@addSnapshotListener
                    }
                    val items = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(VocabularyItem::class.java)?.copy(id = doc.id)
                    }
                    localItemsMap.value = localItemsMap.value.toMutableMap().apply { put(userId, items) }
                    trySend(items)
                }
            awaitClose { listener.remove() }
        }
    }

    fun getVocabPackages(userId: String): Flow<List<VocabPackage>> {
        if (useLocalFallback || firestore == null) {
            return localPackagesMap.map { map -> map[userId] ?: emptyList() }
        }
        return callbackFlow {
            val firestoreInstance = firestore
            if (firestoreInstance == null) {
                trySend(emptyList())
                close()
                return@callbackFlow
            }
            val listener = firestoreInstance.collection("users")
                .document(userId)
                .collection("packages")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        trySend(localPackagesMap.value[userId] ?: emptyList())
                        return@addSnapshotListener
                    }
                    val pkgs = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(VocabPackage::class.java)?.copy(id = doc.id)
                    }
                    localPackagesMap.value = localPackagesMap.value.toMutableMap().apply { put(userId, pkgs) }
                    trySend(pkgs)
                }
            awaitClose { listener.remove() }
        }
    }

    suspend fun saveVocabPackage(userId: String, pkg: VocabPackage): Result<Unit> {
        val packageId = if (pkg.id.isBlank()) UUID.randomUUID().toString() else pkg.id
        val updated = pkg.copy(
            id = packageId,
            userId = userId,
            createdAt = if (pkg.createdAt == 0L) System.currentTimeMillis() else pkg.createdAt
        )

        val currentPkgs = localPackagesMap.value[userId] ?: emptyList()
        val existingIndex = currentPkgs.indexOfFirst { it.id == updated.id }
        val newPkgs = if (existingIndex >= 0) {
            currentPkgs.toMutableList().apply { set(existingIndex, updated) }
        } else {
            currentPkgs + updated
        }
        localPackagesMap.value = localPackagesMap.value.toMutableMap().apply { put(userId, newPkgs) }

        if (useLocalFallback || firestore == null) {
            return Result.success(Unit)
        }
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("packages")
                .document(packageId)
                .set(updated)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    suspend fun deleteVocabPackage(userId: String, packageId: String): Result<Unit> {
        val currentPkgs = localPackagesMap.value[userId] ?: emptyList()
        localPackagesMap.value = localPackagesMap.value.toMutableMap().apply {
            put(userId, currentPkgs.filter { it.id != packageId })
        }
        if (useLocalFallback || firestore == null) {
            return Result.success(Unit)
        }
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("packages")
                .document(packageId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    fun getUserProfile(userId: String): Flow<UserProfile> {
        if (useLocalFallback || firestore == null) {
            return localProfileMap.map { map -> map[userId] ?: UserProfile(userId = userId) }
        }
        return callbackFlow {
            val firestoreInstance = firestore
            if (firestoreInstance == null) {
                trySend(UserProfile(userId = userId))
                close()
                return@callbackFlow
            }
            val docRef = firestoreInstance.collection("users")
                .document(userId)
                .collection("profile")
                .document("user_info")

            val listener = docRef.addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) {
                    trySend(localProfileMap.value[userId] ?: UserProfile(userId = userId))
                    return@addSnapshotListener
                }
                val profile = snapshot.toObject(UserProfile::class.java) ?: UserProfile(userId = userId)
                localProfileMap.value = localProfileMap.value.toMutableMap().apply { put(userId, profile) }
                trySend(profile)
            }
            awaitClose { listener.remove() }
        }
    }

    suspend fun saveUserProfile(userId: String, profile: UserProfile): Result<Unit> {
        val updated = profile.copy(userId = userId)
        localProfileMap.value = localProfileMap.value.toMutableMap().apply {
            put(userId, updated)
        }
        if (useLocalFallback || firestore == null) {
            return Result.success(Unit)
        }
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("profile")
                .document("user_info")
                .set(updated)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    fun getGardenState(userId: String): Flow<GardenState> {
        if (useLocalFallback || firestore == null) {
            return localGardenMap.map { map -> map[userId] ?: GardenState() }
        }
        return callbackFlow {
            val firestoreInstance = firestore
            if (firestoreInstance == null) {
                trySend(GardenState())
                close()
                return@callbackFlow
            }
            val docRef = firestoreInstance.collection("users")
                .document(userId)
                .collection("profile")
                .document("garden")
            
            val listener = docRef.addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) {
                    trySend(localGardenMap.value[userId] ?: GardenState())
                    return@addSnapshotListener
                }
                val garden = snapshot.toObject(GardenState::class.java) ?: GardenState()
                trySend(garden)
            }
            awaitClose { listener.remove() }
        }
    }

    suspend fun saveGardenState(userId: String, gardenState: GardenState): Result<Unit> {
        localGardenMap.value = localGardenMap.value.toMutableMap().apply {
            put(userId, gardenState)
        }
        if (useLocalFallback || firestore == null) {
            return Result.success(Unit)
        }
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("profile")
                .document("garden")
                .set(gardenState)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    suspend fun addVocabularyItem(userId: String, item: VocabularyItem): Result<String> {
        val itemId = if (item.id.isBlank()) UUID.randomUUID().toString() else item.id
        val newItem = item.copy(
            id = itemId,
            userId = userId,
            createdAt = if (item.createdAt == 0L) System.currentTimeMillis() else item.createdAt
        )

        val currentList = localItemsMap.value[userId] ?: emptyList()
        localItemsMap.value = localItemsMap.value.toMutableMap().apply {
            put(userId, listOf(newItem) + currentList.filter { it.id != newItem.id })
        }

        if (useLocalFallback || firestore == null) {
            return Result.success(newItem.id)
        }
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("vocabulary")
                .document(newItem.id)
                .set(newItem)
                .await()
            Result.success(newItem.id)
        } catch (e: Exception) {
            Result.success(newItem.id)
        }
    }

    suspend fun updateVocabularyItem(userId: String, item: VocabularyItem): Result<Unit> {
        if (item.id.isBlank()) {
            return Result.failure(IllegalArgumentException("Item ID cannot be blank for update"))
        }
        val updatedItem = item.copy(userId = userId)

        val currentList = localItemsMap.value[userId] ?: emptyList()
        localItemsMap.value = localItemsMap.value.toMutableMap().apply {
            put(userId, currentList.map { if (it.id == updatedItem.id) updatedItem else it })
        }

        if (useLocalFallback || firestore == null) {
            return Result.success(Unit)
        }
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("vocabulary")
                .document(updatedItem.id)
                .set(updatedItem)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    suspend fun deleteVocabularyItem(userId: String, itemId: String): Result<Unit> {
        val currentList = localItemsMap.value[userId] ?: emptyList()
        localItemsMap.value = localItemsMap.value.toMutableMap().apply {
            put(userId, currentList.filter { it.id != itemId })
        }

        if (useLocalFallback || firestore == null) {
            return Result.success(Unit)
        }
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("vocabulary")
                .document(itemId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }
}
