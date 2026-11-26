package cl.duoc.mpazest3

import android.app.Application
import cl.duoc.mpazest3.data.local.AppDatabase
import cl.duoc.mpazest3.data.local.dao.QuizDraftDao
import cl.duoc.mpazest3.data.local.dao.ReportValidationDao
import cl.duoc.mpazest3.data.local.dao.StudentDao
import cl.duoc.mpazest3.data.local.dao.StudentProgressDao
import cl.duoc.mpazest3.data.local.entity.StudentEntity
import cl.duoc.mpazest3.data.remote.RetrofitInstance
import cl.duoc.mpazest3.data.remote.StudentApiService
import cl.duoc.mpazest3.viewmodel.ProgressViewModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class ProgressViewModelSpec : FunSpec({

    val mockApp = mockk<Application>(relaxed = true)
    val mockDb = mockk<AppDatabase>(relaxed = true)
    val mockProgressDao = mockk<StudentProgressDao>(relaxed = true)
    val mockStudentDao = mockk<StudentDao>(relaxed = true)
    val mockDraftDao = mockk<QuizDraftDao>(relaxed = true)
    val mockValidationDao = mockk<ReportValidationDao>(relaxed = true)
    val mockStudentApi = mockk<StudentApiService>(relaxed = true)

    beforeSpec {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        mockkObject(AppDatabase)
        mockkObject(RetrofitInstance)
        every { AppDatabase.getDatabase(any()) } returns mockDb
        every { mockDb.studentProgressDao() } returns mockProgressDao
        every { mockDb.studentDao() } returns mockStudentDao
        every { mockDb.quizDraftDao() } returns mockDraftDao
        every { mockDb.reportValidationDao() } returns mockValidationDao
        every { RetrofitInstance.studentApi } returns mockStudentApi

        every { mockProgressDao.getAllAttempts() } returns flowOf(emptyList())
        every { mockStudentDao.getAllStudents() } returns flowOf(emptyList())
        every { mockValidationDao.observeAll() } returns flowOf(emptyList())
        every { mockDraftDao.observeAll() } returns flowOf(emptyList())
    }

    afterSpec {
        Dispatchers.resetMain()
        unmockkObject(AppDatabase)
        unmockkObject(RetrofitInstance)
    }

    test("saveQuizProgress, validateReport y revokeReportValidation persisten en Room") {
        val viewModel = ProgressViewModel(mockApp)

        viewModel.saveQuizProgress("Ana", "Matemática", 1, 1, "detalle")
        viewModel.validateReport("Ana", "Prof. Carlos", 2)
        viewModel.revokeReportValidation("Ana")

        coVerify { mockDraftDao.upsertDraft(any()) }
        coVerify { mockValidationDao.upsertValidation(any()) }
        coVerify { mockValidationDao.deleteValidation("ana") }
    }

    test("saveAttempt guarda intento cuando hay menos de 2 previos y bloquea al llegar a 2") {
        val viewModel = ProgressViewModel(mockApp)
        var successCalled = false
        var limitCalled = false

        coEvery { mockProgressDao.getAttemptCountForSubject("Ana", "Matemática") } returns 0
        viewModel.saveAttempt(
            "Ana", "Matemática", true, 3, 3, "ok",
            onLimitReached = { limitCalled = true },
            onSuccess = { successCalled = true }
        )
        successCalled shouldBe true
        limitCalled shouldBe false

        coEvery { mockProgressDao.getAttemptCountForSubject("Ana", "Matemática") } returns 2
        viewModel.saveAttempt(
            "Ana", "Matemática", true, 3, 3, "ok",
            onLimitReached = { limitCalled = true },
            onSuccess = { successCalled = true }
        )
        limitCalled shouldBe true
    }

    test("unlockSubject limpia intentos y borrador de la asignatura") {
        val viewModel = ProgressViewModel(mockApp)
        viewModel.unlockSubject("Ana", "Matemática")

        coVerify { mockProgressDao.unlockSubjectForStudent("Ana", "Matemática") }
        coVerify { mockDraftDao.deleteDraft("ana", "Matemática") }
    }

    test("registerStudent y updateStudent validan campos vacíos o PIN distinto a 4 dígitos") {
        val viewModel = ProgressViewModel(mockApp)
        var regOk = true
        var updOk = true

        viewModel.registerStudent("", "JP01", "12") { ok, _ -> regOk = ok }
        regOk shouldBe false

        val student = StudentEntity(1, "JP01", "Juan Pérez", "1234")
        viewModel.updateStudent(student, "", "123") { ok, _ -> updOk = ok }
        updOk shouldBe false
    }

    test("validateStudent encuentra al estudiante registrado en Room") {
        val student = StudentEntity(1, "JP01", "Juan Pérez", "1234")
        coEvery { mockStudentDao.loginStudent("JP01", "1234") } returns student

        val viewModel = ProgressViewModel(mockApp)
        val result = viewModel.validateStudent("jp01", "1234")

        result?.fullName shouldBe "Juan Pérez"
    }
})