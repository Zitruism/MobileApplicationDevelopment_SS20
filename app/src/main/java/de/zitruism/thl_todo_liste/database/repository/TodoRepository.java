package de.zitruism.thl_todo_liste.database.repository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import de.zitruism.thl_todo_liste.database.dao.TodoDAO;
import de.zitruism.thl_todo_liste.database.model.Todo;

public class TodoRepository {

    private TodoDAO todoDAO;

    @Inject
    public TodoRepository(TodoDAO todoDAO) {
        this.todoDAO = todoDAO;
    }

    public LiveData<List<Todo>> getAll(){
        return todoDAO.getAll();
    }

    public List<Todo> getAllTodos(){
        return todoDAO.getAllTodos();
    }

    public LiveData<Todo> getTodo(Long id){
        return todoDAO.getTodo(id);
    }

    public Todo readTodo(Long id){
        return todoDAO.readTodo(id);
    }

    public long insertTodo(Todo todo) {
        return todoDAO.insert(todo);
    }

    public long[] insertTodos(List<Todo> todos){return todoDAO.insertAll(todos);};

    public void updateDone(Long id, boolean isDone){
        todoDAO.updateDone(id, isDone);
    }

    public void updateFavorite(Long id, boolean isFavorite){
        todoDAO.updateFavorite(id, isFavorite);
    }

    public void updateTodo(Todo todo){
        todoDAO.updateTodo(todo);
    }

    public int deleteAll(){return todoDAO.deleteAll();}

    public int delete(Todo todo){
        return todoDAO.delete(todo);
    }

    public int count(){return todoDAO.count();}

}
