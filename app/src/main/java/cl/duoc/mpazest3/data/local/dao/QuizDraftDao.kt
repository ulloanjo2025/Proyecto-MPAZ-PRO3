package cl.duoc.mpazest3.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.duoc.mpazest3.data.local.entity.QuizDraftEntity
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmSuppressWildcards
// Desarrollado por: Jonathan Ulloa
@Dao
interface QuizDraftDao {

    // Inserta el borrador o lo reemplaza si ya existía (mismo estudiante y asignatura)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    suspend fun upsertDraft(draft: QuizDraftEntity): Long

    @Query("SELECT * FROM quiz_drafts")
    fun observeAll(): Flow<List<QuizDraftEntity>>

    @Query("DELETE FROM quiz_drafts WHERE studentName = :studentName AND subjectId = :subjectId")
    @JvmSuppressWildcards
    suspend fun deleteDraft(studentName: String, subjectId: String): Int
}
