package com.marketplace.app

import android.content.Context
import com.marketplace.app.data.local.database.AppDatabase
import com.marketplace.app.data.local.dao.VendedorDao
import com.marketplace.app.data.local.dao.ProductoDao
import com.marketplace.app.data.remote.api.RetrofitClient
import com.marketplace.app.data.remote.api.VendedorApiService
import com.marketplace.app.data.remote.api.ProductoApiService
import com.marketplace.app.domain.repository.VendedorRepository
import com.marketplace.app.domain.repository.ProductoRepository
import com.marketplace.app.domain.repository.ProductoRepositoryImpl
import com.marketplace.app.domain.repository.VendedorRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideVendedorDao(appDatabase: AppDatabase): VendedorDao {
        return appDatabase.vendedorDao()
    }

    @Provides
    @Singleton
    fun provideProductoDao(appDatabase: AppDatabase): ProductoDao {
        return appDatabase.productoDao()
    }

    @Provides
    @Singleton
    fun provideRetrofitClient(): RetrofitClient {
        return RetrofitClient()
    }

    @Provides
    @Singleton
    fun provideVendedorApiService(retrofitClient: RetrofitClient): VendedorApiService {
        return retrofitClient.vendedorApiService
    }

    @Provides
    @Singleton
    fun provideProductoApiService(retrofitClient: RetrofitClient): ProductoApiService {
        return retrofitClient.productoApiService
    }

    @Provides
    @Singleton
    fun provideVendedorRepository(
        vendedorDao: VendedorDao,
        vendedorApiService: VendedorApiService
    ): VendedorRepository {
        return VendedorRepositoryImpl(vendedorDao, vendedorApiService)
    }

    @Provides
    @Singleton
    fun provideProductoRepository(
        productoDao: ProductoDao,
        productoApiService: ProductoApiService
    ): ProductoRepository {
        return ProductoRepositoryImpl(productoDao, productoApiService)
    }
}