package de.zitruism.thl_todo_liste.interfaces;

public interface ITodoStateListener {

    void updateDone(Integer id, boolean isDone);
    void updateFavorite(Integer id, boolean isFavorite);

}
