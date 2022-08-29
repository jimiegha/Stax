package com.hover.stax.futureTransactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hover.stax.domain.model.Channel
import com.hover.stax.data.local.channels.ChannelRepo
import com.hover.stax.schedules.ScheduleRepo
import com.hover.stax.requests.Request
import com.hover.stax.requests.RequestRepo
import com.hover.stax.schedules.Schedule
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class FutureViewModel(val repo: RequestRepo, val channelRepo: ChannelRepo, val scheduleRepo: ScheduleRepo) : ViewModel() {

    var scheduled: LiveData<List<Schedule>> = MutableLiveData()
    var requests: LiveData<List<Request>> = MutableLiveData()

    init {
        scheduled = scheduleRepo.futureTransactions
        requests = repo.liveRequests
    }

    fun requestsByChannel(channelId: Int): LiveData<List<Request>> {
        val channel = runBlocking {
            getChannelAsync(channelId).await()
        }

        return repo.getLiveRequests(channel.institutionId)
    }

    fun scheduledByChannel(channelId: Int): LiveData<List<Schedule>> = scheduleRepo.getFutureTransactions(channelId)

    private fun getChannelAsync(channelId: Int): Deferred<Channel> = viewModelScope.async(Dispatchers.IO) {
        channelRepo.getChannel(channelId)!!
    }
}