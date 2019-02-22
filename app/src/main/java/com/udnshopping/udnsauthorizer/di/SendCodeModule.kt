package com.udnshopping.udnsauthorizer.di

import android.content.Context
import com.udnshopping.udnsauthorizer.repository.QRCodeRepository
import com.udnshopping.udnsauthorizer.view.sendcode.SendCodeViewModel
import dagger.Module
import dagger.Provides

@Module
class SendCodeModule {
    @Provides
    fun provideSendCodeViewModel(qrCodeRepository: QRCodeRepository): SendCodeViewModel {
        return SendCodeViewModel(qrCodeRepository)
    }

    @Provides
    fun provideQRCodeRepository(context: Context): QRCodeRepository {
        return QRCodeRepository(context)
    }
}