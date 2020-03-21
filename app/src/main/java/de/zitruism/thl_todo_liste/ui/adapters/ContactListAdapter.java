package de.zitruism.thl_todo_liste.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.zitruism.thl_todo_liste.R;
import de.zitruism.thl_todo_liste.database.model.Contact;
import de.zitruism.thl_todo_liste.databinding.ItemContactSelectionBinding;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> implements View.OnClickListener {

    private List<Contact> contacts;

    private List<String> selectedIds = new ArrayList<>();

    public ContactListAdapter() {

    }

    public void setData(List<Contact> contacts){
        if(contacts != null){
            this.selectedIds.clear();
            this.contacts = contacts;
            this.notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContactSelectionBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), viewType, parent, false);
        return new ViewHolder(binding, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = this.getContact(position);
        if(contact != null)
            holder.bind(contact, selectedIds.contains(contact.getId()));
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
        return R.layout.item_contact_selection;
    }

    public List<String> getSelectedIds(){
        return this.selectedIds;
    }

    @Override
    public void onClick(View v) {
        String id = (String) v.getTag();
        if(selectedIds.contains(id))
            selectedIds.remove(id);
        else
            selectedIds.add(id);
    }

    public void clearSelection() {
        selectedIds.clear();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ItemContactSelectionBinding binding;
        private final View.OnClickListener onClickListener;

        ViewHolder(@NonNull ItemContactSelectionBinding binding, View.OnClickListener onClickListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.getRoot().setOnClickListener(this);
            this.onClickListener = onClickListener;
        }

        void bind(Contact contact, boolean selected) {
            this.binding.setContact(contact);
            this.binding.setSelected(selected);
            this.binding.getRoot().setTag(contact.getId());
        }

        @Override
        public void onClick(View v) {
            binding.setSelected(!binding.getSelected());
            onClickListener.onClick(v);
        }
    }
}