package com.ariari.mowoori.ui.custom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.ActivitySampleTitleViewBinding
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.util.toastMessage

class SampleTitleViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySampleTitleViewBinding
    private val mainViewModel by viewModels<SampleViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@SampleTitleViewActivity, R.layout.activity_sample_title_view)
        binding.lifecycleOwner = this
        binding.viewModel = mainViewModel
        setBackButtonClickObserve()
        setCloseButtonClickObserve()
        setPlusButtonClickObserve()
    }

    private fun setBackButtonClickObserve() {
        mainViewModel.backButtonClick.observe(this@SampleTitleViewActivity, EventObserver {
            if(it){
                toastMessage("back 버튼 클릭")
            }
        })
    }

    private fun setCloseButtonClickObserve() {
        mainViewModel.closeButtonClick.observe(this@SampleTitleViewActivity, EventObserver {
            if(it){
                toastMessage("close 버튼 클릭")
            }
        })
    }

    private fun setPlusButtonClickObserve() {
        mainViewModel.plusButtonClick.observe(this@SampleTitleViewActivity, EventObserver {
            if(it){
                toastMessage("plus 버튼 클릭")
            }
        })
    }
}
