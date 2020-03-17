package de.zitruism.thl_todo_liste.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import de.zitruism.thl_todo_liste.R;
import de.zitruism.thl_todo_liste.database.model.Todo;
import de.zitruism.thl_todo_liste.databinding.FragmentDetailBinding;
import de.zitruism.thl_todo_liste.interfaces.IMainActivity;
import de.zitruism.thl_todo_liste.interfaces.ITodoStateListener;
import de.zitruism.thl_todo_liste.ui.viewmodel.DetailViewModel;

public class DetailFragment extends Fragment implements View.OnClickListener, ITodoStateListener {

    private IMainActivity mListener;
    private FragmentDetailBinding binding;

    private final static String BUNDLE_IDKEY = "TODO_ID";
    private int todoId;

    @Inject
    DetailViewModel viewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IMainActivity) {
            mListener = (IMainActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MainActivity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public DetailFragment(){}

    public static DetailFragment newInstance(Integer id) {
        DetailFragment df = new DetailFragment();
        Bundle b = new Bundle();
        b.putInt(BUNDLE_IDKEY, id);
        df.setArguments(b);
        return df;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListener.getMyApplication()
                .getApplicationComponent()
                .inject(this);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            todoId = bundle.getInt(BUNDLE_IDKEY, -1);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(todoId >= 0){
            viewModel.getTodo(todoId).observe(getViewLifecycleOwner(), new Observer<Todo>() {
                @Override
                public void onChanged(Todo todo) {
                    binding.setTodo(todo);
                }
            });
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container,false);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.isDone:
                updateDone(binding.getTodo().getId(), !binding.getTodo().isDone());
                break;
            case R.id.isFavorite:
                updateFavorite(binding.getTodo().getId(), !binding.getTodo().isFavorite());
                break;
        }
    }

    @Override
    public void updateDone(Integer id, boolean isDone) {
        viewModel.updateDone(id, isDone);
    }

    @Override
    public void updateFavorite(Integer id, boolean isFavorite) {
        viewModel.updateFavorite(id, isFavorite);
    }
}
