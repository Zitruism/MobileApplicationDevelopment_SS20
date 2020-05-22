package de.zitruism.thl_todo_liste.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.zitruism.thl_todo_liste.R;
import de.zitruism.thl_todo_liste.database.model.Contact;
import de.zitruism.thl_todo_liste.databinding.ItemContactBinding;
import de.zitruism.thl_todo_liste.interfaces.IContactClickListener;
import de.zitruism.thl_todo_liste.interfaces.IListClickListener;

public class TodoContactListAdapter extends RecyclerView.Adapter<TodoContactListAdapter.ViewHolder> {

    private List<Contact> contacts;
    private IContactClickListener mListener;

    public TodoContactListAdapter(IContactClickListener mListener) {
        this.mListener = mListener;
    }

    public void setData(List<Contact> contacts){
        if(contacts != null){
            this.contacts = contacts;
            this.notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContactBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), viewType, parent, false);
        return new ViewHolder(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this.getContact(position));
    }

    @Override
    public int getItemCount() {
        return contacts != null ? contacts.size() : 0;
    }

    private Contact getContact(int position){
        return contacts != null ? contacts.get(position) : null;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_contact;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ItemContactBinding binding;
        private final IContactClickListener mListener;

        ViewHolder(@NonNull ItemContactBinding binding, IContactClickListener mListener) {
            super(binding.getRoot());
            this.mListener = mListener;
            this.binding = binding;
            this.binding.deleteContact.setOnClickListener(this);
            this.binding.callContact.setOnClickListener(this);
            this.binding.messageContact.setOnClickListener(this);
            this.binding.mailContact.setOnClickListener(this);

        }

        void bind(Contact contact) {
            binding.setContact(contact);
            binding.deleteContact.setTag(binding.getContact().getId());
            binding.callContact.setTag(binding.getContact().getId());
            binding.messageContact.setTag(binding.getContact().getId());
            binding.mailContact.setTag(binding.getContact().getId());
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.deleteContact:
                    mListener.onDeleteClick(v);
                    break;
                case R.id.callContact:
                    mListener.onCallClick(v);
                    break;
                case R.id.messageContact:
                    mListener.onMessageClick(v);
                    break;
                case R.id.mailContact:
                    mListener.onMailClick(v);
                    break;
            }
        }
    }
}