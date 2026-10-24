# Grupo 7 - MPAZ PRO 3.º: Apoyo didáctico y seguimiento del aprendizaje

## Integrantes
- Camilo Alarcón (CamiloAlarcon23) (ca.alarconv@duocuc.cl)
- Sebastián Maquera (se.maquera@duocuc.cl)
- Jonathan Ulloa (ulloanjo2025) (jo.ulloan@duocuc.cl)

## Caso
**Organización:** Escuela Escritora Marcela Paz (San Bernardo)  
**Asignatura:** DSY1105 - Desarrollo de Aplicaciones Móviles  
**Carrera:** Ingeniería en Informática - Duoc UC  
**Fecha de Entrega:** 25-10-2026  

## Descripción del caso
Aplicación móvil nativa Android diseñada para la Escuela Escritora Marcela Paz de San Bernardo. La plataforma actúa como un apoyo didáctico interactivo para estudiantes de 3.º Básico en las asignaturas de Lenguaje y Comunicación, Matemática, Ciencias Naturales e Historia, Geografía y Ciencias Sociales (máximo una unidad por asignatura). 

La solución digital reemplaza la gestión manual basada en guías impresas, permitiendo organizar contenidos por Objetivos de Aprendizaje (OA), ofrecer retroalimentación inmediata (respuestas correctas e incorrectas), brindar apoyos auditivos y visuales adaptados al ritmo de aprendizaje infantil, y generar un informe pedagógico reservado para el docente sin emitir diagnósticos académicos automáticos. La aplicación funciona de manera offline con datos y usuarios ficticios almacenados localmente.

## Estructura del proyecto

app/src/main/java/cl/duoc/mpazest3/
├── data/
│   ├── local/
│   │   ├── dao/
│   │   │   ├── StudentDao.kt
│   │   │   └── StudentProgressDao.kt
│   │   ├── entity/
│   │   │   ├── StudentEntity.kt
│   │   │   └── StudentAttemptEntity.kt
│   │   └── AppDatabase.kt
│   └── repository/
│       └── StudentRepository.kt
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
│   │   └── SubjectSelectionScreen.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── util/
│   └── SoundManager.kt
├── viewmodel/
│   ├── AuthViewModel.kt
│   ├── ProgressViewModel.kt
│   └── SubjectViewModel.kt
└── MainActivity.kt

## Tecnologías
- **Lenguaje:** Kotlin
- **Interfaz de Usuario:** Android Jetpack Compose & Material Design 3
- **Arquitectura:** MVVM (Model-View-ViewModel) con StateFlow
- **Base de Datos Local:** Room Database (Almacenamiento offline de respuestas y progreso)
- **Audio Nativo:** MediaPlayer API / SoundPool para apoyos didácticos auditivos

## Cómo ejecutar el proyecto
1. Abrir **Android Studio** (versión recomendada: Ladybug / Iguana o superior).
2. Seleccionar **Open** y buscar la carpeta raíz de este repositorio.
3. Esperar a que **Gradle** finalice la sincronización automática de dependencias (`Gradle Sync`).
4. Seleccionar un emulador Android (API 26 / Android 8.0 o superior) o conectar un dispositivo físico mediante Depuración USB.
5. Presionar el botón **Run 'app'** (`Shift + F10`) para compilar y ejecutar la aplicación.

## Material complementario
Documento oficial en Google Drive con la **Especificación de Requisitos de Software (ERS bajo norma IEEE 830)**, arquitectura técnica, conclusión y referencias de la asignatura DSY1105 (Duoc UC) y la guía *Android Kotlin Desde Cero*:  
https://docs.google.com/document/d/1uA38YsV7oN5Pky360Ku_H9u-j19vd7M9LDZfU69sz7w/edit?usp=drive_web
