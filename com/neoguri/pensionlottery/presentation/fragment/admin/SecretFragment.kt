package com.neoguri.pensionlottery.presentation.fragment.admin

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.neoguri.pensionlottery.R
import com.neoguri.pensionlottery.base.BaseFragment
import com.neoguri.pensionlottery.constant.URLs
import com.neoguri.pensionlottery.data.model.pensionlottery.PensionLotteryData
import com.neoguri.pensionlottery.data.repository.JsoupRequest
import com.neoguri.pensionlottery.databinding.BallViewBinding
import com.neoguri.pensionlottery.databinding.BonusBallViewBinding
import com.neoguri.pensionlottery.databinding.FragmentSecretBinding
import com.neoguri.pensionlottery.dto.PensionLotteryItemList
import com.neoguri.pensionlottery.presentation.lotto.PensionLotteryViewModel
import com.neoguri.pensionlottery.util.JsonTransmission
import com.neoguri.pensionlottery.util.LogUtil
import com.neoguri.pensionlottery.util.LottoColorUtil
import com.neoguri.pensionlottery.util.LottoUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.nodes.Element
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SecretFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SecretFragment : BaseFragment(), LifecycleObserver, View.OnClickListener {

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
            R.id.upload_button -> {
                if (mBinding.modeType.text == "Insert") {
                    viewModel.insertAdminLotto(
                        URLs.JSON_,
                        URLs.LOTTO_MAX_INSERT,
                        JsonTransmission().adminLotto(mLottoNums, "0"))
                } else if (mBinding.modeType.text == "Update") {
                    viewModel.insertAdminLotto(
                        URLs.JSON_,
                        URLs.LOTTO_MAX_UPDATE,
                        JsonTransmission().adminLotto(mLottoNums, "1"))
                } else if (mBinding.modeType.text == "Success") {
                    Toast.makeText(
                        requireActivity(),
                        "입력 완료된 회차 입니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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

    private var _binding: FragmentSecretBinding? = null
    private val mBinding get() = _binding!!

    val mLottoNums = PensionLotteryData("", "","","","","","","","","","","","","")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSecretBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SecretFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SecretFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private val viewModel: PensionLotteryViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                PensionLotteryViewModel(requireActivity().application) as T
        }).get(PensionLotteryViewModel::class.java)
    }

    private fun initLayout() {
        initColorSetting()
        lottoCrolingStart()

        viewModel.lottoItemAdminCheck.observe(viewLifecycleOwner) { lottoNums ->
            // Update the cached copy of the words in the adapter.
            lottoNums?.let {
                if (null != it) {
                    if (it.result == "Y") {
                        if (LottoUtil.isNumeric(mLottoNums.code)) {
                            if (mLottoNums.code.toInt() > it.data[0].code) {
                                mBinding.modeType.text = "Insert"
                            } else if (mLottoNums.code.toInt() == it.data[0].code) {
                                mBinding.modeType.text = "Update"
                                if (it.data[0].pensionlotteryNum != "" && it.data[0].firstPlacePeople != "") {
                                    mBinding.modeType.text = "Success"
                                }
                            }
                        }
                    }
                    mBinding.uploadButton.setOnClickListener(this)
                } else {
                    Toast.makeText(
                        requireActivity(),
                        requireActivity().resources.getString(R.string.network_check),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    private fun initColorSetting() {

        mBinding.includeToolbar.toolbarText.text = resources.getString(R.string.main_open_admin)
        setBack(mBinding.includeToolbar.backLayout)
        setDontTouchBtn(mBinding.idMainDontTouch)

        LottoColorUtil.initLottoColorSetting(
            requireActivity().applicationContext,
            mBinding.includeToolbar.backImage
        )
    }

    private fun lottoCrolingStart() {
        CoroutineScope(Dispatchers.IO).launch {
            val callback: (Element, Element, Boolean) -> Unit =
                { head: Element, body: Element, check: Boolean ->
                    if (check) {
                        getThursday(body)
                    }
                }
            JsoupRequest(requireActivity()).crawl(URLs.LOTTO_SATURDAY_CROLING, callback)
        }
    }

    private fun getThursday(body: Element) {

        var titleNum = "" // title_num
        val fitstLottoNumber = arrayOf("", "", "", "", "", "")

        try {

            val span = body.select("div.win720_num")
            val lottoNumber = span[0].text().replace("조 ", "").split(" ".toRegex())
                .dropLastWhile { it.isEmpty() }.toTypedArray()
            val secondLottoNumber =
                span[1].text().replace("각 ", "").replace("조 ", "").split(" ".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()

            for (i in lottoNumber.indices) {
                if (i == 0) {
                    titleNum = lottoNumber[i]
                } else {
                    fitstLottoNumber[i - 1] = lottoNumber[i]
                }
            }

            val code = body.select("h4")[0].text().split("회")[0]
            val winningResults = code + resources.getString(R.string.main_winning_results)
            mBinding.idPastWinningDetail.text = winningResults

            mLottoNums.code = code
            mLottoNums.pensionlotteryTitleNum = titleNum
            mLottoNums.pensionlotteryNum = fitstLottoNumber.joinToString(",")
            mLottoNums.bonusNum = secondLottoNumber.joinToString(",")

            var i = 1
            var j = 0
            body.select("td.ta_right").forEach {
                if (i % 3 == 0) {
                    when (j) {
                        0 -> {
                            mBinding.winRanking1st.text = it.text()
                            mLottoNums.firstPlacePeople = it.text()
                        }
                        1 -> {
                            mBinding.winRanking2nd.text = it.text()
                            mLottoNums.twoPlacePeople = it.text()
                        }
                        2 -> {
                            mBinding.winRanking3nd.text = it.text()
                            mLottoNums.threePlacePeople = it.text()
                        }
                        3 -> {
                            mBinding.winRanking4nd.text = it.text()
                            mLottoNums.foPlacePeople = it.text()
                        }
                        4 -> {
                            mBinding.winRanking5nd.text = it.text()
                            mLottoNums.fivePlacePeople = it.text()
                        }
                        5 -> {
                            mBinding.winRanking6nd.text = it.text()
                            mLottoNums.sixPlacePeople = it.text()
                        }
                        6 -> {
                            mBinding.winRanking7nd.text = it.text()
                            mLottoNums.sevenPlacePeople = it.text()
                        }
                        7 -> {
                            mBinding.winRankingBonus.text = it.text()
                            mLottoNums.bonusPlacePeople = it.text()
                        }
                    }
                    j++
                }
                i++
            }


            val date: Date? = SimpleDateFormat("(yyyy년 MM월 dd일 추첨)", Locale.KOREA).parse(
                body.select("p.desc").text()
            ) //String to date

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA) //new format

            val dateNewFormat: String = sdf.format(date!!) //result

            val winningResultsDay = " [$dateNewFormat]"
            mBinding.idPastWinningDetailDay.text = winningResultsDay

            mLottoNums.date = dateNewFormat
            mLottoNums.run = "N"

            val lottoNum = ArrayList<PensionLotteryData>()
            lottoNum.add(mLottoNums)
            mainFirstSetting(lottoNum, 0, mBinding.ballView)
            mainBonusSetting(lottoNum, 0, mBinding.bonusBallView)

            getLottoFirst()

        } catch (e: IOException) {
            LogUtil.d(e.printStackTrace().toString())
        }

    }

    private fun getLottoFirst() {
        viewModel.selectGetAdminCheck(
            URLs.JSON_,
            URLs.LOTTO_MAX_CHECK
        )
    }

    private fun startInsert() {

    }

    private fun startUpdate() {

    }

    private fun startSuccess() {

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

}