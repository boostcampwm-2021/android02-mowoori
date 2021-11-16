package com.ariari.mowoori.ui.stamp

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.ariari.mowoori.R
import com.ariari.mowoori.base.BaseFragment
import com.ariari.mowoori.databinding.FragmentStampsBinding
import com.ariari.mowoori.ui.missions.entity.Mission
import com.ariari.mowoori.ui.stamp.adapter.StampsAdapter
import com.ariari.mowoori.ui.stamp.entity.DetailInfo
import com.ariari.mowoori.ui.stamp.entity.DetailMode
import com.ariari.mowoori.ui.stamp.entity.StampInfo
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.widget.ProgressDialogManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StampsFragment : BaseFragment<FragmentStampsBinding>(R.layout.fragment_stamps) {

    private val safeArgs: StampsFragmentArgs by navArgs()
    private lateinit var adapter: StampsAdapter
    private val viewModel: StampsViewModel by viewModels()
    private lateinit var mission: Mission
    private lateinit var userName: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        setStartEnterTransition()
        setMissionInfo()
        setUserName()
        setMissionName()
        setStampList()
        setAdapter()
        setSpanCount()
        setCompleteBtnVisible()
        setCompleteClick()
        setObserver()
    }

    private fun setStartEnterTransition() {
        // 리사이클러 뷰가 측정이 완료될 때까지 트랜지션 지연
        postponeEnterTransition()
        binding.rvStamps.viewTreeObserver.addOnPreDrawListener {
            startPostponedEnterTransition()
            true
        }
    }

    private fun setMissionInfo() {
        mission = safeArgs.mission
    }

    private fun setUserName() {
        userName = safeArgs.userName
    }

    private fun setMissionName() {
        viewModel.setMissionName(mission.missionInfo.missionName)
    }

    private fun setStampList() {
        viewModel.setLoadingEvent(true)
        viewModel.setStampList(mission.missionInfo.stampList)
    }

    private fun setAdapter() {
        adapter = StampsAdapter(object : StampsAdapter.OnItemClickListener {
            override fun itemClick(position: Int, imageView: ImageView) {
                val stampInfo = adapter.currentList[position].stampInfo
                val extras = FragmentNavigatorExtras(
                    imageView to stampInfo.pictureUrl
                )
                this@StampsFragment.findNavController()
                    .navigate(
                        StampsFragmentDirections.actionStampsFragmentToStampDetailFragment(
                            DetailInfo(
                                userName,
                                mission.missionId,
                                mission.missionInfo.missionName,
                                DetailMode.INQUIRY,
                                stampInfo
                            )
                        ), extras
                    )
//                viewModel.setSelectedStampInfo(position, missionInfo.curStamp)
            }
        })
        binding.rvStamps.adapter = adapter
    }

    private fun setCompleteBtnVisible() {
        viewModel.setIsMyMission(mission.missionInfo.userId)
    }

    private fun setCompleteClick() {
        binding.btnStampsComplete.setOnClickListener {
            it.findNavController()
                .navigate(
                    StampsFragmentDirections.actionStampsFragmentToStampDetailFragment(
                        DetailInfo(
                            userName,
                            mission.missionId,
                            mission.missionInfo.missionName,
                            DetailMode.CERTIFY,
                            StampInfo()
                        )
                    )
                )
        }
    }

    private fun setObserver() {
        setLoadingObserver()
        setBackBtnObserver()
        setSpanCountObserver()
        setStampListObserver()
        setSelectedStampInfoObserver()
    }

    private fun setLoadingObserver() {
        viewModel.loadingEvent.observe(viewLifecycleOwner, EventObserver {
            if (it) ProgressDialogManager.instance.show(requireContext())
            else ProgressDialogManager.instance.clear()
        })
    }

    private fun setBackBtnObserver() {
        viewModel.backBtnClick.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigateUp()
        })
    }

    private fun setSpanCountObserver() {
        viewModel.spanCount.observe(viewLifecycleOwner, EventObserver { spanCount ->
            val gridLayoutManager = GridLayoutManager(requireContext(), spanCount)
            binding.rvStamps.layoutManager = gridLayoutManager
        })
    }

    private fun setSpanCount() {
        // 뷰 사이즈 측정 시점을 관찰하는 옵저버
        binding.rvStamps.viewTreeObserver.addOnGlobalLayoutListener(object :
            OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // 뷰의 측정이 자주 일어날 경우 중첩된 리스너 등록을 방지하기 위해 콜백 함수가 호출되면 해당 리스너를 제거한다.
                binding.rvStamps.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val resources = requireActivity().resources
                val recyclerViewWidth = binding.rvStamps.width
                val itemWidth =
                    resources.getDimension(R.dimen.stamp_width) + resources.getDimension(R.dimen.stamp_padding)
                viewModel.setSpanCount(recyclerViewWidth / itemWidth)
            }
        })
    }

    private fun setStampListObserver() {
        viewModel.stampList.observe(viewLifecycleOwner, { stampList ->
            adapter.submitList(stampList)
            viewModel.fillEmptyStamps(mission.missionInfo.totalStamp - stampList.size)
            viewModel.setLoadingEvent(false)
        })
    }

    private fun setSelectedStampInfoObserver() {
        viewModel.selectedStampInfo.observe(viewLifecycleOwner, EventObserver { stampInfo ->
            this.findNavController()
                .navigate(
                    StampsFragmentDirections.actionStampsFragmentToStampDetailFragment(
                        DetailInfo(
                            userName,
                            mission.missionId,
                            mission.missionInfo.missionName,
                            DetailMode.INQUIRY,
                            stampInfo
                        )
                    )
                )
        })
    }
}
