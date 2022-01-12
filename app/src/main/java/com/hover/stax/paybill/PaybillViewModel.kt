package com.hover.stax.paybill

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hover.sdk.actions.HoverAction
import com.hover.stax.R
import com.hover.stax.accounts.Account
import com.hover.stax.database.DatabaseRepo
import com.hover.stax.utils.UIHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class PaybillViewModel(val repo: PaybillRepo, private val dbRepo: DatabaseRepo, val application: Application) : ViewModel() {

    val savedPaybills = MutableLiveData<List<Paybill>>()
    val popularPaybills = MutableLiveData<List<HoverAction>>()

    val selectedPaybill = MutableLiveData<Paybill>()
    val businessNumber = MutableLiveData<String>()
    val accountNumber = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val amount = MutableLiveData<String>()
    val iconDrawable = MutableLiveData<Int>()
    val isEditing = MutableLiveData(true)

    fun getSavedPaybills(accountId: Int) = viewModelScope.launch {
        repo.getSavedPaybills(accountId).collect { savedPaybills.postValue(it) }
    }

    fun getPopularPaybills(accountId: Int) = viewModelScope.launch(Dispatchers.IO) {
        dbRepo.getAccount(accountId)?.let {
            val actions = dbRepo.getActions(it.channelId, HoverAction.C2B)
            popularPaybills.postValue(actions)
        }
    }

    fun selectPaybill(paybill: Paybill) {
        selectedPaybill.value = paybill
    }

    fun setBusinessNumber(number: String) {
        businessNumber.value = number
    }

    fun setAccountNumber(number: String) {
        accountNumber.value = number
    }

    fun setAmount(value: String) {
        amount.value = value
    }

    fun setIconDrawable(drawable: Int) {
        iconDrawable.value = drawable
    }

    fun setNickname(nickname: String) {
        name.value = nickname
    }

    fun setEditing(editing: Boolean) {
        isEditing.value = editing
    }

    fun savePaybill(account: Account?, recurringAmount: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        val businessNo = businessNumber.value
        val accountNo = accountNumber.value

        if (account != null) {
            val payBill = Paybill(name.value!!, businessNo!!, accountNo, account.channelId, account.id, account.logoUrl).apply {
                isSaved = true
                logo = iconDrawable.value ?: 0
            }
            if (recurringAmount) payBill.recurringAmount = amount.value!!.toInt()

            repo.save(payBill)

            launch(Dispatchers.Main) {
                UIHelper.flashMessage(application.applicationContext, R.string.paybill_save_success) //TODO add to other language strings
            }
        } else {
            Timber.e("Active account not set")
        }
    }

    fun businessNoError(): String? = if (businessNumber.value.isNullOrEmpty())
        application.getString(R.string.paybill_error_business_number)
    else null

    fun amountError(): String? {
        return if (!amount.value.isNullOrEmpty() && amount.value!!.matches("[\\d.]+".toRegex()) && !amount.value!!.matches("[0]+".toRegex())) null
        else application.getString(R.string.amount_fielderror)
    }

    fun accountNoError(): String? = if (accountNumber.value.isNullOrEmpty())
        application.getString(R.string.transfer_error_recipient_account)
    else null

    fun nameError(): String? = if (name.value.isNullOrEmpty())
        application.getString(R.string.bill_name_error)
    else null

    fun deletePaybill(paybill: Paybill) = viewModelScope.launch(Dispatchers.IO) {
        repo.delete(paybill)

        launch(Dispatchers.Main) {
            UIHelper.flashMessage(application.applicationContext, R.string.paybill_delete_success)
        }
    }
}