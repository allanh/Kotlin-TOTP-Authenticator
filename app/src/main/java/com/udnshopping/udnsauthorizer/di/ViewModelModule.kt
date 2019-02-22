package com.udnshopping.udnsauthorizer.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.udnshopping.udnsauthorizer.utility.AppViewModelFactory
import com.udnshopping.udnsauthorizer.view.MainActivityViewModel
import com.udnshopping.udnsauthorizer.view.pins.PinsViewModel
import com.udnshopping.udnsauthorizer.view.scan.ScanViewModel
import com.udnshopping.udnsauthorizer.view.sendcode.SendCodeViewModel
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
    @IntoMap
    @ViewModelKey(SendCodeViewModel::class)
    internal abstract fun bindSendCodeViewModel(sendCodeViewModel: SendCodeViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: AppViewModelFactory): ViewModelProvider.Factory
}