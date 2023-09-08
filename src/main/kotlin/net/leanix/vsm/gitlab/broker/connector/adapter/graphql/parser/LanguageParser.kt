package net.leanix.githubbroker.connector.adapter.graphql.parser

import net.leanix.gitlabbroker.connector.adapter.graphql.data.allgroupsquery.RepositoryLanguage
import net.leanix.vsm.gitlab.broker.connector.domain.Language

object LanguageParser {

    fun parse(languages: List<RepositoryLanguage>?): List<Language> {
        return if (!languages.isNullOrEmpty()) {
            languages.map {
                Language(
                    it.name,
                    it.name,
                    it.share!!,
                )
            }
        } else {
            emptyList()
        }
    }
}
