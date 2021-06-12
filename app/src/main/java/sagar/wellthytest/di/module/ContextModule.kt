package sagar.wellthytest.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import sagar.wellthytest.di.interfaces.ApplicationContext
import javax.inject.Singleton

@Module
class ContextModule(private val context: Context) {

    @ApplicationContext
    @Provides
    @Singleton
    fun context(): Context {
        return context.applicationContext
    }
}