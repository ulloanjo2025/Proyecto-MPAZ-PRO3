package cl.duoc.mpazest3.viewmodel
import androidx.lifecycle.ViewModel
import cl.duoc.mpazest3.model.Subject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ViewModel para gestionar la lista de asignaturas y la asignatura seleccionada
class SubjectViewModel : ViewModel() {

    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    private val _selectedSubject = MutableStateFlow<Subject?>(null)
    val selectedSubject: StateFlow<Subject?> = _selectedSubject.asStateFlow()

    init {
        loadSubjects()
    }

    private fun loadSubjects() {
        _subjects.value = listOf(
            Subject("math", "Matemáticas", "Sumas, restas y geometría lúdica", 0, 0xFF4CAF50),
            Subject("lang", "Lenguaje", "Lectura entretenida y vocabulario", 0, 0xFF2196F3),
            Subject("sci", "Ciencias", "Descubre la naturaleza y experimentos", 0, 0xFFFF9800)
        )
    }

    fun selectSubject(subject: Subject) {
        _selectedSubject.value = subject
    }
}