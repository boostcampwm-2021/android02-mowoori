package com.ariari.mowoori.ui.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.FragmentInviteCheckBinding
import dagger.hilt.android.AndroidEntryPoint
import com.ariari.mowoori.ui.group.entity.GroupMode

@AndroidEntryPoint
class InviteCheckFragment : Fragment() {
    private var _binding: FragmentInviteCheckBinding? = null
    private val binding get() = _binding ?: error(getString(R.string.binding_error))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentInviteCheckBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        setNavigateToInviteCode()
        setNavigateToGroupName()
    }

    private fun setNavigateToInviteCode() {
        binding.btnInviteCheckYes.setOnClickListener {
            it.findNavController()
                .navigate(InviteCheckFragmentDirections.actionInviteCheckFragmentToGroupNameFragment(
                    GroupMode.INVITE))
        }
    }

    private fun setNavigateToGroupName() {
        binding.btnInviteCheckNo.setOnClickListener {
            it.findNavController()
                .navigate(InviteCheckFragmentDirections.actionInviteCheckFragmentToGroupNameFragment(
                    GroupMode.NEW))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
