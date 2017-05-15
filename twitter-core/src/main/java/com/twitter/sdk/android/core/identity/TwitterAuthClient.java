/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.twitter.sdk.android.core.identity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import io.fabric.sdk.android.Fabric;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterAuthException;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.scribe.DefaultScribeClient;
import com.twitter.sdk.android.core.internal.scribe.EventNamespace;
import com.twitter.sdk.android.core.internal.scribe.TwitterCoreScribeClientHolder;

/**
 * Client for requesting authorization and email from the user.
 */
public class TwitterAuthClient {

    private static class AuthStateLazyHolder {
        private static final AuthState INSTANCE = new AuthState();
    }

    private static final String SCRIBE_CLIENT = "android";
    private static final String SCRIBE_LOGIN_PAGE = "login";
    private static final String SCRIBE_SHARE_EMAIL_PAGE = "shareemail";
    private static final String SCRIBE_SECTION = ""; // intentionally blank
    private static final String SCRIBE_COMPONENT = ""; // intentionally blank
    private static final String SCRIBE_ELEMENT = ""; // intentionally blank
    private static final String SCRIBE_ACTION = "impression";

    final AuthState authState;
    final SessionManager<TwitterSession> sessionManager;

    private final Context context;
    private final TwitterAuthConfig authConfig;

    public int getRequestCode() {
        return authConfig.getRequestCode();
    }

    /**
     * Constructor.
     *
     * @throws java.lang.IllegalStateException if called before starting Twitter Kit with
     *                                         Fabric.with()
     */
    public TwitterAuthClient() {
        this(TwitterCore.getInstance().getContext(), TwitterCore.getInstance().getAuthConfig(),
                TwitterCore.getInstance().getSessionManager(), AuthStateLazyHolder.INSTANCE);
    }

    TwitterAuthClient(Context context, TwitterAuthConfig authConfig,
            SessionManager<TwitterSession> sessionManager, AuthState authState) {
        this.authState = authState;
        this.context = context;
        this.authConfig = authConfig;
        this.sessionManager = sessionManager;
    }

