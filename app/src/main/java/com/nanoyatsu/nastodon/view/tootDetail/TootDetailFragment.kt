package com.nanoyatsu.nastodon.view.tootDetail


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.data.domain.Attachment
import com.nanoyatsu.nastodon.databinding.FragmentTootDetailBinding
import kotlinx.android.synthetic.main.activity_nav_host.*
import javax.inject.Inject

class TootDetailFragment : Fragment() {
    lateinit var binding: FragmentTootDetailBinding
    @Inject
    lateinit var auth: AuthInfo
    @Inject
    lateinit var apiManager: MastodonApiManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity!!.application as NastodonApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTootDetailBinding.inflate(inflater, container, false)
            .also { initBinding(it) }
        return binding.root
    }

    private fun initBinding(binding: FragmentTootDetailBinding) {
        val args = TootDetailFragmentArgs.fromBundle(arguments!!)
        val factory = TootViewModelFactory(args.toot, auth, apiManager)

        val vm = ViewModelProvider(this, factory).get(TootViewModel::class.java)
        vm.avatarClickEvent.observe(viewLifecycleOwner, Observer { if (it) onAvatarClick(vm) })
        vm.replyEvent.observe(viewLifecycleOwner, Observer { if (it) onReplyClick(vm) })
        vm.reblogEvent.observe(viewLifecycleOwner, Observer { if (it) vm.doReblog() })
        vm.favouriteEvent.observe(viewLifecycleOwner, Observer { if (it) vm.doFav() })

        setupAttachments(requireActivity(), binding.attachments, args.toot.mediaAttachments)

        binding.vm = vm
        binding.lifecycleOwner = this
    }

    private fun setupAttachments(context: Context, view: RecyclerView, contents: List<Attachment>) {
        view.layoutManager = GridLayoutManager(context, 2)
        view.adapter = MediaAttachmentAdapter(contents.toTypedArray())
            .apply {
                publicListener = object : MediaAttachmentAdapter.ThumbnailClickListener {
                    override val onThumbnailClick = { onAttachmentClick(contents) }
                }
            }
    }

    private fun onAvatarClick(vm: TootViewModel) {
        val directions =
            TootDetailFragmentDirections.actionTootDetailFragmentToAccountDetailFragment(vm.toot.value!!.account)
        requireActivity().main_fragment_container.findNavController().navigate(directions)
        vm.onAvatarClickFinished()
    }

    private fun onReplyClick(vm: TootViewModel) {
        val directions =
            TootDetailFragmentDirections.actionTootDetailFragmentToTootEditFragment(vm.toot.value!!)
        requireActivity().main_fragment_container.findNavController().navigate(directions)
        vm.onReplyClickFinished()
    }

    private fun onAttachmentClick(contents: List<Attachment>) {
        val urls = contents.map { it.url }.toTypedArray()
        val directions =
            TootDetailFragmentDirections.actionTootDetailFragmentToImagePagerFragment(urls)
        requireActivity().main_fragment_container.findNavController().navigate(directions)
    }
}