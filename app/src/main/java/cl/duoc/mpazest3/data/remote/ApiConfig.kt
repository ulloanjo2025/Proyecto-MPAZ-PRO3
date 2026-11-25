package cl.duoc.mpazest3.data.remote
// Desarrollado por: Jonathan Ulloa
// Configuración del servicio REST de prueba.
// El archivo servicio/contenido.json se publica en un repositorio PÚBLICO de GitHub
// y se usa su dirección "raw". Es solo contenido propio del grupo (sin datos personales).
object ApiConfig {

    // Debe terminar con "/". Cambia TU_USUARIO por tu usuario de GitHub.
    const val BASE_URL = "https://raw.githubusercontent.com/ulloanjo2025/mpaz-contenido/refs/heads/main/"

    // Mientras siga diciendo TU_USUARIO la app no intenta conectarse y usa el contenido guardado
    val isConfigured: Boolean
        get() = !BASE_URL.contains("TU_USUARIO")
}
