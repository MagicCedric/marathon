package com.malinskiy.marathon.android.executor.listeners.screenshot

import com.android.ddmlib.testrunner.TestIdentifier
import com.malinskiy.marathon.android.AndroidDevice
import com.malinskiy.marathon.android.executor.listeners.TestRunListener
import com.malinskiy.marathon.android.toTest
import com.malinskiy.marathon.device.DevicePoolId
import com.malinskiy.marathon.device.toDeviceInfo
import com.malinskiy.marathon.execution.Attachment
import com.malinskiy.marathon.execution.AttachmentType
import com.malinskiy.marathon.io.FileManager
import com.malinskiy.marathon.io.FileType
import com.malinskiy.marathon.log.MarathonLogging
import com.malinskiy.marathon.report.attachment.AttachmentListener
import com.malinskiy.marathon.report.attachment.AttachmentProvider
import com.malinskiy.marathon.test.Test
import com.malinskiy.marathon.test.toSimpleSafeTestName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlin.coroutines.CoroutineContext

class ScreenCapturerTestRunListener(
    private val fileManager: FileManager,
    private val pool: DevicePoolId,
    private val device: AndroidDevice
) : TestRunListener, CoroutineScope, AttachmentProvider {

    val attachmentListeners = mutableListOf<AttachmentListener>()

    override fun registerListener(listener: AttachmentListener) {
        attachmentListeners.add(listener)
    }

    private var screenCapturerJob: Job? = null
    private val logger = MarathonLogging.logger(ScreenCapturerTestRunListener::class.java.simpleName)
    private val threadPoolDispatcher = newFixedThreadPoolContext(1, "ScreenCapturer - ${device.serialNumber}")
    override val coroutineContext: CoroutineContext
        get() = threadPoolDispatcher

    override fun testStarted(test: Test) {
        logger.debug { "Starting recording for ${test.toSimpleSafeTestName()}" }
        screenCapturerJob = async {
            ScreenCapturer(device, pool, fileManager, test).start()
        }
    }

    override fun testEnded(test: Test, testMetrics: Map<String, String>) {
        logger.debug { "Finished recording for ${test.toSimpleSafeTestName()}" }
        screenCapturerJob?.cancel()
        threadPoolDispatcher.close()

        attachmentListeners.forEach {
            val file = fileManager.createFile(FileType.SCREENSHOT, pool, device.toDeviceInfo(), test)
            val attachment = Attachment(file, AttachmentType.SCREENSHOT)
            it.onAttachment(test, attachment)
        }
    }
}