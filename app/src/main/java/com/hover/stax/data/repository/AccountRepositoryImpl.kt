package com.hover.stax.data.repository

import android.content.Context
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.api.ActionApi
import com.hover.stax.R
import com.hover.stax.data.local.actions.ActionRepo
import com.hover.stax.domain.model.Channel
import com.hover.stax.data.local.channels.ChannelRepo
import com.hover.stax.data.local.accounts.AccountRepo
import com.hover.stax.domain.model.Account
import com.hover.stax.domain.model.PLACEHOLDER
import com.hover.stax.domain.repository.AccountRepository
import com.hover.stax.notifications.PushNotificationTopicsHelper.joinChannelGroup
import com.hover.stax.utils.AnalyticsUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AccountRepositoryImpl(val accountRepo: AccountRepo, val channelRepo: ChannelRepo, val actionRepo: ActionRepo, private val coroutineDispatcher: CoroutineDispatcher) : AccountRepository, KoinComponent {

    private val context: Context by inject()

    override val fetchAccounts: Flow<List<Account>>
        get() = accountRepo.getAccounts()

    override suspend fun createAccounts(channels: List<Channel>): List<Long> {
        val defaultAccount = accountRepo.getDefaultAccountAsync()

        val accounts = channels.mapIndexed { index, channel ->
            val accountName: String = if (getFetchAccountAction(channel.id) == null) channel.name else channel.name.plus(PLACEHOLDER )//placeholder alias for easier identification later
            Account(
                accountName, channel.name, channel.logoUrl, channel.accountNo, channel.id, channel.countryAlpha2,
                channel.id, channel.primaryColorHex, channel.secondaryColorHex, defaultAccount == null && index == 0
            )
        }.onEach {
            logChoice(it)
            ActionApi.scheduleActionConfigUpdate(it.countryAlpha2, 24, context)
        }

        channels.onEach { it.selected = true }.also { channelRepo.update(it) }
        return accountRepo.insert(accounts)
    }

    override suspend fun setDefaultAccount(account: Account) {
        fetchAccounts.collect { accounts ->
            val current = accounts.firstOrNull { it.isDefault }?.also {
                it.isDefault = false
            }

            val defaultAccount = accounts.first { it.id == account.id }.also { it.isDefault = true }

            withContext(coroutineDispatcher) {
                launch {
                    accountRepo.update(listOf(current!!, defaultAccount))
                }
            }
        }
    }

    override suspend fun getAccount(accountId: Int): Account? = accountRepo.getAccount(accountId)

    override suspend fun getAccountsByChannel(channelId: Int): List<Account> = accountRepo.getAccountsByChannel(channelId)

    private fun getFetchAccountAction(channelId: Int): HoverAction? = actionRepo.getActions(channelId, HoverAction.FETCH_ACCOUNTS).firstOrNull()

    private fun logChoice(account: Account) {
        joinChannelGroup(account.channelId, context)
        val args = JSONObject()

        try {
            args.put(context.getString(R.string.added_channel_id), account.channelId)
        } catch (ignored: Exception) {
        }

        AnalyticsUtil.logAnalyticsEvent(context.getString(R.string.new_channel_selected), args, context)
    }
}