package edu.gvsu.cis.kit

import edu.gvsu.cis.kit.data.AppDAO
import edu.gvsu.cis.kit.data.KITRepository
import edu.gvsu.cis.kit.viewModels.CalendarViewModel
import edu.gvsu.cis.kit.viewModels.ContactsViewModel
import edu.gvsu.cis.kit.viewModels.HomeViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun appModule(dao: AppDAO) = module {
    single<AppDAO> { dao }

    single { KITRepository(get()) }

    factory { HomeViewModel(get()) }
    factory { ContactsViewModel(get()) }
    factory { CalendarViewModel(get()) }
}

fun initKoin(dao: AppDAO) {
    try {
        startKoin {
            modules(appModule(dao))
        }
    } catch (_: Exception) { }
}