package com.charlesmuchene.prefedit.navigation

import app.cash.turbine.test
import com.charlesmuchene.prefedit.data.App
import com.charlesmuchene.prefedit.data.Device
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NavigationTest {

    private val dispatcher = StandardTestDispatcher(TestCoroutineScheduler())
    private val scope = TestScope(dispatcher)

    private lateinit var navigation: Navigation

    @BeforeEach
    fun setup() {
        navigation = Navigation(scope)
    }

    @Test
    fun `navigation always has home as start`() = runTest(dispatcher) {
        navigation.screens.test {
            assertEquals(expected = HomeScreen, actual = awaitItem().first())
        }
    }

    @Test
    fun `forward navigation appends screen to stack`() = runTest(dispatcher) {
        navigation.screens.test {

            navigation.navigate(AppsScreen(device))

            val screens = awaitItem()
            assertEquals(expected = HomeScreen, actual = screens.first())
            assertEquals(expected = HomeScreen, actual = screens.first())
        }
    }

    @Test
    fun `backwards navigation pops screens on stack exclusively`() = runTest(dispatcher) {
        navigation.navigate(AppsScreen(device))
        navigation.navigate(PrefListScreen(app, device))

        navigation.screens.test {

            navigation.navigate(AppsScreen(device))

            val screens = awaitItem()
            assertTrue(screens.size == 2)
            assertTrue(screens.last() is AppsScreen)
        }
    }

    @Test
    fun `backwards navigation never pops the home screen`() = runTest(dispatcher) {
        navigation.navigate(AppsScreen(device))
        navigation.navigate(PrefListScreen(app, device))

        navigation.screens.test {

            navigation.navigate(HomeScreen)

            val screens = awaitItem()
            assertTrue(screens.size == 1)
            assertTrue(screens.first() is HomeScreen)
        }
    }

    private val device = Device(
        serial = "1B241CAA5079LR", type = Device.Type.Device, attributes = listOf(
            Device.Attribute(name = "usb", value = "1O845693Y"),
            Device.Attribute(name = "product", value = "redfin"),
            Device.Attribute(name = "model", value = "Pixel_5"),
            Device.Attribute(name = "device", value = "redfin"),
            Device.Attribute(name = "transport_id", value = "4"),
        )
    )

    private val app = App(packageName = "com.charlesmuchene.player")
}