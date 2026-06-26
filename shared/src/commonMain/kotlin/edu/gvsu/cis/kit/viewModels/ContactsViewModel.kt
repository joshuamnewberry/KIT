package edu.gvsu.cis.kit.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.gvsu.cis.kit.data.*
import edu.gvsu.cis.kit.triggerCallIntent
import edu.gvsu.cis.kit.triggerSmsIntent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactsViewModel(private val repository: KITRepository) : ViewModel() {

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    private val _filteredContacts = MutableStateFlow<List<Contact>>(emptyList())
    val filteredContacts: StateFlow<List<Contact>> = _filteredContacts.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedContact = MutableStateFlow<Contact?>(null)
    val selectedContact: StateFlow<Contact?> = _selectedContact.asStateFlow()

    private val _selectedContactReminders = MutableStateFlow<List<CheckInReminder>>(emptyList())
    val selectedContactReminders: StateFlow<List<CheckInReminder>> = _selectedContactReminders.asStateFlow()

    init { loadContacts() }

    fun loadContacts() {
        viewModelScope.launch {
            _contacts.value = repository.getAllContacts()
            applySearchFilter(_searchQuery.value)
        }
    }

    fun deleteContact(contactId: String) {
        viewModelScope.launch {
            repository.getContactById(contactId)?.let { repository.deleteContact(it); loadContacts() }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applySearchFilter(query)
    }

    private fun applySearchFilter(query: String) {
        _filteredContacts.value = if (query.isBlank()) _contacts.value else _contacts.value.filter {
            it.name.contains(query, true) || (it.relationshipType?.contains(query, true) == true)
        }
    }

    fun selectContact(contactId: String) {
        viewModelScope.launch {
            _selectedContact.value = repository.getContactById(contactId)
            _selectedContactReminders.value = repository.getRemindersForContact(contactId)
        }
    }

    fun updateContact(updatedContact: Contact) {
        viewModelScope.launch {
            repository.updateContact(updatedContact)
            _selectedContact.value = updatedContact
            loadContacts()
        }
    }

    fun updateNotes(contactId: String, newNotes: String) {
        viewModelScope.launch {
            _selectedContact.value?.takeIf { it.id == contactId }?.let {
                val updated = it.copy(notes = newNotes)
                repository.updateContact(updated)
                _selectedContact.value = updated
            }
        }
    }

    fun logInteraction(contactId: String) {
        viewModelScope.launch { repository.logInteraction(contactId) }
    }

    fun triggerCall(phoneNumber: String) { if (phoneNumber.isNotBlank()) triggerCallIntent(phoneNumber) }

    fun triggerMessage(phoneNumber: String) { if (phoneNumber.isNotBlank()) triggerSmsIntent(phoneNumber) }

    fun addContact(name: String, phoneNumber: String, email: String, relationshipType: String, profilePictureUri: String? = null) {
        viewModelScope.launch {
            repository.addContact(
                name,
                phoneNumber.ifBlank { null },
                email.ifBlank { null },
                relationshipType.ifBlank { null },
                profilePictureUri
            )
            loadContacts()
        }
    }
}