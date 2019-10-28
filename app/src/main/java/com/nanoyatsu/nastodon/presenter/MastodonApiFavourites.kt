package com.nanoyatsu.nastodon.presenter

import com.nanoyatsu.nastodon.model.Status
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface MastodonApiFavourites {

    // todo GET /api/v1/favourites
    // POST /api/v1/statuses/:id/favourite
    @POST("api/v1/statuses/{id}/favourite")
    suspend fun favourite(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Response<Status>


    // POST /api/v1/statuses/:id/unfavourite
    @POST("api/v1/statuses/{id}/unfavourite")
    suspend fun unFavourite(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Response<Status>
}