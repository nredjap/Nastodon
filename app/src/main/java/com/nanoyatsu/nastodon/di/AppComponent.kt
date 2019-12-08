package com.nanoyatsu.nastodon.di

import android.content.Context
import com.nanoyatsu.nastodon.view.splash.SplashFragment
import com.nanoyatsu.nastodon.view.tootDetail.TootDetailFragment
import com.nanoyatsu.nastodon.view.tootEdit.TootEditFragment
import dagger.BindsInstance
import dagger.Component

@Component(modules = [AccountModule::class])
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(fragment: SplashFragment)
    fun inject(fragment: TootEditFragment)
    fun inject(fragment: TootDetailFragment)
}