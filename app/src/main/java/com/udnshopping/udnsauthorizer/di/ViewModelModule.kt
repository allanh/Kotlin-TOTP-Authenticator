package com.udnshopping.udnsauthorizer.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.udnshopping.udnsauthorizer.utility.AppViewModelFactory
import com.udnshopping.udnsauthorizer.viewmodel.MainActivityViewModel
import com.udnshopping.udnsauthorizer.viewmodel.PinsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
internal abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    internal abstract fun bindMainActivityViewModel(mainActivityViewModel: MainActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PinsViewModel::class)
    internal abstract fun bindPinsViewModel(pinsViewModel: PinsViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: AppViewModelFactory): ViewModelProvider.Factory
}