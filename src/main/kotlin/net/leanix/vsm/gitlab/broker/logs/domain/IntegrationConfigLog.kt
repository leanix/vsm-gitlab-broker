package net.leanix.vsm.gitlab.broker.logs.domain

import java.util.UUID

data class IntegrationConfigLog(
    val runId: UUID,
    val configurationId: UUID,
    val errors: List<ConfigFieldError> = emptyList(),
    val status: TestResult = TestResult.SUCCESSFUL,
)

data class ConfigFieldError(
    val field: String,
    val message: String,
)

enum class TestResult {
    SUCCESSFUL,
    FAILED,
}
