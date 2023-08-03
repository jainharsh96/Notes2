package com.harsh.notes

import com.harsh.notes.repository.NotesRepository
import com.harsh.notes.ui.notesscreen.NotesViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ExampleUnitTest {

    val testDispatcher = UnconfinedTestDispatcher()

    val testDispatcherProvider = object : AppDispatcherProvider{
        override val Default: CoroutineDispatcher
            get() = testDispatcher
        override val Main: CoroutineDispatcher
            get() = testDispatcher
        override val IO: CoroutineDispatcher
            get() = testDispatcher
        override val Unconfined: CoroutineDispatcher
            get() = testDispatcher
    }

    private lateinit var viewModel: NotesViewModel

    @Mock
    lateinit var repo : NotesRepository

    @Before
    fun setup(){
        Dispatchers.setMain(testDispatcher)
        viewModel = NotesViewModel(
            repo,testDispatcherProvider
        )
    }

    @After
    fun onEnd(){
        Dispatchers.resetMain()
    }

    @Test
    fun testViewModel() = runTest(UnconfinedTestDispatcher()) {
        Mockito.doReturn(1).`when`(repo).deleteNote(123)
        Mockito.`when`(repo.changeNoteState(1, 1)).thenReturn(1)
        viewModel.testviewModelScope()
        advanceUntilIdle()
        Mockito.verify(repo, Mockito.times(1)).deleteNote(123)
    }
}