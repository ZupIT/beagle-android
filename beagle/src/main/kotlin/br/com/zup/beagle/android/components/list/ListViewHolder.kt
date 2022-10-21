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

package br.com.zup.beagle.android.components.list

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import br.com.zup.beagle.android.action.AsyncActionStatus
import br.com.zup.beagle.android.action.SetContextInternal
import br.com.zup.beagle.android.components.DEFAULT_INDEX_NAME
import br.com.zup.beagle.android.context.Bind
import br.com.zup.beagle.android.context.ContextComponent
import br.com.zup.beagle.android.context.ContextData
import br.com.zup.beagle.android.data.serializer.BeagleJsonSerializer
import br.com.zup.beagle.android.utils.COMPONENT_NO_ID
import br.com.zup.beagle.android.utils.getListContextData
import br.com.zup.beagle.android.utils.isInitiableComponent
import br.com.zup.beagle.android.utils.safeGet
import br.com.zup.beagle.android.utils.toAndroidId
import br.com.zup.beagle.android.widget.core.IdentifierComponent
import br.com.zup.beagle.android.widget.core.MultiChildComponent
import br.com.zup.beagle.android.widget.core.ServerDrivenComponent
import br.com.zup.beagle.android.widget.core.SingleChildComponent
import org.json.JSONObject
import java.util.LinkedList

