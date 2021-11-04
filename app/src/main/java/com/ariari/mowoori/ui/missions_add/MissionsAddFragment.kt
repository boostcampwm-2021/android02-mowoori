package com.ariari.mowoori.ui.missions_add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.FragmentMissionsAddBinding


class MissionsAddFragment : Fragment() {
    private var _binding: FragmentMissionsAddBinding? = null
    private val binding get() = _binding ?: error(getString(R.string.binding_error))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_missions_add, container, false)
        return inflater.inflate(R.layout.fragment_missions_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
