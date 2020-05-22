package de.zitruism.thl_todo_liste.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import de.zitruism.thl_todo_liste.R;
import de.zitruism.thl_todo_liste.database.model.Todo;
import de.zitruism.thl_todo_liste.databinding.FragmentListBinding;
import de.zitruism.thl_todo_liste.interfaces.IListClickListener;
import de.zitruism.thl_todo_liste.interfaces.IMainActivity;
import de.zitruism.thl_todo_liste.interfaces.ITodoStateListener;
import de.zitruism.thl_todo_liste.ui.adapters.TodoListAdapter;
import de.zitruism.thl_todo_liste.ui.viewmodel.ListViewModel;
import de.zitruism.thl_todo_liste.ui.viewmodel.ViewModelFactory;

public class ListFragment extends Fragment implements View.OnClickListener, ITodoStateListener, IListClickListener {

    private static final String SORTFAVORITEBEFOREDATE = "SORTFAVORITEBEFOREDATE";
    private IMainActivity mListener;
    private FragmentListBinding binding;

    @Inject
    ViewModelFactory viewModelFactory;

    private ListViewModel viewModel;
    private TodoListAdapter adapter;

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

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ListViewModel.class);

        viewModel.getTodos().observe(getViewLifecycleOwner(), new Observer<List<Todo>>() {
            @Override
            public void onChanged(List<Todo> todos) {
                binding.setTodos(todos);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mListener.setToolbarTitle(getString(R.string.listview_title));

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container,false);
        binding.setLifecycleOwner(this);
        adapter = new TodoListAdapter(this, this, getSortOrder());
        binding.listView.setAdapter(adapter);
        binding.addButton.setOnClickListener(this);

        //Show toolbar menu
        setHasOptionsMenu(true);

        return binding.getRoot();
    }

    private boolean getSortOrder() {
        return mListener.getSharedPreferences().getBoolean(SORTFAVORITEBEFOREDATE, true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.listmenu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.sortFavoriteBeforeDate:
                saveSortOrder(true);
                adapter.setSortOrder(true);
                break;
            case R.id.sortDateBeforeFavorite:
                saveSortOrder(false);
                adapter.setSortOrder(false);
                break;
            case R.id.deleteLocal:
                viewModel.deleteLocal();
                break;
            case R.id.deleteRemote:
                viewModel.deleteRemote();
                break;
            case R.id.sync:
                viewModel.syncTodos();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveSortOrder(boolean favoriteBeforeDate){
        mListener.getSharedPreferences().edit().putBoolean(SORTFAVORITEBEFOREDATE, favoriteBeforeDate).apply();
    }

    @Override
    public void onClick(View v) {
        NavHostFragment.findNavController(this).navigate(R.id.action_listFragment_to_detailFragment);
    }

    @Override
    public void updateDone(Long id, boolean isDone) {
        viewModel.updateDone(id, isDone);
    }

    @Override
    public void updateFavorite(Long id, boolean isFavorite) {
        viewModel.updateFavorite(id, isFavorite);
    }

    @Override
    public void onListClick(View v) {
        //Go to Detailview
        Long id = (Long) v.getTag();

        //Navigate to Detailpage with given id.
        Bundle b = new Bundle();
        b.putLong("TODO_ID", id);

        NavHostFragment.findNavController(this).navigate(R.id.action_listFragment_to_detailFragment, b);
    }
}
