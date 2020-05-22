package de.zitruism.thl_todo_liste.interfaces;

public interface ITodoStateListener {

    void updateDone(Long id, boolean isDone);
    void updateFavorite(Long id, boolean isFavorite);

}
