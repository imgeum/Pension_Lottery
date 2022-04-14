package com.neoguri.pensionlottery.presentation.fragment.qrcode

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.neoguri.pensionlottery.PensionLottery
import com.neoguri.pensionlottery.R
import com.neoguri.pensionlottery.base.BaseFragment
import com.neoguri.pensionlottery.constant.Constant
import com.neoguri.pensionlottery.constant.URLs
import com.neoguri.pensionlottery.data.model.pensionlottery.PensionLotteryData
import com.neoguri.pensionlottery.data.repository.JsoupRequest
import com.neoguri.pensionlottery.databinding.FragmentQRCodeBinding
import com.neoguri.pensionlottery.dto.PensionLotteryItemList
import com.neoguri.pensionlottery.dto.QrLottoNum
import com.neoguri.pensionlottery.presentation.lotto.MainActivity
import com.neoguri.pensionlottery.presentation.lotto.PensionLotteryViewModel
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
 * Use the [QRCodeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class QRCodeFragment : BaseFragment(), View.OnClickListener, DefaultLifecycleObserver {

    private var param1: String? = null
    private var param2: String? = null

    override fun onResume(owner: LifecycleOwner) {
        barcodeView!!.resume()
        lastText = ""
    }

    override fun onPause(owner: LifecycleOwner) {
        barcodeView!!.pause()
        lastText = ""
    }

    override fun onDestroy(owner: LifecycleOwner) {
    }

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
            R.id.flash_on_off -> {
                if (!mTorchFlag) {
                    barcodeView!!.setTorchOn()
                    mBinding.includeToolbar.flashImage.setImageResource(R.drawable.flash_off)
                    LottoColorUtil.initLottoColorSetting(
                        requireActivity(),
                        mBinding.includeToolbar.flashImage
                    )
                    mTorchFlag = true
                } else if (mTorchFlag) {
                    barcodeView!!.setTorchOff()
                    mBinding.includeToolbar.flashImage.setImageResource(R.drawable.flash_on)
                    LottoColorUtil.initLottoColorSetting(
                        requireActivity(),
                        mBinding.includeToolbar.flashImage
                    )
                    mTorchFlag = false
                }
            }
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (result.text == null || result.text == lastText) {
                // Prevent duplicate scans
                return
            }

            lastText = result.text

            beepManager!!.playBeepSoundAndVibrate()

            qrCroling(result.text)

        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }

    private var _binding: FragmentQRCodeBinding? = null
    private val mBinding get() = _binding!!

    private var barcodeView: DecoratedBarcodeView? = null
    private var beepManager: BeepManager? = null
    private var lastText: String? = null

    private var mTorchFlag: Boolean = false

    private var mLottoItemList = ArrayList<PensionLotteryData>()

    private var isNotDrawn = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentQRCodeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment QRCodeFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            QRCodeFragment().apply {
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

        Constant.QRCODERESULT = "0"

        mBinding.includeToolbar.toolbarText.text =
            resources.getString(R.string.main_qr_code_enter_insert_winnings)
        setBack(mBinding.includeToolbar.backLayout)
        setDontTouchBtn(mBinding.idMainDontTouch)

        LottoColorUtil.initLottoColorSetting(requireActivity(), mBinding.includeToolbar.backImage)
        LottoColorUtil.initLottoColorSetting(requireActivity(), mBinding.includeToolbar.flashImage)

        barcodeView = mBinding.barcodeScanner

        mBinding.includeToolbar.flashOnOff.visibility = View.VISIBLE

        mBinding.includeToolbar.flashOnOff.setOnClickListener(this)

        val intent = requireActivity().intent

        beepManager = BeepManager(requireActivity())
        beepManager!!.isBeepEnabled = false
        beepManager!!.isVibrateEnabled = true

        val formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39)
        barcodeView!!.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcodeView!!.initializeFromIntent(intent)

        viewModel.mRJLottoNum.observe(viewLifecycleOwner) { lottoNums ->
            // Update the cached copy of the words in the adapter.
            lottoNums?.let {
                mLottoItemList = it
                barcodeView!!.decodeContinuous(callback)
            }
        }

        viewModel.qrStartCheck.observe(viewLifecycleOwner) { qrStartCheck ->
            // Update the cached copy of the words in the adapter.
            qrStartCheck?.let {
                if (qrStartCheck) {
                    barcodeView!!.resume()
                    lastText = ""
                } else {
                    barcodeView!!.pause()
                }
            }
        }

    }

    private fun qrCroling(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val urlReName: String = urlRedirect(url)

            val callback: (Element, Element, Boolean) -> Unit =
                { head: Element, body: Element, check: Boolean ->
                    if (check) {
                        isNotDrawn = false
                        checkResult(qrCompare(body, urlReName))
                    }
                }

            JsoupRequest(requireActivity()).crawl(urlReName, callback)
        }
    }

    private fun urlRedirect(urlString: String): String {

        val split = urlString.split("\\?v=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        return if (urlString.contains("?v=")) {
            URLs.QR_LOTTO_ADDRESS + split[1]
        } else {
            urlString
        }

    }

    private fun isNumeric(s: String): Boolean {

        return try {
            java.lang.Double.parseDouble(s)
            true
        } catch (e: NumberFormatException) {
            false
        }

    }

    private fun checkResult(qrCompare: ArrayList<QrLottoNum>) {

        when {
            Constant.QRCODERESULT == "1" -> {
                Toast.makeText(
                    requireActivity(),
                    resources.getString(R.string.qrcode_check),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            Constant.QRCODERESULT == "2" -> {
                Toast.makeText(
                    requireActivity(),
                    resources.getString(R.string.lotto_qrcode_check),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            Constant.QRCODERESULT == "3" -> {
                Toast.makeText(
                    requireActivity(),
                    resources.getString(R.string.network_check),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            Constant.QRCODERESULT == "10" -> {
                if (Constant.QRPENSIONLOTTERY != "") {
                    val intent = Intent(Intent.ACTION_VIEW)
                    val uri = Uri.parse(Constant.QRPENSIONLOTTERY)
                    if (PensionLottery.APP_MODE == PensionLottery.APP_MODE_DEBUG) {
                        intent.setPackage("com.android.chrome")
                    }
                    intent.data = uri
                    startActivity(intent)
                }
                return
            }
            qrCompare.size == 0 -> {
                Toast.makeText(
                    requireActivity(),
                    resources.getString(R.string.main_qr_code_reroad),
                    Toast.LENGTH_SHORT
                ).show()
                lastText = ""
                return
            }
            else -> {
                barcodeView!!.pause()
                val fragment: Fragment = QRMyNumFragment.newInstance(isNotDrawn)
                viewModel.myQRLottoNum(qrCompare)
                (activity as MainActivity?)!!.startLongHamberger(fragment, "QRMyNumFragment")
            }
        }
    }

    private fun qrCompare(body: Element, urlReName: String): ArrayList<QrLottoNum> {
        val lottoInfo = ArrayList<QrLottoNum>()

        val h3 = ArrayList<String>() // 로또인지 연금복권인지
        val h3List = ArrayList<String>() // 몇회인지
        var titleNum = "" // title_num
        val lottoNumArray = arrayOf("", "", "", "", "", "")

        if (isNumeric(urlReName)) { //숫자면 걍 return 시킴
            Constant.QRCODERESULT = "1"
            return lottoInfo
        }

        if (!urlReName.contains("lott")) {
            Constant.QRCODERESULT = "2"
            return lottoInfo
        }

        try {

            val systemCheckingBody = body.select("ul.win720_lottery_list") //필요한 녀석만 꼬집어서 지정

            for (elem in systemCheckingBody) {

                val span = elem.select("div.win720_num")

                val lottoNumber = span[0].text().replace("조", "").split(" ".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()

                for (i in lottoNumber.indices) {

                    if (i == 0) {
                        titleNum = lottoNumber[i]
                    } else {
                        lottoNumArray[i - 1] = lottoNumber[i]
                    }

                }

            }

            val mElementDataSize = body.select("div.contents") //필요한 녀석만 꼬집어서 지정

            for (elem in mElementDataSize) { //이렇게 요긴한 기능이
                h3.add(elem.select("h3").text()) //h3 안에 있는 span인 key_clr1을 가져오기
                h3List.add(elem.select("h3 span.key_clr1").text()) //h3 안에 있는 span인 key_clr1을 가져오기
            }

            if (h3[0].contains("로또")) {
                Constant.QRCODERESULT = "10"
                Constant.QRPENSIONLOTTERY = urlReName
                return lottoInfo
            }

            val cap = body.select("p.cap") //필요한 녀석만 꼬집어서 지정

            cap.forEach {
                if(it.select("strong").text().trim() == "추첨 전입니다."){
                    isNotDrawn = true
                }
            }

            val date: Date? = SimpleDateFormat("yyyy년 MM월 dd일 추첨", Locale.KOREA).parse(
                mElementDataSize.select("span.date").text()
            ) //String to date

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA) //new format

            val dateNewFormat: String = sdf.format(date!!) //result

            lottoInfo.add(
                lottoArraySet(
                    h3List[0],
                    LottoUtil.nowDate(),
                    LottoUtil.nowTime(),
                    titleNum,
                    lottoNumArray,
                    dateNewFormat,
                    "N"
                )
            )

            Constant.QRCODERESULT = "0"
            return lottoInfo

        } catch (e: IOException) {
            LogUtil.d(e.printStackTrace().toString())
            Constant.QRCODERESULT = "3"
            return lottoInfo
        }

    }

    private fun lottoArraySet(
        lotto_round: String,
        lotto_date: String,
        lotto_time: String,
        lotto_title_item: String,
        lotto_item: Array<String>,
        lottery_date: String,
        favorit: String
    ): QrLottoNum {
        val s0 = lotto_item[0]
        val s1 = lotto_item[1]
        val s2 = lotto_item[2]
        val s3 = lotto_item[3]
        val s4 = lotto_item[4]
        val s5 = lotto_item[5]

        val qrLottoNum = QrLottoNum()
        qrLottoNum.lotto_round = lotto_round
        qrLottoNum.lotto_date = lotto_date
        qrLottoNum.lotto_time = lotto_time
        qrLottoNum.lotto_title_item = lotto_title_item
        qrLottoNum.lotto_item = "$s0,$s1,$s2,$s3,$s4,$s5"
        qrLottoNum.lottery_date = lottery_date
        qrLottoNum.favorit = favorit
        return qrLottoNum
    }

}