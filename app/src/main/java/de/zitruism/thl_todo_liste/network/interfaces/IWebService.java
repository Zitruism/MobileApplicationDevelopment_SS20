package de.zitruism.thl_todo_liste.network.interfaces;

import java.util.List;

import de.zitruism.thl_todo_liste.database.model.Todo;
import de.zitruism.thl_todo_liste.database.model.User;
import de.zitruism.thl_todo_liste.network.Webservice;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface IWebService {

    @PUT(Webservice.AUTHENTICATE)
    Call<Boolean> getAuthentication(@Body User user);

    @DELETE(Webservice.LIST)
    Call<Boolean> deleteAll();

    @GET(Webservice.LIST)
    Call<List<Todo>> getAll();

    @GET(Webservice.SINGLE)
    Call<Todo> getTodo(@Path("todoId") Long id);

    @PUT(Webservice.SINGLE)
    Call<Todo> putTodo(@Path("todoId") Long id, @Body Todo todo);

    @POST(Webservice.LIST)
    Call<Todo> postTodo(@Body Todo todo);

    @DELETE(Webservice.SINGLE)
    Call<Boolean> deleteTodo(@Path("todoId") Long id);
}
