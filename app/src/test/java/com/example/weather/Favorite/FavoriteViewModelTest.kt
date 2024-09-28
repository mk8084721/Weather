package com.example.weather.Favorite


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather.Favorite.viewModel.FavoriteViewModel
import com.example.weather.Repo.IWeatherRepo
import com.example.weather.data.FakeWeatherRepo
import com.example.weather.database.model.Favorite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class FavoriteViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var fakeRepo: IWeatherRepo

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeWeatherRepo()
        viewModel = FavoriteViewModel(fakeRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testFavouriteLocations_initialValueIsEmpty() = runTest {

        //Then
        assertEquals(0, viewModel.allFavoriteWeatherSF.value.size)
    }

   /* @Test
    fun getAllFavWeather_updatesStateFlowCorrectly() = runTest(timeout = 60.seconds) {

        val latch = CountDownLatch(1)

        viewModel.getAllFavWeather()

        // Collect in a coroutine to assert values
        launch {
            viewModel.allFavoriteWeatherSF.collect { value ->
                assertThat(value, `is`((fakeRepo as FakeWeatherRepo).fakeFavorites))
                latch.countDown() // Signal completion
            }
        }

        // Wait for the collection to complete
        latch.await(60, TimeUnit.SECONDS)
    }
*/
    @Test
    fun insertFavWeather_insertsCorrectly() = runTest {
        // Given
        val newFavorite = Favorite(0.0,0.0,"NewLocation")

        // When
        viewModel.insertFavWeather(newFavorite)

        // Advance coroutine
        testScheduler.advanceUntilIdle()

        // Then
        assertThat((fakeRepo as FakeWeatherRepo).insertedFavorites, hasItem(newFavorite))
    }

    @Test
    fun deleteFavWeather_deletesCorrectly() = runTest {
        // Given
        val favoriteToDelete = (fakeRepo as FakeWeatherRepo).fakeFavorites[0]

        // When
        viewModel.deleteFavWeather(favoriteToDelete)

        // Advance coroutine
        testScheduler.advanceUntilIdle()

        // Then
        assertThat((fakeRepo as FakeWeatherRepo).deletedFavorites, hasItem(favoriteToDelete))
    }
}
