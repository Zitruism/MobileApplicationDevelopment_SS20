package de.zitruism.thl_todo_liste.ui.viewmodel;

import android.os.AsyncTask;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.zitruism.thl_todo_liste.database.model.Todo;
import de.zitruism.thl_todo_liste.database.repository.TodoRepository;

public class DetailViewModel extends ViewModel {

    private TodoRepository todoRepository;

    @Inject
    public DetailViewModel(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public LiveData<Todo> getTodo(int id){
        return todoRepository.getTodo(id);
    }

    public void updateDone(Integer id, boolean isDone){
        new UpdateDone(todoRepository, id, isDone).execute();
    }

    public void updateFavorite(Integer id, boolean isFavorite){
        new UpdateFavorite(todoRepository, id, isFavorite).execute();
    }

    private static class UpdateDone extends AsyncTask<Void, Void, Void> {

        private final TodoRepository todoRepository;
        private final Integer id;
        private final boolean isDone;

        UpdateDone(TodoRepository todoRepository, Integer id, boolean isDone) {
            this.todoRepository = todoRepository;
            this.id = id;
            this.isDone = isDone;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            todoRepository.updateDone(id, isDone);
            return null;
        }
    }

    private static class UpdateFavorite extends AsyncTask<Void, Void, Void> {

        private final TodoRepository todoRepository;
        private final Integer id;
        private final boolean isDone;

        UpdateFavorite(TodoRepository todoRepository, Integer id, boolean isDone) {
            this.todoRepository = todoRepository;
            this.id = id;
            this.isDone = isDone;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            todoRepository.updateFavorite(id, isDone);
            return null;
        }
    }



}
