package com.example.tracklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TaskViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _allTasks = MutableLiveData<List<Task>>()
    private val _filteredTasks = MutableLiveData<List<Task>>()
    val filteredTasks: LiveData<List<Task>> = _filteredTasks

    private var currentFilter: String? = null
    private var currentSortOrder: SortOrder = SortOrder.DATE

    init {
        fetchTasks()
    }

    private fun fetchTasks() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val snapshot = firestore.collection("users").document(userId)
                .collection("tasks")
                .get()
                .await()
            val tasks = snapshot.toObjects(Task::class.java)
            _allTasks.value = tasks
            applyFilterAndSort()
        }
    }

    fun insertTask(task: Task) = viewModelScope.launch {
        val userId = auth.currentUser?.uid ?: return@launch
        firestore.collection("users").document(userId)
            .collection("tasks")
            .add(task)
        fetchTasks()
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        val userId = auth.currentUser?.uid ?: return@launch
        firestore.collection("users").document(userId)
            .collection("tasks")
            .document(task.id)
            .set(task)
        fetchTasks()
    }

    fun deleteTask(taskId: String) = viewModelScope.launch {
        val userId = auth.currentUser?.uid ?: return@launch
        firestore.collection("users").document(userId)
            .collection("tasks")
            .document(taskId)
            .delete()
        fetchTasks()
    }

    fun getTaskById(taskId: String): LiveData<Task?> {
        val result = MutableLiveData<Task?>()
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val document = firestore.collection("users").document(userId)
                .collection("tasks")
                .document(taskId)
                .get()
                .await()
            result.value = document.toObject(Task::class.java)
        }
        return result
    }

    fun filterTasks(category: String?) {
        currentFilter = category
        applyFilterAndSort()
    }

    fun sortTasks(sortOrder: SortOrder) {
        currentSortOrder = sortOrder
        applyFilterAndSort()
    }

    private fun applyFilterAndSort() {
        viewModelScope.launch {
            var filteredList = _allTasks.value ?: emptyList()

            // Apply filter
            currentFilter?.let { filter ->
                filteredList = filteredList.filter { it.category == filter }
            }

            // Apply sort
            filteredList = when (currentSortOrder) {
                SortOrder.DATE -> filteredList.sortedBy { it.dueDate }
                SortOrder.PRIORITY -> filteredList.sortedByDescending { it.priority }
                SortOrder.ALPHABETICAL -> filteredList.sortedBy { it.title }
            }

            _filteredTasks.postValue(filteredList)
        }
    }

    enum class SortOrder {
        DATE, PRIORITY, ALPHABETICAL
    }
}