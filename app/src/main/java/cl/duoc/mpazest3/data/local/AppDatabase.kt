// Desarrollado por: Camilo Alarcón (data/local)
package cl.duoc.mpazest3.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.mpazest3.data.local.dao.StudentDao
import cl.duoc.mpazest3.data.local.dao.StudentProgressDao
import cl.duoc.mpazest3.data.local.entity.StudentAttemptEntity
import cl.duoc.mpazest3.data.local.entity.StudentEntity

@Database(
    entities = [StudentAttemptEntity::class, StudentEntity::class],
    version = 4, // Incrementado a versión 4
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentProgressDao(): StudentProgressDao
    abstract fun studentDao(): StudentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mpaz_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}