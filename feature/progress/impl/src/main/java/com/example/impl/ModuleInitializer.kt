package ru.sirius.grable.feature.progress.impl

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.di.AbstractInitializer
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.sirius.grable.feature.progress.api.Constants
import ru.sirius.grable.navigation.api.FragmentProvider
import ru.sirius.grable.progress.StatisticsInteractor
import ru.sirius.grable.progress.StatisticsViewModel
import ru.sirius.grable.progress.StatsFragment
import ru.sirius.grable.progress.data.repository.StatisticsRepository
import ru.sirius.grable.progress.data.repository.StatisticsRepositoryImpl


internal class ModuleInitializer : AbstractInitializer<Unit>() {
    override fun create(context: Context) {
        loadKoinModules(statisticModule)
    }
}

val statisticModule = module {

    factory<FragmentProvider>(named(Constants.STATS_SCREEN)) {
        object : FragmentProvider {
            override fun create(arguments: Bundle?): Fragment {
                return StatsFragment().apply { this.arguments = arguments }
            }
        }
    }

    factory<Class<out Fragment>>(named(Constants.STATS_SCREEN)) { StatsFragment::class.java }

    factory<StatisticsRepository> { StatisticsRepositoryImpl(get()) }
    factory { StatisticsInteractor(get()) }
    viewModel { StatisticsViewModel(get()) }
}
