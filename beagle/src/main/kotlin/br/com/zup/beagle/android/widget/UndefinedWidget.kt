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

package br.com.zup.beagle.android.widget

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import br.com.zup.beagle.android.setup.Environment
import br.com.zup.beagle.android.view.ViewFactory
import br.com.zup.beagle.android.annotation.RegisterWidget

@RegisterWidget("undefinedWidget")
internal class UndefinedWidget : WidgetView() {

    @SuppressLint("SetTextI18n")
    override fun buildView(rootView: RootView): View {
        return if (rootView.getBeagleConfigurator().environment == Environment.DEBUG) {
            ViewFactory.makeTextView(rootView.getContext()).apply {
                text = "undefined component"
                setTextColor(Color.RED)
                setBackgroundColor(Color.YELLOW)
            }
        } else {
            ViewFactory.makeView(rootView.getContext())
        }
    }
}
