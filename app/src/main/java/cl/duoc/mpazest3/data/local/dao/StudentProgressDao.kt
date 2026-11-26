package cl.duoc.mpazest3.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.duoc.mpazest3.data.local.entity.StudentAttemptEntity
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmSuppressWildcards

@Dao
interface StudentProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    suspend fun insertAttempt(attempt: StudentAttemptEntity): Long

    @Query("SELECT * FROM student_attempts ORDER BY id DESC")
    fun getAllAttempts(): Flow<List<StudentAttemptEntity>>

    @Query("SELECT * FROM student_attempts WHERE subjectId = :subjectId")
    fun getAttemptsBySubject(subjectId: String): Flow<List<StudentAttemptEntity>>

    @Query("SELECT COUNT(*) FROM student_attempts WHERE LOWER(studentName) = LOWER(:studentName) AND subjectId = :subjectId")
    @JvmSuppressWildcards
    suspend fun getAttemptCountForSubject(studentName: String, subjectId: String): Int

    @Query("DELETE FROM student_attempts WHERE LOWER(studentName) = LOWER(:studentName) AND subjectId = :subjectId")
    @JvmSuppressWildcards
    suspend fun unlockSubjectForStudent(studentName: String, subjectId: String): Int

    // Al eliminar un estudiante se borran sus evaluaciones
    @Query("DELETE FROM student_attempts WHERE LOWER(studentName) = LOWER(:studentName)")
    @JvmSuppressWildcards
    suspend fun deleteAttemptsOfStudent(studentName: String): Int

    // Si el docente cambia el nombre de un estudiante, sus evaluaciones siguen asociadas a él
    @Query("UPDATE student_attempts SET studentName = :newName WHERE LOWER(studentName) = LOWER(:oldName)")
    @JvmSuppressWildcards
    suspend fun renameStudentInAttempts(oldName: String, newName: String): Int
}