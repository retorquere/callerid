package com.example.simplecallerid.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplecallerid.db.UserDatabase
import com.example.simplecallerid.db.UserRepository
import com.example.simplecallerid.models.User

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val repository: UserRepository

    init {
        val userDao = UserDatabase.getDatabase(app).userDao()
        repository = UserRepository(userDao)
    }

    fun insert(user: User) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(user)
    }

    fun update(user: User) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(user)
    }

    fun delete(user: User) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(user)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }

}
