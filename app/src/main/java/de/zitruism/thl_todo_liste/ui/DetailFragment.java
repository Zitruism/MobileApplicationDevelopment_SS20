package de.zitruism.thl_todo_liste.ui;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.ViewSwitcher;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
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

    private AlertDialog datetimeDialog;
    private DatePicker datePicker;
    private TimePicker timePicker;

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
        binding.dueDateEditText.setKeyListener(null);
        binding.dueDateEditText.setOnClickListener(this);

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
            case R.id.dueDateEditText:
                openDateTimePicker();
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


}
