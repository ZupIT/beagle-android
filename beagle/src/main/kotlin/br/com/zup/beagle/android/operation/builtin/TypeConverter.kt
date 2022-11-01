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
import br.com.zup.beagle.android.operation.OperationType
import java.lang.NumberFormatException

class TypeConverter {

    companion object {

        fun convertStringToInt(argument: String): OperationType {
            if (argument.isNullOrEmpty()) return OperationType.Null

            try {
                val converted = argument.toInt()
                return OperationType.TypeNumber(converted)
            } catch (exception: NumberFormatException) {

            }

            return OperationType.Null
        }

        fun convertDoubleToInt(argument: Double): OperationType.TypeNumber {
            return OperationType.TypeNumber(argument.toInt())
        }

        fun convertStringToDouble(argument: String):OperationType {
            if (argument.isNullOrEmpty()) return OperationType.Null

            try {
                val converted = argument.toDouble()
                return OperationType.TypeNumber(converted)
            } catch (exception: NumberFormatException) {

            }

            return OperationType.Null
        }

        fun convertIntegerToDouble(argument: Int): OperationType.TypeNumber {
            return OperationType.TypeNumber(argument.toDouble())
        }

        fun convertIntToString(argument: Int): OperationType.TypeString {
            return OperationType.TypeString(argument.toString())
        }

        fun convertDoubleToString(argument: Double): OperationType.TypeString {
            return OperationType.TypeString(argument.toString())
        }

    }
}