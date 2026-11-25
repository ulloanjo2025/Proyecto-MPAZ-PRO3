// Desarrollado por: Jonathan Ulloa
package cl.duoc.mpazest3.navigation

sealed class Screen(val route: String) {
    object RoleSelection : Screen("role_selection")
    object HomeStudent : Screen("home_student")
    object HomeTeacher : Screen("home_teacher")
    object ActivityDetail : Screen("activity_detail/{subjectId}") {
        fun createRoute(subjectId: String) = "activity_detail/$subjectId"
    }
}