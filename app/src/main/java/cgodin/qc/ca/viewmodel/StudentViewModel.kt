package cgodin.qc.ca.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cgodin.qc.ca.model.StudentRegistrationRequest
import cgodin.qc.ca.model.StudentRegistrationResponse
import cgodin.qc.ca.repository.StudentRepository
import kotlinx.coroutines.launch

class StudentViewModel(private val studentRepository: StudentRepository) : ViewModel() {
    val registrationResponse = MutableLiveData<StudentRegistrationResponse>()

    fun registerStudent(request: StudentRegistrationRequest) {
        viewModelScope.launch {
            try {
                val response = studentRepository.registerStudent(request)
                if (response.isSuccessful && response.body() != null) {
                    registrationResponse.postValue(response.body())
                } else {
                }
            } catch (e: Exception) {
            }
        }
    }
}
