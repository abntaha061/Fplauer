package com.finalplayer.app.di

import androidx.room.Room
import com.finalplayer.app.data.database.FinalPlayerDatabase
import com.finalplayer.app.data.network.client.FtpNetworkClient
import com.finalplayer.app.data.network.client.HttpDirectClient
import com.finalplayer.app.data.network.client.NetworkClientFactory
import com.finalplayer.app.data.network.client.SmbNetworkClient
import com.finalplayer.app.data.network.proxy.ProxyManager
import com.finalplayer.app.data.preferences.OnboardingPreferences
import com.finalplayer.app.data.repository.MediaStoreVideoScanner
import com.finalplayer.app.data.repository.NetworkSourceRepositoryImpl
import com.finalplayer.app.data.repository.PlaybackRepositoryImpl
import com.finalplayer.app.data.repository.VideoRepositoryImpl
import com.finalplayer.app.domain.repository.NetworkSourceRepository
import com.finalplayer.app.domain.repository.PlaybackRepository
import com.finalplayer.app.domain.repository.VideoRepository
import com.finalplayer.app.domain.usecase.BuildStreamUrlUseCase
import com.finalplayer.app.domain.usecase.ConnectToNetworkSourceUseCase
import com.finalplayer.app.domain.usecase.GetRecentlyPlayedUseCase
import com.finalplayer.app.domain.usecase.GetVideoLibraryUseCase
import com.finalplayer.app.domain.usecase.GetVideosByFolderUseCase
import com.finalplayer.app.domain.usecase.SavePlaybackProgressUseCase
import com.finalplayer.app.domain.usecase.ScanForVideosUseCase
import com.finalplayer.app.player.PlayerViewModel
import com.finalplayer.app.player.core.MPVController
import com.finalplayer.app.ui.browser.NetworkBrowserViewModel
import com.finalplayer.app.ui.home.HomeViewModel
import com.finalplayer.app.ui.onboarding.OnboardingViewModel
import com.finalplayer.app.ui.search.SearchViewModel
import com.finalplayer.app.ui.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    includes(preferencesModule)

    // Preferences
    single { OnboardingPreferences(androidContext()) }

    // Database & DAOs
    single {
        Room.databaseBuilder(
            androidContext(),
            FinalPlayerDatabase::class.java,
            "finalplayer.db"
        ).fallbackToDestructiveMigration().build()
    }
    single { get<FinalPlayerDatabase>().videoDao() }
    single { get<FinalPlayerDatabase>().playbackProgressDao() }
    single { get<FinalPlayerDatabase>().playlistDao() }
    single { get<FinalPlayerDatabase>().networkSourceDao() }

    // Scanner & Repositories
    single { MediaStoreVideoScanner(androidContext()) }
    single<VideoRepository> { VideoRepositoryImpl(get(), get()) }
    single<PlaybackRepository> { PlaybackRepositoryImpl(get()) }
    single<NetworkSourceRepository> { NetworkSourceRepositoryImpl(get(), get()) }

    // MPV Player Controller
    single { MPVController(androidContext()) }

    // Network Clients & Proxy
    single { SmbNetworkClient() }
    single { FtpNetworkClient() }
    single { HttpDirectClient() }
    single { NetworkClientFactory(get(), get(), get()) }
    single { ProxyManager() }

    // Domain Use Cases
    factory { GetVideoLibraryUseCase(get()) }
    factory { GetVideosByFolderUseCase(get()) }
    factory { ScanForVideosUseCase(get()) }
    factory { SavePlaybackProgressUseCase(get()) }
    factory { GetRecentlyPlayedUseCase(get()) }
    factory { ConnectToNetworkSourceUseCase(get()) }
    factory { BuildStreamUrlUseCase(get()) }

    // ViewModels
    viewModel { OnboardingViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { PlayerViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get(), get(), get()) }
    viewModel { NetworkBrowserViewModel(get(), get()) }
    viewModel { SearchViewModel(get()) }
}

