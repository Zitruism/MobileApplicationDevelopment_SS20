package de.zitruism.thl_todo_liste.interfaces;

import android.app.Activity;
import android.content.ContentResolver;

import de.zitruism.thl_todo_liste.MyApplication;

public interface IMainActivity {

    Activity getActivity();
    MyApplication getMyApplication();
    ContentResolver getActivityContentResolver();

}
