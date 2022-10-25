/*
 * Copyright 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
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

package br.com.zup.beagle.android.operation.builtin
import java.lang.NumberFormatException

class TypeConverter {

    companion object {

        fun convertStringToInt(argument: String): Int? {
            if (argument.isNullOrEmpty()) return null

            var converted: Int? = null
            try {
                converted = argument.toInt()
            } catch (exception: NumberFormatException) {

            }

            return converted
        }

        fun convertDoubleToInt(argument: Double): Int {
            return argument.toInt()
        }

        fun convertStringToDouble(argument: String): Double? {
            if (argument.isNullOrEmpty()) return null

            var converted: Double? = null
            try {
                converted = argument.toDouble()
            } catch (exception: NumberFormatException) {

            }

            return converted
        }

        fun convertIntegerToDouble(argument: Int): Double {
            return argument.toDouble()
        }

        fun convertIntToString(argument: Int): String {
            return argument.toString()
        }

        fun convertDoubleToString(argument: Double): String {
            return argument.toString()
        }

    }
}