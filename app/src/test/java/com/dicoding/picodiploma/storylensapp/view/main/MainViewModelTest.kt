package com.dicoding.picodiploma.storylensapp.view.main

import org.junit.Assert
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.picodiploma.storylensapp.DataDummyStory
import com.dicoding.picodiploma.storylensapp.DispatcherStoryRule
import com.dicoding.picodiploma.storylensapp.data.repository.ListStoryRepository
import com.dicoding.picodiploma.storylensapp.data.repository.UserRepository
import com.dicoding.picodiploma.storylensapp.data.response.ListStoryItem
import com.dicoding.picodiploma.storylensapp.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    //Mock & Rule for testing
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRules = DispatcherStoryRule()

    @Mock
    private lateinit var listStoryRepository: ListStoryRepository

    @Mock
    private lateinit var userRepository: UserRepository

    //initialize mock before start
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    //POSITIVE CASE for test dummy data
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when Get Quote Should Not Null and Return Data`() = runTest {
        val dummyStories = DataDummyStory.generateDummyQuoteResponse()
        val dataPositive: PagingData<ListStoryItem> = StoriesPagingSource.snapshot(dummyStories)
        val expectedStoory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStoory.value = dataPositive
        Mockito.`when`(listStoryRepository.getStoryPager()).thenReturn(expectedStoory)

        val mainViewModel = MainViewModel(userRepository, listStoryRepository)
        val actualStoory: PagingData<ListStoryItem> = mainViewModel.getStoryPager.getOrAwaitValue()

        //move paging data to adapter
        val differ = AsyncPagingDataDiffer(
            diffCallback = MainPagingAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStoory)

        //check result to testing
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0], differ.snapshot()[0])
    }


    //NEGATIVE CASE for test failed source
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when Get Quote Empty Should Return No Data`() = runTest {
        val dataNegative: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStoory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStoory.value = dataNegative
        Mockito.`when`(listStoryRepository.getStoryPager()).thenReturn(expectedStoory)
        val mainViewModel = MainViewModel(userRepository, listStoryRepository)
        val actualStoory: PagingData<ListStoryItem> = mainViewModel.getStoryPager.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = MainPagingAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStoory)
        Assert.assertEquals(0, differ.snapshot().size)
    }
}

//using in main class
val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

//get data dummy list for testing
class StoriesPagingSource : PagingSource<Int, LiveData<List<ListStoryItem>>>() {
    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
        return 0
    }
}
