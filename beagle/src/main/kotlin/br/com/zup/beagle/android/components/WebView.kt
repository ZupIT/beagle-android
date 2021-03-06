/*
 * Copyright 2020, 2022 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.zup.beagle.android.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import br.com.zup.beagle.android.context.Bind
import br.com.zup.beagle.android.context.expressionOrConstant
import br.com.zup.beagle.android.utils.observeBindChanges
import br.com.zup.beagle.android.view.BeagleActivity
import br.com.zup.beagle.android.view.ServerDrivenState
import br.com.zup.beagle.android.view.ViewFactory
import br.com.zup.beagle.android.widget.RootView
import br.com.zup.beagle.android.widget.WidgetView
import br.com.zup.beagle.android.annotation.RegisterWidget

/**
 * A WebView widget will define a WebView natively using the server driven information received through Beagle.
 *
 * @param url
 *              define the initial page that the WebView will load when presented .
 *              This attribute must be declared and it cannot be null.
 *
 */
@RegisterWidget("webView")
data class WebView(
    val url: Bind<String>,
) : WidgetView() {

    constructor(url: String) : this(expressionOrConstant(url))

    @SuppressLint("SetJavaScriptEnabled")
    override fun buildView(rootView: RootView): View {
        val webView = ViewFactory.makeWebView(rootView.getContext())
        webView.webViewClient = BeagleWebViewClient(rootView.getContext())
        webView.settings.javaScriptEnabled = true
        observeBindChanges(rootView, webView, url) {
            it?.let { webView.loadUrl(it) }
        }
        return webView
    }

    class BeagleWebViewClient(val context: Context) : WebViewClient() {

        override fun onPageFinished(view: WebView?, url: String?) {
            notify(loading = false)
            view?.requestLayout()
        }

        override fun onPageStarted(
            view: WebView?,
            url: String?,
            favicon: Bitmap?,
        ) {
            notify(loading = true)
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?,
        ) {
            val throwable = Error("$error")
            notify(state = ServerDrivenState.WebViewError(throwable) { view?.reload() })
        }

        private fun notify(loading: Boolean) {
            if (loading) {
                notify(state = ServerDrivenState.Started)
            } else {
                notify(state = ServerDrivenState.Finished)
            }
        }

        private fun notify(state: ServerDrivenState) {
            (context as? BeagleActivity)?.onServerDrivenContainerStateChanged(state)
        }
    }

}
