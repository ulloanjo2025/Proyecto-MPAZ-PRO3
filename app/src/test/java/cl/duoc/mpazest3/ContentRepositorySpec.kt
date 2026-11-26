package cl.duoc.mpazest3

import cl.duoc.mpazest3.data.local.dao.ContentDao
import cl.duoc.mpazest3.data.local.entity.LearningObjectiveEntity
import cl.duoc.mpazest3.data.local.entity.QuestionEntity
import cl.duoc.mpazest3.data.remote.ContentApiService
import cl.duoc.mpazest3.data.remote.ContentResponseDto
import cl.duoc.mpazest3.data.remote.ObjectiveDto
import cl.duoc.mpazest3.data.remote.QuestionDto
import cl.duoc.mpazest3.data.remote.SyncResult
import cl.duoc.mpazest3.data.repository.ContentRepository
import cl.duoc.mpazest3.model.ContentStatus
import com.google.gson.JsonSyntaxException
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

// Pruebas del repositorio con Kotest + MockK: el servicio REST y la base de datos son simulados (mocks),
// así se prueba la lógica sin internet y sin emulador.
class ContentRepositorySpec : FunSpec({

    val dao = mockk<ContentDao>(relaxed = true)
    val api = mockk<ContentApiService>()

    fun repositorio(configurado: Boolean = true) =
        ContentRepository(dao, api, isApiConfigured = { configurado })

    fun objetivoDto(summary: String = "Resumen") =
        ObjectiveDto("MA03 OA 08", "1", "Unidad", "Tablas de multiplicar", summary, null, null, "PENDIENTE")

    fun preguntaDto() =
        QuestionDto(1, "MA03 OA 08", 1, "¿Cuánto es 5 × 3?", "8", "15", "12", 1, "Porque sí")

    fun respuesta(summary: String = "Resumen") =
        ContentResponseDto(1, listOf(objetivoDto(summary)), listOf(preguntaDto()))

    fun objetivoLocal(summary: String = "Resumen", status: String) =
        LearningObjectiveEntity("MA03 OA 08", "1", "Unidad", "Tablas de multiplicar", summary, null, null, status)

    fun preguntaLocal() =
        QuestionEntity(1, "MA03 OA 08", 1, "¿Cuánto es 5 × 3?", "8", "15", "12", 1, "Porque sí")

    beforeTest { clearAllMocks() }

    test("si el servicio no está configurado no se conecta") {
        val resultado = repositorio(configurado = false).syncFromRemote()

        resultado shouldBe SyncResult.NotConfigured
        coVerify(exactly = 0) { api.getContent() }
    }

    test("sin internet devuelve un mensaje claro y no toca la base de datos") {
        coEvery { api.getContent() } throws IOException("sin red")

        val resultado = repositorio().syncFromRemote()

        resultado.shouldBeInstanceOf<SyncResult.Error>().message shouldContain "Sin conexión"
        coVerify(exactly = 0) { dao.insertObjective(any()) }
    }

    test("un error HTTP informa el código") {
        val error = HttpException(Response.error<ContentResponseDto>(404, "".toResponseBody()))
        coEvery { api.getContent() } throws error

        val resultado = repositorio().syncFromRemote()

        resultado.shouldBeInstanceOf<SyncResult.Error>().message shouldContain "404"
    }

    test("un JSON mal formado devuelve un error y no guarda nada") {
        coEvery { api.getContent() } throws JsonSyntaxException("formato inválido")

        val resultado = repositorio().syncFromRemote()

        resultado.shouldBeInstanceOf<SyncResult.Error>().message shouldContain "formato"
        coVerify(exactly = 0) { dao.insertObjective(any()) }
    }

    test("un objetivo nuevo se guarda como pendiente junto con sus preguntas") {
        coEvery { api.getContent() } returns respuesta()
        coEvery { dao.getObjective("MA03 OA 08") } returns null
        coEvery { dao.getQuestionsOf("MA03 OA 08") } returns emptyList()

        val resultado = repositorio().syncFromRemote()

        resultado shouldBe SyncResult.Success(objectivesChecked = 1, objectivesUpdated = 1)
        coVerify(exactly = 1) {
            dao.insertObjective(match { it.code == "MA03 OA 08" && it.status == ContentStatus.PENDIENTE })
        }
        coVerify(exactly = 1) { dao.insertQuestion(any()) }
    }

    test("un objetivo sin cambios conserva la autorización de UTP y no escribe en la base") {
        coEvery { api.getContent() } returns respuesta()
        coEvery { dao.getObjective("MA03 OA 08") } returns objetivoLocal(status = ContentStatus.AUTORIZADO)
        coEvery { dao.getQuestionsOf("MA03 OA 08") } returns listOf(preguntaLocal())

        val resultado = repositorio().syncFromRemote()

        resultado shouldBe SyncResult.Success(objectivesChecked = 1, objectivesUpdated = 0)
        coVerify(exactly = 0) { dao.insertObjective(any()) }
        coVerify(exactly = 0) { dao.deleteQuestionsOf(any()) }
    }

    test("un objetivo con cambios vuelve a pendiente para que UTP lo revise otra vez") {
        coEvery { api.getContent() } returns respuesta(summary = "Resumen nuevo")
        coEvery { dao.getObjective("MA03 OA 08") } returns
            objetivoLocal(summary = "Resumen viejo", status = ContentStatus.AUTORIZADO)
        coEvery { dao.getQuestionsOf("MA03 OA 08") } returns listOf(preguntaLocal())

        val resultado = repositorio().syncFromRemote()

        resultado shouldBe SyncResult.Success(objectivesChecked = 1, objectivesUpdated = 1)
        coVerify(exactly = 1) {
            dao.insertObjective(match { it.summary == "Resumen nuevo" && it.status == ContentStatus.PENDIENTE })
        }
    }

    test("el contenido inicial se guarda solo si la base está vacía") {
        coEvery { dao.countObjectives() } returns 0

        runTest {
            repositorio().ensureSeeded()

            coVerify(exactly = 8) { dao.insertObjective(any()) }
            coVerify(exactly = 24) { dao.insertQuestion(any()) }
        }
    }

    test("si la base ya tiene contenido no se vuelve a cargar") {
        coEvery { dao.countObjectives() } returns 8

        repositorio().ensureSeeded()

        coVerify(exactly = 0) { dao.insertObjective(any()) }
        coVerify(exactly = 0) { dao.insertQuestion(any()) }
    }
})
