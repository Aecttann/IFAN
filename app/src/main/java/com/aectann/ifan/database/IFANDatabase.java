package com.aectann.ifan.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.aectann.ifan.models.Fact;

@Database(entities = Fact.class, exportSchema = false, version = 1)
public abstract class IFANDatabase extends RoomDatabase {
    private static final String LOG_TAG = IFANDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "ifan";
    private static IFANDatabase sInstance;

    public static  IFANDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                                IFANDatabase.class, IFANDatabase.DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract FactDao factDao();
}