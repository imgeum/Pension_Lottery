package com.neoguri.pensionlottery.presentation.fragment.fastwinlist.viewpageradapter.pagerfragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.neoguri.pensionlottery.R
import com.neoguri.pensionlottery.base.BaseFragment
import com.neoguri.pensionlottery.data.model.pensionlottery.PensionLotteryData
import com.neoguri.pensionlottery.databinding.FragmentFastWinFirstBinding
import com.neoguri.pensionlottery.dto.PensionLotteryItemList
import com.neoguri.pensionlottery.presentation.fragment.fastwinlist.viewpageradapter.pagerfragment.adapter.FastWinFirstAdapter
import com.neoguri.pensionlottery.presentation.lotto.PensionLotteryViewModel
import com.neoguri.pensionlottery.util.LogUtil


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FastWinFirstFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FastWinFirstFragment : BaseFragment(), LifecycleObserver {

    private var param1: String? = null
    private var param2: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycle.addObserver(this)
    }

    override fun onDetach() {
        super.onDetach()
        mFastWinFirstFragment = null
        lifecycle.removeObserver(this)
    }

    fun getInstace(): FastWinFirstFragment? {
        return mFastWinFirstFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFastWinFirstFragment = this
        viewModel = ViewModelProvider(requireActivity()).get(PensionLotteryViewModel::class.java)
        viewModel.winnigStoreFirstSuccess(true)
        initLayout()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var _binding: FragmentFastWinFirstBinding? = null
    private val mBinding get() = _binding!!

    private lateinit var viewModel: PensionLotteryViewModel

    private var mAdapter: FastWinFirstAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFastWinFirstBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FastWinFirstFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FastWinFirstFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        private var mFastWinFirstFragment: FastWinFirstFragment? = null
    }

    private fun initLayout() {
        mAdapter = FastWinFirstAdapter(requireActivity(), R.layout.recyclerview_first_list)
        mBinding.pastFsRecyclerView.adapter = mAdapter

        mBinding.pastFsRecyclerView.layoutManager = LinearLayoutManager(activity)
        mBinding.pastFsRecyclerView.itemAnimator = DefaultItemAnimator()

        val animator: RecyclerView.ItemAnimator = mBinding.pastFsRecyclerView.itemAnimator as DefaultItemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }

    }

    fun initData(
        arrayList: ArrayList<PensionLotteryData>
    ) {
        mAdapter?.updateBlockNumListItems(arrayList)
        mBinding.pastFsRecyclerView.post { mBinding.pastFsRecyclerView.scrollToPosition(0) }

    }

}