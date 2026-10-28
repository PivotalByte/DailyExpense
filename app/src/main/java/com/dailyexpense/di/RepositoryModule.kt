package com.dailyexpense.di

import com.dailyexpense.data.repository.AccountRepository
import com.dailyexpense.data.repository.CategoryRepository
import com.dailyexpense.data.repository.DataRepository
import com.dailyexpense.data.repository.RoomAccountRepository
import com.dailyexpense.data.repository.RoomCategoryRepository
import com.dailyexpense.data.repository.RoomDataRepository
import com.dailyexpense.data.repository.RoomTagRepository
import com.dailyexpense.data.repository.RoomTransactionRepository
import com.dailyexpense.data.repository.TagRepository
import com.dailyexpense.data.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAccountRepository(impl: RoomAccountRepository): AccountRepository

    @Binds
    abstract fun bindTransactionRepository(impl: RoomTransactionRepository): TransactionRepository

    @Binds
    abstract fun bindCategoryRepository(impl: RoomCategoryRepository): CategoryRepository

    @Binds
    abstract fun bindTagRepository(impl: RoomTagRepository): TagRepository

    @Binds
    abstract fun bindDataRepository(impl: RoomDataRepository): DataRepository
}
