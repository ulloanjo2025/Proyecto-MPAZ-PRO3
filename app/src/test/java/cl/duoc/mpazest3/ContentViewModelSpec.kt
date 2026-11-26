package cl.duoc.mpazest3

import android.app.Application
import cl.duoc.mpazest3.data.local.AppDatabase
import cl.duoc.mpazest3.data.local.entity.LearningObjectiveEntity
import cl.duoc.mpazest3.data.local.entity.QuestionEntity
import cl.duoc.mpazest3.data.remote.ContentApiService
import cl.duoc.mpazest3.data.remote.RetrofitInstance
import cl.duoc.mpazest3.data.remote.SyncResult
import cl.duoc.mpazest3.data.repository.ContentRepository
import cl.duoc.mpazest3.viewmodel.ContentViewModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class ContentViewModelSpec : FunSpec({

    val mockApp = mockk<Application>(relaxed = true)

    beforeSpec {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        mockkObject(AppDatabase)
        mockkObject(RetrofitInstance)
        every { AppDatabase.getDatabase(any()) } returns mockk(relaxed = true)
        every { RetrofitInstance.api } returns mockk<ContentApiService>(relaxed = true)
        mockkConstructor(ContentRepository::class)
        every { anyConstructed<ContentRepository>().objectives } returns flowOf(emptyList())
    }

    afterSpec {
        Dispatchers.resetMain()
        unmockkAll()
    }

    test("loadSubjectContent carga OA autorizados y preguntas, y clearSubjectContent los limpia") {
        val oa = mockk<LearningObjectiveEntity>(relaxed = true)
        val q = mockk<QuestionEntity>(relaxed = true)

        coEvery { anyConstructed<ContentRepository>().getAuthorizedObjectives("math") } returns listOf(oa)
        coEvery { anyConstructed<ContentRepository>().getQuizQuestions("math") } returns listOf(q)

        val viewModel = ContentViewModel(mockApp)
        viewModel.loadSubjectContent("math")

        viewModel.subjectContent.value?.subjectId shouldBe "math"
        viewModel.subjectContent.value?.objectives?.size shouldBe 1
        viewModel.subjectContent.value?.questions?.size shouldBe 1
        viewModel.getAuthorizedObjectives("math").size shouldBe 1
        viewModel.getQuizQuestions("math").size shouldBe 1

        viewModel.clearSubjectContent()
        viewModel.subjectContent.value.shouldBeNull()
    }

    test("loadContent y syncContent actualizan syncMessage en Success, NotConfigured y Error") {
        coEvery { anyConstructed<ContentRepository>().ensureSeeded() } returns Unit

        // Caso 1: Success sin cambios
        coEvery { anyConstructed<ContentRepository>().syncFromRemote() } returns SyncResult.Success(
            objectivesChecked = 6,
            objectivesUpdated = 0
        )
        val viewModel = ContentViewModel(mockApp)
        viewModel.loadContent()
        viewModel.isSyncing.value shouldBe false
        viewModel.syncMessage.value shouldBe "Contenido al día (6 objetivos revisados)."

        // Caso 2: Success con objetivos actualizados
        coEvery { anyConstructed<ContentRepository>().syncFromRemote() } returns SyncResult.Success(
            objectivesChecked = 6,
            objectivesUpdated = 2
        )
        viewModel.syncContent()
        viewModel.syncMessage.value shouldBe "Contenido actualizado: 2 objetivo(s) con cambios quedaron pendientes de autorización de UTP."

        // Caso 3: NotConfigured
        coEvery { anyConstructed<ContentRepository>().syncFromRemote() } returns SyncResult.NotConfigured
        viewModel.syncContent()
        viewModel.syncMessage.value shouldBe "Falta configurar la dirección del servicio (ApiConfig.kt). Se usa el contenido guardado en el teléfono."

        // Caso 4: Error
        coEvery { anyConstructed<ContentRepository>().syncFromRemote() } returns SyncResult.Error("Sin conexión al servidor")
        viewModel.syncContent()
        viewModel.syncMessage.value shouldBe "Sin conexión al servidor"
    }

    test("setObjectiveStatus delega el cambio de estado UTP al repositorio") {
        coEvery { anyConstructed<ContentRepository>().setObjectiveStatus("MAT-01", "AUTORIZADO") } returns Unit

        val viewModel = ContentViewModel(mockApp)
        viewModel.setObjectiveStatus("MAT-01", "AUTORIZADO")

        coVerify { anyConstructed<ContentRepository>().setObjectiveStatus("MAT-01", "AUTORIZADO") }
    }
})