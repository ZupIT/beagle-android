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

package br.com.zup.beagle.android.compiler.beaglesdk

import br.com.zup.beagle.android.compiler.BeagleSetupProcessor.Companion.BEAGLE_SETUP_GENERATED
import br.com.zup.beagle.android.compiler.DependenciesRegistrarComponentsProvider
import br.com.zup.beagle.android.compiler.PROPERTIES_REGISTRAR_CLASS_NAME
import br.com.zup.beagle.android.compiler.PROPERTIES_REGISTRAR_METHOD_NAME
import br.com.zup.beagle.android.compiler.extensions.compile
import br.com.zup.beagle.android.compiler.mocks.BEAGLE_CONFIG_IMPORTS
import br.com.zup.beagle.android.compiler.mocks.HTTP_CLIENT_FACTORY_IMPORTS
import br.com.zup.beagle.android.compiler.mocks.LIST_OF_HTTP_CLIENT_FACTORY
import br.com.zup.beagle.android.compiler.mocks.SIMPLE_BEAGLE_CONFIG
import br.com.zup.beagle.android.compiler.mocks.VALID_HTTP_CLIENT_FACTORY
import br.com.zup.beagle.android.compiler.mocks.VALID_HTTP_CLIENT_FACTORY_BEAGLE_SDK
import br.com.zup.beagle.android.compiler.mocks.VALID_HTTP_CLIENT_FACTORY_BEAGLE_SDK_FROM_REGISTRAR
import br.com.zup.beagle.android.compiler.mocks.VALID_SECOND_HTTP_CLIENT
import br.com.zup.beagle.android.compiler.mocks.VALID_THIRD_HTTP_CLIENT_FACTORY
import br.com.zup.beagle.android.compiler.processor.BeagleAnnotationProcessor
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import io.mockk.every
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

@DisplayName("Given Beagle Annotation Processor")
internal class HttpClientFactoryTest : BeagleSdkBaseTest() {

    @TempDir
    lateinit var tempPath: Path

    @DisplayName("When register http client factory")
    @Nested
    inner class RegisterHttpClientFactory {

        @Test
        @DisplayName("Then should add the http client factory in beagle sdk")
        fun testGenerateHttpClientFactoryCorrect() {
            // GIVEN
            val kotlinSource = SourceFile.kotlin(
                FILE_NAME,
                BEAGLE_CONFIG_IMPORTS + VALID_HTTP_CLIENT_FACTORY +
                        VALID_SECOND_HTTP_CLIENT + SIMPLE_BEAGLE_CONFIG
            )

            // WHEN
            val compilationResult = compile(kotlinSource, BeagleAnnotationProcessor(), tempPath)

            // THEN
            val file = compilationResult.generatedFiles.find { file ->
                file.name.startsWith(BEAGLE_SETUP_GENERATED)
            }!!

            val fileGeneratedInString = file.readText().replace(REGEX_REMOVE_SPACE, "")
            val fileExpectedInString = VALID_HTTP_CLIENT_FACTORY_BEAGLE_SDK
                .replace(REGEX_REMOVE_SPACE, "")

            assertEquals(fileExpectedInString, fileGeneratedInString)
            assertEquals(KotlinCompilation.ExitCode.OK, compilationResult.exitCode)
        }

    }

