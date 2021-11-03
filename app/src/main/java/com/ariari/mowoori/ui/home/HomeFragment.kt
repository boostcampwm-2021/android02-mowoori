package com.ariari.mowoori.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.FragmentHomeBinding
import com.ariari.mowoori.ui.home.adapter.DrawerAdapter
import com.ariari.mowoori.ui.home.adapter.DrawerAdapterDecoration

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding ?: error(getString(R.string.binding_error))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        setDrawerOpenListener()
        setDrawerAdapter()
        setRecyclerViewDecoration()
    }

    private fun setDrawerOpenListener() {
        binding.tbHome.setNavigationOnClickListener {
            binding.drawerHome.open()
        }
    }

    private fun setDrawerAdapter() {
        val adapter: DrawerAdapter by lazy {
            DrawerAdapter(object : DrawerAdapter.OnItemClickListener {
                override fun itemClick(position: Int) {
                    // TODO: 그룹 이동
                    // TODO: 그룹 아이템 배경 색상 변경
                    binding.drawerHome.close()
                }
            })
        }
        binding.rvDrawer.adapter = adapter
    }

    private fun setRecyclerViewDecoration() {
        val itemDecoration = DrawerAdapterDecoration()
        binding.rvDrawer.addItemDecoration(itemDecoration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
