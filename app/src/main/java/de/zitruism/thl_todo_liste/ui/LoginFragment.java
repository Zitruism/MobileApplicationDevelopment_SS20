package de.zitruism.thl_todo_liste.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import de.zitruism.thl_todo_liste.R;
import de.zitruism.thl_todo_liste.databinding.FragmentLoginBinding;
import de.zitruism.thl_todo_liste.interfaces.IMainActivity;
import de.zitruism.thl_todo_liste.ui.viewmodel.DetailViewModel;
import de.zitruism.thl_todo_liste.ui.viewmodel.LoginViewModel;
import de.zitruism.thl_todo_liste.ui.viewmodel.ViewModelFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String REMEMBERME = "REMEMBERME";
    private static final String EMAIL = "EMAIL";
    private IMainActivity mListener;

    @Inject
    ViewModelFactory viewModelFactory;

    private FragmentLoginBinding binding;
    private LoginViewModel viewModel;

    private Handler mHandler = new Handler();
    private Runnable loginCheck = new Runnable() {
        @Override
        public void run() {
            viewModel.doLoginCheck(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                    //On true => navigate to list
                    if (response.body() != null && response.body()) {
                        //navigate to listview
                        NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_loginFragment_to_listFragment);
                    } else {
                        //Show login error
                        viewModel.setLoginError(true);
                    }


                    viewModel.setLoading(false);

                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    viewModel.setLoginError(true);
                    viewModel.setLoading(false);
                }
            });
        }
    };


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

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
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

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel.class);

        viewModel.getEmail().observe(this.getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.setEmail(s);
            }
        });

        viewModel.getPassword().observe(this.getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.setPassword(s);
            }
        });

        viewModel.getEmailValid().observe(this.getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.setIsEmailValid(aBoolean);
            }
        });

        viewModel.getLoginError().observe(this.getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.setLoginError(aBoolean);
            }
        });

        viewModel.getLoading().observe(this.getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.setLoading(aBoolean);
            }
        });

        viewModel.getRememberMe().observe(this.getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.setRememberme(aBoolean);
            }
        });


        if(mListener.getSharedPreferences().getBoolean(REMEMBERME, false)){
            viewModel.setRememberMe(true);
            viewModel.setEmail(mListener.getSharedPreferences().getString(EMAIL, ""));
        }else{
            viewModel.setRememberMe(false);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mListener.setToolbarTitle(getString(R.string.loginview_title));

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);

        binding.btnLogin.setOnClickListener(this);

        binding.emailEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Reset error messages, as soon as a char is changed
                viewModel.setEmailValid(true);
                viewModel.setLoginError(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setEmail(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.passwordEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Reset error messages, as soon as a char is changed
                viewModel.setLoginError(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setPassword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.emailEdittext.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String mail = viewModel.getEmail().getValue();
                    boolean validEmail = checkForValidEmail(mail);
                    viewModel.setEmailValid(validEmail);
                    return true;
                }
                return false;
            }
        });

        binding.emailEdittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //Input lost focus
                    viewModel.setEmailValid(checkForValidEmail(viewModel.getEmail().getValue()));
                }
            }
        });

        binding.rememberMe.setOnCheckedChangeListener(this);

        return binding.getRoot();
    }

    private boolean checkForValidEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    private void startLoginCheck() {
        viewModel.setLoading(true);

        //If user checked the box, save email tosharedprefs
        if(binding.getRememberme()){
            mListener.getSharedPreferences().edit()
                    .putBoolean(REMEMBERME, true)
                    .putString(EMAIL, binding.getEmail())
            .apply();
        }else{
            mListener.getSharedPreferences().edit()
                    .putBoolean(REMEMBERME, false)
                    .remove(EMAIL)
                    .apply();
        }
        mHandler.postDelayed(loginCheck, 2000);
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacksAndMessages(null);
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        //Handle login press.
        if (v.getId() == R.id.btn_login) {
            this.startLoginCheck();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        viewModel.setRememberMe(isChecked);
    }
}
