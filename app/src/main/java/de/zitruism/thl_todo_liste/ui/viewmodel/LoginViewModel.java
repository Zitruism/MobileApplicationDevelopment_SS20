package de.zitruism.thl_todo_liste.ui.viewmodel;

import android.text.TextUtils;

import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.zitruism.thl_todo_liste.database.model.User;
import de.zitruism.thl_todo_liste.network.interfaces.IWebService;
import retrofit2.Call;
import retrofit2.Callback;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<String> email = new MutableLiveData<>();
    private MutableLiveData<String> password = new MutableLiveData<>();
    private MutableLiveData<Boolean> emailValid = new MutableLiveData<>(true);
    private MutableLiveData<Boolean> loginError = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> rememberMe = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    private IWebService webservice;

    LoginViewModel(IWebService webservice) {
        this.webservice = webservice;
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email.setValue(email);
    }

    public void setPassword(String password) {
        this.password.setValue(password);
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public void setPassword(MutableLiveData<String> password) {
        this.password = password;
    }

    public void doLoginCheck(Callback<Boolean> callback){
        User user = new User(email.getValue(), password.getValue());
        Call<Boolean> call = webservice.getAuthentication(user);
        call.enqueue(callback);
    }

    public MutableLiveData<Boolean> getEmailValid() {
        return emailValid;
    }

    public void setEmailValid(boolean emailValid) {
        this.emailValid.setValue(emailValid);
    }

    public void setLoginError(boolean loginError) {
        this.loginError.setValue(loginError);
    }

    public MutableLiveData<Boolean> getLoginError() {
        return loginError;
    }

    public void setRememberMe(boolean rememberMe){
        this.rememberMe.setValue(rememberMe);
    }

    public MutableLiveData<Boolean> getRememberMe(){
        return rememberMe;
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    public void setLoading(Boolean loading) {
        this.loading.setValue(loading);
    }
}
