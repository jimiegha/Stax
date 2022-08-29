package com.hover.stax.domain.repository

import com.hover.stax.domain.model.Account
import com.hover.stax.domain.model.Channel
import kotlinx.coroutines.flow.Flow

interface AccountRepository {

    val fetchAccounts: Flow<List<Account>>

    suspend fun createAccounts(channels: List<Channel>): List<Long>

    suspend fun setDefaultAccount(account: Account)
}