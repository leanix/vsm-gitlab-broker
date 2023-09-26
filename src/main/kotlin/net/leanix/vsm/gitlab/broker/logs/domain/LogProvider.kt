package net.leanix.vsm.gitlab.broker.logs.domain

interface LogProvider {
    fun sendAdminLog(adminLog: AdminLog)
    fun sendStatusLog(statusLog: StatusLog)
    fun sendIntegrationConfigLog(integrationConfigLog: IntegrationConfigLog)
}
