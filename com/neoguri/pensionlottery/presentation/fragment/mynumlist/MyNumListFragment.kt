package com.neoguri.pensionlottery.presentation.fragment.mynumlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.neoguri.pensionlottery.R
import com.neoguri.pensionlottery.base.BaseFragment
import com.neoguri.pensionlottery.data.model.lottomyitem.LottoMyItemList
import com.neoguri.pensionlottery.databinding.FragmentMyNumListBinding
import com.neoguri.pensionlottery.presentation.db.myLottoNum.MyLottoNum
import com.neoguri.pensionlottery.presentation.fragment.mynumlist.adapter.PastMyNumListAdapter
import com.neoguri.pensionlottery.presentation.lotto.PensionLotteryViewModel
import com.neoguri.pensionlottery.util.LogUtil
import com.neoguri.pensionlottery.util.LottoColorUtil

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyNumListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyNumListFragment : BaseFragment(), View.OnClickListener, LifecycleObserver {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.l_remove -> {
                var choiceNumCheck = false

                mMinNum = 0
                for (i in mItemB.indices) {
                    if (mItemB[i]) {
                        choiceNumCheck = true
                        mMinNum = i
                        break
                    }
                }

                if (choiceNumCheck) {
                    val builder = AlertDialog.Builder(requireActivity(), R.style.DialogThem)

                    builder.setTitle("알림").setMessage("선택하신 로또번호들이 삭제 됩니다.\n삭제 하시겠습니까?")
                        .setPositiveButton("삭제") { _, _ ->

                            dataRemove()

                        }.setNegativeButton("닫기") { _, _ -> }

                    val dialog = builder.create()

                    dialog.setOnShowListener { dialogInterface ->
                        (dialogInterface as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(ContextCompat.getColor(requireActivity(), R.color.alram_text))
                        dialogInterface.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                            ContextCompat.getColor(requireActivity(), R.color.alram_text))
                    }

                    dialog.show()
                } else {
                    Toast.makeText(requireActivity(), resources.getString(R.string.my_num_choice_num_no), Toast.LENGTH_SHORT).show()
                }
            }

            R.id.tab_check_view_click -> {
                dataAllTouch()
                itemSetting()
                countSetting()
            }

            R.id.share_layout -> {
                var choiceNumCheck = false

                for (i in mItemB.indices) {
                    if (mItemB[i]) {
                        choiceNumCheck = true
                    }
                }

                if (choiceNumCheck) {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, lottoInfo())
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                } else {
                    Toast.makeText(requireActivity(), resources.getString(R.string.my_num_choice_num_no), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private var _binding: FragmentMyNumListBinding? = null
    private val mBinding get() = _binding!!

    private var mAdapter: PastMyNumListAdapter? = null

    private var mList: ArrayList<LottoMyItemList> = ArrayList()
    private var mItemS: List<MyLottoNum> = ArrayList()
    private var mItemB: ArrayList<Boolean> = ArrayList()
    private var mAllTouch: Boolean = false
    private var mAllCount: Int = 0
    private var mMinNum: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMyNumListBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyNumListFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyNumListFragment().apply {
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

        mBinding.includeToolbar.toolbarText.text = resources.getString(R.string.main_left_generation_num_list)
        setBack(mBinding.includeToolbar.backLayout)
        setDontTouchBtn(mBinding.idMainDontTouch)

        LottoColorUtil.initLottoColorSetting(
            requireActivity().applicationContext,
            mBinding.includeToolbar.backImage
        )

        initClickListener()

        mAdapter = PastMyNumListAdapter(requireActivity(), R.layout.adapter_main_my_num_list)
        mAdapter!!.setHasStableIds(true)

        mBinding.mainMyNumListRecyclerView.adapter = mAdapter

        mBinding.mainMyNumListRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        mBinding.mainMyNumListRecyclerView.itemAnimator = DefaultItemAnimator()

        val animator: RecyclerView.ItemAnimator = mBinding.mainMyNumListRecyclerView.itemAnimator as DefaultItemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }

        itemClick()

        viewModel.mMyLottoNum.observe(viewLifecycleOwner, { myLottoNums ->
            // Update the cached copy of the words in the adapter.
            myLottoNums?.let {

                mAllCount = it.size
                mItemS = it

                initDB()
                countSetting()
                itemSetting()

                if (mList.size == 0) {
                    mBinding.textNoNum.visibility = View.VISIBLE
                } else {
                    mBinding.textNoNum.visibility = View.GONE
                }

                mAdapter?.notifyItemRangeChanged(mMinNum, mList.count())
            }
        })

    }

    private fun initClickListener() {
        mBinding.tabCheckViewClick.setOnClickListener(this)
        mBinding.lRemove.setOnClickListener(this)
        mBinding.shareLayout.setOnClickListener(this)
    }

    private fun lottoInfo(): String {

        var lotto = ""

        mAllCount = mItemS.size

        var count = 0

        for (i in mItemS.indices) {
            if(mItemB[i]){
                count += 1
                lotto += if(lotto == ""){
                    "[" + count + "] " + mList[i].myLottoNum.lotto_title_item + requireActivity().resources.getString(R.string.ball_title) + " "+ mList[i].myLottoNum.lotto_item.replace(",", ", ")
                } else {
                    "\n" + "[" + count + "] " + mList[i].myLottoNum.lotto_title_item + requireActivity().resources.getString(R.string.ball_title) + " " + mList[i].myLottoNum.lotto_item.replace(",", ", ")
                }
            }
        }

        return lotto
    }

    private fun itemClick() {
        mAdapter?.setItemClick(object : PastMyNumListAdapter.ItemClick {
            override fun onClick(view: View, position: Int, item: LottoMyItemList) {

                var allTouchFlag = true

                //mList[position].isCheck = !mList[position].isCheck

                if(mItemB[position]){
                    mItemB[position] = false
                    mAllCount += 1
                } else {
                    mItemB[position] = true
                    mAllCount -= 1
                }

                countSetting()

                for (i in mItemB.indices) {
                    if(!mItemB[i]){
                        allTouchFlag = false
                    }
                }

                if(allTouchFlag){
                    if (mBinding.tabCheckImage.visibility == View.GONE) {
                        mAllTouch = true
                        mBinding.tabCheckImage.visibility = View.VISIBLE
                    }
                } else {
                    if (mBinding.tabCheckImage.visibility == View.VISIBLE) {
                        mAllTouch = false
                        mBinding.tabCheckImage.visibility = View.GONE
                    }
                }

                itemSetting()

            }
        })
    }

    private fun initDB() {

        mItemB.clear()

        for (i in mItemS.indices) {
            mItemB.add(false)
        }

        itemSetting()
    }

    private fun itemSetting() {
        mList.clear()
        for (i in mItemS.indices) {
            val lottoMyItemList = LottoMyItemList()
            lottoMyItemList.myLottoNum = mItemS[i]
            lottoMyItemList.isCheck = mItemB[i]
            mList.add(lottoMyItemList)
        }

        mAdapter?.updateLottoMyItemListItems(mList)
    }

    private fun dataRemove() {
        for (i in mList.indices) {
            if (mItemB[i]) {
                viewModel.deleteMyNum(mList[i].myLottoNum)
            }
        }
    }

    private fun dataAllTouch() {
        for (i in mItemB.indices) {
            mItemB[i] = !mAllTouch
        }

        if (!mAllTouch) {
            mAllTouch = true
            mAllCount = 0
            mBinding.tabCheckImage.visibility = View.VISIBLE
            mBinding.myNumBadge.visibility = View.GONE
            mBinding.myNumBadgeText.text = mAllCount.toString()
        } else {
            mAllTouch = false
            mAllCount = mItemS.size
            mBinding.tabCheckImage.visibility = View.GONE
            mBinding.myNumBadge.visibility = View.VISIBLE
            mBinding.myNumBadgeText.text = mAllCount.toString()
        }
    }

    private fun countSetting() {
        if (mAllCount == 0) {
            mBinding.myNumBadge.visibility = View.GONE
        } else {
            mBinding.myNumBadge.visibility = View.VISIBLE
            mBinding.myNumBadgeText.text = mAllCount.toString()
        }

    }

}