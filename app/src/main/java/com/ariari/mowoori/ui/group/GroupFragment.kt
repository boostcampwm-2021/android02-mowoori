package com.ariari.mowoori.ui.group

import android.animation.Animator
import android.animation.AnimatorInflater
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.FragmentGroupBinding
import com.ariari.mowoori.ui.group.entity.GroupMode
import com.ariari.mowoori.util.hideKeyBoard
import com.ariari.mowoori.util.isNetWorkAvailable
import com.ariari.mowoori.widget.NetworkDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupFragment : Fragment() {

    private var _binding: FragmentGroupBinding? = null
    private val binding get() = _binding ?: error(getString(R.string.binding_error))
    private val viewModel: GroupViewModel by viewModels()
    private val args: GroupFragmentArgs by navArgs()

    // 애니메이션 xml 파일 객체화
    private val objectAnimator: Animator by lazy {
        AnimatorInflater.loadAnimator(requireContext(), R.animator.animator_invalid_vibrate)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        setAnimationTarget()
        setValidationObserver()
        setAddGroupCompleteObserver()
        setOnCompleteClickListener()
        setGroupNameChangeListener()
        setNetworkDialogObserver()
        setRootClick()
        when (args.groupMode) {
            GroupMode.INVITE -> {
                setTitle(R.string.group_invite_title)
            }
            GroupMode.NEW -> {
                setTitle(R.string.group_name_title)
                viewModel.setGroupName()
            }
        }
    }

    private fun setAnimationTarget() {
        objectAnimator.setTarget(binding.etGroup)
    }

    private fun setTitle(resId: Int) {
        binding.tvGroupTitle.setText(resId)
    }

    private fun setValidationObserver() {
        viewModel.inValidMode.observe(viewLifecycleOwner, {
            binding.tvInvalid.isVisible = true
            binding.tvInvalid.text = it.message
            objectAnimator.start()
        })
    }

    private fun setAddGroupCompleteObserver() {
        viewModel.successAddGroup.observe(viewLifecycleOwner, {
            if (it) findNavController().navigate(R.id.action_groupNameFragment_to_homeFragment)
        })
    }

    private fun setGroupNameChangeListener() {
        binding.etGroup.doOnTextChanged { _, _, _, _ ->
            binding.tvInvalid.isInvisible = true
        }
    }

    private fun setOnCompleteClickListener() {
        binding.btnGroupComplete.setOnClickListener {
            joinOrAddGroup()
        }
    }

    private fun joinOrAddGroup() {
        if (requireContext().isNetWorkAvailable()) {
            when (args.groupMode) {
                GroupMode.INVITE -> viewModel.joinGroup()
                GroupMode.NEW -> viewModel.addNewGroup()
            }
        } else {
            showNetworkDialog()
        }
    }

    private fun setNetworkDialogObserver() {
        viewModel.isNetworkDialogShowed.observe(viewLifecycleOwner, {
            if (it) showNetworkDialog()
        })
    }

    private fun showNetworkDialog() {
        NetworkDialogFragment(object : NetworkDialogFragment.NetworkDialogListener {
            override fun onCancelClick(dialog: DialogFragment) {
                dialog.dismiss()
                findNavController().navigate(R.id.action_groupNameFragment_to_homeFragment)
            }

            override fun onRetryClick(dialog: DialogFragment) {
                dialog.dismiss()
                joinOrAddGroup()
                viewModel.resetNetworkDialog()
            }
        }).show(requireActivity().supportFragmentManager, "NetworkDialogFragment")
    }

    private fun setRootClick() {
        binding.constraintLayout.setOnClickListener {
            requireContext().hideKeyBoard(it)
            requireActivity().currentFocus?.clearFocus()
        }
    }
}
