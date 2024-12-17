package com.dicoding.picodiploma.storylensapp

import com.dicoding.picodiploma.storylensapp.data.response.ListStoryItem

object DataDummyStory {
    fun generateDummyQuoteResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {

            //dummy data list story
            val storyItem = ListStoryItem(
                id = "id_$i",
                name = "Author $i",
                description = "This is a description for story $i.",
                photoUrl = "https://example.com/photo_$i.jpg",
                createdAt = "2024-12-16T10:00:00Z",
                lon = 100.0 + i,
                lat = -6.0 - i
            )
            items.add(storyItem)
        }
        return items
    }
}