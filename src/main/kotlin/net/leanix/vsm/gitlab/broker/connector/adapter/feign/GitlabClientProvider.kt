package net.leanix.vsm.gitlab.broker.connector.adapter.feign

import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.GitlabGroup
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.GitlabUser

interface GitlabClientProvider {
    fun getCurrentUser(): GitlabUser
    fun getGroupByFullPath(fullPath: String): GitlabGroup?
    fun getVersion(): String
}
