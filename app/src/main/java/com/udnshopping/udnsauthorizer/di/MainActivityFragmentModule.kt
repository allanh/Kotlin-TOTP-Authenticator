package com.udnshopping.udnsauthorizer.di

import com.udnshopping.udnsauthorizer.view.*
import com.udnshopping.udnsauthorizer.view.config.ConfigFragment
import com.udnshopping.udnsauthorizer.view.pins.PinsFragment
import com.udnshopping.udnsauthorizer.view.sendcode.SendCodeFragment
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

    @ContributesAndroidInjector(modules = [SendCodeModule::class])
    abstract fun contributeSendCodeFragment(): SendCodeFragment
}