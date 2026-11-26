package cl.duoc.mpazest3.data.remote

// Direcciones de los servicios que consume la app.
// En el emulador de Android Studio, 10.0.2.2 significa "el computador donde corre el emulador".
// Si usas un teléfono real, cambia 10.0.2.2 por la IP de tu computador (ej: 192.168.1.50).
object ApiConfig {

    // Microservicio propio de contenido (Spring Boot, puerto 8081)
    //const val BASE_URL = "http://10.0.2.2:8081/"
    const val BASE_URL = "http://192.168.1.13:8081/"


    // Microservicio propio de seguimiento: estudiantes e intentos (Spring Boot, puerto 8082)
    //const val TRACKING_BASE_URL = "http://10.0.2.2:8082/"
    const val TRACKING_BASE_URL = "http://192.168.1.13:8082/"


    // API externa pública (clima), no necesita clave
    const val WEATHER_BASE_URL = "https://api.open-meteo.com/"

    // Ubicación de la escuela (San Bernardo) para consultar el clima
    const val SCHOOL_CITY = "San Bernardo"
    const val SCHOOL_LATITUDE = -33.59
    const val SCHOOL_LONGITUDE = -70.70

    // Los microservicios son propios: siempre están "configurados"
    val isConfigured: Boolean
        get() = true
}
//direccion de mi router 192.168.1.3