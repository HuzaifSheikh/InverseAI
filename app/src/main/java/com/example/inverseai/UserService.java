package com.example.inverseai;

import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.app.Service;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class UserService extends Service{
    private static final String LOGGER_INFO = "USER_SERVICE";
    protected User user;

    /**
     * Class used for the client Binder.
     */
    public class UserServiceBinder extends Binder {
        UserService getService() {
            return UserService.this; //return instance of ServiceBinder so clients can call public methods
        }
    }

    /**
     * The binder to be given to clients.
     */
    private final UserServiceBinder binder = new UserServiceBinder();

    @Override
    public void onCreate() {
        Log.i(LOGGER_INFO, "CREATED");
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOGGER_INFO, "STARTED");
        return START_STICKY;
    }

    /**
     * Registers a specified user to be this session's user.
     * @param usr The user.
     */
    public void registerUser(User usr) {
        user = usr;
    }

    /**
     * De-registers a specified user to be this session's user.
     */
    public void deregisterUser() {
        user = null;
    }

    @Override
    @Nullable
    public IBinder onBind(Intent intent) { return binder; }
}
