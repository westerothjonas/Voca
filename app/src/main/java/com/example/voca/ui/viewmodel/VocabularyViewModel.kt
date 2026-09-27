package com.example.voca.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voca.data.model.GardenState
import com.example.voca.data.model.SrsRating
import com.example.voca.data.model.UserProfile
import com.example.voca.data.model.VocabPackage
import com.example.voca.data.model.VocabularyItem
import com.example.voca.data.repository.AuthRepository
import com.example.voca.data.repository.VocabularyRepository
import com.example.voca.utils.VocaNotificationHelper
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class VocabularyUiState(
    val items: List<VocabularyItem> = emptyList(),
    val filteredItems: List<VocabularyItem> = emptyList(),
    val vocabPackages: List<VocabPackage> = emptyList(),
    val searchQuery: String = "",
    val selectedPackFilter: String? = null,
    val packs: List<String> = emptyList(),
    val gardenState: GardenState = GardenState(),
    val userProfile: UserProfile = UserProfile(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val user: FirebaseUser? = null
)

class VocabularyViewModel(
    private val repository: VocabularyRepository = VocabularyRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(VocabularyUiState(isLoading = true))
    val uiState: StateFlow<VocabularyUiState> = _uiState.asStateFlow()

    private var dataCollectionJob: Job? = null
    private var reminderJob: Job? = null

    val isUserSignedIn: Boolean
        get() = authRepository.isUserSignedIn()

    private val currentUserId: String
        get() = _uiState.value.user?.uid ?: authRepository.currentUser?.uid ?: "anonymous"

    init {
        viewModelScope.launch {
            authRepository.userFlow.collect { firebaseUser ->
                val userId = firebaseUser?.uid ?: "anonymous"
                _uiState.update { it.copy(user = firebaseUser, isLoading = true) }
                
                dataCollectionJob?.cancel()
                dataCollectionJob = launch {
                    launch {
                        repository.getVocabularyItems(userId).collect { rawItems ->
                            _uiState.update { currentState ->
                                val packs = (listOf("Alle Pakete") + rawItems.map { it.vocabularyPack }.distinct().sorted()).distinct()
                                val filtered = filterItems(
                                    items = rawItems,
                                    query = currentState.searchQuery,
                                    packFilter = currentState.selectedPackFilter
                                )
                                currentState.copy(
                                    items = rawItems,
                                    filteredItems = filtered,
                                    packs = packs,
                                    isLoading = false
                                )
                            }
                        }
                    }
                    launch {
                        repository.getVocabPackages(userId).collect { pkgs ->
                            _uiState.update { currentState ->
                                currentState.copy(vocabPackages = pkgs)
                            }
                        }
                    }
                    launch {
                        repository.getGardenState(userId).collect { garden ->
                            val checkedGarden = checkAndUpdateStreak(userId, garden)
                            _uiState.update { currentState ->
                                currentState.copy(gardenState = checkedGarden)
                            }
                        }
                    }
                    launch {
                        repository.getUserProfile(userId).collect { profile ->
                            _uiState.update { currentState ->
                                val email = firebaseUser?.email ?: profile.email
                                val name = profile.displayName.ifBlank { firebaseUser?.displayName ?: email.substringBefore("@") }
                                currentState.copy(userProfile = profile.copy(email = email, displayName = name))
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun checkAndUpdateStreak(userId: String, garden: GardenState): GardenState {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val today = dateFormat.format(Date())
        if (garden.lastStudyDate == today) return garden

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = dateFormat.format(calendar.time)

        val newStreak = when (garden.lastStudyDate) {
            yesterday -> garden.streakDays + 1
            today -> garden.streakDays
            else -> if (garden.lastStudyDate.isBlank()) 1 else 1
        }
        val updated = garden.copy(streakDays = maxOf(1, newStreak), lastStudyDate = today)
        repository.saveGardenState(userId, updated)

        // Save streak & lastStudyDate directly in UserProfile in Firestore (users/{userId}/profile/user_info)
        val currentProfile = _uiState.value.userProfile
        val updatedProfile = currentProfile.copy(
            streakDays = updated.streakDays,
            lastStudyDate = today
        )
        repository.saveUserProfile(userId, updatedProfile)

        return updated
    }

    fun startReminderService(context: Context) {
        if (reminderJob != null) return
        reminderJob = viewModelScope.launch {
            while (true) {
                delay(30 * 60 * 1000L) // Check every 30 minutes
                val profile = _uiState.value.userProfile
                if (profile.reminderEnabled) {
                    val calendar = Calendar.getInstance()
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    if (hour == 19) { // 19:00 Uhr
                        VocaNotificationHelper.showStudyReminder(context)
                    }
                }
            }
        }
    }

    private fun filterItems(
        items: List<VocabularyItem>,
        query: String,
        packFilter: String?
    ): List<VocabularyItem> {
        return items.filter { item ->
            val matchesSearch = query.isBlank() ||
                item.baseForm.contains(query, ignoreCase = true) ||
                item.translation.contains(query, ignoreCase = true) ||
                item.vocabularyPack.contains(query, ignoreCase = true) ||
                item.irregularForms.infinitiv.contains(query, ignoreCase = true) ||
                item.irregularForms.simplePast.contains(query, ignoreCase = true) ||
                item.irregularForms.pastParticiple.contains(query, ignoreCase = true) ||
                item.irregularForms.thirdPersonSingular.contains(query, ignoreCase = true) ||
                item.mnemonic.contains(query, ignoreCase = true)

            val matchesPack = packFilter.isNullOrBlank() || packFilter == "Alle Pakete" || item.vocabularyPack == packFilter

            matchesSearch && matchesPack
        }
    }

    fun updateUserProfile(
        displayName: String? = null,
        base64Avatar: String? = null,
        dailyGoal: Int? = null,
        reminderEnabled: Boolean? = null,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val userId = currentUserId
            val currentProfile = _uiState.value.userProfile
            val updated = currentProfile.copy(
                userId = userId,
                displayName = displayName ?: currentProfile.displayName,
                base64Avatar = base64Avatar ?: currentProfile.base64Avatar,
                dailyGoal = dailyGoal ?: currentProfile.dailyGoal,
                reminderEnabled = reminderEnabled ?: currentProfile.reminderEnabled,
                email = _uiState.value.user?.email ?: currentProfile.email
            )
            repository.saveUserProfile(userId, updated)
            _uiState.update { it.copy(userProfile = updated) }
            onComplete()
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { currentState ->
            val filtered = filterItems(
                items = currentState.items,
                query = query,
                packFilter = currentState.selectedPackFilter
            )
            currentState.copy(searchQuery = query, filteredItems = filtered)
        }
    }

    fun setPackFilter(pack: String?) {
        _uiState.update { currentState ->
            val newPack = if (currentState.selectedPackFilter == pack) null else pack
            val filtered = filterItems(
                items = currentState.items,
                query = currentState.searchQuery,
                packFilter = newPack
            )
            currentState.copy(selectedPackFilter = newPack, filteredItems = filtered)
        }
    }

    fun saveItem(item: VocabularyItem, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = currentUserId
            val result = if (item.id.isBlank()) {
                repository.addVocabularyItem(userId, item)
            } else {
                repository.updateVocabularyItem(userId, item)
            }
            result.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Fehler beim Speichern der Vokabel") }
            }
            _uiState.update { it.copy(isLoading = false) }
            onComplete()
        }
    }

    fun deleteItem(itemId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteVocabularyItem(currentUserId, itemId).onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Fehler beim Löschen der Vokabel") }
            }
            onComplete()
        }
    }

    fun saveVocabPackage(pkg: VocabPackage, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveVocabPackage(currentUserId, pkg).onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Fehler beim Speichern des Pakets") }
            }
            onComplete()
        }
    }

    fun deleteVocabPackage(packageId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteVocabPackage(currentUserId, packageId)
            onComplete()
        }
    }

    fun reviewItemSrs(item: VocabularyItem, rating: SrsRating) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            var interval = item.interval
            var repetition = item.repetition
            var easeFactor = item.easeFactor

            when (rating) {
                SrsRating.AGAIN -> {
                    interval = 0
                    repetition = 0
                    easeFactor = maxOf(1.3, easeFactor - 0.2)
                }
                SrsRating.HARD -> {
                    interval = maxOf(1, (interval * 1.2).toInt())
                    easeFactor = maxOf(1.3, easeFactor - 0.15)
                }
                SrsRating.GOOD -> {
                    interval = maxOf(1, if (repetition == 0) 1 else if (repetition == 1) 6 else (interval * easeFactor).toInt())
                    repetition++
                }
                SrsRating.EASY -> {
                    interval = maxOf(2, if (interval == 0) 4 else (interval * easeFactor * 1.3).toInt())
                    repetition++
                    easeFactor += 0.15
                }
            }

            val nextReviewDate = now + interval * 24L * 60L * 60L * 1000L
            val isSuccess = rating == SrsRating.GOOD || rating == SrsRating.EASY
            val nextStars = if (isSuccess) minOf(5, item.masteryStars + 1) else maxOf(1, item.masteryStars - 1)

            val updated = item.copy(
                interval = interval,
                repetition = repetition,
                easeFactor = easeFactor,
                nextReviewDate = nextReviewDate,
                masteryStars = nextStars
            )
            repository.updateVocabularyItem(currentUserId, updated)
        }
    }

    fun signUpWithEmail(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            authRepository.signUpWithEmail(email, pass).fold(
                onSuccess = { onSuccess() },
                onFailure = { onError(it.localizedMessage ?: "Registrierung fehlgeschlagen") }
            )
        }
    }

    fun signInWithEmail(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            authRepository.signInWithEmail(email, pass).fold(
                onSuccess = { onSuccess() },
                onFailure = { onError(it.localizedMessage ?: "Anmeldung fehlgeschlagen") }
            )
        }
    }

    fun signOut() {
        authRepository.signOut()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
