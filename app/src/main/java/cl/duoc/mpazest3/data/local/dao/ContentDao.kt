package cl.duoc.mpazest3.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.duoc.mpazest3.data.local.entity.LearningObjectiveEntity
import cl.duoc.mpazest3.data.local.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmSuppressWildcards
// Desarrollado por: Jonathan Ulloa
@Dao
interface ContentDao {

    // Cada insert devuelve Long (el mismo patrón de StudentDao) para evitar el error de KSP "unexpected jvm signature V"
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    suspend fun insertObjective(objective: LearningObjectiveEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Query("SELECT COUNT(*) FROM learning_objectives")
    @JvmSuppressWildcards
    suspend fun countObjectives(): Int

    // Un OA por su código (null si no existe todavía)
    @Query("SELECT * FROM learning_objectives WHERE code = :code LIMIT 1")
    @JvmSuppressWildcards
    suspend fun getObjective(code: String): LearningObjectiveEntity?

    // Todas las preguntas de un OA
    @Query("SELECT * FROM questions WHERE objectiveCode = :code ORDER BY id ASC")
    @JvmSuppressWildcards
    suspend fun getQuestionsOf(code: String): List<QuestionEntity>

    // Borra las preguntas de un OA (se usa al sincronizar cuando cambiaron)
    @Query("DELETE FROM questions WHERE objectiveCode = :code")
    @JvmSuppressWildcards
    suspend fun deleteQuestionsOf(code: String): Int

    // Todos los OA (la pantalla de UTP los observa para autorizar o rechazar)
    @Query("SELECT * FROM learning_objectives ORDER BY code ASC")
    fun getAllObjectives(): Flow<List<LearningObjectiveEntity>>

    // Solo los OA autorizados de una asignatura (lo que ve el estudiante)
    @Query("SELECT * FROM learning_objectives WHERE subjectId = :subjectId AND status = 'AUTORIZADO' ORDER BY code ASC")
    @JvmSuppressWildcards
    suspend fun getAuthorizedObjectives(subjectId: String): List<LearningObjectiveEntity>

    // Preguntas de los OA autorizados de una asignatura: por OA y de menor a mayor dificultad
    @Query("SELECT q.* FROM questions q INNER JOIN learning_objectives o ON q.objectiveCode = o.code WHERE o.subjectId = :subjectId AND o.status = 'AUTORIZADO' ORDER BY o.code ASC, q.difficulty ASC, q.id ASC")
    @JvmSuppressWildcards
    suspend fun getAuthorizedQuestions(subjectId: String): List<QuestionEntity>

    // Actualizar el estado de autorización de un OA
    @Query("UPDATE learning_objectives SET status = :status WHERE code = :code")
    @JvmSuppressWildcards
    suspend fun updateObjectiveStatus(code: String, status: String): Int
}
