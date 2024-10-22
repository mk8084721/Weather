package com.example.weather.Repo

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.UserHandle
import android.view.Display
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather.Repo.WeatherRepo
import com.example.weather.database.ILocalDataSource
import com.example.weather.database.LocalDataSource
import com.example.weather.database.WeatherDao
import com.example.weather.database.model.Alert
import com.example.weather.database.model.Favorite
import com.example.weather.database.model.HomeWeather
import com.example.weather.database.model.Hourly
import com.example.weather.network.ApiService
import com.example.weather.network.model.Clouds
import com.example.weather.network.model.Coord
import com.example.weather.network.model.CurrentWeather
import com.example.weather.network.model.ForecastWeather
import com.example.weather.network.model.Temp
import com.example.weather.network.model.WeatherStatus
import com.example.weather.network.model.Wind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
@RunWith(AndroidJUnit4::class)
class WeatherRepoTest {

    private lateinit var weatherRepo: WeatherRepo
    private lateinit var localDataSource: FakeLocalDataSource
    private lateinit var remoteDataSource: FakeApiService

    @Before
    fun setUp() {
        localDataSource = FakeLocalDataSource()
        remoteDataSource = FakeApiService()
        weatherRepo = WeatherRepo(localDataSource, remoteDataSource)
    }

    @Test
    fun `getCurrentWeather should return correct data from remote`() = runTest {
        val lon = 30.0f
        val lat = 50.0f
        val currentWeather = weatherRepo.getCurrentWeather(lon, lat).first()

        assertEquals(lon, currentWeather.coord.longitude, 0.01f)
    }

    @Test
    fun `getWeatherForecast should return correct data from remote`() = runTest {
        val lon = 30.0f
        val lat = 50.0f
        val currentWeather = weatherRepo.getWeatherForecast(lon, lat).first()

        assertEquals(0, currentWeather.list.size)
    }

    @Test
    fun `getLocationFromPreferences should return correct location`() = runTest {
        // Insert a sample location into the fake shared prefs
        localDataSource.saveLocation(30.0f, 50.0f)

        val (lat, lon) = weatherRepo.getLocationFromPreferences(localDataSource.context)

        assertEquals(30.0f, lat)
        assertEquals(50.0f, lon)
    }

    // You can add more tests for other methods, following similar patterns
}

// Fake LocalDataSource implementation for testing
class FakeLocalDataSource() : ILocalDataSource {
    private val homeWeatherList = mutableListOf<HomeWeather>()
    override var context: Context = ApplicationProvider.getApplicationContext()

    override fun getHomeWeather() = flow { emit(homeWeatherList) }

    override suspend fun insertHomeWeather(homeWeather: List<HomeWeather>) {
        homeWeatherList.addAll(homeWeather)
    }

    override fun updateHomeWeather(homeWeather: List<HomeWeather>) {
        // Update logic here if needed
    }

    override fun deleteHomeWeather(homeWeather: List<HomeWeather>) {
        TODO("Not yet implemented")
    }

    override fun getAllFavWeather(): Flow<List<Favorite>> {
        TODO("Not yet implemented")
    }

    override fun insertFavWeather(favWeather: List<Favorite>) {
        TODO("Not yet implemented")
    }

    override fun updateFavWeather(favWeather: List<Favorite>) {
        TODO("Not yet implemented")
    }

    override fun deleteFavWeather(favWeather: List<Favorite>) {
        TODO("Not yet implemented")
    }

    override fun getAlerts(): Flow<List<Alert>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlert(alert: Alert) {
        TODO("Not yet implemented")
    }

    override fun getHourlyWeather(): Flow<List<Hourly>> {
        TODO("Not yet implemented")
    }

    override fun insertHourlyWeather(hourlyWeather: List<Hourly>) {
        TODO("Not yet implemented")
    }

    override fun updateHourlyWeather(hourlyWeather: List<Hourly>) {
        TODO("Not yet implemented")
    }

    override fun deleteHourlyWeather(hourlyWeather: List<Hourly>) {
        TODO("Not yet implemented")
    }

