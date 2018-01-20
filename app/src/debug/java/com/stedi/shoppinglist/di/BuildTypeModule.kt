package com.stedi.shoppinglist.di

import android.content.Context
import com.stedi.shoppinglist.Constants
import com.stedi.shoppinglist.model.repository.DatabaseShoppingRepository
import com.stedi.shoppinglist.model.repository.ShoppingRepository
import dagger.Module
import dagger.Provides

@Module
class BuildTypeModule {

    @Provides
    fun provideShoppingRepository(@AppContext context: Context): ShoppingRepository {
        return DatabaseShoppingRepository(context, Constants.DATABASE_NAME, Constants.DATABASE_VERSION)
    }
}