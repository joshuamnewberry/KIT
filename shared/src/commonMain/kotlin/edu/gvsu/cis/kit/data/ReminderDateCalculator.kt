package edu.gvsu.cis.kit.data

import edu.gvsu.cis.kit.getCurrentTimeMillis

object ReminderDateCalculator {
    fun calculateNextDate(
        frequencyType: ReminderFrequencyType,
        frequencyValue: Int? = null,
        currentDateMillis: Long = getCurrentTimeMillis()
    ): Long {
        val oneDayMillis = 24L * 60 * 60 * 1000L
        return when (frequencyType) {
            ReminderFrequencyType.DAILY -> currentDateMillis + oneDayMillis
            ReminderFrequencyType.WEEKLY -> currentDateMillis + (7 * oneDayMillis)
            ReminderFrequencyType.MONTHLY -> currentDateMillis + (30 * oneDayMillis)
            ReminderFrequencyType.YEARLY -> currentDateMillis + (365 * oneDayMillis)
            ReminderFrequencyType.CUSTOM -> currentDateMillis + ((frequencyValue ?: 1) * oneDayMillis)
        }
    }
}