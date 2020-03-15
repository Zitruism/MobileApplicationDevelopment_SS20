package de.zitruism.thl_todo_liste.injection;

import android.app.Application;

import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import de.zitruism.thl_todo_liste.database.AppDatabase;
import de.zitruism.thl_todo_liste.database.dao.TodoDAO;
import de.zitruism.thl_todo_liste.database.repository.TodoRepository;

@Module
public class RoomModule {

    private final AppDatabase dataBase;

    public RoomModule(final Application application) {
        this.dataBase = Room.databaseBuilder(
                application,
                AppDatabase.class,
                "db_1.0"
        ).build();
    }

    @Provides
    @Singleton
    TodoRepository provideRuleRepository(TodoDAO todoDAO) {
        return new TodoRepository(todoDAO);
    }

    @Provides
    @Singleton
    TodoDAO provideTodoDAO() {
        return dataBase.todoDAO();
    }
}