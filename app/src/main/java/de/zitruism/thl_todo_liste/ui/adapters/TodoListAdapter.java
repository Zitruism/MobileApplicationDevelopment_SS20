package de.zitruism.thl_todo_liste.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.zitruism.thl_todo_liste.R;
import de.zitruism.thl_todo_liste.database.model.Todo;
import de.zitruism.thl_todo_liste.databinding.ItemListviewBinding;
import de.zitruism.thl_todo_liste.interfaces.IListClickListener;
import de.zitruism.thl_todo_liste.interfaces.ITodoStateListener;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.ViewHolder> implements View.OnClickListener {

    private final AsyncListDiffer<Todo> mDiffer = new AsyncListDiffer<Todo>(this, DIFF_CALLBACK);

    private static final DiffUtil.ItemCallback<Todo> DIFF_CALLBACK
            = new DiffUtil.ItemCallback<Todo>() {
        @Override
        public boolean areItemsTheSame(
                @NonNull Todo oldUser, @NonNull Todo newUser) {
            // User properties may have changed if reloaded from the DB, but ID is fixed
            return oldUser.getId().equals(newUser.getId());
        }
        @Override
        public boolean areContentsTheSame(
                @NonNull Todo oldUser, @NonNull Todo newUser) {
            // NOTE: if you use equals, your object must properly override Object#equals()
            // Incorrectly returning false here will result in too many animations.
            return oldUser.equals(newUser);
        }
    };

    private final ITodoStateListener mListener;
    private final IListClickListener clickListener;

    private boolean favoritebeforedate;

    public void setSortOrder(boolean favoritebeforedate){
        this.favoritebeforedate = favoritebeforedate;
        this.sortData();
    }

    public TodoListAdapter(ITodoStateListener mListener, IListClickListener clickListener, boolean favoritebeforedate) {
        this.mListener = mListener;
        this.clickListener = clickListener;
        this.favoritebeforedate = favoritebeforedate;
    }

    private void sortData(){
        List<Todo> sortedList = new ArrayList<>(mDiffer.getCurrentList());
        Collections.sort(sortedList, new TodoComparator(favoritebeforedate));
        mDiffer.submitList(sortedList);
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

    public void submitList(List<Todo> list) {
        Collections.sort(list, new TodoComparator(favoritebeforedate));
        mDiffer.submitList(list);
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    private Todo getTodo(int position){
        return mDiffer.getCurrentList().get(position);
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
                if(o1.isFavourite() && !o2.isFavourite())
                    i = -1;
                else if(!o1.isFavourite() && o2.isFavourite())
                    i = 1;
                if(i != 0) return i;
                i = o1.getExpiry().compareTo(o2.getExpiry());
            }else{
                i = o1.getExpiry().compareTo(o2.getExpiry());
                if(i != 0) return i;

                if(o1.isFavourite() && !o2.isFavourite())
                    i = -1;
                else if(!o1.isFavourite() && o2.isFavourite())
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
                    mListener.updateFavorite(binding.getTodo().getId(), !binding.getTodo().isFavourite());
                    break;
            }
        }
    }
}