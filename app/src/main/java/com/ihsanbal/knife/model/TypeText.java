/*
 * Created by ihsan on 5/14/17 6:40 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 5/7/17 11:14 PM
 *
 */

package com.ihsanbal.knife.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twitter.sdk.android.core.models.MentionEntity;
import com.twitter.sdk.android.core.models.Tweet;

/**
 * @author ihsan on 30/04/2017.
 */

public class TypeText implements Parcelable {

    private Type type;
    private boolean inReply;
    private Long id;
    private String screenName;
    private String text;

    public TypeText(Tweet item, boolean inReply) {
        this.id = item.getId();
        this.screenName = inReply ? replyEntitiesMention(item) : item.user.screenName;
        this.inReply = inReply;
        this.type = Type.REPLY;
    }

    private String replyEntitiesMention(Tweet item) {
        StringBuilder builder = new StringBuilder(item.user.screenName);
        for (MentionEntity mention : item.entities.userMentions) {
            builder.append(" @");
            builder.append(mention.screenName);
        }
        return builder.toString();
    }

    public TypeText() {
        this.type = Type.NEW;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getScreenName() {
        return screenName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        NEW, REPLY, SHARED
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeByte(this.inReply ? (byte) 1 : (byte) 0);
        dest.writeValue(this.id);
        dest.writeString(this.screenName);
        dest.writeString(this.text);
    }

    private TypeText(Parcel in) {
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : Type.values()[tmpType];
        this.inReply = in.readByte() != 0;
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.screenName = in.readString();
        this.text = in.readString();
    }

    public static final Parcelable.Creator<TypeText> CREATOR = new Parcelable.Creator<TypeText>() {
        @Override
        public TypeText createFromParcel(Parcel source) {
            return new TypeText(source);
        }

        @Override
        public TypeText[] newArray(int size) {
            return new TypeText[size];
        }
    };
}
