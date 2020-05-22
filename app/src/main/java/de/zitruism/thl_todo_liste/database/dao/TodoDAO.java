package de.zitruism.thl_todo_liste.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import de.zitruism.thl_todo_liste.database.model.Todo;

@Dao
public interface TodoDAO {

    @Query("SELECT * FROM Todo")
    LiveData<List<Todo>> getAll();

    @Query("SELECT * FROM Todo")
    List<Todo> getAllTodos();

    @Query("SELECT * FROM Todo WHERE id LIKE :id")
    LiveData<Todo> getTodo(Long id);

    @Query("SELECT * FROM Todo WHERE id LIKE :id")
    Todo readTodo(Long id);

    @Insert
    long[] insertAll(List<Todo> todos);

    @Insert
    long insert(Todo todo);

    @Query("UPDATE Todo SET done = :isDone WHERE id LIKE :id;")
    int updateDone(Long id, boolean isDone);

    @Query("UPDATE Todo SET favourite = :isFavorite WHERE id LIKE :id;")
    int updateFavorite(Long id, boolean isFavorite);

    @Delete
    int delete(Todo todo);

    @Query("DELETE FROM Todo")
    int deleteAll();

    @Update
    void updateTodo(Todo todo);

    @Query("SELECT count(*) FROM Todo")
    int count();

}