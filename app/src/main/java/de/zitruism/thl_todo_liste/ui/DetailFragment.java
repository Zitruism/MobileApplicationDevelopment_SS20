package de.zitruism.thl_todo_liste.ui;

import android.Manifest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewSwitcher;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.SharedElementCallback;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import de.zitruism.thl_todo_liste.R;
import de.zitruism.thl_todo_liste.database.model.Contact;
import de.zitruism.thl_todo_liste.database.model.ContactDetailElement;
import de.zitruism.thl_todo_liste.database.model.Todo;
import de.zitruism.thl_todo_liste.databinding.FragmentDetailBinding;
import de.zitruism.thl_todo_liste.interfaces.IContactClickListener;
import de.zitruism.thl_todo_liste.interfaces.IDetailViewCallback;
import de.zitruism.thl_todo_liste.interfaces.IMainActivity;
import de.zitruism.thl_todo_liste.network.ActiveContactLoaderTask;
import de.zitruism.thl_todo_liste.network.AvailableContactLoaderTask;
import de.zitruism.thl_todo_liste.ui.adapters.ContactListAdapter;
import de.zitruism.thl_todo_liste.ui.adapters.TodoContactListAdapter;
import de.zitruism.thl_todo_liste.ui.viewmodel.DetailViewModel;
import de.zitruism.thl_todo_liste.ui.viewmodel.ViewModelFactory;

public class DetailFragment extends Fragment implements View.OnClickListener, IContactClickListener, IDetailViewCallback {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int MY_PERMISSIONS_REQUEST_READ_ONLY_TODO_CONTACTS = 101;
    private IMainActivity mListener;
    private FragmentDetailBinding binding;

    private final static String BUNDLE_IDKEY = "TODO_ID";
    private Long todoId;

    private AlertDialog datetimeDialog;
    private DatePicker datePicker;
    private TimePicker timePicker;

    private DetailViewModel viewModel;
    private AlertDialog contactListDialog;
    private ContactListAdapter contactListAdapter;

    private AlertDialog deleteConfirmDialog;

