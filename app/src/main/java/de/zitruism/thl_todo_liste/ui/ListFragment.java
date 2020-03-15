package de.zitruism.thl_todo_liste.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import de.zitruism.thl_todo_liste.R;
import de.zitruism.thl_todo_liste.database.model.Todo;
import de.zitruism.thl_todo_liste.databinding.FragmentListBinding;
import de.zitruism.thl_todo_liste.interfaces.IMainActivity;
import de.zitruism.thl_todo_liste.ui.adapters.ListAdapter;
import de.zitruism.thl_todo_liste.ui.viewmodel.ListViewModel;

public class ListFragment extends Fragment implements View.OnClickListener {

    private IMainActivity mListener;
    private FragmentListBinding binding;

    @Inject
    ListViewModel viewModel;

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

    public ListFragment(){}

    public static ListFragment newInstance() {
        return new ListFragment();
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

        viewModel.getTodos().observe(getViewLifecycleOwner(), new Observer<List<Todo>>() {
            @Override
            public void onChanged(List<Todo> todos) {
                binding.setTodos(todos);
                System.out.println(todos);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container,false);
        binding.setLifecycleOwner(this);
        binding.listView.setAdapter(new ListAdapter());
        binding.addButton.setOnClickListener(this);

        return binding.getRoot();
    }


    @Override
    public void onClick(View v) {
        System.out.println("Clicked...");
        viewModel.insert(new Todo("Erstes TODO", "Beschreibung", false, false, new Date()));
    }
}
