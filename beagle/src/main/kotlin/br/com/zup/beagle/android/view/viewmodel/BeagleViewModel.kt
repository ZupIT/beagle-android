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

package br.com.zup.beagle.android.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.zup.beagle.android.data.ComponentRequester
import br.com.zup.beagle.android.exception.BeagleException
import br.com.zup.beagle.android.logger.BeagleLoggerProxy
import br.com.zup.beagle.android.networking.RequestData
import br.com.zup.beagle.android.setup.BeagleConfigurator
import br.com.zup.beagle.android.utils.BeagleRetry
import br.com.zup.beagle.android.utils.CoroutineDispatchers
import br.com.zup.beagle.android.widget.core.IdentifierComponent
import br.com.zup.beagle.android.widget.core.ServerDrivenComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicReference

sealed class ViewState {
    data class Error(val throwable: Throwable, val retry: BeagleRetry) : ViewState()
    data class Loading(val value: Boolean) : ViewState()
    data class DoRender(val screenId: String?, val component: ServerDrivenComponent) : ViewState()

    object DoCancel : ViewState()
}

internal open class BeagleViewModel(
    private val beagleConfigurator: BeagleConfigurator,
    private val ioDispatcher: CoroutineDispatcher = CoroutineDispatchers.IO,
    private val componentRequester: ComponentRequester = ComponentRequester(
        beagleConfigurator = beagleConfigurator,
        viewClient = beagleConfigurator.viewClient,
        serializer = beagleConfigurator.serializer),
) : ViewModel() {

    var fetchComponent: FetchComponentLiveData? = null

    companion object {
        fun provideFactory(
            beagleConfigurator: BeagleConfigurator,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BeagleViewModel(
                    beagleConfigurator = beagleConfigurator
                ) as T
            }
        }
    }

    fun fetchComponent(requestData: RequestData, screen: ServerDrivenComponent? = null): LiveData<ViewState> {
        val fetchComponentLiveData = FetchComponentLiveData(
            requestData,
            screen,
            componentRequester,
            viewModelScope,
            ioDispatcher,
        )
        fetchComponent = fetchComponentLiveData

        return fetchComponentLiveData
    }

    fun fetchForCache(url: String) = viewModelScope.launch(ioDispatcher) {
        try {
            componentRequester.prefetchComponent(RequestData(url = url))
        } catch (exception: BeagleException) {
            BeagleLoggerProxy.warning(exception.message)
        }
    }

    fun isFetchComponent(): Boolean {
        return fetchComponent?.checkFetchComponent() ?: false
    }

    internal class FetchComponentLiveData(
        private val requestData: RequestData,
        private val screen: ServerDrivenComponent?,
        private val componentRequester: ComponentRequester,
        private val coroutineScope: CoroutineScope,
        private val ioDispatcher: CoroutineDispatcher,
    ) : LiveData<ViewState>() {

        var job: Job? = null
        private val isRenderedReference = AtomicReference(false)

        override fun onActive() {
            if (isRenderedReference.get().not()) {
                fetchComponents()
            }
        }

        private fun fetchComponents() {
            job = coroutineScope.launch(ioDispatcher) {
                val identifier = getIdentifierComponentId()
                if (requestData.url.isNotEmpty()) {
                    try {
                        setLoading(true)
                        val component = componentRequester.fetchComponent(requestData)
                        postLiveDataResponse(ViewState.DoRender(requestData.url, component))
                    } catch (exception: BeagleException) {
                        if (screen != null) {
                            postLiveDataResponse(ViewState.DoRender(identifier, screen))
                        } else {
                            postLiveDataResponse(ViewState.Error(exception) { fetchComponents() })
                        }
                    }
                } else if (screen != null) {
                    postLiveDataResponse(ViewState.DoRender(identifier, screen))
                }
            }
        }

        fun checkFetchComponent(): Boolean {
            return job?.let {
                fetchComponentIsCompleted(it)
            } ?: false
        }

        private fun fetchComponentIsCompleted(job: Job): Boolean {
            val isCompleted = !job.isCompleted
            if (isCompleted) {
                cancelFetchComponent(job)
            }
            return isCompleted
        }

        private fun cancelFetchComponent(job: Job) {
            job.cancel()
            coroutineScope.launch(ioDispatcher) {
                postLiveDataResponse(ViewState.DoCancel)
            }
        }

        private fun getIdentifierComponentId() = (screen as? IdentifierComponent)?.id

        private suspend fun postLiveDataResponse(viewState: ViewState) {
            postValue(viewState)
            setLoading(false)
            isRenderedReference.set(true)
        }

        private suspend fun setLoading(loading: Boolean) {
            withContext(coroutineScope.coroutineContext) {
                value = ViewState.Loading(loading)
            }
        }
    }
}
