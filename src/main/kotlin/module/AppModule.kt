package org.delcom.module

import org.delcom.repositories.IDollRepository
import org.delcom.repositories.DollRepository
import org.delcom.repositories.IPlantRepository
import org.delcom.repositories.PlantRepository
import org.delcom.services.DollService
import org.delcom.services.PlantService
import org.delcom.services.ProfileService
import org.koin.dsl.module

val appModule = module {
    // Plant Repository
    single<IPlantRepository> {
        PlantRepository()
    }

    // Doll Repository (Versi Boneka)
    single<IDollRepository> {
        DollRepository()
    }

    // Plant Service
    single {
        PlantService(get())
    }

    // Doll Service (Versi Boneka)
    single {
        DollService(get())
    }

    // Profile Service
    single {
        ProfileService()
    }
}