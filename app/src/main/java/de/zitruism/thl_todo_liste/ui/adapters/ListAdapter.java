package de.zitruism.thl_todo_liste.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.zitruism.thl_todo_liste.R;
import de.zitruism.thl_todo_liste.database.model.Todo;
import de.zitruism.thl_todo_liste.databinding.ItemListviewBinding;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<Todo> todos;

    public void setData(List<Todo> todos){
        if(todos != null){
            this.todos = todos;
            this.notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListviewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), viewType, parent, false);
        return new ViewHolder(binding);
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

    static class ViewHolder extends RecyclerView.ViewHolder {

        ItemListviewBinding binding;

        ViewHolder(@NonNull ItemListviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Todo todo) {
            binding.setTodo(todo);
            binding.isDone.setTag(todo.getId());
            binding.isFavorite.setTag(todo.getId());
        }



    }
}