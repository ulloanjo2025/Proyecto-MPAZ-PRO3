package cl.duoc.mpazest3.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.duoc.mpazest3.data.local.entity.ReportValidationEntity
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmSuppressWildcards
// Desarrollado por: Jonathan Ulloa
@Dao
interface ReportValidationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    suspend fun upsertValidation(validation: ReportValidationEntity): Long

    @Query("SELECT * FROM report_validations")
    fun observeAll(): Flow<List<ReportValidationEntity>>

    @Query("DELETE FROM report_validations WHERE studentName = :studentName")
    @JvmSuppressWildcards
    suspend fun deleteValidation(studentName: String): Int
}
