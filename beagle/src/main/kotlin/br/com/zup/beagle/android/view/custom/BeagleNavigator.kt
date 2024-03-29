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

package br.com.zup.beagle.android.view.custom

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import br.com.zup.beagle.android.action.NavigationContext
import br.com.zup.beagle.android.action.Route
import br.com.zup.beagle.android.logger.BeagleLoggerProxy
import br.com.zup.beagle.android.networking.HttpAdditionalData
import br.com.zup.beagle.android.networking.HttpMethod
import br.com.zup.beagle.android.networking.RequestData
import br.com.zup.beagle.android.networking.urlbuilder.UrlBuilderFactory
import br.com.zup.beagle.android.setup.BeagleConfigurator
import br.com.zup.beagle.android.view.BeagleActivity
import br.com.zup.beagle.android.view.BeagleFragment
import br.com.zup.beagle.android.widget.RootView

internal object BeagleNavigator {

    fun openExternalURL(context: Context, url: String) {
        try {
            val webPage: Uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, webPage)
            context.startActivity(intent)
        } catch (e: Exception) {
            BeagleLoggerProxy.error(e.toString())
        }
    }

    fun openNativeRoute(
        rootView: RootView,
        route: String,
        data: Map<String, String>?,
        shouldResetApplication: Boolean,
    ) {
        rootView.getBeagleConfigurator().deepLinkHandler?.getDeepLinkIntent(
            rootView, route, data, shouldResetApplication)?.let {
            rootView.getContext().startActivity(it)
        }
    }

    fun popStack(rootView: RootView, navigationContext: NavigationContext?) {
        val context = rootView.getContext()
        if (context is AppCompatActivity) {
            setContextInPreviousActivity(rootView.getContext()
                as AppCompatActivity, navigationContext, rootView.getBeagleConfigurator())
            context.finish()
        }
    }

    fun pushView(rootView: RootView, route: Route, navigationContext: NavigationContext?) {
        if (rootView.getContext() is BeagleActivity) {
            when (route) {
                is Route.Remote -> (rootView.getContext() as BeagleActivity).navigateTo(
                    createRequestData(route),
                    route.fallback,
                    navigationContext
                )
                is Route.Local -> (rootView.getContext() as BeagleActivity).navigateTo(
                    RequestData(url = ""),
                    route.screen,
                    navigationContext
                )
            }
        } else {
            rootView.getContext().startActivity(generateIntent(rootView, route, null, navigationContext))
        }
    }

    fun popView(context: Context, navigationContext: NavigationContext? = null) {
        val f = (context as? FragmentActivity)?.supportFragmentManager?.fragments?.lastOrNull {
            it is DialogFragment
        } as DialogFragment?

        if (f != null) {
            f.dismiss()
        } else if (context is AppCompatActivity) {
            if (context.supportFragmentManager.backStackEntryCount <= 1) {
                setContextInPreviousActivity(context, navigationContext, (context as BeagleActivity).beagleConfigurator)
                context.onBackPressed()
                return
            }
            context.onBackPressed()
            updateContext(context, navigationContext)
        }
    }

    private fun setContextInPreviousActivity(context: AppCompatActivity,
                                             navigationContext: NavigationContext?,
                                             beagleConfigurator: BeagleConfigurator) {
        navigationContext?.let {
            context.setResult(Activity.RESULT_OK, Intent().putExtras(BeagleActivity.bundleOf(navigationContext,
                beagleConfigurator)))
        }
    }

    fun popToView(rootView: RootView, route: String, navigationContext: NavigationContext?) {
        val context = rootView.getContext()
        if (context is AppCompatActivity) {
            /**
             * The popBackStackImmediate it's important to update [ContextData] because they get last fragment after pop
             * and function popBackStack will execute in the next event loop cycle because this has a better performance
             * but to this case I need to use this function
             */
            context.supportFragmentManager.popBackStackImmediate(getFragmentName(rootView,route, context,
                rootView.getBeagleConfigurator().baseUrl), 0)
            updateContext(context, navigationContext)
        }
    }

    private fun updateContext(context: AppCompatActivity, navigationContext: NavigationContext?) {
        val fragment = context.supportFragmentManager.fragments.lastOrNull()
        if (fragment is BeagleFragment && navigationContext != null) {
            fragment.updateNavigationContext(navigationContext)
        }
    }

    private fun getFragmentName(rootView: RootView,
                                route: String,
                                context: AppCompatActivity,
                                baseUrl: String): String {
        var fragmentName = route
        val urlBuilder = UrlBuilderFactory().make(rootView.getBeagleConfigurator())
        val routeFormatted = urlBuilder.format(baseUrl, route)
        for (index in 0 until context.supportFragmentManager.backStackEntryCount) {
            val backStackEntryName = context.supportFragmentManager.getBackStackEntryAt(index).name
            var nameFormatted: String? = null
            backStackEntryName?.let {
                nameFormatted = urlBuilder.format(baseUrl, it)
            }
            if (nameFormatted != null && nameFormatted == routeFormatted) {
                fragmentName = backStackEntryName as String
                break
            }
        }
        return fragmentName
    }

    fun pushStack(
        rootView: RootView,
        route: Route,
        controllerName: String?,
        navigationContext: NavigationContext?,
    ) {
        val context = rootView.getContext()
        if (context is BeagleActivity) {
            context.nextActivity.launch(generateIntent(rootView, route, controllerName, navigationContext))
        } else {
            context.startActivity(generateIntent(rootView, route, controllerName, navigationContext))
        }
    }

    fun resetApplication(
        rootView: RootView,
        route: Route,
        controllerName: String?,
        navigationContext: NavigationContext?,
    ) {
        val context = rootView.getContext()
        context.startActivity(generateIntent(rootView, route, controllerName, navigationContext).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    fun resetStack(
        rootView: RootView,
        route: Route,
        controllerName: String?,
        navigationContext: NavigationContext?,
    ) {
        popStack(rootView, null)
        pushStack(rootView, route, controllerName, navigationContext)
    }

    private fun generateIntent(
        rootView: RootView, route: Route,
        controllerName: String?,
        navigationContext: NavigationContext?,
    ): Intent {
        val context = rootView.getContext()
        val bundle = when (route) {
            is Route.Remote -> {
                if (route.fallback != null) {
                    BeagleActivity.bundleOf(
                        createRequestData(route),
                        route.fallback,
                        navigationContext,
                        rootView.getBeagleConfigurator()
                    )
                } else {
                    BeagleActivity.bundleOf(
                        createRequestData(route),
                        navigationContext,
                        rootView.getBeagleConfigurator()
                    )
                }
            }
            is Route.Local -> BeagleActivity.bundleOf(route.screen, navigationContext, rootView.getBeagleConfigurator())
        }

        val activityClass = rootView.getBeagleConfigurator().controllerReference?.classFor(controllerName)

        return Intent(context, activityClass).apply {
            putExtras(bundle)
        }
    }

    private fun createRequestData(route: Route.Remote): RequestData {
        val httpAdditionalData = HttpAdditionalData(
            body = route.httpAdditionalData?.body,
            method = route.httpAdditionalData?.method ?: HttpMethod.GET,
            headers = route.httpAdditionalData?.headers ?: hashMapOf()
        )

        return RequestData(
            url = route.url.value as String,
            httpAdditionalData = httpAdditionalData,
        )
    }
}
