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

package br.com.zup.beagle.android.compiler

import br.com.zup.beagle.android.annotation.BeagleComponent
import br.com.zup.beagle.android.compiler.beaglesetupmanage.PropertyImplementationManager
import br.com.zup.beagle.android.compiler.beaglesetupmanage.TypeElementImplementationManager
import br.com.zup.beagle.compiler.shared.implements
import br.com.zup.beagle.compiler.shared.multipleDefinitionErrorMessage
import com.squareup.kotlinpoet.PropertySpec
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

internal class BeagleSetupPropertyGenerator(private val processingEnv: ProcessingEnvironment) {

    fun generate(
        roundEnvironment: RoundEnvironment,
        onlyPropertiesRegisteredInsideModule: Boolean = false,
    ): List<PropertySpec> {
        val propertySpecifications = PropertySpecifications()

        // Get properties possibly registered in dependency modules
        if (!onlyPropertiesRegisteredInsideModule) {

            DependenciesRegistrarComponentsProvider
                .getRegisteredComponentsInDependencies(
                    processingEnv,
                    PROPERTIES_REGISTRAR_CLASS_NAME,
                    PROPERTIES_REGISTRAR_METHOD_NAME,
                )
                .forEach { registeredDependency ->
                    var typeElement =
                        processingEnv
                            .elementUtils.getTypeElement(registeredDependency.second.removeSuffix("()"))

                    if (typeElement == null) {
                        typeElement =
                            processingEnv
                                .elementUtils.getTypeElement(registeredDependency.second.substringBefore("::"))
                    }

                    checkIfHandlersExists(typeElement, propertySpecifications)
                    checkIfOtherAttributesExists(typeElement, propertySpecifications)
                }
        }

        // Get properties registered in current module
        roundEnvironment.getElementsAnnotatedWith(BeagleComponent::class.java).forEach { element ->
            val typeElement = element as TypeElement
            checkIfHandlersExists(typeElement, propertySpecifications)
            checkIfOtherAttributesExists(typeElement, propertySpecifications)
        }

        return createListOfPropertySpec(propertySpecifications)
    }

    private fun checkIfHandlersExists(
        typeElement: TypeElement,
        propertySpecifications: PropertySpecifications?,
    ) {
        TypeElementImplementationManager.manage(processingEnv, typeElement, propertySpecifications)
    }

    private fun checkIfOtherAttributesExists(
        typeElement: TypeElement,
        propertySpecifications: PropertySpecifications?,
    ) {
        when {
            typeElement.implements(DESIGN_SYSTEM, processingEnv) -> {
                val element = propertySpecifications?.designSystem
                if (element == null) {
                    propertySpecifications?.designSystem = typeElement
                } else {
                    logImplementationErrorMessage(
                        typeElement,
                        element,
                        "DesignSystem"
                    )
                }
            }
            typeElement.implements(ANALYTICS_PROVIDER, processingEnv) -> {
                val element = propertySpecifications?.analyticsProvider
                if (element == null) {
                    propertySpecifications?.analyticsProvider = typeElement
                } else {
                    logImplementationErrorMessage(
                        typeElement,
                        element,
                        "AnalyticsProvider"
                    )
                }
            }
        }
    }

    private fun logImplementationErrorMessage(
        typeElement: TypeElement,
        propertySpecificationsElement: TypeElement,
        element: String,
    ) {
        processingEnv.messager?.multipleDefinitionErrorMessage(
            typeElement,
            propertySpecificationsElement,
            element,
        )
    }

    private fun createListOfPropertySpec(
        propertySpecifications: PropertySpecifications?,
    ): List<PropertySpec> {
        return PropertyImplementationManager.manage(propertySpecifications).toMutableList()
    }
}

internal data class PropertySpecifications(
    var deepLinkHandler: TypeElement? = null,
    var httpClientFactory: TypeElement? = null,
    var designSystem: TypeElement? = null,
    var beagleActivities: List<TypeElement>? = null,
    var urlBuilder: TypeElement? = null,
    var viewClient: TypeElement? = null,
    var analyticsProvider: TypeElement? = null,
    var logger: TypeElement? = null,
    var imageDownloader: TypeElement? = null,
    var config: TypeElement? = null,
)
