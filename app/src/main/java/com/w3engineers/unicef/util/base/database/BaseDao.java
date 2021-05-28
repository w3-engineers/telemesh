package com.w3engineers.unicef.util.base.database;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import java.util.List;

@Dao
public abstract class BaseDao<T> {

    /**
     * Insert vararg objects in the database.
     *
     * @param rows the objects to be inserted.
     * @return inserted rows id
     */
    @SuppressWarnings("unchecked")
    @Insert
    public abstract long[] insert(T... rows);//varargs
    /**
     * Insert vararg objects in the database.
     *
     * @param row the objects to be inserted.
     * @return inserted rows id
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertOrUpdate(T row);//varargs

    /**
     * Insert vararg objects in the database.
     *
     * @param rows the objects to be inserted.
     * @return inserted rows id
     */
    @Insert
    public abstract long[] insert(List<T> rows);

    /**
     * Update object from the database. The update is on primary key
     *
     * @param objects the object to be updated
     * @return affected rows count
     */
    @SuppressWarnings("unchecked")
    @Update
    public abstract int update(T... objects);

    /**
     * Update object from the database. The update is on primary key
     *
     * @param objects the object to be updated
     * @return affected rows count
     */
    @Update
    public abstract int update(List<T> objects);

    /**
     * Delete an object from the database based on primary key
     * Developers can generate own deleteAll (normally wouldn't need much)
     * @param objects the object to be deleted
     * @return affected rows count
     */
    @SuppressWarnings("unchecked")
    @Delete
    public abstract int delete(T... objects);

    /**
     * Delete an object from the database based on primary key
     * @param objects the object to be deleted
     * @return affected rows count
     */
    @Delete
    public abstract int delete(List<T> objects);

}