    @Inject
    ViewModelFactory viewModelFactory;

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
            todoId = bundle.getLong(BUNDLE_IDKEY, -1);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DetailViewModel.class);

        if(todoId >= 0){

            /*
             *  Prevent data reload in case of orientation switch
             */
            if(viewModel.getTodo().getValue() == null)
                //Get todo from db (with id) and set todo in viewmodel
                viewModel.getTodo(todoId).observe(getViewLifecycleOwner(), new Observer<Todo>() {
                    @Override
                    public void onChanged(Todo todo) {
                        if(todo != null){
                            viewModel.setTodo(todo);
                            binding.setTodo(todo);
                            setBindingContacts();
                        }
                    }
                });
            else
                //Get viewmodel todo instead of database
                viewModel.getTodo().observe(getViewLifecycleOwner(), new Observer<Todo>() {
                    @Override
                    public void onChanged(Todo todo) {
                        if(todo != null){
                            binding.setTodo(todo);
                            setBindingContacts();
                        }
                    }
                });
        }else{
            if(viewModel.getTodo().getValue() == null)
                viewModel.setTodo(new Todo());
            viewModel.getTodo().observe(getViewLifecycleOwner(), new Observer<Todo>() {
                @Override
                public void onChanged(Todo todo) {
                    if(todo != null){
                        binding.setTodo(todo);
                        setBindingContacts();
                    }
                }
            });
        }
        viewModel.getTodoContacts().observe(getViewLifecycleOwner(), new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {
                binding.setContacts(contacts);
            }
        });

        viewModel.getLocked().observe(this.getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.setLocked(aBoolean);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mListener.setToolbarTitle(getString(R.string.detailview_title));

        //Show toolbar menu
        if(todoId != -1)
            setHasOptionsMenu(true);

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
    public void onPause() {
        if(datetimeDialog != null){
            datetimeDialog.dismiss();
            datetimeDialog = null;
        }
        if(contactListDialog != null){
            contactListDialog.dismiss();
            contactListDialog = null;
        }

        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailmenu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.delete){
            showDeleteConfirm();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.isDone:
                binding.getTodo().setDone(!binding.getTodo().isDone());
                binding.invalidateAll();
                break;
            case R.id.isFavorite:
                binding.getTodo().setFavourite(!binding.getTodo().isFavourite());
                binding.invalidateAll();
                break;
            case R.id.dueDateEditText:
                openDateTimePicker();
                break;
            case R.id.addContact:
                if(checkForContactPermission(false))
                    openContactList();
                break;
            case R.id.delete:
                showDeleteConfirm();
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

    private void showDeleteConfirm() {
        if(deleteConfirmDialog == null)
            deleteConfirmDialog = createDeleteConfirmDialog();
        deleteConfirmDialog.show();
    }

    private AlertDialog createDeleteConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mListener.getActivity());
        builder.setMessage(R.string.dialog_confirmTodoDeletion)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        viewModel.setLocked(true);
                        viewModel.deleteTodo(binding.getTodo(), mListener.isWebServiceAvailable(), DetailFragment.this);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    private void createTodo(Todo todo) {
        viewModel.insert(todo, mListener.isWebServiceAvailable());
    }

    private void saveTodo(Todo todo){
        viewModel.updateTodo(todo, mListener.isWebServiceAvailable());
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

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        c.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getHour(), timePicker.getMinute(), 0);
                    }else{
                        c.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                    }
                    binding.getTodo().setExpiry(c.getTime().getTime());
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

        if(binding.getTodo().getExpiry() != null) {
            //Set date and time according to todo-Item
            Calendar c = Calendar.getInstance();
            c.setTime(new Date(binding.getTodo().getExpiry()));
            datePicker.updateDate(
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DATE));
            timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
        }

        datetimeDialog.show();
    }

    private boolean checkForContactPermission(boolean onlyTodo){
        //Check for permission
        if (ContextCompat.checkSelfPermission(mListener.getActivity(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if(onlyTodo){
                requestPermissions(
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_ONLY_TODO_CONTACTS);
                return false;
            }else{
                requestPermissions(
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openContactList();
            }
        }else if (requestCode == MY_PERMISSIONS_REQUEST_READ_ONLY_TODO_CONTACTS) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new ActiveContactLoaderTask(binding.getTodo().getContacts(), viewModel, mListener).execute();
            }
        }
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
                    viewModel.setTodo(todo);
                    contactListDialog.dismiss();
                }
            });
        }
        contactListAdapter.setData(null);
        new AvailableContactLoaderTask(mListener, binding.getTodo().getContacts(), contactListAdapter).execute();
        contactListDialog.show();
    }

    private void setBindingContacts(){
        if(checkForContactPermission(true))
            new ActiveContactLoaderTask(binding.getTodo().getContacts(), viewModel, mListener).execute();
    }

    @Override
    public void onDeleteClick(View v) {
        //Contact Row deletion
        String key = (String) v.getTag();
        Todo todo = binding.getTodo();
        List<String> contacts = todo.getContacts();
        contacts.remove(key);
        todo.setContacts(contacts);
        binding.setTodo(todo);
        setBindingContacts();
        viewModel.setTodo(todo);
    }

    private Contact getSelectedContact(String key){
        List contacts = binding.getContacts();
        for(int i=0; i<contacts.size(); i++){
            Contact c = (Contact) contacts.get(i);
            if(c.getId().equals(key))
                return c;
        }
        return null;
    }

    @Override
    public void onCallClick(View v) {
        String key = (String) v.getTag();
        Contact contact = getSelectedContact(key);
        if(contact != null){

            //Check for phonenumbers
            final List<ContactDetailElement> numbers = contact.getNumbers();
            String[] phoneItems = new String[numbers.size()];
            for(int i=0; i<numbers.size(); i++){
                phoneItems[i] = numbers.get(i).toString();
            }
            if(numbers.size() > 1){
                AlertDialog.Builder builder = openDetailElementSelection(key);
                builder.setItems(phoneItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callIntent(numbers.get(which).getValue());
                        dialog.dismiss();
                    }
                });
                builder.show();
            }else if(numbers.size() == 1){
                callIntent(numbers.get(0).getValue());
            }
        }
    }

    private void callIntent(String number){
        //Open phone app and pass number as parameter
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+number));
        startActivity(Intent.createChooser(intent, getString(R.string.executewith)));
    }

    @Override
    public void onMessageClick(View v) {
        String key = (String) v.getTag();
        Contact contact = getSelectedContact(key);
        if(contact != null){

            //Check for phonenumbers
            final List<ContactDetailElement> numbers = contact.getNumbers();
            String[] phoneItems = new String[numbers.size()];
            for(int i=0; i<numbers.size(); i++){
                phoneItems[i] = numbers.get(i).toString();
            }
            if(numbers.size() > 1){
                AlertDialog.Builder builder = openDetailElementSelection(key);
                builder.setItems(phoneItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        messageIntent(numbers.get(which).getValue());
                        dialog.dismiss();
                    }
                });
                builder.show();
            }else if(numbers.size() == 1){
                messageIntent(numbers.get(0).getValue());
            }
        }
    }

    private void messageIntent(String number){
        //Open messaging app and pass number as parameter
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("sms:"+number));
        intent.putExtra("sms_body", binding.getTodo().toString());
        startActivity(Intent.createChooser(intent, getString(R.string.executewith)));
    }

    @Override
    public void onMailClick(View v) {
        String key = (String) v.getTag();
        Contact contact = getSelectedContact(key);
        if(contact != null){

            //Check for phonenumbers
            final List<ContactDetailElement> mailAddresses = contact.getEmails();
            String[] mailItems = new String[mailAddresses.size()];
            for(int i=0; i<mailAddresses.size(); i++){
                mailItems[i] = mailAddresses.get(i).toString();
            }
            if(mailAddresses.size() > 1){
                AlertDialog.Builder builder = openDetailElementSelection(key);
                builder.setItems(mailItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mailIntent(mailAddresses.get(which).getValue());
                        dialog.dismiss();
                    }
                });
                builder.show();
            }else if(mailAddresses.size() == 1){
                mailIntent(mailAddresses.get(0).getValue());
            }
        }
    }

    private void mailIntent(String mail){
        //Open messaging app and pass number as parameter
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"+mail));
        intent.putExtra(Intent.EXTRA_SUBJECT, binding.getTodo().getName());
        intent.putExtra(Intent.EXTRA_TEXT, binding.getTodo().getDescription());
        startActivity(Intent.createChooser(intent, getString(R.string.executewith)));
    }

    private ListAdapter createContactDetailElementAdapter(final ContactDetailElement[] items){
        return new ArrayAdapter<ContactDetailElement>(
                mListener.getActivity().getApplicationContext(), R.layout.item_contact_actionselection, items) {

            ViewHolder holder;

            class ViewHolder {
                TextView title;
                TextView value;
            }

            public View getView(int position, View convertView,
                                ViewGroup parent) {
                final LayoutInflater inflater = (LayoutInflater) mListener.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                if (convertView == null) {
                    assert inflater != null;
                    convertView = inflater.inflate(
                            R.layout.item_contact_actionselection, null);

                    holder = new ViewHolder();
                    holder.value = convertView.findViewById(R.id.value);
                    holder.title = convertView.findViewById(R.id.title);
                    convertView.setTag(holder);
                } else {
                    // view already defined, retrieve view holder
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.title.setText(items[position].getTitle());
                holder.value.setText(items[position].getValue());

                return convertView;
            }
        };
    }

    private AlertDialog.Builder openDetailElementSelection(String contactId){
        AlertDialog.Builder builder = new AlertDialog.Builder(mListener.getActivity());
        builder.setTitle(R.string.selectnumber);
        return builder;
    }

    @Override
    public void onDeleted() {
        navToList();
    }
}
