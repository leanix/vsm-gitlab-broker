package net.leanix.vsm.gitlab.broker.connector.application

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import net.leanix.vsm.gitlab.broker.connector.adapter.graphql.GitlabGraphqlProvider
import net.leanix.vsm.gitlab.broker.connector.application.WebhookConsumerService.Companion.computeWebhookEventType
import net.leanix.vsm.gitlab.broker.connector.domain.EventType
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabConfiguration
import net.leanix.vsm.gitlab.broker.connector.domain.Repository
import net.leanix.vsm.gitlab.broker.connector.domain.RepositoryProvider
import net.leanix.vsm.gitlab.broker.connector.domain.WebhookEventType
import net.leanix.vsm.gitlab.broker.shared.cache.AssignmentsCache
import net.leanix.vsm.gitlab.broker.shared.exception.GitlabPayloadNotSupportedException
import net.leanix.vsm.gitlab.broker.shared.exception.GitlabTokenException
import net.leanix.vsm.gitlab.broker.shared.exception.NamespaceNotMatchException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID.randomUUID

const val PAYLOAD_TOKEN = "payload_token"

class WebhookConsumerServiceImplTest {

    private val repositoryProvider: RepositoryProvider = mockk(relaxed = true)
    private val gitlabGraphqlProvider: GitlabGraphqlProvider = mockk(relaxed = true)
    private val doraService: DoraService = mockk(relaxed = true)

    private val subject =
        spyk(WebhookConsumerService(PAYLOAD_TOKEN, repositoryProvider, gitlabGraphqlProvider, doraService))

    @BeforeEach
    fun setupMock() {
        every { subject.logInfoMessages(any(), any(), any()) } returns Unit
        every { subject.logFailedStatus(any(), any()) } returns Unit
    }

    @Test
    fun `should return webhook event type REPOSITORY when event_name = project_create in payload`() {
        val payload = this::class.java.getResource("/webhook_calls/project_created.json")!!.readText()

        val result = computeWebhookEventType(payload)

        assertEquals(WebhookEventType.REPOSITORY, result)
    }

    @Test
    fun `should return webhook event type REPOSITORY when event_name = project_update in payload`() {
        val payload = this::class.java.getResource("/webhook_calls/project_name_changed.json")!!.readText()

        val result = computeWebhookEventType(payload)

        assertEquals(WebhookEventType.REPOSITORY, result)
    }

    @Test
    fun `should return webhook event type REPOSITORY when event_name = project_rename in payload`() {
        val payload = this::class.java.getResource("/webhook_calls/project_path_changed.json")!!.readText()

        val result = computeWebhookEventType(payload)

        assertEquals(WebhookEventType.REPOSITORY, result)
    }

    @Test
    fun `should return webhook event type REPOSITORY when event_name = project_transfer`() {
        val payload = this::class.java.getResource("/webhook_calls/project_transferred.json")!!.readText()

        val result = computeWebhookEventType(payload)

        assertEquals(WebhookEventType.REPOSITORY, result)
    }

    @Test
    fun `should return webhook event type MERGE_REQUEST when object_kind = merge_request and action = merged`() {
        val payload = this::class.java.getResource("/webhook_calls/merge_request_merged.json")!!.readText()

        val result = computeWebhookEventType(payload)

        assertEquals(WebhookEventType.MERGE_REQUEST, result)
    }

    @Test
    fun `should throw GitlabPayloadNotSupportedException when object_kind = merge_request and action != merged`() {
        val payload = this::class.java.getResource("/webhook_calls/merge_request_opened.json")!!.readText()

        assertThrows<GitlabPayloadNotSupportedException> { computeWebhookEventType(payload) }
    }

    @Test
    fun `should throw GitlabPayloadNotSupportedException when payload has no supported fields`() {
        assertThrows<GitlabPayloadNotSupportedException> {
            computeWebhookEventType("{ \"dummy_key\": \"dummy value\" }")
        }
    }

    @Test
    fun `should throw GitlabTokenException when payload token is null`() {
        assertThrows<GitlabTokenException> {
            subject.consumeWebhookEvent(null, getProjectPayload())
        }
    }

    @Test
    fun `should throw GitlabTokenException when payload token not equal to expected token`() {
        assertThrows<GitlabTokenException> {
            subject.consumeWebhookEvent("different_token", getProjectPayload())
        }
    }

    @Test
    fun `should throw NamespaceNotFoundInCacheException when namespace not match any org name in AssignmentCache`() {
        AssignmentsCache.deleteAll()
        AssignmentsCache.addAll(
            listOf(
                GitLabAssignment(randomUUID(), randomUUID(), randomUUID(), GitLabConfiguration("cider/ops/special"))
            )
        )
        assertThrows<NamespaceNotMatchException> {
            subject.consumeWebhookEvent(PAYLOAD_TOKEN, getProjectPayload())
        }
    }

    @Test
    fun `should call repositoryProvider save when namespace matches any org name in AssignmentCache`() {
        val gitlabAssignment = GitLabAssignment(randomUUID(), randomUUID(), randomUUID(), GitLabConfiguration("cider"))

        AssignmentsCache.deleteAll()
        AssignmentsCache.addAll(listOf(gitlabAssignment))

        val repository = getRepository()
        every { gitlabGraphqlProvider.getRepositoryByPath("cider/ops/ahmed-test-2") } returns repository

        subject.consumeWebhookEvent(PAYLOAD_TOKEN, getProjectPayload())

        verify(exactly = 1) { repositoryProvider.save(repository, gitlabAssignment, eq(EventType.CHANGE)) }
        verify(exactly = 1) { doraService.generateDoraEvents(repository, gitlabAssignment) }
        verify(exactly = 1) {
            subject.logInfoMessages(eq("vsm.repos.imported"), arrayOf("cider/ops/ahmed-test-2"), gitlabAssignment)
        }
    }

    @Test
    fun `should call repositoryProvider delete when namespace matches any org name in AssignmentCache and archived`() {
        val gitlabAssignment = GitLabAssignment(randomUUID(), randomUUID(), randomUUID(), GitLabConfiguration("cider"))

        AssignmentsCache.deleteAll()
        AssignmentsCache.addAll(listOf(gitlabAssignment))

        val repository = getRepository(archived = true)
        every { gitlabGraphqlProvider.getRepositoryByPath("cider/ops/ahmed-test-2") } returns repository

        subject.consumeWebhookEvent(PAYLOAD_TOKEN, getProjectPayload())

        verify(exactly = 1) {
            repositoryProvider.delete(
                repository.id,
                gitlabAssignment.connectorConfiguration.orgName
            )
        }
        verify(exactly = 1) {
            subject.logInfoMessages(eq("vsm.repos.imported"), arrayOf("cider/ops/ahmed-test-2"), gitlabAssignment)
        }
    }

    private fun getProjectPayload() = this::class.java.getResource("/webhook_calls/project_created.json")!!.readText()
}

fun getRepository(
    archived: Boolean = false
) = Repository(
    id = "21",
    name = "ahmed-test-2",
    description = "",
    archived = archived,
    url = "",
    visibility = "private",
    languages = emptyList(),
    tags = emptyList(),
    defaultBranch = "empty-branch",
    groupName = "cider/ops/",
    path = "ahmed-test-2",
)
