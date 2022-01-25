package com.neoguri.pensionlottery.presentation.fragment.webview

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import androidx.lifecycle.LifecycleObserver
import com.neoguri.pensionlottery.R
import com.neoguri.pensionlottery.base.BaseFragment
import com.neoguri.pensionlottery.databinding.FragmentWebviewBinding
import com.neoguri.pensionlottery.util.LogUtil
import com.neoguri.pensionlottery.util.LottoColorUtil

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WebviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WebviewFragment : BaseFragment(), LifecycleObserver {

    private var mWebViewName: String? = null
    private var mWebViewUrl: String? = null

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
            mWebViewName = it.getString(ARG_PARAM1)
            mWebViewUrl = it.getString(ARG_PARAM2)
        }
    }

    private var _binding: FragmentWebviewBinding? = null
    private val mBinding get() = _binding!!

    private var isGoBackFlag: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWebviewBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WebviewFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WebviewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun initLayout() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val swController = ServiceWorkerController.getInstance()
            swController.setServiceWorkerClient(object : ServiceWorkerClient() {
                override fun shouldInterceptRequest(request: WebResourceRequest): WebResourceResponse? {
                    return null
                }
            })

            swController.serviceWorkerWebSettings.allowContentAccess = true
        }

        LottoColorUtil.initLottoColorSetting(requireActivity().applicationContext, mBinding.backImage)

        setBack(mBinding.webBack)
        setDontTouchBtn(mBinding.idMainDontTouch)
        mBinding.webToolbarText.text = mWebViewName

        goURL(mWebViewUrl!!)

    }

    private fun goURL(url: String) {

        //  mWebView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);

        val webSettings = mBinding.webviewLayout.settings
        webSettings.loadsImagesAutomatically = true
        //  webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //   webSettings.setLoadWithOverviewMode(true);
        webSettings.useWideViewPort = true
        webSettings.loadsImagesAutomatically = true

        mBinding.webviewLayout.webViewClient = LottoShopWebClient() // 이걸 안해주면 새창이 뜸

        mBinding.webviewLayout.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(view: WebView?, progress: Int) {
                super.onProgressChanged(view, progress)

                if (progress < 100) {
                    mBinding.progressBar1.visibility = ProgressBar.VISIBLE
                } else if (progress == 100) {
                    mBinding.progressBar1.visibility = ProgressBar.GONE
                    Handler(Looper.getMainLooper()).postDelayed({
                        mBinding.blackCurton.visibility = View.GONE
                        mBinding.blackBar.visibility = View.VISIBLE
                    }, 500)
                }
                if (progress <= 10) {
                    mBinding.progressBar1.progress = 10
                } else if (progress > 10) {
                    mBinding.progressBar1.progress = progress
                }

            }
        }

        mBinding.webviewLayout.loadUrl(url)

    }

    private fun backPressed() {
        mBinding.webviewLayout.post {
            val list = mBinding.webviewLayout.copyBackForwardList()

            if (list.currentIndex <= 0 && !mBinding.webviewLayout.canGoBack()) {
                // 처음 들어온 페이지이거나, history가 없는경우
                activity?.onBackPressed()
            } else {
                // history가 있는 경우
                // 현재 페이지로 부터 history 수 만큼 뒷 페이지로 이동
                isGoBackFlag = true
                mBinding.webviewLayout.goBackOrForward(-list.currentIndex)

                // history 삭제
                mBinding.webviewLayout.clearHistory()
            }
        }
    }

    inner class LottoShopWebClient : WebViewClient() {

        private var flag: Boolean? = false

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            super.onReceivedSslError(view, handler, error)

            val builder = AlertDialog.Builder(view!!.context)
            builder.setMessage("보안 인증서에 문제가 있습니다. 계속 진행 하시겠습니까?")
            builder.setPositiveButton("예") { _, _ -> handler!!.proceed() }
            builder.setNegativeButton("아니오") { _, _ -> handler!!.cancel() }
            val dialog = builder.create()
            dialog.show()

        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (isGoBackFlag) {
                isGoBackFlag = true
                return false
            } else {
                return if (!flag!!) {
                    if (url!!.endsWith(".pdf")) {
                        val intent = Intent(Intent.ACTION_VIEW)
                        val uri = Uri.parse(url)
                        intent.data = uri
                        startActivity(intent)
                        true
                    } else {
                        view!!.loadUrl(url)
                        true
                    }
                } else {
                    LogUtil.d("WebviewActivity" + "     - under Loading... (SKIP...) ")
                    false
                }
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            flag = false
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            flag = true

            if (isGoBackFlag) {
                isGoBackFlag = false
                return
            }
        }

    }

}