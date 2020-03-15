package de.zitruism.thl_todo_liste.database.repository;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import de.zitruism.thl_todo_liste.database.dao.TodoDAO;
import de.zitruism.thl_todo_liste.database.model.Todo;

public class TodoRepository {

    //TODO: Either save locally or post to webservice

    private TodoDAO todoDAO;


    @Inject
    public TodoRepository(TodoDAO todoDAO) {
        this.todoDAO = todoDAO;
    }

    public LiveData<List<Todo>> getAll(){
        return todoDAO.getAll();
    }

    public LiveData<Todo> getTodo(Integer id){
        return todoDAO.getTodo(id);
    }

    public void insertTodo(Todo todo) {
        todoDAO.insert(todo);
    }

}
