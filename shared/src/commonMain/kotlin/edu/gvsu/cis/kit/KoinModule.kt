package edu.gvsu.cis.kit

import edu.gvsu.cis.kit.data.AppDAO
import edu.gvsu.cis.kit.data.KITRepository
import edu.gvsu.cis.kit.viewModels.ContactsViewModel
import edu.gvsu.cis.kit.viewModels.HomeViewModel
import edu.gvsu.cis.kit.viewModels.RemindersViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun appModule(dao: AppDAO) = module {
    single<AppDAO> { dao }

    single { KITRepository(get()) }

    factory { HomeViewModel(get()) }
    factory { ContactsViewModel(get()) }
    factory { RemindersViewModel(get()) }
}

fun initKoin(dao: AppDAO, context: Any? = null) {
    try {
        startKoin {
            modules(appModule(dao))
        }
        // Initialize platform context safely
        if (context != null) {
            initPlatformContext(context)
        }
    } catch (_: Exception) { }
}

// Platform-specific hook (actual implementations define this inside Platform.kt for iOS/Android)
expect fun initPlatformContext(context: Any)