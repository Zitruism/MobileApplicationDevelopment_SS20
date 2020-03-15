package de.zitruism.thl_todo_liste.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import de.zitruism.thl_todo_liste.database.model.Todo;

@Dao
public interface TodoDAO {

    @Query("SELECT * FROM Todo")
    LiveData<List<Todo>> getAll();

    @Query("SELECT * FROM Todo WHERE id LIKE :id")
    LiveData<Todo> getTodo(Integer id);

    @Insert
    void insertAll(Todo... todos);

    @Insert
    void insert(Todo todo);

    @Delete
    void delete(Todo todo);
}