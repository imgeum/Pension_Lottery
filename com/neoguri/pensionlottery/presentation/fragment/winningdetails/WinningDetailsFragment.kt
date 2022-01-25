package com.neoguri.pensionlottery.presentation.fragment.winningdetails

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.neoguri.pensionlottery.R
import com.neoguri.pensionlottery.base.BaseFragment
import com.neoguri.pensionlottery.data.model.pensionlottery.PensionLotteryData
import com.neoguri.pensionlottery.databinding.*
import com.neoguri.pensionlottery.dto.PensionLotteryItemList
import com.neoguri.pensionlottery.presentation.lotto.PensionLotteryViewModel
import com.neoguri.pensionlottery.util.LottoColorUtil
import com.neoguri.pensionlottery.util.LottoUtil

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [WinningDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WinningDetailsFragment : BaseFragment(), LifecycleObserver, View.OnClickListener, View.OnLongClickListener {


    private var mPosition = 0

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
            R.id.relativelayout_left_move -> {
                mPosition -= 1
                initDataLayout(mPosition)
            }
            R.id.relativelayout_right_move -> {
                mPosition += 1
                initDataLayout(mPosition)
            }
        }
    }

    override fun onLongClick(v: View?): Boolean {
        when (v?.id) {
            R.id.relativelayout_left_move -> {
                mPosition = 0
                initDataLayout(mPosition)
            }
            R.id.relativelayout_right_move -> {
                mPosition = mLottoAllData.size - 1
                initDataLayout(mPosition)
            }
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPosition = it.getInt(ARG_PARAM1)
        }
    }

    private var _binding: FragmentWinningDetailsBinding? = null
    private val mBinding get() = _binding!!

    private var mLottoAllData = ArrayList<PensionLotteryData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWinningDetailsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WinningDetailsFragment.
         */
        @JvmStatic
        fun newInstance(position: Int) =
            WinningDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, position)
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

        mBinding.includeToolbar.toolbarText.text = resources.getString(R.string.main_fragment_winning_details)
        setBack(mBinding.includeToolbar.backLayout)
        setDontTouchBtn(mBinding.idMainDontTouch)

        initColorSetting()
        initListener()

        viewModel.mRJLottoNum.observe(viewLifecycleOwner, { lottoNums ->
            // Update the cached copy of the words in the adapter.
            lottoNums?.let {
                mLottoAllData = it
                initDataLayout(mPosition)
            }
        })

    }

    private fun initColorSetting() {
        LottoColorUtil.initLottoColorSetting(
            requireActivity().applicationContext,
            mBinding.includeToolbar.backImage
        )
        LottoColorUtil.initLottoColorSetting(
            requireActivity().applicationContext,
            mBinding.fragmentLeft
        )
        LottoColorUtil.initLottoColorSetting(
            requireActivity().applicationContext,
            mBinding.fragmentRight
        )
    }

    private fun initListener() {
        mBinding.relativelayoutLeftMove.setOnClickListener(this)
        mBinding.relativelayoutLeftMove.setOnLongClickListener(this)
        mBinding.relativelayoutRightMove.setOnClickListener(this)
        mBinding.relativelayoutRightMove.setOnLongClickListener(this)
    }

    private fun initDataLayout(position: Int) {

        val winningResults = mLottoAllData[position].code + resources.getString(R.string.main_winning_results)
        mBinding.idPastWinningDetail.text = winningResults
        val winningResultsDay = " [" + mLottoAllData[position].date + "]"
        mBinding.idPastWinningDetailDay.text = winningResultsDay

        mainFirstSetting(mLottoAllData, position, mBinding.ballView)
        mainBonusSetting(mLottoAllData, position, mBinding.bonusBallView)
        mainFirstSetting(mLottoAllData, position, mBinding.firstBallView)
        mainOtherSetting(mLottoAllData, position, mBinding.twoBallView, 2)
        mainOtherSetting(mLottoAllData, position, mBinding.threeBallView, 3)
        mainOtherSetting(mLottoAllData, position, mBinding.foBallView, 4)
        mainOtherSetting(mLottoAllData, position, mBinding.fiveBallView, 5)
        mainOtherSetting(mLottoAllData, position, mBinding.sixBallView, 6)
        mainOtherSetting(mLottoAllData, position, mBinding.sevenBallView, 7)
        mainOtherBonusSetting(mLottoAllData, position, mBinding.bonusOtherBallView)

        initRankingData(position)

        setArrow(position)

    }

    private fun initRankingData(position: Int) {
        mBinding.winRanking1st.text = mLottoAllData[position].firstPlacePeople
        mBinding.winRanking2nd.text = mLottoAllData[position].twoPlacePeople
        mBinding.winRanking3nd.text = mLottoAllData[position].threePlacePeople
        mBinding.winRanking4nd.text = mLottoAllData[position].foPlacePeople
        mBinding.winRanking5nd.text = mLottoAllData[position].fivePlacePeople
        mBinding.winRanking6nd.text = mLottoAllData[position].sixPlacePeople
        mBinding.winRanking7nd.text = mLottoAllData[position].sevenPlacePeople
        mBinding.winRankingBonus.text = mLottoAllData[position].bonusPlacePeople
    }

    private fun mainFirstSetting(
        pensionLotteryItemList: ArrayList<PensionLotteryData>,
        position: Int,
        ballView: BallViewBinding
    ) {
        val imageViewList = ArrayList<ImageView>()
        val imageViewTaedooriList = ArrayList<ImageView>()
        val textViewList = ArrayList<TextView>()

        imageViewList.add(ballView.lotto1)
        imageViewList.add(ballView.lotto2)
        imageViewList.add(ballView.lotto3)
        imageViewList.add(ballView.lotto4)
        imageViewList.add(ballView.lotto5)
        imageViewList.add(ballView.lotto6)

        imageViewTaedooriList.add(ballView.lotto1Taedoori)
        imageViewTaedooriList.add(ballView.lotto2Taedoori)
        imageViewTaedooriList.add(ballView.lotto3Taedoori)
        imageViewTaedooriList.add(ballView.lotto4Taedoori)
        imageViewTaedooriList.add(ballView.lotto5Taedoori)
        imageViewTaedooriList.add(ballView.lotto6Taedoori)

        textViewList.add(ballView.lottoText1)
        textViewList.add(ballView.lottoText2)
        textViewList.add(ballView.lottoText3)
        textViewList.add(ballView.lottoText4)
        textViewList.add(ballView.lottoText5)
        textViewList.add(ballView.lottoText6)

        LottoUtil.initLottoSetting(
            requireActivity(),
            pensionLotteryItemList,
            position,
            imageViewList,
            imageViewTaedooriList,
            textViewList,
            ballView.lotto7,
            ballView.lotto7Taedoori,
            ballView.lottoText7
        )
    }

    private fun mainFirstSetting(
        pensionLotteryItemList: ArrayList<PensionLotteryData>,
        position: Int,
        ballView: DetailBallViewBinding
    ) {
        val imageViewList = ArrayList<ImageView>()
        val imageViewTaedooriList = ArrayList<ImageView>()
        val textViewList = ArrayList<TextView>()

        imageViewList.add(ballView.lotto1)
        imageViewList.add(ballView.lotto2)
        imageViewList.add(ballView.lotto3)
        imageViewList.add(ballView.lotto4)
        imageViewList.add(ballView.lotto5)
        imageViewList.add(ballView.lotto6)

        imageViewTaedooriList.add(ballView.lotto1Taedoori)
        imageViewTaedooriList.add(ballView.lotto2Taedoori)
        imageViewTaedooriList.add(ballView.lotto3Taedoori)
        imageViewTaedooriList.add(ballView.lotto4Taedoori)
        imageViewTaedooriList.add(ballView.lotto5Taedoori)
        imageViewTaedooriList.add(ballView.lotto6Taedoori)

        textViewList.add(ballView.lottoText1)
        textViewList.add(ballView.lottoText2)
        textViewList.add(ballView.lottoText3)
        textViewList.add(ballView.lottoText4)
        textViewList.add(ballView.lottoText5)
        textViewList.add(ballView.lottoText6)

        LottoUtil.initLottoSetting(
            requireActivity(),
            pensionLotteryItemList,
            position,
            imageViewList,
            imageViewTaedooriList,
            textViewList,
            ballView.lotto7,
            ballView.lotto7Taedoori,
            ballView.lottoText7
        )
    }

    private fun mainOtherSetting(
        pensionLotteryItemList: ArrayList<PensionLotteryData>,
        position: Int,
        ballView: DetailOtherBallViewBinding,
        i: Int
    ) {
        val imageViewList = ArrayList<ImageView>()
        val imageViewTaedooriList = ArrayList<ImageView>()
        val textViewList = ArrayList<TextView>()

        imageViewList.add(ballView.ballViewLotto1)
        imageViewList.add(ballView.ballViewLotto2)
        imageViewList.add(ballView.ballViewLotto3)
        imageViewList.add(ballView.ballViewLotto4)
        imageViewList.add(ballView.ballViewLotto5)
        imageViewList.add(ballView.ballViewLotto6)

        imageViewTaedooriList.add(ballView.ballViewLotto1Taedoori)
        imageViewTaedooriList.add(ballView.ballViewLotto2Taedoori)
        imageViewTaedooriList.add(ballView.ballViewLotto3Taedoori)
        imageViewTaedooriList.add(ballView.ballViewLotto4Taedoori)
        imageViewTaedooriList.add(ballView.ballViewLotto5Taedoori)
        imageViewTaedooriList.add(ballView.ballViewLotto6Taedoori)

        textViewList.add(ballView.ballViewLottoText1)
        textViewList.add(ballView.ballViewLottoText2)
        textViewList.add(ballView.ballViewLottoText3)
        textViewList.add(ballView.ballViewLottoText4)
        textViewList.add(ballView.ballViewLottoText5)
        textViewList.add(ballView.ballViewLottoText6)

        LottoUtil.initLottoSetting(
            requireActivity(),
            pensionLotteryItemList,
            position,
            imageViewList,
            imageViewTaedooriList,
            textViewList,
            false
        )

        if(i >= 3){
            ballView.ballViewLotto1.visibility = View.GONE
            ballView.ballViewLotto1Taedoori.visibility = View.GONE
            ballView.ballViewLottoText1.visibility = View.GONE
        }

        if(i >= 4){
            ballView.ballViewLotto2.visibility = View.GONE
            ballView.ballViewLotto2Taedoori.visibility = View.GONE
            ballView.ballViewLottoText2.visibility = View.GONE
        }

        if(i >= 5){
            ballView.ballViewLotto3.visibility = View.GONE
            ballView.ballViewLotto3Taedoori.visibility = View.GONE
            ballView.ballViewLottoText3.visibility = View.GONE
        }

        if(i >= 6){
            ballView.ballViewLotto4.visibility = View.GONE
            ballView.ballViewLotto4Taedoori.visibility = View.GONE
            ballView.ballViewLottoText4.visibility = View.GONE
        }

        if(i >= 7){
            ballView.ballViewLotto5.visibility = View.GONE
            ballView.ballViewLotto5Taedoori.visibility = View.GONE
            ballView.ballViewLottoText5.visibility = View.GONE
        }

    }

    private fun mainBonusSetting(
        pensionLotteryItemList: ArrayList<PensionLotteryData>,
        position: Int,
        bonusBallView: BonusBallViewBinding
    ) {
        val imageViewList = ArrayList<ImageView>()
        val imageViewTaedooriList = ArrayList<ImageView>()
        val textViewList = ArrayList<TextView>()

        imageViewList.add(bonusBallView.bonusBallViewLotto1)
        imageViewList.add(bonusBallView.bonusBallViewLotto2)
        imageViewList.add(bonusBallView.bonusBallViewLotto3)
        imageViewList.add(bonusBallView.bonusBallViewLotto4)
        imageViewList.add(bonusBallView.bonusBallViewLotto5)
        imageViewList.add(bonusBallView.bonusBallViewLotto6)

        imageViewTaedooriList.add(bonusBallView.bonusBallViewLotto1Taedoori)
        imageViewTaedooriList.add(bonusBallView.bonusBallViewLotto2Taedoori)
        imageViewTaedooriList.add(bonusBallView.bonusBallViewLotto3Taedoori)
        imageViewTaedooriList.add(bonusBallView.bonusBallViewLotto4Taedoori)
        imageViewTaedooriList.add(bonusBallView.bonusBallViewLotto5Taedoori)
        imageViewTaedooriList.add(bonusBallView.bonusBallViewLotto6Taedoori)

        textViewList.add(bonusBallView.bonusBallViewLottoText1)
        textViewList.add(bonusBallView.bonusBallViewLottoText2)
        textViewList.add(bonusBallView.bonusBallViewLottoText3)
        textViewList.add(bonusBallView.bonusBallViewLottoText4)
        textViewList.add(bonusBallView.bonusBallViewLottoText5)
        textViewList.add(bonusBallView.bonusBallViewLottoText6)

        LottoUtil.initLottoSetting(
            requireActivity(),
            pensionLotteryItemList,
            position,
            imageViewList,
            imageViewTaedooriList,
            textViewList,
            true
        )
    }

    private fun mainOtherBonusSetting(
        pensionLotteryItemList: ArrayList<PensionLotteryData>,
        position: Int,
        bonusBallView: DetailOtherBonusBallViewBinding
    ) {
        val imageViewList = ArrayList<ImageView>()
        val imageViewTaedooriList = ArrayList<ImageView>()
        val textViewList = ArrayList<TextView>()

        imageViewList.add(bonusBallView.bonusBallViewLotto1)
        imageViewList.add(bonusBallView.bonusBallViewLotto2)
        imageViewList.add(bonusBallView.bonusBallViewLotto3)
        imageViewList.add(bonusBallView.bonusBallViewLotto4)
        imageViewList.add(bonusBallView.bonusBallViewLotto5)
        imageViewList.add(bonusBallView.bonusBallViewLotto6)

        imageViewTaedooriList.add(bonusBallView.bonusBallViewLotto1Taedoori)
        imageViewTaedooriList.add(bonusBallView.bonusBallViewLotto2Taedoori)
        imageViewTaedooriList.add(bonusBallView.bonusBallViewLotto3Taedoori)
        imageViewTaedooriList.add(bonusBallView.bonusBallViewLotto4Taedoori)
        imageViewTaedooriList.add(bonusBallView.bonusBallViewLotto5Taedoori)
        imageViewTaedooriList.add(bonusBallView.bonusBallViewLotto6Taedoori)

        textViewList.add(bonusBallView.bonusBallViewLottoText1)
        textViewList.add(bonusBallView.bonusBallViewLottoText2)
        textViewList.add(bonusBallView.bonusBallViewLottoText3)
        textViewList.add(bonusBallView.bonusBallViewLottoText4)
        textViewList.add(bonusBallView.bonusBallViewLottoText5)
        textViewList.add(bonusBallView.bonusBallViewLottoText6)

        LottoUtil.initLottoSetting(
            requireActivity(),
            pensionLotteryItemList,
            position,
            imageViewList,
            imageViewTaedooriList,
            textViewList,
            true
        )
    }

    private fun setArrow(position: Int) {
        if(mLottoAllData.size - 1 == position){
            mBinding.relativelayoutLeftMove.visibility = View.VISIBLE
            mBinding.fragmentLeft.visibility = View.VISIBLE
            mBinding.relativelayoutRightMove.visibility = View.GONE
            mBinding.fragmentRight.visibility = View.GONE
        } else if(mLottoAllData.size > position && position > 0) {
            mBinding.relativelayoutLeftMove.visibility = View.VISIBLE
            mBinding.fragmentLeft.visibility = View.VISIBLE
            mBinding.relativelayoutRightMove.visibility = View.VISIBLE
            mBinding.fragmentRight.visibility = View.VISIBLE
        } else if(0 == position){
            mBinding.relativelayoutLeftMove.visibility = View.GONE
            mBinding.fragmentLeft.visibility = View.GONE
            mBinding.relativelayoutRightMove.visibility = View.VISIBLE
            mBinding.fragmentRight.visibility = View.VISIBLE
        }
    }

}