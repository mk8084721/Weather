package com.example.weather.Home.viewModel

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.weather.data.FakeWeatherRepo
import com.example.weather.database.model.HomeWeather
import com.example.weather.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

//import kotlin.test.assertEquals
//import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var fakeRepo: FakeWeatherRepo

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        fakeRepo = FakeWeatherRepo()
        viewModel = HomeViewModel(fakeRepo)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun insertHomeWeather_addsDataToRepository() = runTest {
        val homeWeather = HomeWeather(1, 30.0f, 31.0f,"location 1",
                "",
            "",
            "",
            0.0f,0.0f,""
            )

        viewModel.insertEmptyHomeWeather(homeWeather)

        // Check if homeWeather was added in Fake Repo
        assertEquals(1, fakeRepo.getHomeWeather().first().size)
        assertEquals(homeWeather, fakeRepo.getHomeWeather().first()[0])
    }

    @Test
    fun saveLocationSHP_savesCorrectLocation() {
        // Given
        val latitude = 30.0f
        val longitude = 31.0f

        // When
        viewModel.saveLocationSHP(ApplicationProvider.getApplicationContext(), longitude, latitude)

        // Then
        assertEquals(latitude, fakeRepo.savedLatitude)
        assertEquals(longitude, fakeRepo.savedLongitude)
    }
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }
}
