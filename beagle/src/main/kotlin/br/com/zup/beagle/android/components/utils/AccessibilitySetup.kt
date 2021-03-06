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

package br.com.zup.beagle.android.components.utils

import android.view.View
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import br.com.zup.beagle.android.widget.core.AccessibilityComponent
import br.com.zup.beagle.android.widget.core.ServerDrivenComponent


class AccessibilitySetup {

    fun applyAccessibility(view: View, component: ServerDrivenComponent) {
        (component as? AccessibilityComponent)?.let { accessibilityComponent ->
            accessibilityComponent.accessibility?.accessible?.let { isAccessible ->
                view.applyImportantForAccessibility(isAccessible)
            }

            accessibilityComponent.accessibility?.accessibilityLabel?.let { accessibilityLabel ->
                view.contentDescription = accessibilityLabel
            }

            accessibilityComponent.accessibility?.isHeader?.let { isHeader ->

                ViewCompat.setAccessibilityDelegate(view, object : AccessibilityDelegateCompat() {
                    override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
                        super.onInitializeAccessibilityNodeInfo(host, info)
                        info.isHeading = isHeader
                    }
                })
            }
        }
    }
}

internal fun View.applyImportantForAccessibility(isAccessible: Boolean) {
    this.importantForAccessibility = if (isAccessible)
        View.IMPORTANT_FOR_ACCESSIBILITY_YES
    else
        View.IMPORTANT_FOR_ACCESSIBILITY_NO
}
