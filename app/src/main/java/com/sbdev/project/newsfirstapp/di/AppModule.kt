package com.sbdev.project.newsfirstapp.di

import android.app.Application
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sbdev.project.newsfirstapp.data.local.ArticleDatabase
import com.sbdev.project.newsfirstapp.data.remote.FirebaseAuthSource
import com.sbdev.project.newsfirstapp.data.repository.AuthRepository
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
    fun providesFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideAuthSource(auth: FirebaseAuth): FirebaseAuthSource = FirebaseAuthSource(auth)

    @Provides
    @Singleton
    fun provideAuthRepository(authSource: FirebaseAuthSource): AuthRepository =
        AuthRepository(authSource)

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