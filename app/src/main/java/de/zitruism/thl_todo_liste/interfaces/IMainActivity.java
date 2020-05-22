package de.zitruism.thl_todo_liste.interfaces;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.view.View;

import de.zitruism.thl_todo_liste.MyApplication;

public interface IMainActivity {

    Activity getActivity();
    MyApplication getMyApplication();
    ContentResolver getActivityContentResolver();
    boolean isWebServiceAvailable();
    void setWebServiceAvailable(boolean available);
    View getRootView();
    SharedPreferences getSharedPreferences();
    void setToolbarTitle(String title);
}
