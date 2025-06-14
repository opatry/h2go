package net.opatry.test.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

@ExperimentalCoroutinesApi
class MainDispatcherExtension : BeforeAllCallback, AfterAllCallback {
    override fun beforeAll(context: ExtensionContext?) {
        Dispatchers.setMain(StandardTestDispatcher())
    }
    override fun afterAll(context: ExtensionContext?) {
        Dispatchers.resetMain()
    }
}