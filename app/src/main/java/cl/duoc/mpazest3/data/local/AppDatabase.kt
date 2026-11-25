// Desarrollado por: Camilo Alarcón (data/local)
package cl.duoc.mpazest3.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cl.duoc.mpazest3.data.local.dao.ContentDao
import cl.duoc.mpazest3.data.local.dao.QuizDraftDao
import cl.duoc.mpazest3.data.local.dao.ReportValidationDao
import cl.duoc.mpazest3.data.local.dao.StudentDao
import cl.duoc.mpazest3.data.local.dao.StudentProgressDao
import cl.duoc.mpazest3.data.local.entity.LearningObjectiveEntity
import cl.duoc.mpazest3.data.local.entity.QuestionEntity
import cl.duoc.mpazest3.data.local.entity.QuizDraftEntity
import cl.duoc.mpazest3.data.local.entity.ReportValidationEntity
import cl.duoc.mpazest3.data.local.entity.StudentAttemptEntity
import cl.duoc.mpazest3.data.local.entity.StudentEntity

@Database(
    entities = [
        StudentAttemptEntity::class,
        StudentEntity::class,
        LearningObjectiveEntity::class,
        QuestionEntity::class,
        QuizDraftEntity::class,
        ReportValidationEntity::class
    ],
    version = 7, // Versión 7: se agrega la tabla report_validations (validación del informe)
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentProgressDao(): StudentProgressDao
    abstract fun studentDao(): StudentDao
    abstract fun contentDao(): ContentDao
    abstract fun quizDraftDao(): QuizDraftDao
    abstract fun reportValidationDao(): ReportValidationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migración 5 a 6: solo agrega la tabla nueva y NO borra los datos que ya existen
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `quiz_drafts` (" +
                        "`studentName` TEXT NOT NULL, " +
                        "`subjectId` TEXT NOT NULL, " +
                        "`questionIndex` INTEGER NOT NULL, " +
                        "`correctAnswers` INTEGER NOT NULL, " +
                        "`detailsLog` TEXT NOT NULL, " +
                        "PRIMARY KEY(`studentName`, `subjectId`))"
                )
            }
        }

        // Migración 6 a 7: solo agrega la tabla nueva y NO borra los datos que ya existen
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `report_validations` (" +
                        "`studentName` TEXT NOT NULL, " +
                        "`validatedBy` TEXT NOT NULL, " +
                        "`validatedAt` INTEGER NOT NULL, " +
                        "`attemptsAtValidation` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`studentName`))"
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mpaz_database"
                )
                    .addMigrations(MIGRATION_5_6, MIGRATION_6_7)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
