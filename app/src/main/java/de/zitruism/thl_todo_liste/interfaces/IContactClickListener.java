package de.zitruism.thl_todo_liste.interfaces;

import android.view.View;

public interface IContactClickListener {
    void onDeleteClick(View v);
    void onCallClick(View v);
    void onMessageClick(View v);
    void onMailClick(View v);
}
