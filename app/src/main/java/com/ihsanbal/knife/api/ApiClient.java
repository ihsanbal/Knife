/*
 * Created by ihsan on 5/14/17 6:39 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 5/1/17 1:14 PM
 *
 */

package com.ihsanbal.knife.api;

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Media;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * @author ihsan on 05/04/2017.
 */
public class ApiClient extends TwitterApiClient {

    public ApiClient(TwitterSession session, OkHttpClient okHttpClient) {
        super(session, okHttpClient);
    }

    public Api getService() {
        return getService(Api.class);
    }

    public interface Api {
        @GET("/1.1/statuses/show.json")
        Observable<Tweet> status(@Query("id") long id);

        @GET("/1.1/statuses/home_timeline.json")
        Observable<List<Tweet>> tweets(@Query("user_id") long user_id);

        @GET("/1.1/statuses/home_timeline.json")
        Observable<List<Tweet>> tweets(@Query("user_id") long user_id, @Query("max_id") long max_id);

        @GET("/1.1/statuses/home_timeline.json")
        Observable<List<Tweet>> news(@Query("user_id") long user_id, @Query("since_id") long since_id);

        @GET("/1.1/users/show.json")
        Observable<User> show(@Query("user_id") long id);

        @POST("/1.1/statuses/update.json")
        Observable<Tweet> status(@Query("status") String status, @Query("in_reply_to_status_id") Long id, @Query("media_ids") List<Long> media_ids);

        @Multipart
        @POST("https://upload.twitter.com/1.1/media/upload.json")
        Observable<Media> upload(@Part("media") RequestBody media,
                                 @Part("media_data") RequestBody mediaData,
                                 @Part("additional_owners") RequestBody additionalOwners);
    }

}