    override fun clearHourlyTable() {
        TODO("Not yet implemented")
    }

    override fun deleteAlert(alert: Alert) {
        TODO("Not yet implemented")
    }

    // Simulate saving location to shared preferences
    fun saveLocation(lat: Float, lon: Float) {
        val sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("lat", lat)
        editor.putFloat("lon", lon)
        editor.apply()
    }

}

// Fake ApiService implementation for testing
class FakeApiService : ApiService {

    override suspend fun getCurrentWeather(
        lon: Float,
        lat: Float,
        lang: String,
        units: String,
        apiKey: String
    ): CurrentWeather {
        return CurrentWeather(Coord(lon,lat), arrayOf(WeatherStatus(1,"","","")),"",
            Temp(0.0f,0.0f,0.0f,0.0f,0,0,0,0),
            0,
            Wind(0.0f,0,0.0f),
            0,0,"",0,"", Clouds(0)
        ) // Return a fake current weather
    }

    override suspend fun getWeatherForecast(
        lon: Float,
        lat: Float,
        lang: String,
        units: String,
        apiKey: String
    ): ForecastWeather {
        return ForecastWeather(
            emptyArray()
        )
    }
}
/*

// Simulated context for shared preferences
class TestContext : Context() {
    private val prefs = mutableMapOf<String, MutableMap<String, Float>>()
    override fun getAssets(): AssetManager {
        TODO("Not yet implemented")
    }

    override fun getResources(): Resources {
        TODO("Not yet implemented")
    }

    override fun getPackageManager(): PackageManager {
        TODO("Not yet implemented")
    }

    override fun getContentResolver(): ContentResolver {
        TODO("Not yet implemented")
    }

    override fun getMainLooper(): Looper {
        TODO("Not yet implemented")
    }

    override fun getApplicationContext(): Context {
        TODO("Not yet implemented")
    }

    override fun setTheme(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun getTheme(): Resources.Theme {
        TODO("Not yet implemented")
    }

    override fun getClassLoader(): ClassLoader {
        TODO("Not yet implemented")
    }

    override fun getPackageName(): String {
        TODO("Not yet implemented")
    }

    override fun getApplicationInfo(): ApplicationInfo {
        TODO("Not yet implemented")
    }

    override fun getPackageResourcePath(): String {
        TODO("Not yet implemented")
    }

    override fun getPackageCodePath(): String {
        TODO("Not yet implemented")
    }

    override fun getSharedPreferences(name: String, mode: Int): SharedPreferences {
        return TestSharedPreferences(prefs.getOrPut(name) { mutableMapOf() })
    }

    override fun moveSharedPreferencesFrom(p0: Context?, p1: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun deleteSharedPreferences(p0: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun openFileInput(p0: String?): FileInputStream {
        TODO("Not yet implemented")
    }

    override fun openFileOutput(p0: String?, p1: Int): FileOutputStream {
        TODO("Not yet implemented")
    }

    override fun deleteFile(p0: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getFileStreamPath(p0: String?): File {
        TODO("Not yet implemented")
    }

    override fun getDataDir(): File {
        TODO("Not yet implemented")
    }

    override fun getFilesDir(): File {
        TODO("Not yet implemented")
    }

    override fun getNoBackupFilesDir(): File {
        TODO("Not yet implemented")
    }

    override fun getExternalFilesDir(p0: String?): File? {
        TODO("Not yet implemented")
    }

    override fun getExternalFilesDirs(p0: String?): Array<File> {
        TODO("Not yet implemented")
    }

    override fun getObbDir(): File {
        TODO("Not yet implemented")
    }

    override fun getObbDirs(): Array<File> {
        TODO("Not yet implemented")
    }

    override fun getCacheDir(): File {
        TODO("Not yet implemented")
    }

    override fun getCodeCacheDir(): File {
        TODO("Not yet implemented")
    }

    override fun getExternalCacheDir(): File? {
        TODO("Not yet implemented")
    }

    override fun getExternalCacheDirs(): Array<File> {
        TODO("Not yet implemented")
    }

    override fun getExternalMediaDirs(): Array<File> {
        TODO("Not yet implemented")
    }

    override fun fileList(): Array<String> {
        TODO("Not yet implemented")
    }

    override fun getDir(p0: String?, p1: Int): File {
        TODO("Not yet implemented")
    }

    override fun openOrCreateDatabase(
        p0: String?,
        p1: Int,
        p2: SQLiteDatabase.CursorFactory?
    ): SQLiteDatabase {
        TODO("Not yet implemented")
    }

    override fun openOrCreateDatabase(
        p0: String?,
        p1: Int,
        p2: SQLiteDatabase.CursorFactory?,
        p3: DatabaseErrorHandler?
    ): SQLiteDatabase {
        TODO("Not yet implemented")
    }

    override fun moveDatabaseFrom(p0: Context?, p1: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun deleteDatabase(p0: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getDatabasePath(p0: String?): File {
        TODO("Not yet implemented")
    }

    override fun databaseList(): Array<String> {
        TODO("Not yet implemented")
    }

    override fun getWallpaper(): Drawable {
        TODO("Not yet implemented")
    }

    override fun peekWallpaper(): Drawable {
        TODO("Not yet implemented")
    }

    override fun getWallpaperDesiredMinimumWidth(): Int {
        TODO("Not yet implemented")
    }

    override fun getWallpaperDesiredMinimumHeight(): Int {
        TODO("Not yet implemented")
    }

    override fun setWallpaper(p0: Bitmap?) {
        TODO("Not yet implemented")
    }

    override fun setWallpaper(p0: InputStream?) {
        TODO("Not yet implemented")
    }

    override fun clearWallpaper() {
        TODO("Not yet implemented")
    }

    override fun startActivity(p0: Intent?) {
        TODO("Not yet implemented")
    }

    override fun startActivity(p0: Intent?, p1: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun startActivities(p0: Array<out Intent>?) {
        TODO("Not yet implemented")
    }

    override fun startActivities(p0: Array<out Intent>?, p1: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun startIntentSender(p0: IntentSender?, p1: Intent?, p2: Int, p3: Int, p4: Int) {
        TODO("Not yet implemented")
    }

    override fun startIntentSender(
        p0: IntentSender?,
        p1: Intent?,
        p2: Int,
        p3: Int,
        p4: Int,
        p5: Bundle?
    ) {
        TODO("Not yet implemented")
    }

    override fun sendBroadcast(p0: Intent?) {
        TODO("Not yet implemented")
    }

    override fun sendBroadcast(p0: Intent?, p1: String?) {
        TODO("Not yet implemented")
    }

    override fun sendOrderedBroadcast(p0: Intent?, p1: String?) {
        TODO("Not yet implemented")
    }

    override fun sendOrderedBroadcast(
        p0: Intent,
        p1: String?,
        p2: BroadcastReceiver?,
        p3: Handler?,
        p4: Int,
        p5: String?,
        p6: Bundle?
    ) {
        TODO("Not yet implemented")
    }

    override fun sendBroadcastAsUser(p0: Intent?, p1: UserHandle?) {
        TODO("Not yet implemented")
    }

    override fun sendBroadcastAsUser(p0: Intent?, p1: UserHandle?, p2: String?) {
        TODO("Not yet implemented")
    }

    override fun sendOrderedBroadcastAsUser(
        p0: Intent?,
        p1: UserHandle?,
        p2: String?,
        p3: BroadcastReceiver?,
        p4: Handler?,
        p5: Int,
        p6: String?,
        p7: Bundle?
    ) {
        TODO("Not yet implemented")
    }

    override fun sendStickyBroadcast(p0: Intent?) {
        TODO("Not yet implemented")
    }

    override fun sendStickyOrderedBroadcast(
        p0: Intent?,
        p1: BroadcastReceiver?,
        p2: Handler?,
        p3: Int,
        p4: String?,
        p5: Bundle?
    ) {
        TODO("Not yet implemented")
    }

    override fun removeStickyBroadcast(p0: Intent?) {
        TODO("Not yet implemented")
    }

    override fun sendStickyBroadcastAsUser(p0: Intent?, p1: UserHandle?) {
        TODO("Not yet implemented")
    }

    override fun sendStickyOrderedBroadcastAsUser(
        p0: Intent?,
        p1: UserHandle?,
        p2: BroadcastReceiver?,
        p3: Handler?,
        p4: Int,
        p5: String?,
        p6: Bundle?
    ) {
        TODO("Not yet implemented")
    }

    override fun removeStickyBroadcastAsUser(p0: Intent?, p1: UserHandle?) {
        TODO("Not yet implemented")
    }

    override fun registerReceiver(p0: BroadcastReceiver?, p1: IntentFilter?): Intent? {
        TODO("Not yet implemented")
    }

    override fun registerReceiver(p0: BroadcastReceiver?, p1: IntentFilter?, p2: Int): Intent? {
        TODO("Not yet implemented")
    }

    override fun registerReceiver(
        p0: BroadcastReceiver?,
        p1: IntentFilter?,
        p2: String?,
        p3: Handler?
    ): Intent? {
        TODO("Not yet implemented")
    }

    override fun registerReceiver(
        p0: BroadcastReceiver?,
        p1: IntentFilter?,
        p2: String?,
        p3: Handler?,
        p4: Int
    ): Intent? {
        TODO("Not yet implemented")
    }

    override fun unregisterReceiver(p0: BroadcastReceiver?) {
        TODO("Not yet implemented")
    }

    override fun startService(p0: Intent?): ComponentName? {
        TODO("Not yet implemented")
    }

    override fun startForegroundService(p0: Intent?): ComponentName? {
        TODO("Not yet implemented")
    }

    override fun stopService(p0: Intent?): Boolean {
        TODO("Not yet implemented")
    }

    override fun bindService(p0: Intent, p1: ServiceConnection, p2: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun unbindService(p0: ServiceConnection) {
        TODO("Not yet implemented")
    }

    override fun startInstrumentation(p0: ComponentName, p1: String?, p2: Bundle?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getSystemService(p0: String): Any {
        TODO("Not yet implemented")
    }

    override fun getSystemServiceName(p0: Class<*>): String? {
        TODO("Not yet implemented")
    }

    override fun checkPermission(p0: String, p1: Int, p2: Int): Int {
        TODO("Not yet implemented")
    }

    override fun checkCallingPermission(p0: String): Int {
        TODO("Not yet implemented")
    }

    override fun checkCallingOrSelfPermission(p0: String): Int {
        TODO("Not yet implemented")
    }

    override fun checkSelfPermission(p0: String): Int {
        TODO("Not yet implemented")
    }

    override fun enforcePermission(p0: String, p1: Int, p2: Int, p3: String?) {
        TODO("Not yet implemented")
    }

    override fun enforceCallingPermission(p0: String, p1: String?) {
        TODO("Not yet implemented")
    }

    override fun enforceCallingOrSelfPermission(p0: String, p1: String?) {
        TODO("Not yet implemented")
    }

    override fun grantUriPermission(p0: String?, p1: Uri?, p2: Int) {
        TODO("Not yet implemented")
    }

    override fun revokeUriPermission(p0: Uri?, p1: Int) {
        TODO("Not yet implemented")
    }

    override fun revokeUriPermission(p0: String?, p1: Uri?, p2: Int) {
        TODO("Not yet implemented")
    }

    override fun checkUriPermission(p0: Uri?, p1: Int, p2: Int, p3: Int): Int {
        TODO("Not yet implemented")
    }

    override fun checkUriPermission(
        p0: Uri?,
        p1: String?,
        p2: String?,
        p3: Int,
        p4: Int,
        p5: Int
    ): Int {
        TODO("Not yet implemented")
    }

    override fun checkCallingUriPermission(p0: Uri?, p1: Int): Int {
        TODO("Not yet implemented")
    }

    override fun checkCallingOrSelfUriPermission(p0: Uri?, p1: Int): Int {
        TODO("Not yet implemented")
    }

    override fun enforceUriPermission(p0: Uri?, p1: Int, p2: Int, p3: Int, p4: String?) {
        TODO("Not yet implemented")
    }

    override fun enforceUriPermission(
        p0: Uri?,
        p1: String?,
        p2: String?,
        p3: Int,
        p4: Int,
        p5: Int,
        p6: String?
    ) {
        TODO("Not yet implemented")
    }

    override fun enforceCallingUriPermission(p0: Uri?, p1: Int, p2: String?) {
        TODO("Not yet implemented")
    }

    override fun enforceCallingOrSelfUriPermission(p0: Uri?, p1: Int, p2: String?) {
        TODO("Not yet implemented")
    }

    override fun createPackageContext(p0: String?, p1: Int): Context {
        TODO("Not yet implemented")
    }

    override fun createContextForSplit(p0: String?): Context {
        TODO("Not yet implemented")
    }

    override fun createConfigurationContext(p0: Configuration): Context {
        TODO("Not yet implemented")
    }

    override fun createDisplayContext(p0: Display): Context {
        TODO("Not yet implemented")
    }

    override fun createDeviceProtectedStorageContext(): Context {
        TODO("Not yet implemented")
    }

    override fun isDeviceProtectedStorage(): Boolean {
        TODO("Not yet implemented")
    }

    // Other required context methods can be simulated here
}

class TestSharedPreferences(private val data: MutableMap<String, Float>) : SharedPreferences {
    override fun getAll(): MutableMap<String, *> {
        TODO("Not yet implemented")
    }

    override fun getString(p0: String?, p1: String?): String? {
        TODO("Not yet implemented")
    }

    override fun getStringSet(p0: String?, p1: MutableSet<String>?): MutableSet<String>? {
        TODO("Not yet implemented")
    }

    override fun getInt(p0: String?, p1: Int): Int {
        TODO("Not yet implemented")
    }

    override fun getLong(p0: String?, p1: Long): Long {
        TODO("Not yet implemented")
    }

    override fun getFloat(key: String, defValue: Float): Float {
        return data.getOrDefault(key, defValue)
    }

    override fun getBoolean(p0: String?, p1: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun contains(p0: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun edit(): SharedPreferences.Editor {
        return TestSharedPreferencesEditor(data)
    }

    override fun registerOnSharedPreferenceChangeListener(p0: SharedPreferences.OnSharedPreferenceChangeListener?) {
        TODO("Not yet implemented")
    }

    override fun unregisterOnSharedPreferenceChangeListener(p0: SharedPreferences.OnSharedPreferenceChangeListener?) {
        TODO("Not yet implemented")
    }

    // Implement other SharedPreferences methods if needed
}

class TestSharedPreferencesEditor(private val data: MutableMap<String, Float>) : SharedPreferences.Editor {
    override fun putString(p0: String?, p1: String?): SharedPreferences.Editor {
        TODO("Not yet implemented")
    }

    override fun putStringSet(p0: String?, p1: MutableSet<String>?): SharedPreferences.Editor {
        TODO("Not yet implemented")
    }

    override fun putInt(p0: String?, p1: Int): SharedPreferences.Editor {
        TODO("Not yet implemented")
    }

    override fun putLong(p0: String?, p1: Long): SharedPreferences.Editor {
        TODO("Not yet implemented")
    }

    override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
        data[key] = value
        return this
    }

    override fun putBoolean(p0: String?, p1: Boolean): SharedPreferences.Editor {
        TODO("Not yet implemented")
    }

    override fun remove(p0: String?): SharedPreferences.Editor {
        TODO("Not yet implemented")
    }

    override fun clear(): SharedPreferences.Editor {
        TODO("Not yet implemented")
    }

    override fun commit(): Boolean {
        TODO("Not yet implemented")
    }

    override fun apply() {
        // Apply changes to map
    }

    // Implement other Editor methods if needed
}
*/
