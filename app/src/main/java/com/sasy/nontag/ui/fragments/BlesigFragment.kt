package com.sasy.nontag.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sasy.nontag.databinding.FragmentBlesigBinding
import com.sasy.nontag.utils.seekbar.BubbleSeekBar
import com.sasy.nontag.utils.showToast


class BlesigFragment : Fragment() {
    private lateinit var binding: FragmentBlesigBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentBlesigBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlesigBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.seekBar.setCustomSectionTextArray { _, array ->
            array.clear()
            array.put(1, "AC")
            array.put(2, "AB")
            array.put(3, "AA")
            array.put(4, "A9")
            array.put(5, "A8")
            array.put(6, "A7")
            array.put(7, "A6")
            array.put(8, "A5")
            array.put(9, "A4")
            array.put(10, "A3")
            array
        }
//        seekBar.configBuilder
//            .progress(0f)
//            .sectionCount(10)
//            .showSectionText()
//            .sectionTextSize(14)
//            .showThumbText()
//            .touchToSeek()
//            .thumbTextSize(18)
//            .bubbleTextSize(18)
//            .showSectionMark()
//            .seekBySection()
//            .autoAdjustSectionMark()
//            .sectionTextPosition(BubbleSeekBar.TextPosition.BELOW_SECTION_MARK)
//            .build()

        binding.seekBar.onProgressChangedListener =
            object : BubbleSeekBar.OnProgressChangedListenerAdapter() {
                override fun onProgressChanged(
                    bubbleSeekBar: BubbleSeekBar,
                    progress: Int,
                    progressFloat: Float,
                    fromUser: Boolean
                ) {
                }

                override fun getProgressOnActionUp(
                    bubbleSeekBar: BubbleSeekBar,
                    progress: Int,
                    progressFloat: Float
                ) {
                }

                override fun getProgressOnFinally(
                    bubbleSeekBar: BubbleSeekBar,
                    progress: Int,
                    progressFloat: Float,
                    fromUser: Boolean
                ) {
                    showToast("" + progress + " : " + progressFloat)
                }
            }
    }
}