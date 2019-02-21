package com.udnshopping.udnsauthorizer.di

import com.udnshopping.udnsauthorizer.view.MainActivity
import com.udnshopping.udnsauthorizer.view.scan.ScanActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityModule {
    @ContributesAndroidInjector(modules = [MainActivityFragmentModule::class])
    internal abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [ScanActivityModule::class])
    internal abstract fun contributeScanActivity(): ScanActivity
}