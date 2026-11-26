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
Tecnologías
Lenguaje: Kotlin 2.x

Interfaz de Usuario: Android Jetpack Compose & Material Design 3

Arquitectura: MVVM (Model-View-ViewModel) con StateFlow y Corrutinas

Base de Datos Local: Room Database (Almacenamiento offline de OA, preguntas, borradores, intentos y validaciones)

Consumo de APIs REST: Retrofit 2 + Gson + Coil (carga asíncrona de sprites)

Backend Propio (Microservicios): Spring Boot (ms-contenido en puerto 8081 y ms-seguimiento en puerto 8082)

Audio Nativo: MediaPlayer API / SoundPool para apoyos didácticos auditivos

Testing y Calidad: JUnit 4/5, Kotest (FunSpec), MockK y kotlinx-coroutines-test

Endpoints Utilizados
1. Microservicios Propios (Spring Boot)
Configurados en cl.duoc.mpazest3.data.remote.ApiConfig:

ms-contenido (Puerto 8081):

GET /api/objetivos — Sincroniza los Objetivos de Aprendizaje (OA) disponibles.

GET /api/preguntas — Sincroniza el banco de preguntas asociadas a los OA.

ms-seguimiento (Puerto 8082):

GET /api/estudiantes — Obtiene la nómina de estudiantes registrados.

POST /api/estudiantes — Crea un nuevo estudiante en el sistema.

PUT /api/estudiantes/{id} — Actualiza nombre o PIN de un estudiante.

DELETE /api/estudiantes/{id} — Elimina un estudiante y su historial.

POST /api/estudiantes/login — Autenticación remota del estudiante.

POST /api/intentos — Registra el intento y desglose de respuestas tras finalizar un quiz.

DELETE /api/intentos/{studentName}/{subjectId} — Restablece intentos al desbloquear una asignatura.

2. APIs Externas Públicas
Open-Meteo Weather API (https://api.open-meteo.com/):

GET /v1/forecast — Consulta el clima en tiempo real de San Bernardo (-33.59, -70.70) para mostrar contexto ambiental en la vista del estudiante.

PokéAPI (https://pokeapi.co/):

GET /api/v2/pokemon/{id} — Obtiene un Pokémon aleatorio con su sprite oficial como recompensa motivacional al completar una actividad.

Cómo ejecutar el proyecto
Ejecución desde Android Studio
Iniciar los microservicios Spring Boot (ms-contenido en 8081 y ms-seguimiento en 8082) si se desea sincronización remota en tiempo real (la app también opera en modo offline con datos semilla).

Abrir Android Studio (versión recomendada: Ladybug / Iguana o superior).

Seleccionar Open y buscar la carpeta raíz de este repositorio.

Esperar a que Gradle finalice la sincronización automática de dependencias (Gradle Sync).

En ApiConfig.kt, verificar la IPv4 local del servidor si se ejecuta en un dispositivo físico conectado a la misma red Wi-Fi.

Seleccionar un emulador Android (API 26 / Android 8.0 o superior) o conectar un dispositivo físico mediante Depuración USB.

Presionar el botón Run 'app' (Shift + F10) para compilar y ejecutar la aplicación.

APK Firmado y Keystore
Keystore de firma: mpaz_release_key.jks (ubicado en la raíz del repositorio).

Ejecutable firmado: app/release/app-release.apk (generado bajo variante release con firmas V1 y V2).

Pruebas Unitarias y Cobertura
Para ejecutar la suite completa de pruebas unitarias:

Bash
./gradlew testDebugUnitTest
Resultado: 16 suites aprobadas sin fallos (BUILD SUCCESSFUL).

Cobertura de líneas alcanzada en lógica de negocio:

cl.duoc.mpazest3.data: 92%

cl.duoc.mpazest3.viewmodel: 92%

cl.duoc.mpazest3.model: 90%

## Material complementario
Documento oficial en Google Drive con la **Especificación de Requisitos de Software (ERS bajo norma IEEE 830)**, arquitectura técnica, conclusión y referencias de la asignatura DSY1105 (Duoc UC) y la guía *Android Kotlin Desde Cero*:  
https://docs.google.com/document/d/1uA38YsV7oN5Pky360Ku_H9u-j19vd7M9LDZfU69sz7w/edit?usp=drive_web
