package com.sbdev.project.newsfirstapp.di

import android.app.Application
import androidx.room.Room
import com.sbdev.project.newsfirstapp.data.local.ArticleDatabase
import com.sbdev.project.newsfirstapp.data.repository.NewsRepository
import com.sbdev.project.newsfirstapp.data.repository.NewsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideArticleDatabase(app: Application): ArticleDatabase {
        return Room.databaseBuilder(
            app,
            ArticleDatabase::class.java,
            ArticleDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigrationOnDowngrade()
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsRepository(db: ArticleDatabase): NewsRepository {
        return NewsRepositoryImpl(db.getArticleDao())
    }

}