package com.erkindilekci.lifelog.domain.repository

import com.erkindilekci.lifelog.model.Diary
import com.erkindilekci.lifelog.util.RequestState
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

typealias Diaries = RequestState<Map<LocalDate, List<Diary>>>

interface MongoRepository {

    fun configureTheRealm()
    fun getAllDiaries(): Flow<Diaries>
}
