package com.example.voca.utils

enum class MatchResult {
    EXACT,      // Perfekt
    TYPO,       // Fast richtig (Tippfehler)
    WRONG       // Falsch
}

object StringUtils {
    fun levenshteinDistance(s1: String, s2: String): Int {
        val a = s1.lowercase().trim()
        val b = s2.lowercase().trim()
        if (a == b) return 0
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length

        val costs = IntArray(b.length + 1)
        for (j in 0..b.length) costs[j] = j

        for (i in 1..a.length) {
            costs[0] = i
            var nw = i - 1
            for (j in 1..b.length) {
                val cj = minOf(
                    1 + minOf(costs[j], costs[j - 1]),
                    if (a[i - 1] == b[j - 1]) nw else nw + 1
                )
                nw = costs[j]
                costs[j] = cj
            }
        }
        return costs[b.length]
    }

    fun evaluateMatch(input: String, expected: String): MatchResult {
        val cleanInput = input.lowercase().trim()
        val cleanExpected = expected.lowercase().trim()
        if (cleanInput.isBlank()) return MatchResult.WRONG
        if (cleanInput == cleanExpected) return MatchResult.EXACT

        val dist = levenshteinDistance(cleanInput, cleanExpected)
        val maxAllowedTypoDistance = if (cleanExpected.length > 5) 2 else 1

        return if (dist <= maxAllowedTypoDistance) MatchResult.TYPO else MatchResult.WRONG
    }
}
