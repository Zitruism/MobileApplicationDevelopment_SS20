package de.zitruism.thl_todo_liste.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import de.zitruism.thl_todo_liste.R;
import de.zitruism.thl_todo_liste.database.model.Contact;
import de.zitruism.thl_todo_liste.database.model.Todo;
import de.zitruism.thl_todo_liste.databinding.FragmentDetailBinding;
import de.zitruism.thl_todo_liste.interfaces.IListClickListener;
import de.zitruism.thl_todo_liste.interfaces.IMainActivity;
import de.zitruism.thl_todo_liste.ui.adapters.ContactListAdapter;
import de.zitruism.thl_todo_liste.ui.adapters.TodoContactListAdapter;
import de.zitruism.thl_todo_liste.ui.viewmodel.DetailViewModel;

public class DetailFragment extends Fragment implements View.OnClickListener, IListClickListener {

    private IMainActivity mListener;
    private FragmentDetailBinding binding;

    private final static String BUNDLE_IDKEY = "TODO_ID";
    private int todoId;

    private AlertDialog datetimeDialog;
    private DatePicker datePicker;
    private TimePicker timePicker;

    @Inject
    DetailViewModel viewModel;
    private AlertDialog contactListDialog;
    private ContactListAdapter contactListAdapter;

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
                    System.out.println(todo.getContacts());
                    binding.setTodo(todo);
                    setBindingContacts();
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
        binding.dueDateEditText.setKeyListener(null);
        binding.dueDateEditText.setOnClickListener(this);

        binding.addContact.setOnClickListener(this);
        binding.contactList.setAdapter(new TodoContactListAdapter(this));

        binding.abort.setOnClickListener(this);
        binding.save.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.isDone:
                binding.getTodo().setDone(!binding.getTodo().isDone());
                binding.invalidateAll();
                break;
            case R.id.isFavorite:
                binding.getTodo().setFavorite(!binding.getTodo().isFavorite());
                binding.invalidateAll();
                break;
            case R.id.dueDateEditText:
                openDateTimePicker();
                break;
            case R.id.addContact:
                openContactList();
                break;
            case R.id.save:
                if(binding.getTodo().getId() != null)
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

    private void openDateTimePicker(){
        if(datetimeDialog == null){
            LayoutInflater factory = LayoutInflater.from(mListener.getActivity());
            final View datetimeView = factory.inflate(R.layout.layout_datetimepicker, null);
            datetimeDialog = new AlertDialog.Builder(mListener.getActivity()).create();
            datetimeDialog.setView(datetimeView);
            final ViewSwitcher viewSwitcher = datetimeView.findViewById(R.id.viewSwitcher);
            datetimeView.findViewById(R.id.date).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewSwitcher.setDisplayedChild(0);
                }
            });
            datetimeView.findViewById(R.id.time).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewSwitcher.setDisplayedChild(1);
                }
            });
            datePicker = datetimeView.findViewById(R.id.datePicker);
            timePicker = datetimeView.findViewById(R.id.timePicker);
            datetimeView.findViewById(R.id.apply).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //your business logic
                    Calendar c = Calendar.getInstance();
                    System.out.println(datePicker.getYear());
                    System.out.println(datePicker.getMonth());
                    System.out.println(datePicker.getDayOfMonth());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        c.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getHour(), timePicker.getMinute(), 0);
                    }else{
                        c.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                    }
                    binding.getTodo().setDueDate(c.getTime());
                    binding.invalidateAll();
                    datetimeDialog.dismiss();
                }
            });
            datetimeView.findViewById(R.id.abort).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datetimeDialog.dismiss();
                }
            });
        }

        if(binding.getTodo().getDueDate() != null) {
            //Set date and time according to todo-Item
            Calendar c = Calendar.getInstance();
            c.setTime(binding.getTodo().getDueDate());
            datePicker.updateDate(
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DATE));
            timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
        }

        datetimeDialog.show();
    }

    private void openContactList() {
        if(contactListDialog == null){
            LayoutInflater factory = LayoutInflater.from(mListener.getActivity());
            final View view = factory.inflate(R.layout.layout_contactlist, null);
            contactListDialog = new AlertDialog.Builder(mListener.getActivity()).create();
            contactListDialog.setView(view);

            contactListAdapter = new ContactListAdapter();
            ((RecyclerView)view.findViewById(R.id.contactList)).setAdapter(contactListAdapter);

            //TODO: Set on Key listener for edittext (filter list)

            //((RecyclerView)view.findViewById(R.id.contactList)).setAdapter();
            view.findViewById(R.id.abort).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contactListAdapter.clearSelection();
                    contactListDialog.dismiss();
                }
            });
            view.findViewById(R.id.apply).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Save marked contacts to todo.
                    List<String> selectedIds = contactListAdapter.getSelectedIds();
                    Todo todo = binding.getTodo();
                    List<String> contacts = todo.getContacts();
                    contacts.addAll(selectedIds);
                    todo.setContacts(contacts);
                    binding.setTodo(todo);
                    setBindingContacts();
                    contactListDialog.dismiss();
                }
            });
        }

        contactListAdapter.setData(this.getContacts(binding.getTodo().getContacts(), false));
        contactListDialog.show();
    }

    private List<Contact> getContacts(List<String> ids, boolean include){

        ArrayList<Contact> contacts = new ArrayList<>();

        ContentResolver cr = mListener.getActivityContentResolver();
        String[] projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                projection, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {

                Contact contact = new Contact(
                        cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID)),
                        cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY))
                );

                if(ids != null){
                    if(ids.contains(contact.getId()) && include){
                        contacts.add(contact);
                    }else if (!ids.contains(contact.getId()) && !include){
                        contacts.add(contact);
                    }
                }else{
                    contacts.add(contact);
                    contacts.add(contact);
                    contacts.add(contact);
                    contacts.add(contact);
                    contacts.add(contact);
                }
            }
        }
        if(cur!=null){
            cur.close();
        }

        System.out.println(contacts);

        return contacts;


    }

    private void setBindingContacts(){
        binding.setContacts(getContacts(binding.getTodo().getContacts(), true));
    }

    @Override
    public void onListClick(View v) {
        //Contact Row deletion
        if(v.getId() == R.id.deleteContact){
            String key = (String) v.getTag();
            Todo todo = binding.getTodo();
            List<String> contacts = todo.getContacts();
            contacts.remove(key);
            todo.setContacts(contacts);
            binding.setTodo(todo);
            setBindingContacts();
        }

    }
}
