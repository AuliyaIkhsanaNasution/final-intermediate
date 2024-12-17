package com.dicoding.picodiploma.storylensapp.data

import androidx.paging.PagingState
import com.dicoding.picodiploma.storylensapp.data.response.ListStoryItem
import androidx.paging.PagingSource
import com.dicoding.picodiploma.storylensapp.data.api.ApiService

class StoryPagingSource(private val apiService: ApiService) : PagingSource<Int, ListStoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val nextPage = params.key ?: INITIAL_PAGE_INDEX
            val response = apiService.getListStory(page = nextPage, size = params.loadSize)
            val stories = response.listStory
            LoadResult.Page(
                data = stories,
                prevKey = if (nextPage == INITIAL_PAGE_INDEX) null else nextPage - 1,
                nextKey = if (stories.isEmpty()) null else nextPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}