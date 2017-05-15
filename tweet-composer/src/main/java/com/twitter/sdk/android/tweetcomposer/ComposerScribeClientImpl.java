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

package com.twitter.sdk.android.tweetcomposer;

import com.twitter.sdk.android.core.internal.scribe.EventNamespace;
import com.twitter.sdk.android.core.internal.scribe.ScribeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * CardComposerScribeClientImpl implements the scribe events corresponding to the Tweet Composer.
 */
class ComposerScribeClientImpl implements ComposerScribeClient {
    private final ScribeClient scribeClient;

    ComposerScribeClientImpl(ScribeClient scribeClient) {
        if (scribeClient == null) {
            throw new NullPointerException("scribeClient must not be null");
        }
        this.scribeClient = scribeClient;
    }

    @Override
    public void impression(Card card) {
        final EventNamespace ns = ScribeConstants.ComposerEventBuilder
                .setComponent(ScribeConstants.SCRIBE_COMPONENT)
                .setElement(ScribeConstants.SCRIBE_IMPRESSION_ELEMENT)
                .setAction(ScribeConstants.SCRIBE_IMPRESSION_ACTION)
                .builder();
        final List<ScribeItem> items = new ArrayList<>();
        items.add(ScribeConstants.newCardScribeItem(card));
        scribeClient.scribe(ns, items);
    }

    @Override
    public void click(Card card, String element) {
        final EventNamespace ns = ScribeConstants.ComposerEventBuilder
                .setComponent(ScribeConstants.SCRIBE_COMPONENT)
                .setElement(element)
                .setAction(ScribeConstants.SCRIBE_CLICK_ACTION)
                .builder();
        final List<ScribeItem> items = new ArrayList<>();
        items.add(ScribeConstants.newCardScribeItem(card));
        scribeClient.scribe(ns, items);
    }
}
