package com.udnshopping.udnsauthorizer.di

import com.udnshopping.udnsauthorizer.view.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class MainActivityFragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeConfigFragment(): ConfigFragment

    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment

    @ContributesAndroidInjector
    abstract fun contributePinsFragment(): PinsFragment

    @ContributesAndroidInjector
    abstract fun contributeSendCodeFragment(): SendCodeFragment

    @ContributesAndroidInjector
    abstract fun contributeScanFragment(): ScanFragment
}