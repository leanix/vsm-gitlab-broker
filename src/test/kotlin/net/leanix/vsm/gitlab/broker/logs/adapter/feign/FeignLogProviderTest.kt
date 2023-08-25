package net.leanix.vsm.gitlab.broker.logs.adapter.feign

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.leanix.vsm.gitlab.broker.logs.domain.AdminLog
import net.leanix.vsm.gitlab.broker.logs.domain.LogLevel
import net.leanix.vsm.gitlab.broker.logs.domain.LogStatus
import net.leanix.vsm.gitlab.broker.logs.domain.StatusLog
import org.junit.jupiter.api.Test
import java.util.UUID

internal class FeignLogProviderTest {

    private val loggingClient = mockk<LoggingClient>()
    private val feignLogProvider = FeignLogProvider(loggingClient)

    @Test
    fun `sending admin or status log should call the logging client`() {
        val adminLog = AdminLog(
            runId = UUID.randomUUID(),
            configurationId = UUID.randomUUID(),
            subject = "dummy",
            level = LogLevel.INFO,
            message = "dummy"
        )
        every { loggingClient.sendAdminLog(any()) } returns Unit
        feignLogProvider.sendAdminLog(adminLog)
        verify(exactly = 1) { loggingClient.sendAdminLog(any()) }
    }

    @Test
    fun `sending status log should call correct client`() {
        val statusLog = StatusLog(
            runId = UUID.randomUUID(),
            status = LogStatus.IN_PROGRESS,
            message = "Success"
        )
        every { loggingClient.sendStatusLog(any()) } returns Unit
        feignLogProvider.sendStatusLog(statusLog)
        verify(exactly = 1) { loggingClient.sendStatusLog(any()) }
    }
}
