package edu.gvsu.cis.kit.data

object ReminderDateCalculator {

    private const val DAY_IN_MILLIS = 24L * 60L * 60L * 1000L

    fun calculateNextReminderMillis(
        currentTimeMillis: Long,
        frequency: ReminderFrequency
    ): Long {
        return when (frequency) {
            ReminderFrequency.WEEKLY -> currentTimeMillis + 7L * DAY_IN_MILLIS
            ReminderFrequency.MONTHLY -> currentTimeMillis + 30L * DAY_IN_MILLIS
            ReminderFrequency.QUARTERLY -> currentTimeMillis + 90L * DAY_IN_MILLIS
        }
    }
}
