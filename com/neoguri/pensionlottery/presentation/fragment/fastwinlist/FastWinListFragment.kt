package com.neoguri.pensionlottery.presentation.fragment.fastwinlist

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.neoguri.pensionlottery.R
import com.neoguri.pensionlottery.base.BaseFragment
import com.neoguri.pensionlottery.constant.Constant
import com.neoguri.pensionlottery.data.model.pensionlottery.PensionLotteryData
import com.neoguri.pensionlottery.databinding.FragmentFastWinListBinding
import com.neoguri.pensionlottery.dto.PensionLotteryItemList
import com.neoguri.pensionlottery.presentation.fragment.fastwinlist.viewpageradapter.FastWinListPagerAdapter
import com.neoguri.pensionlottery.presentation.fragment.fastwinlist.viewpageradapter.pagerfragment.FastWinBonusFragment
import com.neoguri.pensionlottery.presentation.fragment.fastwinlist.viewpageradapter.pagerfragment.FastWinFirstFragment
import com.neoguri.pensionlottery.presentation.lotto.PensionLotteryViewModel
import com.neoguri.pensionlottery.spineradapter.AutoSpinnerAdapter
import com.neoguri.pensionlottery.util.LottoColorUtil
import java.util.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FastWinListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FastWinListFragment : BaseFragment(), View.OnClickListener, TextView.OnEditorActionListener,
    LifecycleObserver {

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

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (v?.id == R.id.fast_win_list_choice_edittext && actionId == EditorInfo.IME_ACTION_DONE) { // 뷰의 id를 식별, 키보드의 완료 키 입력 검출
            singleItemSelect()
        }
        return false
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.past_win_image -> {
                mBinding.pastWinListSpinner.performClick()
            }
            R.id.fast_win_list_search_buttton -> {
                singleItemSelect()
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(mBinding.fastWinListChoiceEdittext.windowToken, 0)
            }
            R.id.fast_win_list_choice_edittext -> {
                mBinding.fastWinListChoiceEdittext.isFocusableInTouchMode = true
                mBinding.fastWinListChoiceEdittext.requestFocus()
            }

            R.id.r_ws_touch_first -> mBinding.winnigStoreViewpager.currentItem = 0
            R.id.r_ws_touch_bonus -> mBinding.winnigStoreViewpager.currentItem = 1
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var _binding: FragmentFastWinListBinding? = null
    private val mBinding get() = _binding!!

    private var isGetItem = false
    private var isWinnigStoreFirst = false
    private var isWinnigStoreBonus = false

    private var mAscendingDescending: Array<String>? = null

    private var mList = ArrayList<PensionLotteryData>()

    private var mDynamicFirstList = ArrayList<PensionLotteryItemList.PensionLotteryItem>()
    private var mDynamicBonusList = ArrayList<PensionLotteryItemList.PensionLotteryItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFastWinListBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FastWinListFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FastWinListFragment().apply {
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

        mBinding.includeToolbar.toolbarText.text = resources.getString(R.string.was_past_win_confirm)
        setBack(mBinding.includeToolbar.backLayout)
        setDontTouchBtn(mBinding.idMainDontTouch)

        LottoColorUtil.initLottoColorSetting(
            requireActivity().applicationContext,
            mBinding.includeToolbar.backImage
        )
        LottoColorUtil.initSearchColorSetting(
            requireActivity().applicationContext,
            mBinding.searchImage1
        )
        LottoColorUtil.initSearchColorSetting(
            requireActivity().applicationContext,
            mBinding.searchImage2
        )

        mBinding.stemLayout.descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS

        val pagerAdapter = FastWinListPagerAdapter(requireActivity(), 2)
        mBinding.winnigStoreViewpager.offscreenPageLimit =
            2 // 미리 전부 읽게 하기 위한 Limit // 뷰가 보여지진 않았으니 더 밑으로는 안가나봄
        mBinding.winnigStoreViewpager.adapter = pagerAdapter
        mBinding.winnigStoreViewpager.registerOnPageChangeCallback(fastWinListViewpagerCallback)

        mAscendingDescending = resources.getStringArray(R.array.ascending_descending)

        val pastWinArrayAdapter = AutoSpinnerAdapter(
            requireActivity(),
            R.layout.spinner_auto_item,
            mAscendingDescending!!
        )

        mBinding.pastWinListSpinner.dropDownVerticalOffset =
            resources.getDimensionPixelOffset(R.dimen.dimen_40) // API LEVEL 16부터 사용
        mBinding.pastWinListSpinner.adapter = pastWinArrayAdapter

        mDynamicFirstList.clear()
        mDynamicBonusList.clear()

        viewModel.winnigStoreFirst.observe(viewLifecycleOwner, { lottoNums ->
            // Update the cached copy of the words in the adapter.
            lottoNums?.let {
                isWinnigStoreFirst = true
                isAllCheck(isGetItem, isWinnigStoreFirst, isWinnigStoreBonus)
            }
        })

        viewModel.winnigStoreBonus.observe(viewLifecycleOwner, { lottoNums ->
            // Update the cached copy of the words in the adapter.
            lottoNums?.let {
                isWinnigStoreBonus = true
                isAllCheck(isGetItem, isWinnigStoreFirst, isWinnigStoreBonus)
            }
        })

        viewModel.mRJLottoNum.observe(viewLifecycleOwner, { lottoNums ->
            // Update the cached copy of the words in the adapter.
            lottoNums?.let {
                isGetItem = true
                mList = it
                isAllCheck(isGetItem, isWinnigStoreFirst, isWinnigStoreBonus)
            }
        })

    }

    private fun isAllCheck(item: Boolean, first: Boolean, second: Boolean) {
        if (item && first && second) {
            mBinding.pastWinListSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>, view: View?,
                        position: Int, id: Long
                    ) {
                        mPrefUtil!!.setPref(Constant.PASTWINLIST, position)
                        if (position == 0) {
                            initData(ascendingOrder())
                        } else if (position == 1) {
                            initData(descendingOrder())
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
            mBinding.pastWinListSpinner.setSelection(mPrefUtil!!.getPrefInt(Constant.PASTWINLIST))
            mBinding.fastWinListChoiceEdittext.addTextChangedListener(mTextWatcher)

            onClickListenerSetting()
            if (mPrefUtil!!.getPrefInt(Constant.PASTWINLIST) == 0) {
                singleItemSelect()
            }
        }
    }

    private fun initData(array: ArrayList<PensionLotteryData>) {
        val fastWinFirstFragment = FastWinFirstFragment().getInstace()
        val fastWinBonusFragment = FastWinBonusFragment().getInstace()

        fastWinFirstFragment?.initData(array)
        fastWinBonusFragment?.initData(array)
    }

    private fun singleItemSelect() {
        if (mBinding.fastWinListChoiceEdittext.text.toString() != "") {

            val searchArray = ArrayList<PensionLotteryData>()

            searchArray.add(mList[Integer.parseInt(mBinding.fastWinListChoiceEdittext.text.toString()) - 1])

            val fastWinFirstFragment = FastWinFirstFragment().getInstace()
            val fastWinBonusFragment = FastWinBonusFragment().getInstace()

            if (mBinding.pastWinListSpinner.selectedItemPosition == 0) {
                fastWinFirstFragment?.initData(searchArray)
                fastWinBonusFragment?.initData(searchArray)
            } else if (mBinding.pastWinListSpinner.selectedItemPosition == 1) {
                fastWinFirstFragment?.initData(searchArray)
                fastWinBonusFragment?.initData(searchArray)
            }

        } else {

            if (mBinding.pastWinListSpinner.selectedItemPosition == 0) {
                initData(ascendingOrder())
            } else if (mBinding.pastWinListSpinner.selectedItemPosition == 1) {
                initData(descendingOrder())
            }

        }
    }

    private fun ascendingOrder(): ArrayList<PensionLotteryData> {
        val ascendingDescendingItem = ArrayList<PensionLotteryData>()
        for (i in mList.indices) {
            ascendingDescendingItem.add(mList[i])
        }
        return ascendingDescendingItem
    }

    private fun descendingOrder(): ArrayList<PensionLotteryData> {
        val ascendingDescendingItem = ArrayList<PensionLotteryData>()
        for (i in mList.size - 1 downTo 0) {
            ascendingDescendingItem.add(mList[i])
        }
        return ascendingDescendingItem
    }

    private fun onClickListenerSetting() {
        mBinding.pastWinImage.setOnClickListener(this)
        mBinding.fastWinListSearchButtton.setOnClickListener(this)
        mBinding.rWsTouchFirst.setOnClickListener(this)
        mBinding.rWsTouchBonus.setOnClickListener(this)

        mBinding.fastWinListChoiceEdittext.setOnClickListener(this)
        mBinding.fastWinListChoiceEdittext.setOnEditorActionListener(this)
    }

    private val fastWinListViewpagerCallback: ViewPager2.OnPageChangeCallback =
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(pos: Int) {
                super.onPageSelected(pos)
                if (pos == 0) {
                    firstSetting(true)
                    secondSetting(false)
                } else if (pos == 1) {
                    firstSetting(false)
                    secondSetting(true)
                }
            }
        }

    private fun firstSetting(flag: Boolean) {

        if (flag) {
            mBinding.viewTopOneDown.background =
                ContextCompat.getDrawable(requireActivity(), R.color.transparency)
            mBinding.tabLeftBar.background =
                ContextCompat.getDrawable(requireActivity(), R.color.transparency)
            mBinding.tabRightBar.background =
                ContextCompat.getDrawable(requireActivity(), R.color.color_background)
            mBinding.viewBottomOneDown.background =
                ContextCompat.getDrawable(requireActivity(), R.color.color_background)
            mBinding.wsFirstText.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.text_color
                )
            )
            mBinding.wsSecondText.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.color_no_text
                )
            )
        } else {
            mBinding.viewTopOneDown.background =
                ContextCompat.getDrawable(requireActivity(), R.color.color_background)
            mBinding.tabLeftBar.background =
                ContextCompat.getDrawable(requireActivity(), R.color.color_background)
            mBinding.tabRightBar.background =
                ContextCompat.getDrawable(requireActivity(), R.color.transparency)
            mBinding.viewBottomOneDown.background =
                ContextCompat.getDrawable(requireActivity(), R.color.transparency)
        }

    }

    private fun secondSetting(flag: Boolean) {

        if (flag) {
            mBinding.viewTopTwoDown.visibility = View.INVISIBLE
            mBinding.viewBottomTwoDown.visibility = View.VISIBLE
            mBinding.wsFirstText.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.color_no_text
                )
            )
            mBinding.wsSecondText.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.text_color
                )
            )
        } else {
            mBinding.viewTopTwoDown.visibility = View.VISIBLE
            mBinding.viewBottomTwoDown.visibility = View.INVISIBLE
        }

    }

    private val mTextWatcher = object : TextWatcher {
        override fun afterTextChanged(edit: Editable) {
            if (mBinding.fastWinListChoiceEdittext.text.toString() != "") {
                if (Integer.parseInt(mBinding.fastWinListChoiceEdittext.text.toString()) > mList.size) {
                    mBinding.fastWinListChoiceEdittext.setText("")
                    Toast.makeText(
                        requireActivity(),
                        resources.getString(R.string.was_past_win_list_max),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        override fun beforeTextChanged(
            s: CharSequence, start: Int, count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence, start: Int, before: Int,
            count: Int
        ) {
        }
    }

}