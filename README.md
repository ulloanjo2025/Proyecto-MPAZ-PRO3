# Grupo 7 - MPAZ PRO 3º: Apoyo didáctico y seguimiento del aprendizaje

## Integrantes
* **Jonathan Ulloa** (`ulloanjo2025`) — jo.ulloan@duocuc.cl
* **Camilo Alarcón** (`CamiloAlarcon23`) — ca.alarconv@duocuc.cl
* **Sebastián Maquera** — se.maquera@duocuc.cl

## Caso
* **Organización:** Escuela Escritora Marcela Paz (San Bernardo)
* **Asignatura:** DSY1105 - Desarrollo de Aplicaciones Móviles
* **Carrera:** Ingeniería en Informática - Duoc UC
* **Fecha de Entrega:** 25-10-2026

## Descripción del caso
Aplicación móvil nativa Android diseñada para la Escuela Escritora Marcela Paz de San Bernardo. La plataforma actúa como un apoyo didáctico interactivo para estudiantes de 3.º Básico en las asignaturas de Lenguaje y Comunicación, Matemática y Ciencias Naturales (máximo una unidad por asignatura), además de módulos de gestión para Docentes y Unidad Técnico-Pedagógica (UTP).

La solución digital reemplaza la gestión manual basada en guías impresas, permitiendo organizar contenidos por Objetivos de Aprendizaje (OA), ofrecer retroalimentación inmediata (respuestas correctas e incorrectas), brindar apoyos auditivos y visuales adaptados al ritmo de aprendizaje infantil, y generar un informe pedagógico reservado para el docente sin emitir diagnósticos académicos automáticos. La aplicación funciona tanto en modo offline (con datos semilla y persistencia local) como sincronizada con microservicios Spring Boot y APIs externas.

## Estructura del proyecto
'''texto
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
'''
## Tecnologías
* **Lenguaje:** Kotlin
* **Interfaz de Usuario:** Android Jetpack Compose & Material Design 3
* **Arquitectura:** MVVM (Model-View-ViewModel) con `StateFlow`
* **Base de Datos Local:** Room Database (Almacenamiento offline de OA, preguntas, borradores y progreso)
* **Consumo REST e Imágenes:** Retrofit 2, Gson y Coil
* **Backend (Microservicios):** Spring Boot (`ms-contenido` puerto `8081` y `ms-seguimiento` puerto `8082`)
* **Audio Nativo:** MediaPlayer API / SoundPool para apoyos didácticos auditivos
* **Pruebas Unitarias:** JUnit, Kotest, MockK y Coroutines Test

## Endpoints consumidos
* **Microservicio de Contenido (`ms-contenido` — `http://<IP_LOCAL>:8081`):**
  * `GET /api/objetivos` (Sincronización de Objetivos de Aprendizaje)
  * `GET /api/preguntas` (Sincronización de preguntas por OA)
* **Microservicio de Seguimiento (`ms-seguimiento` — `http://<IP_LOCAL>:8082`):**
  * `GET / POST / PUT / DELETE /api/estudiantes` (Gestión CRUD de alumnos)
  * `POST /api/estudiantes/login` (Validación remota de acceso)
  * `POST / DELETE /api/intentos` (Registro y desbloqueo de evaluaciones)
* **APIs Externas:**
  * **Open-Meteo (`https://api.open-meteo.com/v1/forecast`):** Clima actual en San Bernardo.
  * **PokéAPI (`https://pokeapi.co/api/v2/pokemon/{id}`):** Recompensa visual al finalizar cuestionarios.

## Cómo ejecutar el proyecto
1. Levantar los microservicios Spring Boot (`ms-contenido` en `8081` y `ms-seguimiento` en `8082`) si se requiere conexión remota (opcional, soporta modo offline).
2. Abrir Android Studio (versión recomendada: Ladybug / Iguana o superior).
3. Seleccionar **Open** y buscar la carpeta raíz de este repositorio.
4. Esperar a que Gradle finalice la sincronización automática de dependencias (**Gradle Sync**).
5. Configurar la IPv4 local del servidor en `ApiConfig.kt` si se ejecuta en dispositivo físico por Wi-Fi.
6. Seleccionar un emulador Android (API 26 / Android 8.0 o superior) o conectar un dispositivo físico mediante Depuración USB.
7. Presionar el botón **Run 'app'** (`Shift + F10`) para compilar y ejecutar la aplicación.

## APK firmado y pruebas unitarias
* **Keystore (`.jks`):** `mpaz_release_key.jks` (ubicado en la raíz del repositorio).
* **APK Release Firmado:** `app/release/app-release.apk`.
* **Ejecución de pruebas:** `./gradlew testDebugUnitTest` (16 suites aprobadas, >90% de cobertura en `data`, `viewmodel` y `model`).

## Material complementario y gestión ágil
* **Tablero Trello:** [Enlace público de Trello]
* **Documento oficial en Google Drive:** [ERS bajo norma IEEE 830, arquitectura técnica, conclusión y referencias DSY1105](https://docs.google.com/document/d/1uA38YsV7oN5Pky360Ku_H9u-j19vd7M9LDZfU69sz7w/edit?usp=drive_web)
## Material complementario
Documento oficial en Google Drive con la **Especificación de Requisitos de Software (ERS bajo norma IEEE 830)**, arquitectura técnica, conclusión y referencias de la asignatura DSY1105 (Duoc UC) y la guía *Android Kotlin Desde Cero*:  
https://docs.google.com/document/d/1uA38YsV7oN5Pky360Ku_H9u-j19vd7M9LDZfU69sz7w/edit?usp=drive_web
