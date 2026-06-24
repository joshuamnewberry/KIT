package edu.gvsu.cis.kit.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.gvsu.cis.kit.data.AppDAO
import edu.gvsu.cis.kit.data.CheckInReminder
import edu.gvsu.cis.kit.data.Contact
import edu.gvsu.cis.kit.data.KITRepository
import edu.gvsu.cis.kit.getKeyValueStore
import edu.gvsu.cis.kit.getCurrentTimeMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val dao: AppDAO) : ViewModel() {
    private val repository = KITRepository(dao)
    private val store = getKeyValueStore()

    private val _weeklyContactsCount = MutableStateFlow(0)
    val weeklyContactsCount: StateFlow<Int> = _weeklyContactsCount.asStateFlow()

    private val _dueReminders = MutableStateFlow<List<Pair<CheckInReminder, Contact>>>(emptyList())
    val dueReminders: StateFlow<List<Pair<CheckInReminder, Contact>>> = _dueReminders.asStateFlow()

    private val _isDarkMode = MutableStateFlow(store.getBoolean("pref_dark_mode", true))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _dailyDigestEnabled = MutableStateFlow(store.getBoolean("pref_daily_digest", true))
    val dailyDigestEnabled: StateFlow<Boolean> = _dailyDigestEnabled.asStateFlow()

    private val _pushAlertsEnabled = MutableStateFlow(store.getBoolean("pref_push_alerts", true))
    val pushAlertsEnabled: StateFlow<Boolean> = _pushAlertsEnabled.asStateFlow()

    init { loadHomeData() }

    fun loadHomeData() {
        viewModelScope.launch {
            _weeklyContactsCount.value = repository.getWeeklyInteractionCount()

            val reminders = dao.getDueReminders(getCurrentTimeMillis())
            _dueReminders.value = reminders.flatMap { reminder ->
                dao.getContactsForReminder(reminder.id).map { Pair(reminder, it) }
            }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        store.setBoolean("pref_dark_mode", enabled)
        _isDarkMode.value = enabled
    }

    fun toggleDailyDigest(enabled: Boolean) {
        store.setBoolean("pref_daily_digest", enabled)
        _dailyDigestEnabled.value = enabled
    }

    fun togglePushAlerts(enabled: Boolean) {
        store.setBoolean("pref_push_alerts", enabled)
        _pushAlertsEnabled.value = enabled
    }

    fun clearAllData() {
        viewModelScope.launch {
            dao.getAllContacts().forEach { contact ->
                dao.getRemindersForContact(contact.id).forEach { dao.deleteReminder(it) }
                dao.getEventsForContact(contact.id).forEach { dao.deleteEvent(it) }
                dao.getImportantDatesForContact(contact.id).forEach { dao.deleteImportantDate(it) }
                dao.deleteContact(contact)
            }
            loadHomeData()
        }
    }
}