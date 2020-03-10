package hexlay.movyeah.activities

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.ViewGroup
import android.webkit.*
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import hexlay.movyeah.R
import kotlinx.android.synthetic.main.activity_comments.*
import org.jetbrains.anko.toast

@SuppressLint("SetJavaScriptEnabled")
class CommentsActivity : AppCompatActivity() {

    private var id = 0
    private lateinit var commentsChromeView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        toast("Not really ready")
        finish()
        //init()
    }

    private fun init() {
        if (intent.extras == null || intent.extras!!.isEmpty) {
            onBackPressed()
        }
        id = intent.extras!!.getInt("id")
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        frame.setOnClickListener {
            onBackPressed()
        }
        loadComments()
    }

    @SuppressLint("JavascriptInterface")
    private fun loadComments() {
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(comments_view, true)
        comments_view.webViewClient = UriWebViewClient()
        comments_view.webChromeClient = UriChromeClient()
        comments_view.settings.javaScriptEnabled = true
        comments_view.settings.setAppCacheEnabled(true)
        comments_view.settings.domStorageEnabled = true
        comments_view.settings.javaScriptCanOpenWindowsAutomatically = true
        comments_view.settings.setSupportMultipleWindows(true)
        comments_view.settings.setSupportZoom(false)
        comments_view.settings.useWideViewPort = false
        comments_view.settings.builtInZoomControls = false
        comments_view.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        val setupUrl = "https://www.adjaranet.com/movies/${id}"
        comments_view.loadUrl(setupUrl)
    }

    private fun setLoading(isLoading: Boolean) {
        loading_movies.isVisible = isLoading
        comments_view.isVisible = !isLoading
    }

    private inner class UriWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return Uri.parse(url).host != "adjaranet.com"
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            setLoading(true)
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            comments_view.loadUrl("javascript:(function() { " +
                    "document.getElementsByClassName('styled__StyledHeaderWrapper-sc-14ko3df-5')[0].style.display='none'; })()")
            comments_view.loadUrl("javascript:(function() { " +
                    "document.getElementsByClassName('ContentInfo__StyledSearchWrapper-sc-1i6qre9-6')[0].style.display='none'; })()")
            comments_view.loadUrl("javascript:(function() { " +
                    "document.getElementsByClassName('styled__StyledMobileMovieTitleArea-kmnvii-14')[0].style.display='none'; })()")
            comments_view.loadUrl("javascript:(function() { " +
                    "document.getElementsByClassName('styled__StyledContentWrapper-sc-1s5nvmb-14')[0].style.display='none'; })()")
            comments_view.loadUrl("javascript:(function() { " +
                    "document.getElementsByClassName('styled__StyledMobileMovieActions-kmnvii-12')[0].style.display='none'; })()")
            comments_view.loadUrl("javascript:(function() { " +
                    "document.getElementsByClassName('styled__StyledContentWrapper-sc-1s5nvmb-1')[0].style.display='none'; })()")
            comments_view.loadUrl("javascript:(function() { " +
                    "document.getElementsByClassName('styled__StyledMovieTabs-kmnvii-2')[0].style.display='none'; })()")
            comments_view.loadUrl("javascript:(function() { " +
                    "document.getElementsByClassName('styled__StyledBanner-sc-1a0vp7y-0')[0].style.display='none'; })()")
            comments_view.loadUrl("javascript:(function() { " +
                    "document.getElementsByClassName('StyledContainer-zoruuo-0')[0].style.display='none'; })()")
            comments_view.loadUrl("javascript:(function() { " +
                    "document.getElementsByClassName('styled__StyledFooterWrapper-z3pkh4-0')[0].style.display='none'; })()")
            setLoading(false)
            if (url.contains("/plugins/close_popup.php?reload")) {
                Handler().postDelayed({
                    contentView.removeView(commentsChromeView)
                    loadComments()
                }, 500)
            }
        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            setLoading(false)
        }
    }

    private inner class UriChromeClient : WebChromeClient() {

        override fun onCreateWindow(view: WebView, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message): Boolean {
            commentsChromeView = WebView(applicationContext)
            commentsChromeView.isVerticalScrollBarEnabled = false
            commentsChromeView.isHorizontalScrollBarEnabled = false
            commentsChromeView.webViewClient = UriWebViewClient()
            commentsChromeView.webChromeClient = this
            commentsChromeView.settings.javaScriptEnabled = true
            commentsChromeView.settings.domStorageEnabled = true
            commentsChromeView.settings.setSupportZoom(false)
            commentsChromeView.settings.builtInZoomControls = false
            commentsChromeView.settings.setSupportMultipleWindows(true)
            commentsChromeView.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            contentView.addView(commentsChromeView)
            (resultMsg.obj as WebView.WebViewTransport).webView = commentsChromeView
            resultMsg.sendToTarget()
            return true
        }
    }

}
