package cl.duoc.mpazest3

import cl.duoc.mpazest3.data.local.dao.StudentDao
import cl.duoc.mpazest3.data.local.dao.StudentProgressDao
import cl.duoc.mpazest3.data.local.entity.StudentAttemptEntity
import cl.duoc.mpazest3.data.local.entity.StudentEntity
import cl.duoc.mpazest3.data.remote.RemoteResult
import cl.duoc.mpazest3.data.remote.StudentApiService
import cl.duoc.mpazest3.data.remote.StudentDto
import cl.duoc.mpazest3.data.repository.TrackingRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

// Pruebas del repositorio que habla con el microservicio de seguimiento (CRUD de estudiantes).
// El microservicio y la base de datos son simulados con MockK: no necesitan internet ni emulador.
class TrackingRepositorySpec : FunSpec({

    val studentDao = mockk<StudentDao>(relaxed = true)
    val progressDao = mockk<StudentProgressDao>(relaxed = true)
    val api = mockk<StudentApiService>()

    fun repositorio() = TrackingRepository(studentDao, progressDao, api)

    fun alumno(id: Int = 3) = StudentEntity(id = id, username = "JP01", fullName = "Juan Pérez", pin = "1234")

    fun alumnoDto(id: Long = 3L, nombre: String = "Juan Pérez") = StudentDto(id, "JP01", nombre, "1234")

    fun errorHttp(codigo: Int) = HttpException(Response.error<Any>(codigo, "".toResponseBody()))

    beforeTest { clearAllMocks() }

    test("READ: sincronizar deja en Room los estudiantes del microservicio") {
        coEvery { api.getStudents() } returns listOf(alumnoDto(1L), alumnoDto(2L))

        val resultado = repositorio().syncStudents()

        resultado shouldBe RemoteResult.Success("Estudiantes sincronizados: 2")
        coVerify(exactly = 1) { studentDao.deleteAllStudents() }
        coVerify(exactly = 2) { studentDao.insertStudent(any()) }
    }

    test("CREATE: un estudiante nuevo se envía al microservicio y se guarda en Room con su id") {
        coEvery { api.createStudent(any()) } returns alumnoDto(5L)

        val resultado = repositorio().createStudent("JP01", "Juan Pérez", "1234")

        resultado.shouldBeInstanceOf<RemoteResult.Success>()
        coVerify(exactly = 1) { studentDao.insertStudent(match { it.id == 5 && it.username == "JP01" }) }
    }

    test("CREATE: un código repetido (409) muestra un mensaje claro y no guarda nada") {
        coEvery { api.createStudent(any()) } throws errorHttp(409)

        val resultado = repositorio().createStudent("JP01", "Juan Pérez", "1234")

        resultado.shouldBeInstanceOf<RemoteResult.Error>().message shouldContain "Ya existe"
        coVerify(exactly = 0) { studentDao.insertStudent(any()) }
    }

    test("sin conexión con el microservicio informa el problema") {
        coEvery { api.createStudent(any()) } throws IOException("sin red")

        val resultado = repositorio().createStudent("JP01", "Juan Pérez", "1234")

        resultado.shouldBeInstanceOf<RemoteResult.Error>().message shouldContain "No se pudo conectar"
    }

    test("UPDATE: al cambiar el nombre se actualiza Room y las evaluaciones siguen asociadas") {
        coEvery { api.updateStudent(3L, any()) } returns alumnoDto(3L, "Juan Pérez Soto")

        val resultado = repositorio().updateStudent(alumno(3), "Juan Pérez Soto", "1234")

        resultado shouldBe RemoteResult.Success("Estudiante actualizado.")
        coVerify(exactly = 1) { studentDao.insertStudent(match { it.fullName == "Juan Pérez Soto" }) }
        coVerify(exactly = 1) { progressDao.renameStudentInAttempts("Juan Pérez", "Juan Pérez Soto") }
    }

    test("UPDATE: si solo cambia el PIN no se renombran las evaluaciones") {
        coEvery { api.updateStudent(3L, any()) } returns alumnoDto(3L)

        repositorio().updateStudent(alumno(3), "Juan Pérez", "9999")

        coVerify(exactly = 0) { progressDao.renameStudentInAttempts(any(), any()) }
    }

    test("DELETE: se elimina del microservicio y de Room junto con sus evaluaciones") {
        coEvery { api.deleteStudent(3L) } returns Response.success(Unit)

        val resultado = repositorio().deleteStudent(alumno(3))

        resultado shouldBe RemoteResult.Success("Estudiante eliminado.")
        coVerify(exactly = 1) { studentDao.deleteStudentById(3) }
        coVerify(exactly = 1) { progressDao.deleteAttemptsOfStudent("Juan Pérez") }
    }

    test("DELETE: si el microservicio responde 404 no se borra nada en Room") {
        coEvery { api.deleteStudent(3L) } returns Response.error<Unit>(404, "".toResponseBody())

        val resultado = repositorio().deleteStudent(alumno(3))

        resultado.shouldBeInstanceOf<RemoteResult.Error>().message shouldContain "No se encontró"
        coVerify(exactly = 0) { studentDao.deleteStudentById(any()) }
    }

    test("un intento se envía al microservicio con sus datos") {
        coEvery { api.createAttempt(any()) } returns Response.success(Unit)
        val intento = StudentAttemptEntity(
            studentName = "Juan Pérez",
            subjectId = "Matemática",
            attemptNumber = 1,
            score = 5,
            totalQuestions = 6,
            isCorrect = true,
            detailsSummary = "• P1 [MA03 OA 08]: Correcta"
        )

        val resultado = repositorio().sendAttempt(intento)

        resultado.shouldBeInstanceOf<RemoteResult.Success>()
        coVerify(exactly = 1) {
            api.createAttempt(match { it.studentName == "Juan Pérez" && it.score == 5 && it.isCorrect })
        }
    }

    test("el ingreso remoto devuelve null si el código o el PIN son incorrectos (401)") {
        coEvery { api.login(any()) } throws errorHttp(401)

        val estudiante = repositorio().loginRemote("JP01", "0000")

        estudiante.shouldBeNull()
        coVerify(exactly = 0) { studentDao.insertStudent(any()) }
    }

    test("el ingreso remoto guarda en Room al estudiante encontrado") {
        coEvery { api.login(any()) } returns alumnoDto(7L)

        val estudiante = repositorio().loginRemote("JP01", "1234")

        estudiante.shouldNotBeNull()
        coVerify(exactly = 1) { studentDao.insertStudent(match { it.id == 7 }) }
    }
})
