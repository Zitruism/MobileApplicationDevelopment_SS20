package de.zitruism.thl_todo_liste.ui.viewmodel;



import android.view.View;

import androidx.lifecycle.ViewModel;
import de.zitruism.thl_todo_liste.database.model.User;
import de.zitruism.thl_todo_liste.database.repository.TodoRepository;
import de.zitruism.thl_todo_liste.network.SyncTodos;
import de.zitruism.thl_todo_liste.network.interfaces.IWebService;
import retrofit2.Call;
import retrofit2.Callback;

public class StartViewModel extends ViewModel {

    private IWebService webservice;
    private TodoRepository repository;

    StartViewModel(TodoRepository repository, IWebService webservice) {
        this.webservice = webservice;
        this.repository = repository;
    }

    public void syncTodos(){
        new SyncTodos(repository, webservice).execute();
    }

    public void checkForService(Callback<Boolean> callback){
        Call<Boolean> call = webservice.getAuthentication(new User("", ""));
        call.enqueue(callback);
    }
}
