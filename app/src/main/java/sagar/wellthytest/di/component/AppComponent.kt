package sagar.wellthytest.di.component

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import sagar.wellthytest.WellthyApp
import sagar.wellthytest.di.module.AppModule
import sagar.wellthytest.di.module.ViewModelModule
import javax.inject.Singleton

@Singleton
@Component(modules =
                [
                    ViewModelModule::class,
                    AppModule::class,
                    AndroidSupportInjectionModule::class
                ])

interface AppComponent : AndroidInjector<WellthyApp> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

}