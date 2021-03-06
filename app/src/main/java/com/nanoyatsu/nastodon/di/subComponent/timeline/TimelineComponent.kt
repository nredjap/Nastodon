package com.nanoyatsu.nastodon.di.subComponent.timeline

import com.nanoyatsu.nastodon.data.repository.timeline.TimelineRepository
import com.nanoyatsu.nastodon.view.common.ViewModelFactory
import com.nanoyatsu.nastodon.view.timeline.TimelineViewModel
import dagger.BindsInstance
import dagger.Subcomponent


@Subcomponent(modules = [TimelineModule::class])
interface TimelineComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance kind: TimelineViewModel.Kind): TimelineComponent
    }

    fun viewModelFactory(): ViewModelFactory

    fun inject(repo: TimelineRepository)
}