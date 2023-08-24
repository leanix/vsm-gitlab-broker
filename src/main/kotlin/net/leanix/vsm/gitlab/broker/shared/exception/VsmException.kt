package net.leanix.vsm.gitlab.broker.shared.exception

sealed class VsmException(message: String? = null) : RuntimeException(message) {

    class NoRepositoriesFound : VsmException()

    class GraphqlException(message: String?) : VsmException(message)
}
