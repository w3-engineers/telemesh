package com.w3engineers.unicef.util.base.database;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
import androidx.databinding.BaseObservable;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public abstract class BaseEntity extends BaseObservable implements Parcelable {

    //Any new insertion was getting conflict with first Id.
    //Searching for an approach auto generate id (as of raw sqlite) rather autoinc.
    /**
     * Developers can override this primary key on their own. This column would then only work as a
     * ROWID for normal cases. Only annotating with primarykey would be enough.
     * @see <a href="https://developer.android.com/reference/android/arch/persistence/room/PrimaryKey">Primary Key</a>
     * @see <a href="https://www.sqlite.org/lang_createtable.html#rowid">ROWID</a>
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = BaseColumnNames.ID)
    @NonNull
    protected long mId;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
    }

    public BaseEntity() {
    }

    protected BaseEntity(Parcel in) {
        this.mId = in.readLong();
    }

    /**
     * To copy current object to a new Object. Child class should call this method.
     * Useful to copy to new object. Might require as when we transport data through parcelable
     * then all the binders are broadcasted with any change of the source object which might not
     * be always desired
     * @param baseEntity
     * @return
     */
    /*public BaseEntity copy(BaseEntity baseEntity) {
        if(baseEntity == null) {
            return null;
        }

        baseEntity.mId = mId;
        return baseEntity;
    }*/
}