    @DisplayName("When already registered in other module PropertiesRegistrar")
    @Nested
    inner class RegisterFromOtherModule {
        @Test
        @DisplayName("Then should add the http client factory in beagle sdk")
        fun testGenerateHttpClientFactoryFromRegistrarCorrect() {
            // GIVEN
            every {
                DependenciesRegistrarComponentsProvider.getRegisteredComponentsInDependencies(
                    any(),
                    PROPERTIES_REGISTRAR_CLASS_NAME,
                    PROPERTIES_REGISTRAR_METHOD_NAME,
                )
            } returns listOf(
                Pair("""httpClientFactory""", "br.com.test.beagle.HttpClientFactoryTestThree()"),
            )

            val kotlinSource = SourceFile.kotlin(
                FILE_NAME,
                BEAGLE_CONFIG_IMPORTS + HTTP_CLIENT_FACTORY_IMPORTS +
                        VALID_THIRD_HTTP_CLIENT_FACTORY + VALID_SECOND_HTTP_CLIENT + SIMPLE_BEAGLE_CONFIG
            )

            // WHEN
            val compilationResult = compile(kotlinSource, BeagleAnnotationProcessor(), tempPath)

            // THEN
            val file = compilationResult.generatedFiles.find { file ->
                file.name.startsWith(BEAGLE_SETUP_GENERATED)
            }!!

            val fileGeneratedInString = file.readText().replace(REGEX_REMOVE_SPACE, "")
            val fileExpectedInString = VALID_HTTP_CLIENT_FACTORY_BEAGLE_SDK_FROM_REGISTRAR
                .replace(REGEX_REMOVE_SPACE, "")

            assertEquals(fileExpectedInString, fileGeneratedInString)
            assertEquals(KotlinCompilation.ExitCode.OK, compilationResult.exitCode)
        }
    }

    @DisplayName("When register http client factory")
    @Nested
    inner class InvalidHttpClientFactory {

        @Test
        @DisplayName("Then should show error with duplicate http client factory")
        fun testDuplicate() {
            // GIVEN
            val kotlinSource = SourceFile.kotlin(
                FILE_NAME,
                BEAGLE_CONFIG_IMPORTS + LIST_OF_HTTP_CLIENT_FACTORY +
                        VALID_SECOND_HTTP_CLIENT + SIMPLE_BEAGLE_CONFIG
            )

            // WHEN
            val compilationResult = compile(kotlinSource, BeagleAnnotationProcessor(), tempPath)


            // THEN
            assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, compilationResult.exitCode)
            Assertions.assertTrue(
                compilationResult.messages.contains(
                    MESSAGE_DUPLICATE_HTTP_CLIENT_FACTORY
                )
            )
        }

        @Test
        @DisplayName("Then should show error with duplicate http client factory in PropertiesRegistrar")
        fun testDuplicateInRegistrar() {
            // GIVEN
            every {
                DependenciesRegistrarComponentsProvider.getRegisteredComponentsInDependencies(
                    any(),
                    PROPERTIES_REGISTRAR_CLASS_NAME,
                    PROPERTIES_REGISTRAR_METHOD_NAME,
                )
            } returns listOf(
                Pair("""httpClientFactory""", "br.com.test.beagle.HttpClientFactoryTestThree()"),
            )
            val kotlinSource = SourceFile.kotlin(
                FILE_NAME,
                BEAGLE_CONFIG_IMPORTS + VALID_HTTP_CLIENT_FACTORY + VALID_THIRD_HTTP_CLIENT_FACTORY +
                        VALID_SECOND_HTTP_CLIENT + SIMPLE_BEAGLE_CONFIG
            )

            // WHEN
            val compilationResult = compile(kotlinSource, BeagleAnnotationProcessor(), tempPath)

            // THEN
            Assertions.assertTrue(
                compilationResult.messages.contains(
                    MESSAGE_DUPLICATE_HTTP_CLIENT_FACTORY_REGISTRAR
                )
            )
            assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, compilationResult.exitCode)
        }

    }

    companion object {
        private const val FILE_NAME = "File1.kt"
        private val REGEX_REMOVE_SPACE = "\\s".toRegex()
        private const val MESSAGE_DUPLICATE_HTTP_CLIENT_FACTORY =
            "error: HttpClientFactory defined multiple times: "
                .plus("1 - br.com.test.beagle.HttpClientFactoryTestTwo ")
                .plus("2 - br.com.test.beagle.HttpClientFactoryTest. ")
                .plus("You must remove one implementation from the application.")


        private const val MESSAGE_DUPLICATE_HTTP_CLIENT_FACTORY_REGISTRAR =
            "error: HttpClientFactory defined multiple times: "
                .plus("1 - br.com.test.beagle.HttpClientFactoryTest ")
                .plus("2 - br.com.test.beagle.HttpClientFactoryTestThree. ")
                .plus("You must remove one implementation from the application.")
    }

}