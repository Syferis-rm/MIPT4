package com.calculator.app

/**
 * Singleton repository for storing calculation history in memory (runtime only).
 */
object HistoryRepository {
    private val _history = mutableListOf<HistoryEntry>()

    fun getHistory(): List<HistoryEntry> = _history.toList()

    fun addEntry(entry: HistoryEntry) {
        _history.add(0, entry) // newest first
    }

    fun clear() {
        _history.clear()
    }
}
