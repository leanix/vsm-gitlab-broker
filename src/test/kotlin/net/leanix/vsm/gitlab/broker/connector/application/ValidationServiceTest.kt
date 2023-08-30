package net.leanix.vsm.gitlab.broker.connector.application

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.GitlabClient
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.GitlabFeignClientProvider
import net.leanix.vsm.gitlab.broker.connector.shared.DataBuilder
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
    private val validationService = ValidationService(gitlabFeignClientProvider)

    @BeforeEach
    fun setUp() {
        validationService.messageSource = messageSource
        validationService.loggingService = loggingService

        every { messageSource.getMessage(allAny(), allAny(), allAny()) } returns "mock-message"
        every { loggingService.sendAdminLog(any()) } returns Unit
        every { loggingService.sendStatusLog(any()) } returns Unit
    }

    @Test
    fun `it should validate the configuration`() {
        every { gitlabClient.getCurrentUser() } returns DataBuilder.getGitlabCurrentUser()
        every { gitlabClient.getUserById(any()) } returns DataBuilder.getGitlabUser(true)
        every { gitlabClient.getGroupByPath(any()) } returns Unit

        validationService.validateConfiguration(DataBuilder.getGitlabAssignment())

        verify(exactly = 1) { gitlabClient.getCurrentUser() }
        verify(exactly = 1) { gitlabClient.getUserById(any()) }
        verify(exactly = 1) { gitlabClient.getGroupByPath(any()) }
    }

    @Test
    fun `it should not validate the configuration if token is invalid`() {
        every { gitlabClient.getCurrentUser() } throws Exception()
        every { gitlabClient.getUserById(any()) } returns DataBuilder.getGitlabUser(true)
        every { gitlabClient.getGroupByPath(any()) } returns Unit

        assertThrows<InvalidToken> {
            validationService.validateConfiguration(DataBuilder.getGitlabAssignment())
        }

        verify(exactly = 1) { gitlabClient.getCurrentUser() }
        verify(exactly = 0) { gitlabClient.getUserById(any()) }
        verify(exactly = 0) { gitlabClient.getGroupByPath(any()) }
    }

    @Test
    fun `it should not validate the configuration if user is not admin`() {
        every { gitlabClient.getCurrentUser() } returns DataBuilder.getGitlabCurrentUser()
        every { gitlabClient.getUserById(any()) } returns DataBuilder.getGitlabUser(false)
        every { gitlabClient.getGroupByPath(any()) } returns Unit

        assertThrows<AccessLevelValidationFailed> {
            validationService.validateConfiguration(DataBuilder.getGitlabAssignment())
        }

        verify(exactly = 1) { gitlabClient.getCurrentUser() }
        verify(exactly = 1) { gitlabClient.getUserById(any()) }
        verify(exactly = 0) { gitlabClient.getGroupByPath(any()) }
    }

    @Test
    fun `it should not validate the configuration if org name is invalid`() {
        every { gitlabClient.getCurrentUser() } returns DataBuilder.getGitlabCurrentUser()
        every { gitlabClient.getUserById(any()) } returns DataBuilder.getGitlabUser(true)
        every { gitlabClient.getGroupByPath(any()) } throws Exception()

        assertThrows<OrgNameValidationFailed> {
            validationService.validateConfiguration(DataBuilder.getGitlabAssignment())
        }

        verify(exactly = 1) { gitlabClient.getCurrentUser() }
        verify(exactly = 1) { gitlabClient.getUserById(any()) }
        verify(exactly = 1) { gitlabClient.getGroupByPath(any()) }
    }
}
