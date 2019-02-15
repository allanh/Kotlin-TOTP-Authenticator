package com.udnshopping.udnsauthorizer.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.udnshopping.udnsauthorizer.utility.AppViewModelFactory
import com.udnshopping.udnsauthorizer.viewmodel.MainActivityViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
internal abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    internal abstract fun bindMainActivityViewModels(mainActivityViewModel: MainActivityViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: AppViewModelFactory): ViewModelProvider.Factory
}