     /**
     * Requests authorization.
     *
     * @param activity The {@link android.app.Activity} context to use for the authorization flow.
     * @param callback The callback interface to invoke when authorization completes.
     * @throws java.lang.IllegalArgumentException if activity or callback is null.
     */
    public void authorize(Activity activity, Callback<TwitterSession> callback) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity must not be null.");
        }
        if (callback == null) {
            throw new IllegalArgumentException("Callback must not be null.");
        }

        if (activity.isFinishing()) {
            Fabric.getLogger().e(TwitterCore.TAG, "Cannot authorize, activity is finishing.", null);
        } else {
            handleAuthorize(activity, callback);
        }
    }

    private void handleAuthorize(Activity activity, Callback<TwitterSession> callback) {
        scribeAuthorizeImpression();
        final CallbackWrapper callbackWrapper = new CallbackWrapper(sessionManager, callback);
        if (!authorizeUsingSSO(activity, callbackWrapper)
                && !authorizeUsingOAuth(activity, callbackWrapper)) {
            callbackWrapper.failure(new TwitterAuthException("Authorize failed."));
        }
    }

    private boolean authorizeUsingSSO(Activity activity, CallbackWrapper callbackWrapper) {
        if (SSOAuthHandler.isAvailable(activity)) {
            Fabric.getLogger().d(TwitterCore.TAG, "Using SSO");
            return authState.beginAuthorize(activity,
                    new SSOAuthHandler(authConfig, callbackWrapper, authConfig.getRequestCode()));
        } else {
            return false;
        }
    }

    private boolean authorizeUsingOAuth(Activity activity, CallbackWrapper callbackWrapper) {
        Fabric.getLogger().d(TwitterCore.TAG, "Using OAuth");
        return authState.beginAuthorize(activity,
                new OAuthHandler(authConfig, callbackWrapper, authConfig.getRequestCode()));
    }

    private void scribeAuthorizeImpression() {
        final DefaultScribeClient scribeClient = getScribeClient();
        if (scribeClient == null) return;

        final EventNamespace ns = new EventNamespace.Builder()
                .setClient(SCRIBE_CLIENT)
                .setPage(SCRIBE_LOGIN_PAGE)
                .setSection(SCRIBE_SECTION)
                .setComponent(SCRIBE_COMPONENT)
                .setElement(SCRIBE_ELEMENT)
                .setAction(SCRIBE_ACTION)
                .builder();

        scribeClient.scribe(ns);
    }

    /**
     * Call this method when {@link android.app.Activity#onActivityResult(int, int, Intent)}
     * is called to complete the authorization flow.
     *
     * @param requestCode the request code used for SSO
     * @param resultCode the result code returned by the SSO activity
     * @param data the result data returned by the SSO activity
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fabric.getLogger().d(TwitterCore.TAG,
                "onActivityResult called with " + requestCode + " " + resultCode);
        if (!authState.isAuthorizeInProgress()) {
            Fabric.getLogger().e(TwitterCore.TAG, "Authorize not in progress", null);
        } else {
            final AuthHandler authHandler = authState.getAuthHandler();
            if (authHandler != null &&
                    authHandler.handleOnActivityResult(requestCode, resultCode, data)) {
                authState.endAuthorize();
            }
        }
    }

    /**
     * Requests the user's email address.
     *
     * @param session the user session
     * @param callback The callback interface to invoke when the request completes. If the user
     *                 denies access to the email address, or the email address is not available,
     *                 an error is returned.
     * @throws java.lang.IllegalArgumentException if session or callback are null.
     */
    public void requestEmail(TwitterSession session, Callback<String> callback) {
        if (session == null) {
            throw new IllegalArgumentException("Session must not be null.");
        }
        if (callback == null) {
            throw new IllegalArgumentException("Callback must not be null.");
        }
        scribeRequestEmail();
        context.startActivity(newShareEmailIntent(session, callback));
    }

    protected DefaultScribeClient getScribeClient() {
        return TwitterCoreScribeClientHolder.getScribeClient();
    }

    private void scribeRequestEmail() {
        final DefaultScribeClient scribeClient = getScribeClient();
        if (scribeClient == null) return;

        final EventNamespace ns = new EventNamespace.Builder()
                .setClient(SCRIBE_CLIENT)
                .setPage(SCRIBE_SHARE_EMAIL_PAGE)
                .setSection(SCRIBE_SECTION)
                .setComponent(SCRIBE_COMPONENT)
                .setElement(SCRIBE_ELEMENT)
                .setAction(SCRIBE_ACTION)
                .builder();

        scribeClient.scribe(ns);
    }

    Intent newShareEmailIntent(TwitterSession session, Callback<String> callback) {
        return new Intent(context, ShareEmailActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(ShareEmailActivity.EXTRA_SESSION_ID, session.getId())
                .putExtra(ShareEmailActivity.EXTRA_RESULT_RECEIVER,
                        new ShareEmailResultReceiver(callback));
    }

    static class CallbackWrapper extends Callback<TwitterSession> {
        private final SessionManager<TwitterSession> sessionManager;
        private final Callback<TwitterSession> callback;

        public CallbackWrapper(SessionManager<TwitterSession> sessionManager,
                Callback<TwitterSession> callback) {
            this.sessionManager = sessionManager;
            this.callback = callback;
        }

        @Override
        public void success(Result<TwitterSession> result) {
            Fabric.getLogger().d(TwitterCore.TAG, "Authorization completed successfully");
            sessionManager.setActiveSession(result.data);
            callback.success(result);
        }

        @Override
        public void failure(TwitterException exception) {
            Fabric.getLogger().e(TwitterCore.TAG, "Authorization completed with an error",
                    exception);
            callback.failure(exception);
        }
    }
}
