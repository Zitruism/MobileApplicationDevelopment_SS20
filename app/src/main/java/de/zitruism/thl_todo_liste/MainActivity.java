package de.zitruism.thl_todo_liste;

import androidx.appcompat.app.AppCompatActivity;
import de.zitruism.thl_todo_liste.interfaces.IMainActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements IMainActivity {

    private boolean webserviceAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public MyApplication getMyApplication() {
        return (MyApplication) this.getApplication();
    }

    @Override
    public ContentResolver getActivityContentResolver(){
        return this.getContentResolver();
    }

    @Override
    public boolean isWebServiceAvailable() {
        return this.webserviceAvailable;
    }

    @Override
    public void setWebServiceAvailable(boolean available) {
        this.webserviceAvailable = available;
    }

    @Override
    public View getRootView() {
        return findViewById(R.id.rootLayout);
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    @Override
    public void setToolbarTitle(String title) {
        setTitle(title);
    }


}
