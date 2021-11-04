package com.ariari.mowoori.ui.missions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.FragmentMissionsBinding
import com.ariari.mowoori.ui.missions.MissionsViewModel.Companion.NOT_DONE_TYPE
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.util.toastMessage
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MissionsFragment : Fragment() {
    private var _binding: FragmentMissionsBinding? = null
    private val binding get() = _binding ?: error(getString(R.string.binding_error))
    private val missionsViewModel by viewModels<MissionsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMissionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = missionsViewModel
        setPlusBtnClickObserve()
        setMissionsTypeObserve()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setPlusBtnClickObserve() {
        missionsViewModel.plusBtnClick.observe(viewLifecycleOwner, EventObserver {
            requireContext().toastMessage("미션 추가 버튼 클릭")
        })
    }

    private fun setMissionsTypeObserve() {
        missionsViewModel.missionsType.observe(viewLifecycleOwner, EventObserver { type ->
            binding.missionsMode = type
        })
    }
}
