package com.ariari.mowoori.ui.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.FragmentGroupNameBinding

class GroupNameFragment : Fragment() {

    private var _binding: FragmentGroupNameBinding? = null
    private val binding get() = _binding ?: error(getString(R.string.binding_error))
    private val viewModel: GroupViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setGroupName()
        setGroupNameValidation()
        setValidationObserver()
    }

    private fun setGroupName() {
        // TODO: 그룹 이름 생성 (형용사 + 명사)
    }

    private fun setGroupNameValidation() {
        binding.btnGroupNameComplete.setOnClickListener {
            viewModel.checkGroupNameValidation(binding.etGroupName.text.toString())
        }
    }

    private fun setValidationObserver() {
        viewModel.isValid.observe(viewLifecycleOwner, { isValid ->
            if (isValid) {
                viewModel.addNewGroup()
                this.findNavController().navigate(R.id.action_groupNameFragment_to_homeFragment)
            } else {
                // TODO: 그룹 이름이 유효하지 않다고 명시
            }
        })
    }
}
