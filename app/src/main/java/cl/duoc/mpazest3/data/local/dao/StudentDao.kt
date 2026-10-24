package cl.duoc.mpazest3.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.duoc.mpazest3.data.local.entity.StudentEntity
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmSuppressWildcards

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    suspend fun insertStudent(student: StudentEntity): Long

    @Query("SELECT * FROM students WHERE username = :username LIMIT 1")
    @JvmSuppressWildcards
    suspend fun getStudentByUsername(username: String): StudentEntity?

    @Query("SELECT * FROM students WHERE username = :username AND pin = :pin LIMIT 1")
    @JvmSuppressWildcards
    suspend fun loginStudent(username: String, pin: String): StudentEntity?

    @Query("SELECT * FROM students ORDER BY fullName ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>
}