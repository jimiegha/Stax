package com.hover.stax.data.remote

import com.hover.stax.data.remote.dto.StaxUserDto
import com.hover.stax.data.remote.dto.UserUpdateDto
import com.hover.stax.data.remote.dto.UserUploadDto
import com.hover.stax.data.remote.dto.authorization.NAuthRequest
import com.hover.stax.data.remote.dto.authorization.NAuthResponse
import com.hover.stax.data.remote.dto.authorization.NRevokeTokenRequest
import com.hover.stax.data.remote.dto.authorization.NTokenRefresh
import com.hover.stax.data.remote.dto.authorization.NTokenRequest
import com.hover.stax.data.remote.dto.authorization.NTokenResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.Flow

class StaxApi(
    private val client: HttpClient
) {

    suspend fun authorize(authRequest: NAuthRequest): NAuthResponse =
            client.post {
                url("${BASE_URL}authorize")
                setBody(authRequest)
            }.body()

    suspend fun fetchToken(tokenRequest: NTokenRequest): NTokenResponse =
            client.post {
                url("${BASE_URL}token")
                setBody(tokenRequest)
            }.body()

    suspend fun refreshToken(tokenRefresh: NTokenRefresh): NTokenResponse =
            client.post {
                url("${BASE_URL}token")
                setBody(tokenRefresh)
            }.body()

    suspend fun revokeToken(revokeToken: NRevokeTokenRequest): NTokenResponse =
            client.post {
                url("${BASE_URL}revoke")
                setBody(revokeToken)
            }.body()

    suspend fun uploadUserToStax(userDTO: UserUploadDto): StaxUserDto =
            client.post {
                url("${BASE_URL}stax_users")
                setBody(userDTO)
            }.body()

    suspend fun updateUser(email: String, userDTO: UserUpdateDto): StaxUserDto =
            client.post {
                url("${BASE_URL}stax_users/$email")
                setBody(userDTO)
            }.body()

    suspend fun getRewardPoints(email: String): StaxUserDto =
            client.post {
                url("$BASE_URL/api/rewards/reward_points/$email")
            }.body()

    companion object {
        const val BASE_URL = "https://stage.usehover.com/stax_api/"
    }
}