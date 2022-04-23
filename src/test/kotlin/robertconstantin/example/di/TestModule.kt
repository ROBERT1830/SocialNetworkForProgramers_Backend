package robertconstantin.example.di

import org.koin.dsl.module
import robertconstantin.example.repository.user.FakeUserRepository

internal val testModule = module {
    single {
        FakeUserRepository()
    }
}