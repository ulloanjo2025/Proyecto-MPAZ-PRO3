// Desarrollado por: Jonathan Ulloa
package cl.duoc.mpazest3.navigation

// Eventos de navegación desacoplados bajo arquitectura UDF
sealed class NavigationEvent {
    data class NavigateTo(val route: String) : NavigationEvent()
    object PopBackStack : NavigationEvent()
}