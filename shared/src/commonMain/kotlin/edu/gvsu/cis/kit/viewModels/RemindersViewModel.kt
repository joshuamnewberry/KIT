package edu.gvsu.cis.kit.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.gvsu.cis.kit.data.CheckInReminder
import edu.gvsu.cis.kit.data.Contact
import edu.gvsu.cis.kit.data.KITRepository
import edu.gvsu.cis.kit.data.ReminderFrequencyType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RemindersViewModel(private val repository: KITRepository) : ViewModel() {

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    private val _reminders = MutableStateFlow<List<Pair<CheckInReminder, List<Contact>>>>(emptyList())
    val reminders: StateFlow<List<Pair<CheckInReminder, List<Contact>>>> = _reminders.asStateFlow()

    init { loadContacts(); loadReminders() }

    fun loadContacts() { viewModelScope.launch { _contacts.value = repository.getAllContacts() } }

    fun loadReminders() {
        viewModelScope.launch {
            _reminders.value = repository.getAllReminders().map { reminder ->
                Pair(reminder, repository.getContactsForReminder(reminder.id))
            }
        }
    }

    fun addReminder(contactId: String, title: String, frequencyType: ReminderFrequencyType, frequencyValue: Int? = null) {
        viewModelScope.launch {
            repository.addReminder(listOf(contactId), title, frequencyType, frequencyValue)
            loadReminders()
        }
    }

    fun updateReminder(reminder: CheckInReminder, newContactId: String? = null) {
        viewModelScope.launch {
            repository.updateReminder(reminder)
            loadReminders()
        }
    }

    fun deleteReminder(reminder: CheckInReminder) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
            loadReminders()
        }
    }
}