package edu.gvsu.cis.kit.data

object ReminderDateCalculator {

    private const val DAY_IN_MILLIS = 24L * 60L * 60L * 1000L

    fun calculateNextReminderMillis(
        currentTimeMillis: Long,
        frequencyType: ReminderFrequencyType,
        frequencyValue: Int? = null
    ): Long {
        // TODO: Implement precise calendar math (e.g., using kotlinx-datetime) to handle
        // specific days of the week or month using the frequencyValue parameter (KIT-70)
        return when (frequencyType) {
            ReminderFrequencyType.DAILY -> currentTimeMillis + 1L * DAY_IN_MILLIS
            ReminderFrequencyType.WEEKLY -> currentTimeMillis + 7L * DAY_IN_MILLIS
            ReminderFrequencyType.MONTHLY -> currentTimeMillis + 30L * DAY_IN_MILLIS
        }
    }
}