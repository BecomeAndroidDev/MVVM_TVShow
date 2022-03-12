package com.hfad.tvshow.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.hfad.tvshow.dao.TVShowDAO;
import com.hfad.tvshow.models.TVShow;

@Database(entities = TVShow.class, version=1, exportSchema = false)
public abstract class TVShowsDatabase extends RoomDatabase {
    private static TVShowsDatabase tvShowsDatabase;

    public static synchronized TVShowsDatabase getTVShowsDatabase(Context context) {
        if(tvShowsDatabase == null) {
            tvShowsDatabase = Room.databaseBuilder(
                    context,
                    TVShowsDatabase.class,
                    "tv_shows_db").build();
        }
        return tvShowsDatabase;
    }

    public abstract TVShowDAO tvShowDAO();

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }
}
