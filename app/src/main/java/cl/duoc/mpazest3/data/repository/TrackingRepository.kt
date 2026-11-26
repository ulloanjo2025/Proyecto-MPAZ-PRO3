package cl.duoc.mpazest3.data.repository

import cl.duoc.mpazest3.data.local.dao.StudentDao
import cl.duoc.mpazest3.data.local.dao.StudentProgressDao
import cl.duoc.mpazest3.data.local.entity.StudentAttemptEntity
import cl.duoc.mpazest3.data.local.entity.StudentEntity
import cl.duoc.mpazest3.data.remote.AttemptRequest
import cl.duoc.mpazest3.data.remote.LoginRequest
import cl.duoc.mpazest3.data.remote.RemoteResult
import cl.duoc.mpazest3.data.remote.StudentApiService
import cl.duoc.mpazest3.data.remote.StudentRequest
import cl.duoc.mpazest3.data.remote.toEntityOrNull
import com.google.gson.JsonParseException
import java.io.IOException
import retrofit2.HttpException

// Intermediario entre la app y el microservicio de seguimiento.
// El microservicio es la fuente oficial de los estudiantes; Room guarda una copia local.
class TrackingRepository(
    private val studentDao: StudentDao,
    private val progressDao: StudentProgressDao,
    private val api: StudentApiService
) {

    // READ: trae los estudiantes del microservicio y deja Room igual
    suspend fun syncStudents(): RemoteResult = remoteCall {
        val students = api.getStudents().mapNotNull { it.toEntityOrNull() }
        studentDao.deleteAllStudents()
        students.forEach { studentDao.insertStudent(it) }
        "Estudiantes sincronizados: " + students.size
    }

    // CREATE: envía el estudiante al microservicio y lo guarda en Room con el id que le dio
    suspend fun createStudent(username: String, fullName: String, pin: String): RemoteResult = remoteCall {
        val created = api.createStudent(StudentRequest(username, fullName, pin))
        val entity = created.toEntityOrNull() ?: throw JsonParseException("Estudiante inválido")
        studentDao.insertStudent(entity)
        "Estudiante registrado con éxito."
    }

    // UPDATE: cambia el nombre y el PIN (el código no cambia)
    suspend fun updateStudent(student: StudentEntity, fullName: String, pin: String): RemoteResult = remoteCall {
        val updated = api.updateStudent(student.id.toLong(), StudentRequest(student.username, fullName, pin))
        val entity = updated.toEntityOrNull() ?: throw JsonParseException("Estudiante inválido")
        studentDao.insertStudent(entity)
        if (!student.fullName.equals(entity.fullName, ignoreCase = true)) {
            // Sus evaluaciones siguen asociadas al estudiante con su nuevo nombre
            progressDao.renameStudentInAttempts(student.fullName, entity.fullName)
        }
        "Estudiante actualizado."
    }

    // DELETE: elimina al estudiante del microservicio y de Room (junto con sus evaluaciones)
    suspend fun deleteStudent(student: StudentEntity): RemoteResult = remoteCall {
        val response = api.deleteStudent(student.id.toLong())
        if (!response.isSuccessful) throw HttpException(response)
        studentDao.deleteStudentById(student.id)
        progressDao.deleteAttemptsOfStudent(student.fullName)
        "Estudiante eliminado."
    }

    // Ingreso: si el estudiante no está en Room, se busca en el microservicio
    suspend fun loginRemote(username: String, pin: String): StudentEntity? {
        return try {
            val entity = api.login(LoginRequest(username, pin)).toEntityOrNull()
            if (entity != null) {
                studentDao.insertStudent(entity)
            }
            entity
        } catch (e: HttpException) {
            null
        } catch (e: IOException) {
            null
        } catch (e: JsonParseException) {
            null
        }
    }

    // Envía un intento al microservicio (si falla, el intento igual quedó guardado en Room)
    suspend fun sendAttempt(attempt: StudentAttemptEntity): RemoteResult = remoteCall {
        val response = api.createAttempt(
            AttemptRequest(
                studentName = attempt.studentName,
                subjectId = attempt.subjectId,
                attemptNumber = attempt.attemptNumber,
                score = attempt.score,
                totalQuestions = attempt.totalQuestions,
                isCorrect = attempt.isCorrect,
                detailsSummary = attempt.detailsSummary
            )
        )
        if (!response.isSuccessful) throw HttpException(response)
        "Intento enviado al microservicio."
    }

    // Desbloquear una asignatura también borra los intentos en el microservicio
    suspend fun deleteAttempts(studentName: String, subjectId: String): RemoteResult = remoteCall {
        val response = api.deleteAttempts(studentName, subjectId)
        if (!response.isSuccessful) throw HttpException(response)
        "Intentos eliminados en el microservicio."
    }

    // Ejecuta la llamada y convierte cualquier falla de red o de servidor en un mensaje claro
    private suspend fun remoteCall(block: suspend () -> String): RemoteResult {
        return try {
            RemoteResult.Success(block())
        } catch (e: HttpException) {
            RemoteResult.Error(describeHttpError(e.code()))
        } catch (e: IOException) {
            RemoteResult.Error("No se pudo conectar con el microservicio de seguimiento. ¿Está encendido?")
        } catch (e: JsonParseException) {
            RemoteResult.Error("La respuesta del microservicio no tiene el formato esperado.")
        }
    }

    private fun describeHttpError(code: Int): String {
        return when (code) {
            400 -> "Datos inválidos: revisa el código y el PIN (4 números)."
            404 -> "No se encontró el estudiante en el microservicio."
            409 -> "Ya existe un estudiante con ese código."
            else -> "El microservicio respondió con error (código " + code + ")."
        }
    }
}
