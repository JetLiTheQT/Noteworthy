package com.finalprojectteam11.noteworthy.data

import com.algolia.search.model.ObjectID
import com.algolia.search.model.indexing.Indexable
import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
    val title: String,
    val content: String,
    override val objectID: ObjectID
) : Indexable