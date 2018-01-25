package com.stedi.shoppinglist.di

import android.content.Context
import com.stedi.shoppinglist.model.repository.DatabaseShoppingRepository
import com.stedi.shoppinglist.model.repository.ShoppingRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class BuildTypeModule {
    @Provides
    @Singleton
    fun provideShoppingRepository(@AppContext context: Context): ShoppingRepository {
        return DatabaseShoppingRepository(context, "shopping_database", 1)
    }
}