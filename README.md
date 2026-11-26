# Grupo 7 - MPAZ PRO 3º: Apoyo didáctico y seguimiento del aprendizaje

## Integrantes
* **Jonathan Ulloa** (`ulloanjo2025`) — jo.ulloan@duocuc.cl
* **Camilo Alarcón** (`CamiloAlarcon23`) — ca.alarconv@duocuc.cl
* **Sebastián Maquera** — se.maquera@duocuc.cl

---

## Caso
* **Organización:** Escuela Escritora Marcela Paz (San Bernardo)
* **Asignatura:** DSY1105 - Desarrollo de Aplicaciones Móviles
* **Carrera:** Ingeniería en Informática - Duoc UC
* **Contexto:** Evaluación Final / Entrega Integradora

### Descripción del caso
Aplicación móvil nativa Android diseñada para la **Escuela Escritora Marcela Paz de San Bernardo**. La plataforma actúa como un apoyo didáctico interactivo para estudiantes de 3.º Básico en las asignaturas de Lenguaje y Comunicación, Matemática y Ciencias Naturales (máximo una unidad por asignatura), además de módulos de gestión para Docentes y Unidad Técnico-Pedagógica (UTP).

La solución digital reemplaza la gestión manual basada en guías impresas, permitiendo organizar contenidos por Objetivos de Aprendizaje (OA), ofrecer retroalimentación inmediata (respuestas correctas e incorrectas), brindar apoyos auditivos y visuales adaptados al ritmo de aprendizaje infantil (incluyendo recompensas motivacionales), y generar un informe pedagógico reservado para el docente sin emitir diagnósticos académicos automáticos. La aplicación funciona tanto en modo **offline** mediante persistencia local (Room) como conectada vía REST a microservicios propios y APIs externas.

---

## Estructura del proyecto
```text
app/src/main/java/cl/duoc/mpazest3/
├── data/
│   ├── local/
│   │   ├── dao/
│   │   │   ├── ContentDao.kt
│   │   │   ├── QuizDraftDao.kt
│   │   │   ├── ReportValidationDao.kt
│   │   │   ├── StudentDao.kt
│   │   │   └── StudentProgressDao.kt
│   │   ├── entity/
│   │   │   ├── LearningObjectiveEntity.kt
│   │   │   ├── QuestionEntity.kt
│   │   │   ├── QuizDraftEntity.kt
│   │   │   ├── ReportValidationEntity.kt
│   │   │   ├── StudentAttemptEntity.kt
│   │   │   └── StudentEntity.kt
│   │   └── AppDatabase.kt
│   ├── remote/
│   │   ├── ApiConfig.kt
│   │   ├── ContentApiService.kt
│   │   ├── PokeApiService.kt
│   │   ├── RetrofitInstance.kt
│   │   ├── StudentApiService.kt
│   │   └── WeatherApiService.kt
│   └── repository/
│       ├── ContentRepository.kt
│       ├── StudentRepository.kt
│       ├── TrackingRepository.kt
│       └── WeatherRepository.kt
├── model/
│   ├── AuthUiState.kt
│   ├── Role.kt
│   └── Subject.kt
├── navigation/
│   ├── AppNavigation.kt
│   ├── NavigationEvent.kt
│   └── Screen.kt
├── ui/
│   ├── components/
│   │   ├── AudioPlayerButton.kt
│   │   └── SubjectCard.kt
│   ├── screen/
│   │   ├── ActivityScreen.kt
│   │   ├── LoginScreen.kt
│   │   ├── PedagogicalReportScreen.kt
│   │   ├── SubjectSelectionScreen.kt
│   │   └── UtpScreen.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── util/
│   └── SoundManager.kt
├── viewmodel/
│   ├── AuthViewModel.kt
│   ├── ContentViewModel.kt
│   ├── ProgressViewModel.kt
│   ├── SubjectViewModel.kt
│   └── WeatherViewModel.kt
└── MainActivity.kt
```

## Material complementario
Documento oficial en Google Drive con la **Especificación de Requisitos de Software (ERS bajo norma IEEE 830)**, arquitectura técnica, conclusión y referencias de la asignatura DSY1105 (Duoc UC) y la guía *Android Kotlin Desde Cero*:  
https://docs.google.com/document/d/1uA38YsV7oN5Pky360Ku_H9u-j19vd7M9LDZfU69sz7w/edit?usp=drive_web
