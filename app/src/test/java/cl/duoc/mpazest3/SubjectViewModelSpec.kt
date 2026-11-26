package cl.duoc.mpazest3

import cl.duoc.mpazest3.viewmodel.SubjectViewModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class SubjectViewModelSpec : FunSpec({

    test("carga las 3 asignaturas iniciales y permite seleccionar una") {
        val viewModel = SubjectViewModel()

        viewModel.subjects.value shouldHaveSize 3
        viewModel.selectedSubject.value.shouldBeNull()

        val math = viewModel.subjects.value.first()
        viewModel.selectSubject(math)

        viewModel.selectedSubject.value shouldBe math
    }
})