package de.zitruism.thl_todo_liste.ui.binding;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;
import de.zitruism.thl_todo_liste.database.model.Todo;
import de.zitruism.thl_todo_liste.ui.adapters.ListAdapter;

public class BindingUtils {
    @BindingAdapter("data")
    public static void setRecyclerViewData(RecyclerView recyclerView, List<?> items) {
        if(recyclerView.getAdapter() instanceof ListAdapter){
            if(items != null)
                ((ListAdapter) recyclerView.getAdapter()).setData((List<Todo>) items);
        }
    }

    @BindingAdapter("android:text")
    public static void setDateText(TextView textView, Date date){
        if(date != null)
            textView.setText(date.toString());
    }

    @BindingAdapter("isDone")
    public static void setDoneIcon(ImageView imageView, boolean isDone){

    }

    @BindingAdapter("isFavorite")
    public static void setFavoriteIcon(ImageView imageView, boolean isFavorite){

    }

}