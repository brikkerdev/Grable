package ru.sirius.grable.core.database

import android.content.Context
import ru.sirius.di.AbstractInitializer
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

internal class ModuleInitializer : AbstractInitializer<Unit>() {
    override fun create(context: Context) {
        loadKoinModules(databaseModule)
    }
}

val databaseModule = module {
    single<AppDatabase> {
        AppDatabase.getDatabase(get())
    }
    
    // Provide DAOs as well for convenience
    factory { get<AppDatabase>().wordDao() }
    factory { get<AppDatabase>().playlistDao() }
    factory { get<AppDatabase>().statisticsDao() }
    
    // Data sync service
    single { DataSyncService(get<AppDatabase>()) }
}

