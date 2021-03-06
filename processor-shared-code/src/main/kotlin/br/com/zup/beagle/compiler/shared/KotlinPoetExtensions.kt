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

package br.com.zup.beagle.compiler.shared

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType

// Not working with generic types
fun TypeElement.implements(
    beagleClass: BeagleClass,
    processingEnvironment: ProcessingEnvironment
): Boolean {
    return try {
        processingEnvironment.typeUtils.isAssignable(
            this.asType(),
            processingEnvironment.elementUtils.getTypeElement(beagleClass.toString()).asType()
        )
    } catch (exception: Exception) {
        false
    }
}

fun TypeElement.implementsInterface(interfaceName: String): Boolean {
    for (interfaceTypeMirror in this.interfaces) {
        val typeMirror = ((interfaceTypeMirror as DeclaredType)).asElement()
        if (typeMirror.toString() == interfaceName) {
            return true
        }
    }
    return false
}
