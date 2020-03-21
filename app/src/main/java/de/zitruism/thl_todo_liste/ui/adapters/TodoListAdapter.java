package de.zitruism.thl_todo_liste.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.zitruism.thl_todo_liste.R;
import de.zitruism.thl_todo_liste.database.model.Todo;
import de.zitruism.thl_todo_liste.databinding.ItemListviewBinding;
import de.zitruism.thl_todo_liste.interfaces.IListClickListener;
import de.zitruism.thl_todo_liste.interfaces.ITodoStateListener;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.ViewHolder> implements View.OnClickListener {

    private List<Todo> todos;

    private final ITodoStateListener mListener;
    private final IListClickListener clickListener;

    public TodoListAdapter(ITodoStateListener mListener, IListClickListener clickListener) {
        this.mListener = mListener;
        this.clickListener = clickListener;
    }

    public void setData(List<Todo> todos){
        if(todos != null){
            this.todos = todos;
            this.sortData();
        }
    }

    public void sortData(){
        Collections.sort(this.todos, new TodoComparator(false));
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListviewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), viewType, parent, false);
        binding.getRoot().setOnClickListener(this);
        return new ViewHolder(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this.getTodo(position));
    }

    @Override
    public int getItemCount() {
        return todos != null ? todos.size() : 0;
    }

    private Todo getTodo(int position){
        return todos != null ? todos.get(position) : null;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_listview;
    }

    @Override
    public void onClick(View v) {
        clickListener.onListClick(v);
    }

    static class TodoComparator implements Comparator<Todo> {

        private boolean sortFavoriteAndDate;

        TodoComparator(boolean sortFavoriteAndDate) {
            this.sortFavoriteAndDate = sortFavoriteAndDate;
        }

        @Override
        public int compare(Todo o1, Todo o2) {
            int i = 0;
            if(o1.isDone() && !o2.isDone())
                i = -1;
            else if(!o1.isDone() && o2.isDone())
                i = 1;
            if(i != 0) return i;

            if(sortFavoriteAndDate){
                if(o1.isFavorite() && !o2.isFavorite())
                    i = -1;
                else if(!o1.isFavorite() && o2.isFavorite())
                    i = 1;
                if(i != 0) return i;
                i = o1.getDueDate().compareTo(o2.getDueDate());
            }else{
                i = o1.getDueDate().compareTo(o2.getDueDate());
                if(i != 0) return i;

                if(o1.isFavorite() && !o2.isFavorite())
                    i = -1;
                else if(!o1.isFavorite() && o2.isFavorite())
                    i = 1;
            }

            return i;

        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ItemListviewBinding binding;
        private final ITodoStateListener mListener;

        ViewHolder(@NonNull ItemListviewBinding binding, ITodoStateListener mListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.mListener = mListener;
        }

        void bind(Todo todo) {
            binding.setTodo(todo);
            binding.isDone.setOnClickListener(this);
            binding.isFavorite.setOnClickListener(this);
            binding.getRoot().setTag(binding.getTodo().getId());
        }


        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.isDone:
                    //Change done state
                    mListener.updateDone(binding.getTodo().getId(), !binding.getTodo().isDone());
                    break;
                case R.id.isFavorite:
                    //Change favorite state
                    mListener.updateFavorite(binding.getTodo().getId(), !binding.getTodo().isFavorite());
                    break;
            }
        }
    }
}