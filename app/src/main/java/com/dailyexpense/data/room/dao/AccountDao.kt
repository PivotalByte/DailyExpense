package com.dailyexpense.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dailyexpense.data.room.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(accountEntity: AccountEntity)

    @Delete
    suspend fun deleteAccount(accountEntity: AccountEntity)

    @Query("SELECT SUM(balance) FROM DE_account")
    fun getTotalBalance(): Flow<Double>

}