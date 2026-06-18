package edu.gvsu.cis.kit.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.gvsu.cis.kit.data.AppDAO
import edu.gvsu.cis.kit.data.CheckInReminder
import edu.gvsu.cis.kit.data.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val dao: AppDAO
) : ViewModel() {

    // --- Home Screen State ---
    private val _weeklyContactsCount = MutableStateFlow(0)
    val weeklyContactsCount: StateFlow<Int> = _weeklyContactsCount.asStateFlow()

    private val _dueReminders = MutableStateFlow<List<Pair<CheckInReminder, Contact>>>(emptyList())
    val dueReminders: StateFlow<List<Pair<CheckInReminder, Contact>>> = _dueReminders.asStateFlow()

    // --- Settings Screen State ---
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _dailyDigestEnabled = MutableStateFlow(true)
    val dailyDigestEnabled: StateFlow<Boolean> = _dailyDigestEnabled.asStateFlow()

    private val _pushAlertsEnabled = MutableStateFlow(true)
    val pushAlertsEnabled: StateFlow<Boolean> = _pushAlertsEnabled.asStateFlow()

    init {
        loadHomeData()
    }

    // --- Home Screen Functions ---
    fun loadHomeData() {
        viewModelScope.launch {
            _weeklyContactsCount.value = 4

            val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
            val reminders = dao.getDueReminders(now)

            val remindersWithContacts = reminders.mapNotNull { reminder ->
                val contact = dao.getContactById(reminder.contactId)
                if (contact != null) {
                    Pair(reminder, contact)
                } else {
                    null
                }
            }

            _dueReminders.value = remindersWithContacts
        }
    }

    fun markReminderComplete(reminder: CheckInReminder) {
        viewModelScope.launch {
            val updatedReminder = reminder.copy(isCompleted = true)
            dao.updateReminder(updatedReminder)
            loadHomeData()
        }
    }

    // --- Settings Screen Functions ---
    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    fun toggleDailyDigest(enabled: Boolean) {
        _dailyDigestEnabled.value = enabled
    }

    fun togglePushAlerts(enabled: Boolean) {
        _pushAlertsEnabled.value = enabled
    }

    fun clearAllData() {
        // TODO: Call DAO methods to drop tables or delete all rows
    }

    fun exportBackup() {
        // TODO: Handle exporting SQLite to JSON or sending a file intent
    }

    fun importBackup() {
        // TODO: Handle reading a file and reconstructing the database
    }
}