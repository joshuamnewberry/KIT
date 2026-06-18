package edu.gvsu.cis.kit.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.gvsu.cis.kit.data.CheckInReminder
import edu.gvsu.cis.kit.data.Contact
import edu.gvsu.cis.kit.data.ImportantDate
import edu.gvsu.cis.kit.data.ImportantDateType
import edu.gvsu.cis.kit.data.KITRepository
import edu.gvsu.cis.kit.data.ReminderFrequencyType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactsViewModel(
    private val repository: KITRepository
) : ViewModel() {

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())

    private val _filteredContacts = MutableStateFlow<List<Contact>>(emptyList())
    val filteredContacts: StateFlow<List<Contact>> = _filteredContacts.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // State for Individual Contact Screen
    private val _selectedContact = MutableStateFlow<Contact?>(null)
    val selectedContact: StateFlow<Contact?> = _selectedContact.asStateFlow()

    private val _selectedContactReminders = MutableStateFlow<List<CheckInReminder>>(emptyList())
    val selectedContactReminders: StateFlow<List<CheckInReminder>> = _selectedContactReminders.asStateFlow()

    private val _importantDates = MutableStateFlow<List<ImportantDate>>(emptyList())
    val importantDates: StateFlow<List<ImportantDate>> = _importantDates.asStateFlow()

    init {
        loadContacts()
    }

    fun loadContacts() {
        viewModelScope.launch {
            val allContacts = repository.getAllContacts()
            _contacts.value = allContacts
            applySearchFilter(_searchQuery.value)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applySearchFilter(query)
    }

    private fun applySearchFilter(query: String) {
        if (query.isBlank()) {
            _filteredContacts.value = _contacts.value
        } else {
            _filteredContacts.value = _contacts.value.filter {
                it.name.contains(query, ignoreCase = true) ||
                        (it.relationshipType?.contains(query, ignoreCase = true) == true)
            }
        }
    }

    fun selectContact(contactId: String) {
        viewModelScope.launch {
            _selectedContact.value = repository.getContactById(contactId)
            _selectedContactReminders.value = repository.getRemindersForContact(contactId)
            _importantDates.value = repository.getImportantDatesForContact(contactId)
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
            // Fixed: Now wraps contactId in a List and uses the new Enum
            repository.addReminder(
                contactIds = listOf(contactId),
                frequencyType = ReminderFrequencyType.WEEKLY
            )
            _selectedContactReminders.value = repository.getRemindersForContact(contactId)
        }
    }

    fun addMonthlyReminder(contactId: String) {
        viewModelScope.launch {
            // Fixed: Now wraps contactId in a List and uses the new Enum
            repository.addReminder(
                contactIds = listOf(contactId),
                frequencyType = ReminderFrequencyType.MONTHLY
            )
            _selectedContactReminders.value = repository.getRemindersForContact(contactId)
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