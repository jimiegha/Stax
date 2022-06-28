package com.hover.stax.domain.repository

import com.hover.stax.data.model.Account
import com.hover.stax.channels.Channel
import kotlinx.coroutines.flow.Flow

interface AccountRepository {

    suspend fun fetchAccounts(): Flow<List<Account>>

    suspend fun createAccounts(channels: List<Channel>): List<Long>

    suspend fun setDefaultAccount(account: Account)
}