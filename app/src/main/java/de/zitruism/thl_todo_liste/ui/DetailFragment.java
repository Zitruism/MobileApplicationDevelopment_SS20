package de.zitruism.thl_todo_liste.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;
import de.zitruism.thl_todo_liste.R;
import de.zitruism.thl_todo_liste.database.model.Todo;
import de.zitruism.thl_todo_liste.databinding.FragmentDetailBinding;
import de.zitruism.thl_todo_liste.interfaces.IMainActivity;
import de.zitruism.thl_todo_liste.ui.viewmodel.DetailViewModel;

public class DetailFragment extends Fragment implements View.OnClickListener {

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
        }else{
            binding.setTodo(new Todo());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container,false);
        binding.setLifecycleOwner(this);

        binding.isDone.setOnClickListener(this);
        binding.isFavorite.setOnClickListener(this);

        binding.abort.setOnClickListener(this);
        binding.save.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.isDone:
                binding.getTodo().setDone(!binding.getTodo().isDone());
                break;
            case R.id.isFavorite:
                binding.getTodo().setFavorite(!binding.getTodo().isFavorite());
                break;
            case R.id.save:
                if(binding.getTodo().getId() >= 0)
                    saveTodo(binding.getTodo());
                else
                    createTodo(binding.getTodo());
                navToList();
                break;
            case R.id.abort:
                navToList();
                break;
        }
    }

    private void createTodo(Todo todo) {
        viewModel.insert(todo);
    }

    private void saveTodo(Todo todo){
        viewModel.updateTodo(todo);
    }

    private void navToList(){
        NavHostFragment.findNavController(this).navigate(R.id.action_detailFragment_to_listFragment);
    }
}