@Suppress("LongParameterList")
internal class ListViewHolder(
    itemView: View,
    private val template: ServerDrivenComponent,
    private val serializer: BeagleJsonSerializer,
    private val listViewModels: ListViewModels,
    private val jsonTemplate: String,
    private val iteratorName: String,
    private val indexName: String = DEFAULT_INDEX_NAME,
    private val dataSource: Bind<List<Any>>
) : RecyclerView.ViewHolder(itemView) {

    private val viewsWithId = mutableMapOf<String, View>()
    private val viewsWithContext = mutableListOf<View>()
    private val viewsWithOnInit = mutableListOf<View>()
    private val directNestedRecyclers = mutableListOf<RecyclerView>()
    private val directNestedImageViews = mutableListOf<ImageView>()
    private val directNestedTextViews = mutableListOf<TextView>()
    private val contextComponents = mutableListOf<ContextData>()
    var observer: Observer<AsyncActionStatus>? = null
    private var isRecycled = false

    init {
        extractViewInfoFromHolderTemplate(template)
        extractViewInfoFromHolderItemView(itemView)
    }

    private fun extractViewInfoFromHolderTemplate(component: ServerDrivenComponent) {
        (component as? IdentifierComponent)?.let {
            component.id?.let { id ->
                if (id != COMPONENT_NO_ID) {
                    viewsWithId[id] = itemView.findViewById(id.toAndroidId())
                }
            }
        }
        if (component is SingleChildComponent) {
            extractViewInfoFromHolderTemplate(component.child)
        } else if (component is MultiChildComponent) {
            component.children?.forEach { child ->
                extractViewInfoFromHolderTemplate(child)
            }
        }
    }

    private fun extractViewInfoFromHolderItemView(view: View) {
        if (view.isInitiableComponent()) {
            viewsWithOnInit.add(view)
        }
        if (view.getListContextData() != null) {
            viewsWithContext.add(view)
        }
        if (view is ImageView) {
            directNestedImageViews.add(view)
        }
        if (view is TextView) {
            directNestedTextViews.add(view)
        }
        if (view !is ViewGroup) {
            return
        } else if (view is RecyclerView) {
            directNestedRecyclers.add(view)
            return
        }
        val count = view.childCount
        for (i in 0 until count) {
            val child = view.getChildAt(i)
            extractViewInfoFromHolderItemView(child)
        }
    }

    // For each item on the list
    fun onBind(
        parentListViewSuffix: String?,
        key: String?,
        listItem: ListItem,
        position: Int,
        recyclerId: Int
    ) {
        // Clear references to context components
        contextComponents.clear()
        // We check if its holder has been recycled and update its references
        val newTemplate = if (isRecycled) {
            serializer.deserializeComponent(jsonTemplate)
        } else {
            template
        }
        // Using a new template reference we populate the components with context
        initializeContextComponents(newTemplate)

        // Checks whether views with ids and context have been updated based on the key and updates or restore them
        if (listItem.firstTimeBinding) {
            // Generates an suffix identifier based on the parent's suffix, key and item position
            generateItemSuffix(parentListViewSuffix, key, listItem, position)
            // Since the context needs unique id references for each view, we update them here
            updateIdToEachSubView(listItem, isRecycled, position, recyclerId)
            // If the holder is being recycled
            if (isRecycled) {
                // We set the template's default contexts for each view with context
                setDefaultContextToEachContextView()
                // For each RecyclerView nested directly when recycled, we generate a new adapter
                generateAdapterToEachDirectNestedRecycler(listItem)
            } else {
                // When this item is not recycled, we simply recover your current adapters
                saveCreatedAdapterToEachDirectNestedRecycler(listItem)
            }
            // We inform to each adapters directly nested the suffix and the current recyclerId
            updateDirectNestedAdaptersInformation(listItem, recyclerId)
        } else {
            // But if that item on the list already has ids created, we retrieve them
            restoreIds(listItem)
            // Recovers adapters previously created for the RecyclerViews of this cell
            restoreAdapters(listItem)
            // We also recover the contexts of all previously created views with context
            restoreContexts()
        }
        // Finally, we updated the context of that cell effectively.
        listViewModels.contextViewModel.removeContextObserver(iteratorName)

        setContext(iteratorName, listItem)
        setContext(ContextData(id = indexName, value = position))

        if(isObservableExpression(dataSource)) {
            observeSetContextForContext(iteratorName, dataSource, position)
        }

        // Mark this position on the list as finished
        listItem.firstTimeBinding = false
    }

    private fun isObservableExpression(dataSource: Bind<List<Any>>) =
        (dataSource is Bind.Expression
                && dataSource.expressions.size == 1)
            &&
            !hasOperation(dataSource)

    private fun hasOperation(dataSource: Bind<List<Any>>) =
        ((dataSource as Bind.Expression).expressions.first().value.contains("(") &&
            (dataSource).expressions.first().value.contains(")"))

    private fun observeSetContextForContext(
        contextId: String,
        dataSource: Bind<List<Any>>,
        position: Int,
    ) {
        val dataSourceExpression = (dataSource as Bind.Expression).expressions.first().value
        val expressionContextId = dataSourceExpression.split(".").first()
        val expressionPath = if(dataSourceExpression.contains("."))
            dataSourceExpression.replace("${expressionContextId}.", "")
        else ""
        listViewModels.contextViewModel.addContextObserver(contextId) {
            postOnUi {
                listViewModels.contextViewModel.updateContext(
                    originView = itemView,
                    setContextInternal = SetContextInternal(contextId = expressionContextId, value = it.value,
                        path = "${expressionPath}[$position]")
                )
            }
        }
    }

    private fun postOnUi(r: Runnable) = Handler(Looper.getMainLooper()).post(r)

    private fun initializeContextComponents(component: ServerDrivenComponent) {
        (component as? ContextComponent)?.let {
            component.context?.let {
                contextComponents.add(it)
            }
        }
        if (component is SingleChildComponent) {
            initializeContextComponents(component.child)
        } else if (component is MultiChildComponent) {
            component.children?.forEach { child ->
                initializeContextComponents(child)
            }
        }
    }

    private fun generateItemSuffix(
        parentListViewSuffix: String?,
        key: String?,
        listItem: ListItem,
        position: Int
    ) {
        if (listItem.itemSuffix.isEmpty()) {
            val listIdByItemKey = getSuffixByItemIdAndParentId(parentListViewSuffix, key, listItem, position)
            listItem.itemSuffix = listIdByItemKey
        }
    }

    private fun getSuffixByItemIdAndParentId(
        parentListViewSuffix: String?,
        key: String?,
        listItem: ListItem,
        position: Int
    ): String {
        return if (parentListViewSuffix.isNullOrEmpty()) {
            getListIdByKey(key, listItem, position)
        } else {
            "$parentListViewSuffix:" + getListIdByKey(key, listItem, position)
        }
    }

    private fun getListIdByKey(
        key: String?,
        listItem: ListItem,
        position: Int
    ): String {
        val listId = key?.let { ((listItem.data) as JSONObject).safeGet(it) } ?: position
        return listId.toString()
    }

    private fun updateIdToEachSubView(
        listItem: ListItem,
        isRecycled: Boolean,
        position: Int,
        recyclerId: Int
    ) {
        val itemViewId = bindIdToViewModel(position, recyclerId)
        setUpdatedIdToViewAndManagers(itemView, itemViewId, listItem, isRecycled)

        viewsWithId.forEach { (id, view) ->
            val identifierViewId = listViewModels.listViewIdViewModel.getViewId(
                recyclerId, position, id, listItem.itemSuffix
            )
            setUpdatedIdToViewAndManagers(view, identifierViewId, listItem, isRecycled)
        }

        val viewsWithContextAndWithoutId = viewsWithContext.filterNot { viewsWithId.containsValue(it) }
        viewsWithContextAndWithoutId.forEach { view ->
            val subViewId = bindIdToViewModel(position, recyclerId)
            setUpdatedIdToViewAndManagers(view, subViewId, listItem, isRecycled)
        }

        val viewsWithOnInitAndWithoutIdAndContext =
            viewsWithOnInit.filterNot { viewsWithId.containsValue(it) || viewsWithContext.contains(it) }
        viewsWithOnInitAndWithoutIdAndContext.forEach { view ->
            val subViewId = bindIdToViewModel(position, recyclerId)
            setUpdatedIdToViewAndManagers(view, subViewId, listItem, isRecycled)
        }

        directNestedRecyclers
            .filterNot {
                viewsWithId.containsValue(it) ||
                    viewsWithContext.contains(it) ||
                    viewsWithOnInit.contains(it)
            }
            .forEach { innerRecyclerWithoutId ->
                val subViewId = bindIdToViewModel(position, recyclerId)
                setUpdatedIdToViewAndManagers(innerRecyclerWithoutId, subViewId, listItem, isRecycled)
            }
    }

    private fun bindIdToViewModel(position: Int, recyclerId: Int): Int {
        return listViewModels.listViewIdViewModel.getViewId(recyclerId, position)
    }

    private fun setUpdatedIdToViewAndManagers(
        view: View,
        viewId: Int,
        listItem: ListItem,
        isRecycled: Boolean
    ) {
        val viewPreviousId = view.id
        view.id = viewId
        listItem.viewIds.add(viewId)
        if (!isRecycled) {
            if (viewPreviousId == View.NO_ID) {
                listViewModels.contextViewModel.setIdToViewWithContext(view)
            } else {
                listViewModels.contextViewModel.onViewIdChanged(viewPreviousId, view.id, view)
            }
        }
    }

    private fun setDefaultContextToEachContextView() {
        viewsWithContext.forEach { view ->
            val contextsInManager = listViewModels.contextViewModel.getListContextData(view)
            val savedContext = contextComponents.firstOrNull { context ->
                view.getListContextData()?.find { it.id == context.id } != null
            }
            val contextsToUseAsDefault = contextsInManager ?: listOf(savedContext)
            contextsToUseAsDefault.filterNotNull().forEach { contextDefault ->
                listViewModels.contextViewModel.addContext(
                    view,
                    contextDefault,
                    shouldOverrideExistingContext = true
                )
            }
        }
    }

    private fun generateAdapterToEachDirectNestedRecycler(listItem: ListItem) {
        directNestedRecyclers.forEach {
            val oldAdapter = it.adapter as ListAdapter
            val updatedAdapter = oldAdapter.clone()
            it.swapAdapter(updatedAdapter, false)
            listItem.directNestedAdapters.add(updatedAdapter)
        }
    }

    private fun saveCreatedAdapterToEachDirectNestedRecycler(listItem: ListItem) {
        directNestedRecyclers.forEach {
            listItem.directNestedAdapters.add(it.adapter as ListAdapter)
        }
    }

    private fun updateDirectNestedAdaptersInformation(listItem: ListItem, recyclerId: Int) {
        listItem.directNestedAdapters.forEach {
            it.setParentAttributes(listItem.itemSuffix, recyclerId)
        }
    }

    private fun restoreIds(listItem: ListItem) {
        val temporaryViewIds: LinkedList<Int> = LinkedList(listItem.viewIds)
        temporaryViewIds.pollFirst()?.let { savedId ->
            itemView.id = savedId
        }
        viewsWithId.values.forEach { viewWithId ->
            temporaryViewIds.pollFirst()?.let { savedId ->
                viewWithId.id = savedId
            }
        }
        val viewsWithContextAndWithoutId = viewsWithContext.filterNot { viewsWithId.containsValue(it) }
        viewsWithContextAndWithoutId.forEach { viewWithContext ->
            temporaryViewIds.pollFirst()?.let { savedId ->
                viewWithContext.id = savedId
            }
        }

        val viewsWithOnInitAndWithoutIdAndContext =
            viewsWithOnInit.filterNot { viewsWithId.containsValue(it) || viewsWithContext.contains(it) }
        viewsWithOnInitAndWithoutIdAndContext.forEach { viewWithOnInit ->
            temporaryViewIds.pollFirst()?.let { savedId ->
                viewWithOnInit.id = savedId
            }
        }

        directNestedRecyclers
            .filterNot {
                viewsWithId.containsValue(it) ||
                    viewsWithContext.contains(it) ||
                    viewsWithOnInit.contains(it)
            }
            .forEach { innerRecyclerWithoutId ->
                temporaryViewIds.pollFirst()?.let { savedId ->
                    innerRecyclerWithoutId.id = savedId
                }
            }
    }

    private fun restoreAdapters(listItem: ListItem) {
        val temporaryNestedAdapters: LinkedList<ListAdapter> = LinkedList(listItem.directNestedAdapters)
        directNestedRecyclers.forEach {
            it.swapAdapter(temporaryNestedAdapters.pollFirst(), false)
        }
    }

    private fun restoreContexts() {
        viewsWithContext.forEach {
            listViewModels.contextViewModel.restoreContext(it)
        }
    }

    private fun setContext(contextData: ContextData, shouldOverrideExistingContext: Boolean = true) {
        listViewModels.contextViewModel.addContext(
            view = itemView,
            contextData = contextData,
            shouldOverrideExistingContext = shouldOverrideExistingContext
        )
    }

    private fun setContext(iteratorName: String, listItem: ListItem) {
        setContext(ContextData(id = iteratorName, value = listItem.data))
    }

    fun onViewRecycled() {
        isRecycled = true
        clearIds(itemView)
    }

    private fun clearIds(view: View) {
        view.id = View.NO_ID
        if (view is ViewGroup) {
            view.children.forEach {
                clearIds(it)
            }
        }
    }

    fun onViewAttachedToWindow() {
        directNestedTextViews.forEach {
            it.requestLayout()
        }
    }

    fun getTemplate() = template
}
