package net.leanix.vsm.gitlab.broker.connector.application

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.GitlabClient
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.GitlabFeignClientProvider
import net.leanix.vsm.gitlab.broker.connector.shared.getAllGroups
import net.leanix.vsm.gitlab.broker.connector.shared.getGitlabAssignment
import net.leanix.vsm.gitlab.broker.connector.shared.getGitlabCurrentUser
import net.leanix.vsm.gitlab.broker.logs.application.LoggingService
import net.leanix.vsm.gitlab.broker.shared.exception.AccessLevelValidationFailed
import net.leanix.vsm.gitlab.broker.shared.exception.InvalidToken
import net.leanix.vsm.gitlab.broker.shared.exception.OrgNameValidationFailed
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.MessageSource

class ValidationServiceTest {

    private val messageSource = mockk<MessageSource>()
    private val loggingService = mockk<LoggingService>()
    private val gitlabClient = mockk<GitlabClient>()
    private val gitlabFeignClientProvider = GitlabFeignClientProvider(gitlabClient)
    private var validationService: ValidationService? = null

    @BeforeEach
    fun setUp() {
        every { messageSource.getMessage(allAny(), allAny(), allAny()) } returns "mock-message"
        every { loggingService.sendAdminLog(any()) } returns Unit
        every { loggingService.sendStatusLog(any()) } returns Unit
        every { loggingService.sendIntegrationConfigLog(any()) } returns Unit
    }

    @Test
    fun `it should validate the current user and groups from configuration`() {
        setupService()
        every { gitlabClient.getCurrentUser() } returns getGitlabCurrentUser(true)
        every { gitlabClient.getAllGroups() } returns getAllGroups()

        validationService!!.validateConfiguration(getGitlabAssignment())

        verify(exactly = 1) { gitlabClient.getCurrentUser() }
        verify(exactly = 1) { gitlabClient.getAllGroups() }
    }

    @Test
    fun `it should not validate the configuration if token is invalid`() {
        setupService()
        every { gitlabClient.getCurrentUser() } throws Exception()
        every { gitlabClient.getAllGroups() } returns getAllGroups()

        assertThrows<InvalidToken> {
            validationService!!.validateConfiguration(getGitlabAssignment())
        }

        verify(exactly = 1) { gitlabClient.getCurrentUser() }
        verify(exactly = 0) { gitlabClient.getAllGroups() }
    }

    @Test
    fun `it should not validate the configuration if user is not admin`() {
        setupService()
        every { gitlabClient.getCurrentUser() } returns getGitlabCurrentUser(false)
        every { gitlabClient.getAllGroups() } returns getAllGroups()

        assertThrows<AccessLevelValidationFailed> {
            validationService!!.validateConfiguration(getGitlabAssignment())
        }

        verify(exactly = 1) { gitlabClient.getCurrentUser() }
        verify(exactly = 0) { gitlabClient.getAllGroups() }
    }

    @Test
    fun `it should not validate the configuration if group name is invalid`() {
        setupService()
        every { gitlabClient.getCurrentUser() } returns getGitlabCurrentUser(true)
        every { gitlabClient.getAllGroups() } returns emptyList()

        assertThrows<OrgNameValidationFailed> {
            validationService!!.validateConfiguration(getGitlabAssignment())
        }

        verify(exactly = 1) { gitlabClient.getCurrentUser() }
        verify(exactly = 1) { gitlabClient.getAllGroups() }
    }

    @Test
    fun `it should not validate the user if webhook url is blank`() {
        setupService("")
        every { gitlabClient.getAllGroups() } returns emptyList()

        assertThrows<OrgNameValidationFailed> {
            validationService!!.validateConfiguration(getGitlabAssignment())
        }

        verify(exactly = 0) { gitlabClient.getCurrentUser() }
        verify(exactly = 1) { gitlabClient.getAllGroups() }
    }

    private fun setupService(webhookUrl: String = "webhook-url") {
        validationService = ValidationService(gitlabFeignClientProvider, webhookUrl)
        validationService!!.messageSource = messageSource
        validationService!!.loggingService = loggingService
    }
}
