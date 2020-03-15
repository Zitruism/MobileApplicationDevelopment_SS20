package de.zitruism.thl_todo_liste.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import de.zitruism.thl_todo_liste.database.dao.TodoDAO;
import de.zitruism.thl_todo_liste.database.model.Todo;

@Database(entities = {Todo.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TodoDAO todoDAO();

}
