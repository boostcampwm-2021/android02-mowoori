package com.ariari.mowoori.ui.stamp

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.ariari.mowoori.R
import com.ariari.mowoori.base.BaseFragment
import com.ariari.mowoori.databinding.FragmentStampsBinding
import com.ariari.mowoori.ui.missions.entity.MissionInfo
import com.ariari.mowoori.ui.stamp.adapter.StampsAdapter
import com.ariari.mowoori.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StampsFragment : BaseFragment<FragmentStampsBinding>(R.layout.fragment_stamps) {

    private val safeArgs: StampsFragmentArgs by navArgs()
    private lateinit var adapter: StampsAdapter
    private val viewModel: StampsViewModel by viewModels()
    private lateinit var missionInfo: MissionInfo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        setMissionInfo()
        setMissionName()
        setAllEmptyStamps()
        setStampList()
        setStampListObserver()
        // TODO: 사용자 정보 가져와서 본인의 미션이면 "오늘도 완료!" 버튼 visible
        setAdapter()
        setSpanCount()
        setSpanCountObserver()
        setBackBtnObserver()
    }

    private fun setMissionInfo() {
        missionInfo = safeArgs.missionInfo
    }

    private fun setMissionName() {
        viewModel.setMissionName(missionInfo.missionName)
    }

    private fun setAllEmptyStamps() {
        viewModel.setAllEmptyStamps(missionInfo.totalStamp)
    }

    private fun setStampList() {
        viewModel.setStampList(missionInfo.stampList)
    }

    private fun setStampListObserver() {
        viewModel.stampList.observe(viewLifecycleOwner, { stampList ->
            adapter.submitList(stampList)
        })
    }

    private fun setAdapter() {
        adapter = StampsAdapter(object : StampsAdapter.OnItemClickListener {
            override fun itemClick(position: Int) {
                // TODO: 포지션이 스탬프 리스트 사이즈보다 같거나 크면 무시
                // TODO: 현재 선택된 스탬프 라이브데이터 변경 -> 옵저빙해서 네비게이션 이동
                println("Stamp - ${adapter.currentList[position].stampInfo}")
            }
        })
        binding.rvStamps.adapter = adapter
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

    private fun setBackBtnObserver() {
        viewModel.backBtnClick.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigateUp()
        })
    }
}
