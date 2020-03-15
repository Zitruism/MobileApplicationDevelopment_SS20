package de.zitruism.thl_todo_liste;

import androidx.appcompat.app.AppCompatActivity;
import de.zitruism.thl_todo_liste.interfaces.IMainActivity;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements IMainActivity {

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
}
