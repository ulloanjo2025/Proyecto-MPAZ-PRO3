package cl.duoc.mpazest3.data.repository

import cl.duoc.mpazest3.data.local.dao.ContentDao
import cl.duoc.mpazest3.data.local.entity.LearningObjectiveEntity
import cl.duoc.mpazest3.data.local.entity.QuestionEntity
import cl.duoc.mpazest3.data.local.seed.ContentSeed
import cl.duoc.mpazest3.data.remote.ApiConfig
import cl.duoc.mpazest3.data.remote.ContentApiService
import cl.duoc.mpazest3.data.remote.ContentSyncRules
import cl.duoc.mpazest3.data.remote.SyncResult
import cl.duoc.mpazest3.data.remote.toEntityOrNull
import com.google.gson.JsonParseException
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
//Desarrollado por: Camilo Alarcón
// Intermediario entre los ViewModel y las dos fuentes de datos del contenido pedagógico:
// la base de datos local (Room) y el servicio REST (Retrofit).
class ContentRepository(
    private val contentDao: ContentDao,
    private val api: ContentApiService,
    private val isApiConfigured: () -> Boolean = { ApiConfig.isConfigured }
) {
    val objectives: Flow<List<LearningObjectiveEntity>> = contentDao.getAllObjectives()

    // Guarda el contenido inicial solo si la base está vacía (se puede llamar muchas veces sin duplicar).
    suspend fun ensureSeeded() {
        if (contentDao.countObjectives() == 0) {
            ContentSeed.objectives.forEach { contentDao.insertObjective(it) }
            ContentSeed.questions.forEach { contentDao.insertQuestion(it) }
        }
    }

    // Pide el contenido al servicio REST y lo guarda en Room.
    // La autorización de UTP NO se pisa: un OA sin cambios conserva su estado.
    // Si falla la conexión o el servicio, no se toca nada y la app sigue con el contenido guardado.
    suspend fun syncFromRemote(): SyncResult {
        if (!isApiConfigured()) return SyncResult.NotConfigured

        return try {
            val response = api.getContent()
            val remoteObjectives = response.objectives.orEmpty().mapNotNull { it.toEntityOrNull() }
            val remoteQuestions = response.questions.orEmpty().mapNotNull { it.toEntityOrNull() }

            if (remoteObjectives.isEmpty()) {
                return SyncResult.Error("El servicio no entregó objetivos de aprendizaje válidos. Se usa el contenido guardado.")
            }

            var updated = 0
            for (incoming in remoteObjectives) {
                val incomingQuestions = remoteQuestions.filter { it.objectiveCode == incoming.code }
                val oldObjective = contentDao.getObjective(incoming.code)
                val oldQuestions = contentDao.getQuestionsOf(incoming.code)

                val status = ContentSyncRules.resolveStatus(oldObjective, oldQuestions, incoming, incomingQuestions)
                val finalObjective = incoming.copy(status = status)

                val objectiveChanged = oldObjective != finalObjective
                val questionsChanged = oldQuestions.sortedBy { it.id } != incomingQuestions.sortedBy { it.id }

                if (objectiveChanged || questionsChanged) {
                    if (questionsChanged) {
                        contentDao.deleteQuestionsOf(incoming.code)
                        incomingQuestions.forEach { contentDao.insertQuestion(it) }
                    }
                    contentDao.insertObjective(finalObjective)
                    updated++
                }
            }

            SyncResult.Success(objectivesChecked = remoteObjectives.size, objectivesUpdated = updated)
        } catch (e: HttpException) {
            SyncResult.Error("El servicio respondió con error (código " + e.code() + "). Se usa el contenido guardado.")
        } catch (e: IOException) {
            SyncResult.Error("Sin conexión o servicio no disponible. Se usa el contenido guardado en el teléfono.")
        } catch (e: JsonParseException) {
            SyncResult.Error("El contenido recibido no tiene el formato esperado. Se usa el contenido guardado.")
        }
    }

    suspend fun getAuthorizedObjectives(subjectId: String): List<LearningObjectiveEntity> {
        return contentDao.getAuthorizedObjectives(subjectId)
    }

    suspend fun getQuizQuestions(subjectId: String): List<QuestionEntity> {
        return contentDao.getAuthorizedQuestions(subjectId)
    }

    suspend fun setObjectiveStatus(code: String, status: String) {
        contentDao.updateObjectiveStatus(code, status)
    }
}
