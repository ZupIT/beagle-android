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

@file:Suppress("UNCHECKED_CAST")

package br.com.zup.beagle.android.context

import br.com.zup.beagle.android.context.tokenizer.ExpressionToken
import br.com.zup.beagle.android.context.tokenizer.TokenParser
import br.com.zup.beagle.android.utils.BeagleRegex
import br.com.zup.beagle.android.utils.getExpressions
import br.com.zup.beagle.android.widget.core.BeagleJson
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

@BeagleJson
sealed class Bind<T> {
    abstract val type: Type
    abstract val value: Any

    @BeagleJson
    data class Expression<T>(
        val expressions: List<ExpressionToken>,
        override val value: String,
        override val type: Type,
    ) : Bind<T>()

    @BeagleJson
    data class Value<T : Any> constructor(
        override val value: T
    ) : Bind<T>() {
        override val type: Type = value.javaClass
    }

}

internal inline fun <reified T : Any> expressionOrConstant(text: String): Bind<T> =
    if (text.hasExpression()) expressionOf(text) else constant(text) as Bind<T>

internal fun expressionOrValueOfNullable(text: String?): Bind<String>? =
    if (text?.hasExpression() == true) expressionOf(text) else constantOfNullable(text)

inline fun <reified T> expressionOf(expressionText: String): Bind.Expression<T> {
    val tokenParser = TokenParser()
    val expressionTokens = expressionText.getExpressions().map { expression ->
        tokenParser.parse(expression)
    }


    @OptIn(ExperimentalStdlibApi::class)
    var javaType: Type = typeOf<T>().javaType

/*
    *  Moshi always returns Java Class Types when use the function typeOf the type
    *  is some cases is kotlin Types. As most of the time this object is created by moshi
    *  need to keep equal the moshi,
    *  because of this when it is not ParameterizedType will be converted to the java class type.
    */
    if (javaType !is ParameterizedType) {
        javaType = T::class.java
    }

    return Bind.Expression(expressionTokens, expressionText, javaType)
}

inline fun <reified T : Any> constant(value: T) = Bind.Value(value)

inline fun <reified T : Any> constantOfNullable(value: T?): Bind<T>? = value?.let { constant(it) }

internal fun Any.hasExpression() = this.toString().contains(BeagleRegex.EXPRESSION_REGEX)
