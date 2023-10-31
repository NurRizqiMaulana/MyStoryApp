package com.dicoding.mystoryapp

import com.dicoding.mystoryapp.data.remote.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "photoUrl + $i",
                "createdAt $i",
                "name $i",
                "description $i",
                10.0,
                -10.0,
            )
            items.add(story)
        }
        return items
    }
}