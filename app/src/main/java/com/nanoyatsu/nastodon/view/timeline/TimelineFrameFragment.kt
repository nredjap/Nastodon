package com.nanoyatsu.nastodon.view.timeline


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.components.ZoomOutPageTransformer
import com.nanoyatsu.nastodon.databinding.FragmentTimelineFrameBinding

/**
 * TimelineFragmentを覆うViewPagerを持つFragment
 */
class TimelineFrameFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTimelineFrameBinding.inflate(inflater, container, false)
            .also { initBinding(it) }
        return binding.root
    }

    private fun initBinding(binding: FragmentTimelineFrameBinding) {
        // 描画設定
        // ViewPager, ViewPagerTab
        binding.pager.adapter = TimelinePagerAdapter(requireActivity())
        binding.pager.setPageTransformer(ZoomOutPageTransformer())
        TabLayoutMediator(binding.pagerTab, binding.pager) { tab, pos ->
            tab.text = TimelineViewModel.Kind.values()[pos].name
        }.attach()

        // ViewModel設定
        val appComponent = (requireActivity().application as NastodonApplication).appComponent
        val vm = appComponent.viewModelFactory().create(TimelineFrameViewModel::class.java)
        vm.tootEvent.observe(viewLifecycleOwner, Observer { if (it) transTootEdit(vm) })

        binding.vm = vm
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private inner class TimelinePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = TimelineViewModel.Kind.values().size

        override fun createFragment(position: Int): Fragment {
            val kind = TimelineViewModel.Kind.values()[position]
            return TimelineFragment.newInstance(kind)
        }
    }

    private fun transTootEdit(vm: TimelineFrameViewModel) {
        findNavController().navigate(TimelineFrameFragmentDirections.actionTimelineFrameFragmentToTootEditFragment())
        vm.onTootClickFinished()
    }
}
