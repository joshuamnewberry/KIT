package edu.gvsu.cis.kit.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.gvsu.cis.kit.data.Contact
import edu.gvsu.cis.kit.data.ImportantDate
import edu.gvsu.cis.kit.data.ImportantDateType
import edu.gvsu.cis.kit.data.KITRepository
import edu.gvsu.cis.kit.data.ReminderFrequency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ContactsViewModel(
    private val repository: KITRepository
) : ViewModel() {

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts

    private val _importantDates = MutableStateFlow<List<ImportantDate>>(emptyList())
    val importantDates: StateFlow<List<ImportantDate>> = _importantDates

    fun loadContacts() {
        viewModelScope.launch {
            _contacts.value = repository.getAllContacts()
        }
    }

    fun addContact(
        name: String,
        phoneNumber: String,
        email: String,
        relationshipType: String
    ) {
        viewModelScope.launch {
            repository.addContact(
                name = name,
                phoneNumber = phoneNumber.ifBlank { null },
                email = email.ifBlank { null },
                relationshipType = relationshipType.ifBlank { null }
            )
            loadContacts()
        }
    }

    fun addWeeklyReminder(contactId: String) {
        viewModelScope.launch {
            repository.addReminder(contactId, ReminderFrequency.WEEKLY)
        }
    }

    fun addMonthlyReminder(contactId: String) {
        viewModelScope.launch {
            repository.addReminder(contactId, ReminderFrequency.MONTHLY)
        }
    }

    fun addQuarterlyReminder(contactId: String) {
        viewModelScope.launch {
            repository.addReminder(contactId, ReminderFrequency.QUARTERLY)
        }
    }

    fun addBirthday(contactId: String, title: String, dateMillis: Long) {
        viewModelScope.launch {
            repository.addImportantDate(
                contactId = contactId,
                title = title,
                type = ImportantDateType.BIRTHDAY,
                dateMillis = dateMillis
            )
            _importantDates.value = repository.getImportantDatesForContact(contactId)
        }
    }
}
