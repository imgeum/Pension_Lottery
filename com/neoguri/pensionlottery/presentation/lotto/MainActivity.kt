package com.neoguri.pensionlottery.presentation.lotto

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.neoguri.pensionlottery.BuildConfig
import com.neoguri.pensionlottery.PensionLottery
import com.neoguri.pensionlottery.R
import com.neoguri.pensionlottery.base.BaseActivity
import com.neoguri.pensionlottery.constant.Constant
import com.neoguri.pensionlottery.constant.URLs
import com.neoguri.pensionlottery.data.model.pensionlottery.PensionLotteryData
import com.neoguri.pensionlottery.databinding.ActivityMainBinding
import com.neoguri.pensionlottery.presentation.db.allLottoNum.AllLottoNum
import com.neoguri.pensionlottery.presentation.db.myLottoNum.MyLottoNum
import com.neoguri.pensionlottery.presentation.fragment.admin.SecretFragment
import com.neoguri.pensionlottery.presentation.fragment.autogetnum.AutoLottoNumFragment
import com.neoguri.pensionlottery.presentation.fragment.fastwinlist.FastWinListFragment
import com.neoguri.pensionlottery.presentation.fragment.mynumchoice.MyNumChoiceFragment
import com.neoguri.pensionlottery.presentation.fragment.mynumlist.MyNumListFragment
import com.neoguri.pensionlottery.presentation.fragment.qrcode.QRCodeFragment
import com.neoguri.pensionlottery.presentation.fragment.webview.WebviewFragment
import com.neoguri.pensionlottery.presentation.fragment.winningdetails.WinningDetailsFragment
import com.neoguri.pensionlottery.presentation.lotto.adapter.PastMyNumAdapter
import com.neoguri.pensionlottery.util.*
import com.neoguri.pensionlottery.view.CustomCardView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class MainActivity : BaseActivity(), View.OnClickListener, View.OnLongClickListener {

    private lateinit var mBinding: ActivityMainBinding

    private val viewModel: PensionLotteryViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                PensionLotteryViewModel(application) as T
        }).get(PensionLotteryViewModel::class.java)
    }

    private var mPosition = 0

    private var mAnimationRotate: Animation? = null
    private lateinit var mLottoInternalData: List<AllLottoNum>
    private lateinit var mLottoPastItemList: List<MyLottoNum>
    private var mLottoAllData = ArrayList<PensionLotteryData>()

    private val mExpandTitle = ArrayList<LinearLayout>() //네비게이션 메뉴 큰 제목이 있는 레이아웃
    private val mExpandNoTitle = ArrayList<LinearLayout>() //네이게이션 메뉴 큰 제목밑에 소제목의 레이아웃
    private val mExpandImageButton = ArrayList<ImageView>() //네비게이션 역삼각형이 있는 레이아웃
    private val mExpandImageView = ArrayList<ImageView>() //네비게이션 역삼각형 레이아웃

    private var mHambergerCheck = false

    private var mSheetBehavior: BottomSheetBehavior<RelativeLayout>? = null
    private lateinit var mBackPressCloseHandler: BackPressCloseHandler

    private var mAdapter: PastMyNumAdapter? = null

    private var isFirst = false
    private var isSecond = false

    private var mQrCode = ""

    private lateinit var mAdView: AdView
    private var initialLayoutComplete = false

    private var isAllFirstCheck = false

    private val adSize: AdSize
        get() {
            val outMetrics = DisplayMetrics()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val display = display
                display?.getRealMetrics(outMetrics)
            } else {
                @Suppress("DEPRECATION")
                val display = windowManager.defaultDisplay
                @Suppress("DEPRECATION")
                display.getMetrics(outMetrics)
            }

            val density = outMetrics.density

            var adWidthPixels =
                mBinding.appBarMainActivity.contentMainActivity.adViewContainer.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.btn_nav -> {
                if (!mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mBinding.drawerLayout.openDrawer(GravityCompat.START)
                }
            }
            R.id.relativelayout_left_move -> {
                mPosition -= 1
                initDataLayout(mPosition)
                initBottom(mPosition)
            }
            R.id.relativelayout_right_move -> {
                mPosition += 1
                initDataLayout(mPosition)
                initBottom(mPosition)
            }
            R.id.layout_touch_chil_1_1 -> {
                val fragment: Fragment = WinningDetailsFragment.newInstance(mPosition)
                startHamberger(fragment, "WinningDetailsFragment")
                hambergerClose()
            }
            R.id.layout_touch_chil_2_1 -> {
                val fragment: Fragment = MyNumListFragment()
                startHamberger(fragment, "MyNumListFragment")
                hambergerClose()
            }
            R.id.layout_touch_chil_2_2 -> {
                val fragment: Fragment = MyNumChoiceFragment()
                startHamberger(fragment, "MyNumChoiceFragment")
                hambergerClose()
            }
            R.id.layout_touch_chil_2_3 -> {
                val fragment: Fragment = AutoLottoNumFragment()
                startHamberger(fragment, "AutoLottoNumFragment")
                hambergerClose()
            }
            R.id.layout_touch_chil_3_1 -> {
                val fragment: Fragment = FastWinListFragment()
                startHamberger(fragment, "FastWinListFragment")
                hambergerClose()
            }
            R.id.layout_touch_chil_4_1 -> {
                if (Build.VERSION.SDK_INT >= 23) {
                    mQrCode = resources.getString(R.string.main_qr_code_enter_insert_winnings)
                    PermissionCheck().isCheck(
                        this,
                        this,
                        "android.permission.CAMERA",
                        Constant.PERMISSTION_CAMERA
                    )
                } else {
                    val fragment: Fragment = QRCodeFragment()
                    startLongHamberger(fragment, "QRCodeFragment")
                    hambergerClose()
                }
            }
            R.id.open_source_confirm -> {
                if (mNightMode) {
                    val fragment: Fragment = WebviewFragment.newInstance(
                        resources.getString(R.string.main_open_souce_licence),
                        URLs.PREFIX + URLs.OPENSOUCE_LICENCE_NIGHT
                    )
                    startHamberger(fragment, "WebviewFragment")
                    hambergerClose()
                } else {
                    val fragment: Fragment = WebviewFragment.newInstance(
                        resources.getString(R.string.main_open_souce_licence),
                        URLs.PREFIX + URLs.OPENSOUCE_LICENCE
                    )
                    startHamberger(fragment, "WebviewFragment")
                    hambergerClose()
                }
            }
            R.id.open_admin_confirm -> {
                val fragment: Fragment = SecretFragment()
                startHamberger(fragment, "SecretFragment")
                hambergerClose()
            }
            R.id.layout_touch_1, R.id.layout_touch_2, R.id.layout_touch_3, R.id.layout_touch_4 -> {
                expandCollapse(v)
            }

            R.id.btn_represh -> {
                startRefresh()
            }
        }

    }

    override fun onLongClick(v: View?): Boolean {
        when (v?.id) {
            R.id.relativelayout_left_move -> {
                mPosition = 0
                initDataLayout(mPosition)
                initBottom(mPosition)
            }
            R.id.relativelayout_right_move -> {
                mPosition = mLottoAllData.size - 1
                initDataLayout(mPosition)
                initBottom(mPosition)
            }
        }
        return true
    }

    private fun expandCollapse(v: View) {

        val animationIp =
            AnimationUtils.loadAnimation(this, R.anim.rotate_up) //네비게이션 역삼각형이 위로 올라가는 애니메이션
        val animationDown =
            AnimationUtils.loadAnimation(this, R.anim.rotate_down) //네비게이션 역삼각형이 아래로 내려가는 애니메이션

        for (i in mExpandTitle.indices) {
            if (v.id == mExpandTitle[i].id) {
                if (mExpandNoTitle[i].visibility == View.GONE) {
                    LottoUtil.expandMain(
                        this@MainActivity,
                        mExpandNoTitle[i],
                        mBinding.navigationManu.mainScrollview
                    )
                    mExpandImageView[i].startAnimation(animationIp)
                } else {
                    LottoUtil.collapseMain(mExpandNoTitle[i])
                    animationDown!!.duration =
                        (mExpandNoTitle[i].measuredHeight / mExpandNoTitle[i].context.resources.displayMetrics.density).toInt()
                            .toLong()
                    mExpandImageView[i].startAnimation(animationDown)
                }
            }
        }

    }

    private fun startRefresh() {
        mAnimationRotate = AnimationUtils.loadAnimation(this, R.anim.main_search_rotate)
        mAnimationRotate?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                progressOpen()
                viewModel.roomGetLottoSum(mLottoInternalData)
            }

            override fun onAnimationRepeat(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {

            }
        })

        mAnimationRotate?.duration = Constant.MAIN_REFRESH.toLong() //애니메이션 동작시간 MAIN_REFRESH초
        mBinding.appBarMainActivity.btnRepresh.startAnimation(mAnimationRotate)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {

        var isPermissionFlag = true

        if (requestCode == Constant.PERMISSTION_CAMERA) {
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    if (permissions[i] == Manifest.permission.CAMERA
                    ) {
                        isPermissionFlag = false
                    }
                }
            }

            if (isPermissionFlag) {
                val fragment: Fragment = QRCodeFragment()
                startLongHamberger(fragment, "QRCodeFragment")
                hambergerClose()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    resources.getString(R.string.main_start_qrcode),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        Constant.stackView = true
        setTitle(resources.getString(R.string.app_name))

        try {
            val appVersion = packageManager.getPackageInfo(packageName, 0).versionName
            mBinding.navigationManu.appVersion.text = appVersion

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        if (PensionLottery.APP_MODE == PensionLottery.APP_MODE_DEBUG) {
            mBinding.navigationManu.openAdminLayout.visibility = View.VISIBLE
        } else {
            mBinding.navigationManu.openAdminLayout.visibility = View.GONE
        }

        progressOpen()

        initLayout()
        initColorSetting()
        initArray()
        initListener()

        val customCardView: CustomCardView = findViewById(R.id.hidden_layout)
        customCardView.setBinding(mBinding)

        mAdapter = PastMyNumAdapter(
            this,
            R.layout.adapter_main_my_num_serarch
        )
        mAdapter!!.setHasStableIds(true)

        mBinding.appBarMainActivity.contentMainActivity.contentMainBottom.mainMyNumRecyclerView.adapter =
            mAdapter

        mBinding.appBarMainActivity.contentMainActivity.contentMainBottom.mainMyNumRecyclerView.layoutManager =
            LinearLayoutManager(this)
        mBinding.appBarMainActivity.contentMainActivity.contentMainBottom.mainMyNumRecyclerView.itemAnimator =
            DefaultItemAnimator()

        viewModel.mLiveRoomLottoNum.observe(this) { lottoNums ->
            // Update the cached copy of the words in the adapter.
            lottoNums?.let {
                mLottoInternalData = it
                viewModel.roomGetLottoSum(it)
            }
        }

        viewModel.mRJLottoNum.observe(this) { lottoNums ->
            // Update the cached copy of the words in the adapter.
            lottoNums?.let {
                CoroutineScope(Dispatchers.Main).launch {
                    if (null == it) {
                        dialogShow()
                    } else {
                        isFirst = true
                        if (mAnimationRotate?.hasStarted() == true) {
                            mAnimationRotate?.cancel()
                        }
                        mLottoAllData = it
                        mPosition = it.size - 1
                        // Index: 89, Size: 40
                        initDataLayout(mPosition)
                        isAllCheck(isFirst, isSecond)
                    }
                    progressClose()
                }
            }
        }

        viewModel.mMyLottoNum.observe(this) { myLottoNums ->
            // Update the cached copy of the words in the adapter.
            myLottoNums?.let {
                CoroutineScope(Dispatchers.Main).launch {
                    isSecond = true
                    mLottoPastItemList = it
                    isAllCheck(isFirst, isSecond)
                }
            }
        }

        MobileAds.initialize(this) { }

        mAdView = AdView(this)
        mBinding.appBarMainActivity.contentMainActivity.adViewContainer.addView(mAdView)
        // Since we're loading the banner based on the adContainerView size, we need to wait until this
        // view is laid out before we can get the width.
        mBinding.appBarMainActivity.contentMainActivity.adViewContainer.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete) {
                initialLayoutComplete = true
                loadBanner()
            }
        }

    }

    private fun dialogShow() {
        val builder = AlertDialog.Builder(this, R.style.DialogThem)

        builder.setTitle("알림").setMessage("데이터를 받아오는데 실패 하였습니다.")
            .setPositiveButton("재시도") { _, _ ->
                startRefresh()
            }.setNegativeButton("닫기") { _, _ -> finish() }

        val dialog = builder.create()

        dialog.setOnShowListener { dialogInterface ->
            (dialogInterface as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.alram_text))
            dialogInterface.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.alram_text))
        }

        dialog.show()
    }

    private fun loadBanner() {
        if (PensionLottery.APP_MODE == PensionLottery.APP_MODE_DEBUG) {
            mAdView.adUnitId = BuildConfig.banner_ad_unit_id_for_test
        } else {
            mAdView.adUnitId = BuildConfig.banner_ad_unit_id
        }

        mAdView.adSize = adSize

        if (adSize.getHeightInPixels(this) > 0) {
            LottoUtil.setAdmob(
                mBinding.appBarMainActivity.contentMainActivity.adNoView,
                adSize.getHeightInPixels(this)
            )
            LottoUtil.setLayout(
                mBinding.appBarMainActivity.contentMainActivity.viewLine1,
                resources.getDimensionPixelSize(R.dimen.dimen_3)
            )
            LottoUtil.setMarginBottom(
                mBinding.navView,
                adSize.getHeightInPixels(this) + resources.getDimensionPixelSize(R.dimen.dimen_3)
            )
            LottoUtil.setMarginBottom(
                mBinding.container,
                adSize.getHeightInPixels(this) + resources.getDimensionPixelSize(R.dimen.dimen_3)
            )
        } else {
            LottoUtil.setAdmob(mBinding.appBarMainActivity.contentMainActivity.adNoView, 0)
            LottoUtil.setLayout(mBinding.appBarMainActivity.contentMainActivity.viewLine1, 0)
            LottoUtil.setMarginBottom(mBinding.navView, 0)
            LottoUtil.setMarginBottom(mBinding.container, 0)
        }

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this device."
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest)

        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdFailedToLoad(errorCode: LoadAdError) {
                super.onAdFailedToLoad(errorCode)
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }
    }

    private fun isAllCheck(first: Boolean, second: Boolean) {
        if (first && second) {
            val lottoPastItemList: ArrayList<MyLottoNum> =
                mLottoPastItemList as ArrayList<MyLottoNum>
            mAdapter?.updatePastMyNum(lottoPastItemList)
            initBottom(mPosition)
        }
    }

    private fun initBottom(position: Int) {

        mAdapter?.getLottoNum(
            mLottoAllData[position].pensionlotteryTitleNum,
            mLottoAllData[position].pensionlotteryNum,
            mLottoAllData[position].bonusNum
        )
        mAdapter?.notifyItemRangeChanged(0, mLottoPastItemList.count())

        if (mLottoPastItemList.size == 0) {
            mBinding.appBarMainActivity.contentMainActivity.contentMainDown.textNoNum.visibility =
                View.VISIBLE
            mBinding.appBarMainActivity.contentMainActivity.contentMainBottom.rlBottomSheet.visibility =
                View.GONE
            mBinding.appBarMainActivity.contentMainActivity.contentMainDown.myWinLayout.visibility =
                View.GONE
            mBinding.appBarMainActivity.contentMainActivity.contentMainDown.hiddenLayout.visibility =
                View.GONE
            mBinding.navigationManu.myNumBadge.visibility = View.GONE
        } else {
            mBinding.appBarMainActivity.contentMainActivity.contentMainDown.textNoNum.visibility =
                View.GONE
            mBinding.appBarMainActivity.contentMainActivity.contentMainBottom.rlBottomSheet.visibility =
                View.VISIBLE
            mBinding.appBarMainActivity.contentMainActivity.contentMainDown.myWinLayout.visibility =
                View.VISIBLE
            mBinding.appBarMainActivity.contentMainActivity.contentMainDown.hiddenLayout.visibility =
                View.VISIBLE
            mBinding.navigationManu.myNumBadge.visibility = View.VISIBLE
            mBinding.navigationManu.myNumBadgeText.text = mLottoPastItemList.size.toString()
        }

        mBinding.appBarMainActivity.contentMainActivity.contentMainDown.sevenImageview.setColorFilter(
            ContextCompat.getColor(this, R.color.text_color)
        )
        mBinding.appBarMainActivity.contentMainActivity.contentMainDown.sixImageview.setColorFilter(
            ContextCompat.getColor(this, R.color.text_color)
        )
        mBinding.appBarMainActivity.contentMainActivity.contentMainDown.fiveImageview.setColorFilter(
            ContextCompat.getColor(this, R.color.text_color)
        )
        mBinding.appBarMainActivity.contentMainActivity.contentMainDown.foImageview.setColorFilter(
            ContextCompat.getColor(this, R.color.text_color)
        )
        mBinding.appBarMainActivity.contentMainActivity.contentMainDown.threeImageview.setColorFilter(
            ContextCompat.getColor(this, R.color.text_color)
        )
        mBinding.appBarMainActivity.contentMainActivity.contentMainDown.twoImageview.setColorFilter(
            ContextCompat.getColor(this, R.color.text_color)
        )
        mBinding.appBarMainActivity.contentMainActivity.contentMainDown.oneImageview.setColorFilter(
            ContextCompat.getColor(this, R.color.text_color)
        )
        mBinding.appBarMainActivity.contentMainActivity.contentMainDown.bonusImageview.setColorFilter(
            ContextCompat.getColor(this, R.color.text_color)
        )

        winningCheck(mLottoPastItemList, position)
    }

    private fun winningCheck(list: List<MyLottoNum>, position: Int) {
        val firstSplit = mLottoAllData[position].pensionlotteryNum.split(",".toRegex())
            .dropLastWhile { it.isEmpty() }.toTypedArray()
        val bonusSplit =
            mLottoAllData[position].bonusNum.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()

        list.forEach {

            var rankCount = 0

            val split =
                it.lotto_item.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in 5 downTo 0) {
                if (firstSplit[i] == split[i]) {
                    rankCount += 1
                } else {
                    break
                }
            }

            if (rankCount == 6) {
                if (mLottoAllData[position].pensionlotteryTitleNum == it.lotto_title_item) {
                    rankCount += 1
                }
            }

            when (rankCount) {
                1 -> {
                    mBinding.appBarMainActivity.contentMainActivity.contentMainDown.sevenImageview.setColorFilter(
                        ContextCompat.getColor(this, R.color.color_background)
                    )
                }
                2 -> {
                    mBinding.appBarMainActivity.contentMainActivity.contentMainDown.sixImageview.setColorFilter(
                        ContextCompat.getColor(this, R.color.color_background)
                    )
                }
                3 -> {
                    mBinding.appBarMainActivity.contentMainActivity.contentMainDown.fiveImageview.setColorFilter(
                        ContextCompat.getColor(this, R.color.color_background)
                    )
                }
                4 -> {
                    mBinding.appBarMainActivity.contentMainActivity.contentMainDown.foImageview.setColorFilter(
                        ContextCompat.getColor(this, R.color.color_background)
                    )
                }
                5 -> {
                    mBinding.appBarMainActivity.contentMainActivity.contentMainDown.threeImageview.setColorFilter(
                        ContextCompat.getColor(this, R.color.color_background)
                    )
                }
                6 -> {
                    mBinding.appBarMainActivity.contentMainActivity.contentMainDown.twoImageview.setColorFilter(
                        ContextCompat.getColor(this, R.color.color_background)
                    )
                }
                7 -> {
                    mBinding.appBarMainActivity.contentMainActivity.contentMainDown.oneImageview.setColorFilter(
                        ContextCompat.getColor(this, R.color.color_background)
                    )
                }
            }

            if (rankCount < 6) {
                val result = bonusCheck(mLottoAllData[mPosition].bonusNum, it.lotto_item)

                if (result) {
                    mBinding.appBarMainActivity.contentMainActivity.contentMainDown.bonusImageview.setColorFilter(
                        ContextCompat.getColor(this, R.color.color_background)
                    )
                }
            }
        }

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

    private fun initDataLayout(position: Int) {

        val winningResults =
            mLottoAllData[position].code + resources.getString(R.string.main_winning_results)
        mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.idMainWinningResults.text =
            winningResults

        val winningResultsDay = " [" + mLottoAllData[position].date + "]"
        mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.idMainWinningResultsDay.text =
            winningResultsDay

        mainFirstSetting(mLottoAllData, position)
        mainSecondSetting(mLottoAllData, position)
        mainBonusSetting(mLottoAllData, position)

        setArrow(position)

    }

    private fun setArrow(position: Int) {
        if (mLottoAllData.size - 1 == position) {
            mBinding.appBarMainActivity.contentMainActivity.contentMainTop.relativelayoutLeftMove.visibility =
                View.VISIBLE
            mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentLeft.visibility =
                View.VISIBLE
            mBinding.appBarMainActivity.contentMainActivity.contentMainTop.relativelayoutRightMove.visibility =
                View.GONE
            mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentRight.visibility =
                View.GONE
        } else if (mLottoAllData.size > position && position > 0) {
            mBinding.appBarMainActivity.contentMainActivity.contentMainTop.relativelayoutLeftMove.visibility =
                View.VISIBLE
            mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentLeft.visibility =
                View.VISIBLE
            mBinding.appBarMainActivity.contentMainActivity.contentMainTop.relativelayoutRightMove.visibility =
                View.VISIBLE
            mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentRight.visibility =
                View.VISIBLE
        } else if (0 == position) {
            mBinding.appBarMainActivity.contentMainActivity.contentMainTop.relativelayoutLeftMove.visibility =
                View.GONE
            mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentLeft.visibility =
                View.GONE
            mBinding.appBarMainActivity.contentMainActivity.contentMainTop.relativelayoutRightMove.visibility =
                View.VISIBLE
            mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentRight.visibility =
                View.VISIBLE
        }
    }

    private fun initLayout() {
        mBackPressCloseHandler = BackPressCloseHandler(this)

        mSheetBehavior =
            BottomSheetBehavior.from(mBinding.appBarMainActivity.contentMainActivity.contentMainBottom.rlBottomSheet)

        mSheetBehavior!!.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                //안씀
            }

            override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {
                // React to dragging events
                runOnUiThread {
                    mBinding.appBarMainActivity.contentMainActivity.contentMainBottom.bottomSheetArrow.rotation =
                        slideOffset * (-Constant.MAIN_BOTTOM_ROTATE)
                }
            }
        })
    }

    private fun initColorSetting() {
        LottoColorUtil.initLottoColorSetting(
            applicationContext,
            mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentLeft
        )
        LottoColorUtil.initLottoColorSetting(
            applicationContext,
            mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentRight
        )
        LottoColorUtil.initLottoColorSetting(
            applicationContext,
            mBinding.appBarMainActivity.naviImage
        )
        LottoColorUtil.initLottoColorSetting(
            applicationContext,
            mBinding.appBarMainActivity.represhImage
        )
        LottoColorUtil.initLottoColorSetting(
            applicationContext,
            mBinding.appBarMainActivity.contentMainActivity.contentMainBottom.bottomSheetArrow
        )
        LottoColorUtil.initLeftArrowColorSetting(
            applicationContext,
            mBinding.navigationManu.childLayoutBox1Arrow
        )
        LottoColorUtil.initLeftArrowColorSetting(
            applicationContext,
            mBinding.navigationManu.childLayoutBox2Arrow
        )
        LottoColorUtil.initLeftArrowColorSetting(
            applicationContext,
            mBinding.navigationManu.childLayoutBox3Arrow
        )
        LottoColorUtil.initLeftArrowColorSetting(
            applicationContext,
            mBinding.navigationManu.childLayoutBox4Arrow
        )
    }

    fun progressOpen() {
        if (mBinding.customProgressbar.root.visibility == View.GONE) {
            mBinding.customProgressbar.root.visibility = View.VISIBLE
        }
    }

    fun progressClose() {
        if (mBinding.customProgressbar.root.visibility == View.VISIBLE) {
            mBinding.customProgressbar.root.visibility = View.GONE
        }
    }

    private fun initArray() {
        mExpandTitle.clear()
        mExpandNoTitle.clear()
        mExpandImageView.clear()
        mExpandImageButton.clear()

        mExpandTitle.add(mBinding.navigationManu.layoutTouch1)
        mExpandTitle.add(mBinding.navigationManu.layoutTouch2)
        mExpandTitle.add(mBinding.navigationManu.layoutTouch3)
        mExpandTitle.add(mBinding.navigationManu.layoutTouch4)
        mExpandNoTitle.add(mBinding.navigationManu.layoutTouchChil1)
        mExpandNoTitle.add(mBinding.navigationManu.layoutTouchChil2)
        mExpandNoTitle.add(mBinding.navigationManu.layoutTouchChil3)
        mExpandNoTitle.add(mBinding.navigationManu.layoutTouchChil4)
        mExpandImageView.add(mBinding.navigationManu.childLayoutBox1Arrow)
        mExpandImageView.add(mBinding.navigationManu.childLayoutBox2Arrow)
        mExpandImageView.add(mBinding.navigationManu.childLayoutBox3Arrow)
        mExpandImageView.add(mBinding.navigationManu.childLayoutBox4Arrow)
        mExpandImageButton.add(mBinding.navigationManu.childLayoutBox1ImageView)
        mExpandImageButton.add(mBinding.navigationManu.childLayoutBox2ImageView)
        mExpandImageButton.add(mBinding.navigationManu.childLayoutBox3ImageView)
        mExpandImageButton.add(mBinding.navigationManu.childLayoutBox4ImageView)
    }

    private fun initListener() {

        mBinding.appBarMainActivity.btnNav.setOnClickListener(this)
        mBinding.navigationManu.layoutTouch1.setOnClickListener(this)
        mBinding.navigationManu.layoutTouch2.setOnClickListener(this)
        mBinding.navigationManu.layoutTouch3.setOnClickListener(this)
        mBinding.navigationManu.layoutTouch4.setOnClickListener(this)
        mBinding.appBarMainActivity.btnRepresh.setOnClickListener(this)
        mBinding.appBarMainActivity.contentMainActivity.contentMainTop.relativelayoutLeftMove.setOnClickListener(
            this
        )
        mBinding.appBarMainActivity.contentMainActivity.contentMainTop.relativelayoutLeftMove.setOnLongClickListener(
            this
        )
        mBinding.appBarMainActivity.contentMainActivity.contentMainTop.relativelayoutRightMove.setOnClickListener(
            this
        )
        mBinding.appBarMainActivity.contentMainActivity.contentMainTop.relativelayoutRightMove.setOnLongClickListener(
            this
        )

        mBinding.navigationManu.layoutTouchChil11.setOnClickListener(this)
        mBinding.navigationManu.layoutTouchChil21.setOnClickListener(this)
        mBinding.navigationManu.layoutTouchChil22.setOnClickListener(this)
        mBinding.navigationManu.layoutTouchChil23.setOnClickListener(this)
        mBinding.navigationManu.layoutTouchChil31.setOnClickListener(this)
        mBinding.navigationManu.layoutTouchChil41.setOnClickListener(this)
        mBinding.navigationManu.openSourceConfirm.setOnClickListener(this)
        mBinding.navigationManu.openAdminConfirm.setOnClickListener(this)

    }

    private fun mainFirstSetting(
        pensionLotteryItemList: ArrayList<PensionLotteryData>,
        position: Int
    ) {
        val imageViewList = ArrayList<ImageView>()
        val imageViewTaedooriList = ArrayList<ImageView>()
        val textViewList = ArrayList<TextView>()

        imageViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.ballView.lotto1)
        imageViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.ballView.lotto2)
        imageViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.ballView.lotto3)
        imageViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.ballView.lotto4)
        imageViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.ballView.lotto5)
        imageViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.ballView.lotto6)

        imageViewTaedooriList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.ballView.lotto1Taedoori)
        imageViewTaedooriList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.ballView.lotto2Taedoori)
        imageViewTaedooriList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.ballView.lotto3Taedoori)
        imageViewTaedooriList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.ballView.lotto4Taedoori)
        imageViewTaedooriList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.ballView.lotto5Taedoori)
        imageViewTaedooriList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.ballView.lotto6Taedoori)

        textViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.ballView.lottoText1)
        textViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.ballView.lottoText2)
        textViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.ballView.lottoText3)
        textViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.ballView.lottoText4)
        textViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.ballView.lottoText5)
        textViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.ballView.lottoText6)

        LottoUtil.initLottoSetting(
            this,
            pensionLotteryItemList,
            position,
            imageViewList,
            imageViewTaedooriList,
            textViewList,
            mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.ballView.lotto7,
            mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.ballView.lotto7Taedoori,
            mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.ballView.lottoText7
        )
    }

    private fun mainSecondSetting(
        pensionLotteryItemList: ArrayList<PensionLotteryData>,
        position: Int
    ) {
        val imageViewList = ArrayList<ImageView>()
        val imageViewTaedooriList = ArrayList<ImageView>()
        val textViewList = ArrayList<TextView>()

        imageViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.secondBallView.ballViewLotto1)
        imageViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.secondBallView.ballViewLotto2)
        imageViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.secondBallView.ballViewLotto3)
        imageViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.secondBallView.ballViewLotto4)
        imageViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.secondBallView.ballViewLotto5)
        imageViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.secondBallView.ballViewLotto6)

        imageViewTaedooriList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.secondBallView.ballViewLotto1Taedoori)
        imageViewTaedooriList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.secondBallView.ballViewLotto2Taedoori)
        imageViewTaedooriList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.secondBallView.ballViewLotto3Taedoori)
        imageViewTaedooriList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.secondBallView.ballViewLotto4Taedoori)
        imageViewTaedooriList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.secondBallView.ballViewLotto5Taedoori)
        imageViewTaedooriList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.secondBallView.ballViewLotto6Taedoori)

        textViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.secondBallView.ballViewLottoText1)
        textViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.secondBallView.ballViewLottoText2)
        textViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.secondBallView.ballViewLottoText3)
        textViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.secondBallView.ballViewLottoText4)
        textViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.secondBallView.ballViewLottoText5)
        textViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.secondBallView.ballViewLottoText6)

        LottoUtil.initLottoSetting(
            this,
            pensionLotteryItemList,
            position,
            imageViewList,
            imageViewTaedooriList,
            textViewList,
            false
        )
    }

    private fun mainBonusSetting(
        pensionLotteryItemList: ArrayList<PensionLotteryData>,
        position: Int
    ) {
        val imageViewList = ArrayList<ImageView>()
        val imageViewTaedooriList = ArrayList<ImageView>()
        val textViewList = ArrayList<TextView>()

        imageViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.bonusBallView.bonusBallViewLotto1)
        imageViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.bonusBallView.bonusBallViewLotto2)
        imageViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.bonusBallView.bonusBallViewLotto3)
        imageViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.bonusBallView.bonusBallViewLotto4)
        imageViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.bonusBallView.bonusBallViewLotto5)
        imageViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.bonusBallView.bonusBallViewLotto6)

        imageViewTaedooriList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.bonusBallView.bonusBallViewLotto1Taedoori)
        imageViewTaedooriList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.bonusBallView.bonusBallViewLotto2Taedoori)
        imageViewTaedooriList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.bonusBallView.bonusBallViewLotto3Taedoori)
        imageViewTaedooriList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.bonusBallView.bonusBallViewLotto4Taedoori)
        imageViewTaedooriList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.bonusBallView.bonusBallViewLotto5Taedoori)
        imageViewTaedooriList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.bonusBallView.bonusBallViewLotto6Taedoori)

        textViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.bonusBallView.bonusBallViewLottoText1)
        textViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.bonusBallView.bonusBallViewLottoText2)
        textViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.bonusBallView.bonusBallViewLottoText3)
        textViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.bonusBallView.bonusBallViewLottoText4)
        textViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.bonusBallView.bonusBallViewLottoText5)
        textViewList.add(mBinding.appBarMainActivity.contentMainActivity.contentMainTop.fragmentMain.bonusBallView.bonusBallViewLottoText6)

        LottoUtil.initLottoSetting(
            this,
            pensionLotteryItemList,
            position,
            imageViewList,
            imageViewTaedooriList,
            textViewList,
            true
        )
    }

    fun startLongHamberger(fragment: Fragment, fragment_name: String) {
        addStackTag(fragment_name)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .add(R.id.no_long_container, fragment, fragment_name).commit()
    }

    fun startHamberger(fragment: Fragment, fragment_name: String) {
        addStackTag(fragment_name)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .add(R.id.container, fragment, fragment_name).commit()
    }

    private fun addStackTag(string: String) {
        Constant.stackTag.add(string)
    }

    private fun hambergerOpen() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun hambergerClose() {
        mHambergerCheck = true
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun String.toSpanned(): Spanned {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            return Html.fromHtml(this)
        }
    }

    override fun onBackPressed() {

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            if (Constant.stackTag.size > 0) {
                val getTag = Constant.stackTag[Constant.stackTag.size - 1]
                val f: Fragment? = supportFragmentManager.findFragmentByTag(getTag)
                if (f != null) {
                    supportFragmentManager.beginTransaction().remove(f).commit()
                    Constant.stackTag.remove(getTag)
                    //supportFragmentManager.popBackStack()
                    //f.onDestroy()
                    //f.onDetach()
                }
            } else {
                drawerLayout.closeDrawer(GravityCompat.START)
                mHambergerCheck = false
            }
        } else {
            if (mSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
                mSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            } else if (mSheetBehavior?.state == BottomSheetBehavior.STATE_COLLAPSED) {

                if (Constant.stackView) {
                    if (Constant.stackTag.size > 0) {
                        val getTag = Constant.stackTag[Constant.stackTag.size - 1]
                        val f: Fragment? = supportFragmentManager.findFragmentByTag(getTag)
                        if (f != null) {
                            supportFragmentManager.beginTransaction().remove(f).commit()
                            Constant.stackTag.remove(getTag)
                            //supportFragmentManager.popBackStack()
                            //f.onDestroy()
                            //f.onDetach()
                            if (mHambergerCheck) {
                                if (Constant.stackTag.size == 0) {
                                    mHambergerCheck = false
                                    hambergerOpen()
                                }
                            }
                        }
                    } else {
                        mBackPressCloseHandler.onBackPressed()
                    }
                }

            }
        }
    }

}