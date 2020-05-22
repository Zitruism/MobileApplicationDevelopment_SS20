package de.zitruism.thl_todo_liste.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import de.zitruism.thl_todo_liste.R;
import de.zitruism.thl_todo_liste.interfaces.IMainActivity;
import de.zitruism.thl_todo_liste.network.interfaces.IWebService;
import de.zitruism.thl_todo_liste.ui.viewmodel.StartViewModel;
import de.zitruism.thl_todo_liste.ui.viewmodel.ViewModelFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartFragment extends Fragment {

    private IMainActivity mListener;
    private StartViewModel viewModel;

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IMainActivity) {
            mListener = (IMainActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IMainActivity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public StartFragment(){}

    public static StartFragment newInstance() {
        return new StartFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListener.getMyApplication()
                .getApplicationComponent()
                .inject(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(StartViewModel.class);


        // Check if webservice is available
        Snackbar.make(mListener.getRootView(), R.string.checkingforwebservice, Snackbar.LENGTH_INDEFINITE)
                .show();

        viewModel.checkForService(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                mListener.setWebServiceAvailable(true);

                Snackbar.make(mListener.getRootView(), R.string.syncingtodos, Snackbar.LENGTH_LONG)
                        .show();

                viewModel.syncTodos();
                NavHostFragment.findNavController(StartFragment.this).navigate(R.id.action_startFragment_to_loginFragment);

            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

                Snackbar.make(mListener.getRootView(), R.string.webservicenotavailable, Snackbar.LENGTH_LONG)
                        .show();

                mListener.setWebServiceAvailable(false);
                NavHostFragment.findNavController(StartFragment.this).navigate(R.id.action_startFragment_to_listFragment);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start, container,false);
    }
}
