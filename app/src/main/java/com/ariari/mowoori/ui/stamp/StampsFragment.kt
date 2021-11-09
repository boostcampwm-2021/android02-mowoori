package com.ariari.mowoori.ui.stamp

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.ariari.mowoori.R
import com.ariari.mowoori.base.BaseFragment
import com.ariari.mowoori.databinding.FragmentStampsBinding
import timber.log.Timber

class StampsFragment : BaseFragment<FragmentStampsBinding>(R.layout.fragment_stamps) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val safeArgs: StampsFragmentArgs by navArgs()
        val missionInfo = safeArgs.missionInfo
        Timber.d(missionInfo.toString())
    }
}
