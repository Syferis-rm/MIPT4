package com.calculator.app

/**
 * Represents a single calculation history entry.
 */
data class HistoryEntry(
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)
