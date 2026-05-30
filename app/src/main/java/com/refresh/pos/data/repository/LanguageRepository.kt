package com.refresh.pos.data.repository

import com.refresh.pos.data.local.dao.LanguageDao
import com.refresh.pos.data.local.entity.LanguageEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class LanguageRepository @Inject constructor(
    private val languageDao: LanguageDao
) {
    fun getLanguage(): Flow<String> =
        languageDao.get().map { it?.language ?: "en" }

    suspend fun setLanguage(locale: String) {
        languageDao.upsert(LanguageEntity(id = 1, language = locale))
    }
}
