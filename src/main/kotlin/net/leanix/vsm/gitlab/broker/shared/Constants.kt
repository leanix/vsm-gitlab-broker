package net.leanix.vsm.gitlab.broker.shared

object Constants {

    const val GITLAB_ENTERPRISE_CONNECTOR = "gitlab-enterprise-connector"
    const val GITLAB_ENTERPRISE = "gitlab-enterprise"
    const val API_USER = "apitoken"
    const val GITLAB_ON_PREM_VERSION_HEADER = "X-LX-VsmGitLabBroker-Version"
    const val EVENT_TYPE_HEADER = "X-LX-CanopyItem-EventType"
    const val PROJECT_DESTROY_WEBHOOK_EVENT_NAME = "project_destroy"
    val PROJECT_EVENTS =
        listOf(
            "project_create",
            "project_update",
            "project_rename",
            "project_transfer",
            PROJECT_DESTROY_WEBHOOK_EVENT_NAME
        )
}
