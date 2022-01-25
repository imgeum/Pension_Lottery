package com.neoguri.pensionlottery.presentation.fragment.autogetnum

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.neoguri.pensionlottery.R
import com.neoguri.pensionlottery.base.BaseFragment
import com.neoguri.pensionlottery.databinding.FragmentAutoLottoNumBinding
import com.neoguri.pensionlottery.presentation.db.myLottoNum.MyLottoNum
import com.neoguri.pensionlottery.presentation.fragment.mynumlist.MyNumListFragment
import com.neoguri.pensionlottery.presentation.lotto.MainActivity
import com.neoguri.pensionlottery.presentation.lotto.PensionLotteryViewModel
import com.neoguri.pensionlottery.util.LogUtil
import com.neoguri.pensionlottery.util.LottoColorUtil
import com.neoguri.pensionlottery.util.LottoUtil

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AutoLottoNumFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AutoLottoNumFragment : BaseFragment(), View.OnClickListener, LifecycleObserver {

    private var param1: String? = null
    private var param2: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycle.addObserver(this)
    }

    override fun onDetach() {
        super.onDetach()
        lifecycle.removeObserver(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.auto_new_num_start -> {
                getAutoNum()
            }
            R.id.auto_num_save -> {
                viewModel.insertMyNum(MyLottoNum(LottoUtil.nowDate(), LottoUtil.nowTime(), mLottoNumTitme, mLottoNumArray.joinToString(","), "", "N"))
                Toast.makeText(requireActivity(), resources.getString(R.string.my_num_save_success), Toast.LENGTH_SHORT).show()
            }
            R.id.generation_num_list_layout -> {
                val fragment: Fragment = MyNumListFragment()
                (activity as MainActivity?)!!.startHamberger(fragment, "MyNumListFragment")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var _binding: FragmentAutoLottoNumBinding? = null
    private val mBinding get() = _binding!!

    private val mImageViewList = ArrayList<ImageView>()
    private val mTextViewList = ArrayList<TextView>()
    private val mImageViewTaedooriList = ArrayList<ImageView>()
    private val mImageColor = ArrayList<Int>()

    var mLottoNumTitme = ""
    val mLottoNumArray = arrayOf("", "", "", "", "", "")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAutoLottoNumBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AutoLottoNumFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AutoLottoNumFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private val viewModel: PensionLotteryViewModel by lazy {
        ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                PensionLotteryViewModel(requireActivity().application) as T
        }).get(PensionLotteryViewModel::class.java)
    }

    private fun initLayout() {

        mBinding.includeToolbar.toolbarText.text = resources.getString(R.string.main_left_auto_generation)
        setBack(mBinding.includeToolbar.backLayout)
        setDontTouchBtn(mBinding.idMainDontTouch)

        LottoColorUtil.initLottoColorSetting(
            requireActivity().applicationContext,
            mBinding.includeToolbar.backImage
        )

        initListener()
        initSetting()

    }

    private fun initListener() {
        mBinding.autoNewNumStart.setOnClickListener(this)
        mBinding.autoNumSave.setOnClickListener(this)
        mBinding.generationNumListLayout.setOnClickListener(this)
    }

    private fun initSetting() {
        mImageViewList.clear()
        mImageViewTaedooriList.clear()
        mTextViewList.clear()

        mImageViewList.add(mBinding.myNumBallView.lotto7)
        mImageViewList.add(mBinding.myNumBallView.lotto1)
        mImageViewList.add(mBinding.myNumBallView.lotto2)
        mImageViewList.add(mBinding.myNumBallView.lotto3)
        mImageViewList.add(mBinding.myNumBallView.lotto4)
        mImageViewList.add(mBinding.myNumBallView.lotto5)
        mImageViewList.add(mBinding.myNumBallView.lotto6)

        mImageViewTaedooriList.add(mBinding.myNumBallView.lotto7Taedoori)
        mImageViewTaedooriList.add(mBinding.myNumBallView.lotto1Taedoori)
        mImageViewTaedooriList.add(mBinding.myNumBallView.lotto2Taedoori)
        mImageViewTaedooriList.add(mBinding.myNumBallView.lotto3Taedoori)
        mImageViewTaedooriList.add(mBinding.myNumBallView.lotto4Taedoori)
        mImageViewTaedooriList.add(mBinding.myNumBallView.lotto5Taedoori)
        mImageViewTaedooriList.add(mBinding.myNumBallView.lotto6Taedoori)

        mTextViewList.add(mBinding.myNumBallView.lottoText7)
        mTextViewList.add(mBinding.myNumBallView.lottoText1)
        mTextViewList.add(mBinding.myNumBallView.lottoText2)
        mTextViewList.add(mBinding.myNumBallView.lottoText3)
        mTextViewList.add(mBinding.myNumBallView.lottoText4)
        mTextViewList.add(mBinding.myNumBallView.lottoText5)
        mTextViewList.add(mBinding.myNumBallView.lottoText6)

        mImageColor.add(ContextCompat.getColor(requireActivity(), R.color.color_teadoori_))
        mImageColor.add(ContextCompat.getColor(requireActivity(), R.color.color_red))
        mImageColor.add(ContextCompat.getColor(requireActivity(), R.color.color_orange))
        mImageColor.add(ContextCompat.getColor(requireActivity(), R.color.color_yellow))
        mImageColor.add(ContextCompat.getColor(requireActivity(), R.color.color_blue))
        mImageColor.add(ContextCompat.getColor(requireActivity(), R.color.color_purple))
        mImageColor.add(ContextCompat.getColor(requireActivity(), R.color.color_gray))
    }

    private fun getAutoNum() {
        for (i in 0..6) {
            if (i == 0) {
                val range = (1..5) // 1 <= n <= 9
                mLottoNumTitme = range.random().toString()
            } else if (i > 0) {
                val range = (0..9) // 0 <= n <= 9
                mLottoNumArray[i - 1] = range.random().toString()
            }
        }
        setNumber(mLottoNumTitme, mLottoNumArray.joinToString(","))
    }

    private fun setNumber(mLottoNumTitme: String, joinToString: String) {

        LottoUtil.initBonusData(
            requireActivity(),
            mImageViewList[0],
            mImageViewTaedooriList[0],
            mTextViewList[0],
            mLottoNumTitme,
            mImageColor[0]
        )

        val joinToStringArray = joinToString.split(",".toRegex()) .dropLastWhile { it.isEmpty() } .toTypedArray()

        for (i in joinToStringArray.indices) {
            LottoUtil.initBonusData(
                requireActivity(),
                mImageViewList[i + 1],
                mImageViewTaedooriList[i + 1],
                mTextViewList[i + 1],
                joinToStringArray[i],
                mImageColor[i + 1]
            )
        }

    }

}