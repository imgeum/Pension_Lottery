package com.neoguri.pensionlottery.presentation.fragment.qrcode

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.neoguri.pensionlottery.R
import com.neoguri.pensionlottery.base.BaseFragment
import com.neoguri.pensionlottery.data.model.pensionlottery.PensionLotteryData
import com.neoguri.pensionlottery.databinding.BallViewBinding
import com.neoguri.pensionlottery.databinding.BonusBallViewBinding
import com.neoguri.pensionlottery.databinding.FragmentQRMyNumBinding
import com.neoguri.pensionlottery.databinding.QrBallViewBinding
import com.neoguri.pensionlottery.dto.PensionLotteryItemList
import com.neoguri.pensionlottery.dto.QrLottoNum
import com.neoguri.pensionlottery.presentation.db.myLottoNum.MyLottoNum
import com.neoguri.pensionlottery.presentation.lotto.PensionLotteryViewModel
import com.neoguri.pensionlottery.util.LottoColorUtil
import com.neoguri.pensionlottery.util.LottoUtil
import java.util.ArrayList

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [QRMyNumFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class QRMyNumFragment : BaseFragment(), LifecycleObserver, View.OnClickListener {

    private var isNotDrawn: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.qrStartCheck.postValue(false)
        lifecycle.addObserver(this)
    }

    override fun onDetach() {
        super.onDetach()
        viewModel.qrStartCheck.postValue(true)
        lifecycle.removeObserver(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.l_save -> {
                viewModel.insertMyNum(
                    MyLottoNum(
                        LottoUtil.nowDate(),
                        LottoUtil.nowTime(),
                        mQrLottoNum.lotto_title_item,
                        mQrLottoNum.lotto_item,
                        "",
                        "N"
                    )
                )
                Toast.makeText(
                    requireActivity(),
                    resources.getString(R.string.my_num_save_success),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isNotDrawn = it.getBoolean(ARG_PARAM1)
        }
    }

    private var _binding: FragmentQRMyNumBinding? = null
    private val mBinding get() = _binding!!

    private var isAllFirstCheck = false
    private var isAllSecondCheck = false

    private var mLottoItemList = ArrayList<PensionLotteryData>()
    private var mQrLottoNum = QrLottoNum()

    private var mPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentQRMyNumBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment QRMyNumFragment.
         */
        @JvmStatic
        fun newInstance(param1: Boolean) =
            QRMyNumFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_PARAM1, param1)
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

        mBinding.includeToolbar.toolbarText.text =
            resources.getString(R.string.main_qr_code_enter_insert_winnings)
        setBack(mBinding.includeToolbar.backLayout)
        setDontTouchBtn(mBinding.idMainDontTouch)

        LottoColorUtil.initLottoColorSetting(requireActivity(), mBinding.includeToolbar.backImage)

        viewModel.mRJLottoNum.observe(viewLifecycleOwner) { lottoNums ->
            // Update the cached copy of the words in the adapter.
            lottoNums?.let {
                mLottoItemList = it
                isAllFirstCheck = true
                isAllCheck(isAllFirstCheck, isAllSecondCheck)
            }
        }

        viewModel.myQRLottoNumItemList.observe(viewLifecycleOwner) { lottoNums ->
            // Update the cached copy of the words in the adapter.
            lottoNums?.let {
                isAllSecondCheck = true
                if (lottoNums.size != 0) {
                    mQrLottoNum = it[0]
                    val position = mQrLottoNum.lotto_round.replace("제", "").replace("회", "").trim()
                    mPosition = position.toInt() - 1
                }
                isAllCheck(isAllFirstCheck, isAllSecondCheck)
            }
        }

    }

    private fun isAllCheck(first: Boolean, second: Boolean) {
        if (first && second) {
            mBinding.lSave.setOnClickListener(this)
            winningCheck(mQrLottoNum, mPosition)
            if (isNotDrawn) {
                val winningResults = mQrLottoNum.lotto_round.replace("제", "").replace("회", "")
                    .trim() + resources.getString(R.string.main_winning_results)
                mBinding.idPastWinningDetail.text = winningResults
                val winningResultsDay = " [" + mQrLottoNum.lotto_time + "]"
                mBinding.idPastWinningDetailDay.text = winningResultsDay

                mBinding.myWinLayout.visibility = View.GONE
                mBinding.detailWinTopLayout.visibility = View.GONE
                mBinding.detailWinBottomLayout.visibility = View.GONE
                initNoDataLayout()
            } else {
                val winningResults =
                    mLottoItemList[mPosition].code + resources.getString(R.string.main_winning_results)
                mBinding.idPastWinningDetail.text = winningResults
                val winningResultsDay = " [" + mLottoItemList[mPosition].date + "]"
                mBinding.idPastWinningDetailDay.text = winningResultsDay

                mBinding.myWinLayout.visibility = View.VISIBLE
                mBinding.detailWinTopLayout.visibility = View.VISIBLE
                mBinding.detailWinBottomLayout.visibility = View.VISIBLE
                initDataLayout()
            }
        }
    }

    private fun winningCheck(qrLottoNum: QrLottoNum, position: Int) {
        val firstSplit = mLottoItemList[mPosition].pensionlotteryNum.split(",".toRegex())
            .dropLastWhile { it.isEmpty() }.toTypedArray()

        var rankCount = 0

        val split =
            qrLottoNum.lotto_item.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (i in 5 downTo 0) {
            if (firstSplit[i] == split[i]) {
                rankCount += 1
            } else {
                break
            }
        }

        if (rankCount == 6) {
            if (mLottoItemList[position].pensionlotteryTitleNum == qrLottoNum.lotto_title_item) {
                rankCount += 1
            }
        }

        when (rankCount) {
            1 -> {
                mBinding.sevenImageview.setColorFilter(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.color_background
                    )
                )
            }
            2 -> {
                mBinding.sixImageview.setColorFilter(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.color_background
                    )
                )
            }
            3 -> {
                mBinding.fiveImageview.setColorFilter(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.color_background
                    )
                )
            }
            4 -> {
                mBinding.foImageview.setColorFilter(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.color_background
                    )
                )
            }
            5 -> {
                mBinding.threeImageview.setColorFilter(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.color_background
                    )
                )
            }
            6 -> {
                mBinding.twoImageview.setColorFilter(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.color_background
                    )
                )
            }
            7 -> {
                mBinding.oneImageview.setColorFilter(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.color_background
                    )
                )
            }
        }

        if (rankCount < 6) {
            val result = bonusCheck(mLottoItemList[mPosition].bonusNum, qrLottoNum.lotto_item)

            if (result) {
                mBinding.bonusImageview.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_background
                    )
                )
            }
        }

    }

    private fun initNoDataLayout() {

        mBinding.idQrNumWinning.text = requireActivity().resources.getString(R.string.win_no_price)

        val split = mQrLottoNum.lotto_item.split(",".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()

        mBinding.qrBallView.lottoText1.text = split[0]
        mBinding.qrBallView.lottoText2.text = split[1]
        mBinding.qrBallView.lottoText3.text = split[2]
        mBinding.qrBallView.lottoText4.text = split[3]
        mBinding.qrBallView.lottoText5.text = split[4]
        mBinding.qrBallView.lottoText6.text = split[5]
        mBinding.qrBallView.lottoText7.text = mQrLottoNum.lotto_title_item

    }

    private fun initDataLayout() {
        mainFirstSetting(mBinding.ballView)
        mainBonusSetting(mBinding.bonusBallView)
        qrMySetting(mBinding.qrBallView)
    }

    private fun mainFirstSetting(
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
            mLottoItemList,
            mPosition,
            imageViewList,
            imageViewTaedooriList,
            textViewList,
            ballView.lotto7,
            ballView.lotto7Taedoori,
            ballView.lottoText7
        )
    }

    private fun mainBonusSetting(
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
            mLottoItemList,
            mPosition,
            imageViewList,
            imageViewTaedooriList,
            textViewList,
            true
        )
    }

    private fun qrMySetting(qrBallView: QrBallViewBinding) {
        val imageViewList = ArrayList<ImageView>()
        val imageViewTaedooriList = ArrayList<ImageView>()
        val textViewList = ArrayList<TextView>()
        val imageColor = ArrayList<Int>()

        initSetting(imageViewList, imageViewTaedooriList, textViewList, imageColor, qrBallView)

        var rankCount = 0

        val firstSplit = mLottoItemList[mPosition].pensionlotteryNum.split(",".toRegex())
            .dropLastWhile { it.isEmpty() }.toTypedArray()
        val split = mQrLottoNum.lotto_item.split(",".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()

        mBinding.qrBallView.lottoText1.text = split[0]
        mBinding.qrBallView.lottoText2.text = split[1]
        mBinding.qrBallView.lottoText3.text = split[2]
        mBinding.qrBallView.lottoText4.text = split[3]
        mBinding.qrBallView.lottoText5.text = split[4]
        mBinding.qrBallView.lottoText6.text = split[5]
        mBinding.qrBallView.lottoText7.text = mQrLottoNum.lotto_title_item

        for (i in 5 downTo 0) {
            if (firstSplit[i] == split[i]) {
                rankCount += 1
                LottoUtil.initBonusData(
                    requireActivity(),
                    imageViewList[i],
                    imageViewTaedooriList[i],
                    textViewList[i],
                    split[i],
                    imageColor[i]
                )
            } else {
                break
            }
        }

        if (rankCount == 6) {
            if (mLottoItemList[mPosition].pensionlotteryTitleNum == mQrLottoNum.lotto_title_item) {
                rankCount += 1
                LottoUtil.initBonusData(
                    requireActivity(),
                    qrBallView.lotto7,
                    qrBallView.lotto7Taedoori,
                    qrBallView.lottoText7,
                    mQrLottoNum.lotto_title_item,
                    ContextCompat.getColor(requireActivity(), R.color.color_teadoori_)
                )
            }
        }

        when (rankCount) {
            1 -> {
                mBinding.idQrNumWinning.text =
                    settingText(R.string.popup_window_7nd, R.string.win_7_price).toSpanned()
            }
            2 -> {
                mBinding.idQrNumWinning.text =
                    settingText(R.string.popup_window_6nd, R.string.win_6_price).toSpanned()
            }
            3 -> {
                mBinding.idQrNumWinning.text =
                    settingText(R.string.popup_window_5nd, R.string.win_5_price).toSpanned()
            }
            4 -> {
                mBinding.idQrNumWinning.text =
                    settingText(R.string.popup_window_4nd, R.string.win_4_price).toSpanned()
            }
            5 -> {
                mBinding.idQrNumWinning.text =
                    settingText(R.string.popup_window_3nd, R.string.win_3_price).toSpanned()
            }
            6 -> {
                mBinding.idQrNumWinning.text =
                    settingText(R.string.popup_window_2nd, R.string.win_2_price).toSpanned()
            }
            7 -> {
                mBinding.idQrNumWinning.text =
                    settingText(R.string.popup_window_first_place, R.string.win_1_price).toSpanned()
            }
        }

        qrBallView.joText.visibility = View.VISIBLE

        if (rankCount == 0) {
            mBinding.idQrNumWinning.text = requireActivity().resources.getString(R.string.next_time)
        }

        if (rankCount < 6) {
            val result = bonusCheck(mLottoItemList[mPosition].bonusNum, mQrLottoNum.lotto_item)

            if (result) {
                qrBallView.joText.visibility = View.GONE

                mBinding.idQrNumWinning.text =
                    settingText(R.string.popup_window_bonus, R.string.win_bonus_price).toSpanned()

                qrBallView.lotto7.visibility = View.GONE
                qrBallView.lotto7Taedoori.visibility = View.GONE
                qrBallView.lottoText7.visibility = View.GONE

                val bonusSplit = mLottoItemList[mPosition].bonusNum.split(",".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()

                for (i in 5 downTo 0) {
                    if (bonusSplit[i] == split[i]) {
                        LottoUtil.initBonusData(
                            requireActivity(),
                            imageViewList[i],
                            imageViewTaedooriList[i],
                            textViewList[i],
                            split[i],
                            imageColor[i]
                        )
                    } else {
                        break
                    }
                }
            }
        }

    }

    private fun initNoSetting(
        imageViewList: ArrayList<ImageView>,
        imageViewTaedooriList: ArrayList<ImageView>,
        textViewList: ArrayList<TextView>,
        ballView: QrBallViewBinding
    ) {
        imageViewList.clear()
        imageViewTaedooriList.clear()
        textViewList.clear()

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

    }

    private fun initSetting(
        imageViewList: ArrayList<ImageView>,
        imageViewTaedooriList: ArrayList<ImageView>,
        textViewList: ArrayList<TextView>,
        imageColor: ArrayList<Int>,
        ballView: QrBallViewBinding
    ) {
        imageViewList.clear()
        imageViewTaedooriList.clear()
        textViewList.clear()
        imageColor.clear()

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

        imageColor.add(ContextCompat.getColor(requireActivity(), R.color.color_red))
        imageColor.add(ContextCompat.getColor(requireActivity(), R.color.color_orange))
        imageColor.add(ContextCompat.getColor(requireActivity(), R.color.color_yellow))
        imageColor.add(ContextCompat.getColor(requireActivity(), R.color.color_blue))
        imageColor.add(ContextCompat.getColor(requireActivity(), R.color.color_purple))
        imageColor.add(ContextCompat.getColor(requireActivity(), R.color.color_gray))
    }

    private fun settingText(popupWindow: Int, winPrice: Int): String {
        val text = requireActivity().resources.getString(popupWindow)
        val textPrice = requireActivity().resources.getString(winPrice)
        val textColor = Integer.toHexString(
            ContextCompat.getColor(
                requireActivity(),
                R.color.text_color
            ) and 0x00ffffff
        )
        var autoPastMinusTextString = "<font color=\"#FF0000\">$text :</font>"
        autoPastMinusTextString =
            "$autoPastMinusTextString<font color=\"$textColor\"> $textPrice</font>"

        return autoPastMinusTextString
    }

    private fun bonusCheck(lottoBonusNum: String, lottoItem: String): Boolean {
        var rankCount = 0

        val bonusSplit =
            lottoBonusNum.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val split = lottoItem.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        for (i in 5 downTo 0) {
            if (bonusSplit[i] == split[i]) {
                rankCount += 1
            } else {
                break
            }
        }

        return rankCount == 6
    }

    private fun String.toSpanned(): Spanned {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            return Html.fromHtml(this)
        }
    }

}