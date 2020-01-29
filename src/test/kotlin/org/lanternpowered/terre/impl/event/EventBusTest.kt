/*
 * Terre
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.terre.impl.event

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.lanternpowered.terre.dispatcher.runAsync
import org.lanternpowered.terre.event.Event
import org.lanternpowered.terre.event.EventBus
import org.lanternpowered.terre.event.Subscribe
import org.lanternpowered.terre.event.subscribe
import org.lanternpowered.terre.plugin.PluginContainer
import java.util.concurrent.atomic.LongAdder
import kotlin.test.assertEquals

class EventBusTest {

  private val plugin = SimplePluginContainer("test")

  @Test fun `test active plugin`(): Unit = runBlocking {
    val counter = LongAdder()
    EventBus.subscribe<TestEvent>(plugin) {
      assertEquals(plugin, PluginContainer.Active)
      counter.increment()
    }
    EventBus.post(TestEvent)
    assertEquals(1, counter.toInt())
  }

  @Test fun `test active plugin - in async task`(): Unit = runBlocking {
    val counter = LongAdder()
    EventBus.subscribe<TestEvent>(plugin) {
      runAsync {
        assertEquals(plugin, PluginContainer.Active)
        counter.increment()
      }.join()
    }
    EventBus.post(TestEvent)
    assertEquals(1, counter.toInt())
  }

  @Test fun `test generic registration`(): Unit = runBlocking {
    val counter = LongAdder()

    EventBus.subscribe<TestEvent>(plugin) {
      counter.add(1)
    }
    EventBus.post(TestEvent)

    assertEquals(plugin, PluginContainer.Active)
    assertEquals(1, counter.toInt())
  }

  @Test fun `test instance registration`(): Unit = runBlocking {
    val listeners = TestListeners()
    EventBus.subscribe(plugin, listeners)
    EventBus.post(TestEvent)

    delay(100) // Wait for async task

    assertEquals(3, listeners.counter.toInt())
  }

  class TestListeners {

    val counter = LongAdder()

    @Subscribe
    fun onTest(event: TestEvent) {
      this.counter.add(1)
    }

    @Subscribe
    suspend fun onSuspendTest(event: TestEvent) {
      this.counter.add(1)

      runAsync {
        EventBus.post(OtherEvent(2000))
      }
    }

    @Subscribe
    fun onOtherTest(event: OtherEvent) {
      this.counter.add(1)
    }
  }

  object TestEvent : Event

  class OtherEvent(val value: Int) : Event

  class SimplePluginContainer(
      override val id: String,
      override val name: String = id,
      override val description: String? = null,
      override val authors: List<String> = emptyList(),
      override val instance: Any = Any(),
      override val version: String? = null,
      override val url: String? = null
  ) : PluginContainer
}