package com.udnshopping.udnsauthorizer.di

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.NonNull
import com.udnshopping.udnsauthorizer.R
import com.udnshopping.udnsauthorizer.repository.SecretRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PersistenceModule {
    @Provides
    @Singleton
    fun provideSharePreferences(@NonNull context: Context): SharedPreferences {
        return context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun provideSecretRepository(context: Context, sharedPreferences: SharedPreferences): SecretRepository {
        return SecretRepository(context, sharedPreferences)
    }
}