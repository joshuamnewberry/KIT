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

    // Weekly summary state
    private val _weeklyContactsCount = MutableStateFlow(0)
    val weeklyContactsCount: StateFlow<Int> = _weeklyContactsCount.asStateFlow()

    // Upcoming reminders state
    private val _dueReminders = MutableStateFlow<List<Pair<CheckInReminder, Contact>>>(emptyList())
    val dueReminders: StateFlow<List<Pair<CheckInReminder, Contact>>> = _dueReminders.asStateFlow()

    // Settings state
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _dailyDigestEnabled = MutableStateFlow(true)
    val dailyDigestEnabled: StateFlow<Boolean> = _dailyDigestEnabled.asStateFlow()

    private val _pushAlertsEnabled = MutableStateFlow(true)
    val pushAlertsEnabled: StateFlow<Boolean> = _pushAlertsEnabled.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            // TODO: Implement interaction logging and retrieval (KIT-87)
            _weeklyContactsCount.value = 4

            val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
            val reminders = dao.getDueReminders(now)

            val remindersWithContacts = reminders.flatMap { reminder ->
                val contacts = dao.getContactsForReminder(reminder.id)
                contacts.map { contact ->
                    Pair(reminder, contact)
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

    // Settings toggle functions
    fun toggleDarkMode(enabled: Boolean) { _isDarkMode.value = enabled }
    fun toggleDailyDigest(enabled: Boolean) { _dailyDigestEnabled.value = enabled }
    fun togglePushAlerts(enabled: Boolean) { _pushAlertsEnabled.value = enabled }

    // Data management functions
    fun clearAllData() {
        // TODO: Implement "Clear All Data" database wipe logic (KIT-80)
    }

    fun exportBackup() {
        // TODO: Implement "Export Backup" (KIT-81)
    }

    fun importBackup() {
        // TODO: Implement "Import Backup" (KIT-82)
    }
}