// Desarrollado por: Camilo Alarcón (data/repository)
package cl.duoc.mpazest3.data.repository

import cl.duoc.mpazest3.data.local.dao.StudentProgressDao
import cl.duoc.mpazest3.data.local.entity.StudentAttemptEntity
import kotlinx.coroutines.flow.Flow

class StudentRepository(
    private val studentProgressDao: StudentProgressDao
) {
    val allAttempts: Flow<List<StudentAttemptEntity>> = studentProgressDao.getAllAttempts()

    fun getAttemptsBySubject(subjectId: String): Flow<List<StudentAttemptEntity>> {
        return studentProgressDao.getAttemptsBySubject(subjectId)
    }

    suspend fun saveAttempt(studentName: String, subjectId: String, isCorrect: Boolean) {
        val attempt = StudentAttemptEntity(
            studentName = studentName,
            subjectId = subjectId,
            isCorrect = isCorrect
        )
        studentProgressDao.insertAttempt(attempt)
    }
}