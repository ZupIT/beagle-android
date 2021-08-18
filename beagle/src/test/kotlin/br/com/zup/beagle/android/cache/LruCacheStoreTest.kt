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

package br.com.zup.beagle.android.cache

import android.util.LruCache
import br.com.zup.beagle.android.BaseTest
import br.com.zup.beagle.android.testutil.RandomData
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

private val CACHE_KEY = RandomData.string()

class LruCacheStoreTest : BaseTest() {

    private val cachedData: LruCache<String, BeagleCache> = mockk()

    private lateinit var cacheStore: LruCacheStore

    private val beagleCache: BeagleCache = mockk()

    @BeforeAll
    override fun setUp() {
        super.setUp()

        cacheStore = LruCacheStore(cache = cachedData)
    }

    @Test
    fun save_should_add_new_beagleHashKey_to_cache() {
        // Given
        val timerCacheSlot = slot<BeagleCache>()
        every { cachedData.put(any(), capture(timerCacheSlot)) } returns null

        // When
        cacheStore.save(CACHE_KEY, beagleCache)

        // Then
        verify(exactly = 1) { cachedData.put(CACHE_KEY, timerCacheSlot.captured) }
        assertEquals(timerCacheSlot.captured, beagleCache)
    }

    @Test
    fun restore_should_return_cached_timerCache() {
        // Given
        every { cachedData[CACHE_KEY] } returns beagleCache

        // When
        val actualTimerCache = cacheStore.restore(CACHE_KEY)

        // Then
        assertEquals(beagleCache, actualTimerCache)
    }
}