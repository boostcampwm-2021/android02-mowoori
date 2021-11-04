package com.ariari.mowoori.ui.missions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.FragmentMissionsBinding
import com.ariari.mowoori.ui.missions.adapter.MissionsAdapter
import com.ariari.mowoori.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MissionsFragment : Fragment() {
    private var _binding: FragmentMissionsBinding? = null
    private val binding get() = _binding ?: error(getString(R.string.binding_error))
    private val missionsViewModel by viewModels<MissionsViewModel>()
    private val missionsAdapter = MissionsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_missions, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = missionsViewModel
        setMissionsRvAdapter()
        setPlusBtnClickObserve()
        setMissionsTypeObserve()
        setMissionsListObserve()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setMissionsRvAdapter() {
        binding.rvMissions.adapter = missionsAdapter
    }

    private fun setPlusBtnClickObserve() {
        missionsViewModel.plusBtnClick.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigate(R.id.action_missionsFragment_to_missionsAddFragment)
        })
    }

    private fun setMissionsTypeObserve() {
        missionsViewModel.missionsType.observe(viewLifecycleOwner, EventObserver { type ->
            binding.missionsMode = type
            missionsViewModel.setMissionsList()
        })
    }

    private fun setMissionsListObserve() {
        missionsViewModel.missionsList.observe(viewLifecycleOwner) { missionsList ->
            missionsAdapter.submitList(missionsList)
        }
    }
}
