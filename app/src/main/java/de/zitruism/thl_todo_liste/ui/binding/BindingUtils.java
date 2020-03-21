package de.zitruism.thl_todo_liste.ui.binding;

import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;
import de.zitruism.thl_todo_liste.R;
import de.zitruism.thl_todo_liste.database.model.Contact;
import de.zitruism.thl_todo_liste.database.model.Todo;
import de.zitruism.thl_todo_liste.ui.adapters.TodoContactListAdapter;
import de.zitruism.thl_todo_liste.ui.adapters.TodoListAdapter;

public class BindingUtils {
    @BindingAdapter("data")
    public static void setRecyclerViewData(RecyclerView recyclerView, List<?> items) {
        if(items != null){
            if(recyclerView.getAdapter() instanceof TodoListAdapter){
                ((TodoListAdapter) recyclerView.getAdapter()).setData((List<Todo>) items);
            }else if(recyclerView.getAdapter() instanceof TodoContactListAdapter){
                ((TodoContactListAdapter) recyclerView.getAdapter()).setData((List<Contact>) items);
            }
        }
    }

    @BindingAdapter("date")
    public static void setDateText(TextView textView, Date date){
        if(date != null){
            textView.setText(DateFormat.getDateTimeInstance().format(date));
        }
    }

    @BindingAdapter("date")
    public static void setDateText(TextInputEditText textView, Date date){
        if(date != null){
            textView.setText(DateFormat.getDateTimeInstance().format(date));
        }
    }

    @BindingAdapter("isDone")
    public static void setDoneIcon(ImageView imageView, boolean isDone){
        if(isDone)
            imageView.setImageResource(R.drawable.ic_check_green_24dp);
        else
            imageView.setImageResource(R.drawable.ic_check_black_24dp_transparent);
    }

    @BindingAdapter("isFavorite")
    public static void setFavoriteIcon(ImageView imageView, boolean isFavorite){
        if(isFavorite)
            imageView.setImageResource(R.drawable.ic_star_yellow_24dp);
        else
            imageView.setImageResource(R.drawable.ic_star_border_black_24dp);
    }

}