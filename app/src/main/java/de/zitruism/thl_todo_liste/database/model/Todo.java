package de.zitruism.thl_todo_liste.database.model;



import java.util.Date;
import java.util.List;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import de.zitruism.thl_todo_liste.database.converters.DateConverter;

@Entity
@TypeConverters(DateConverter.class)
public class Todo {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    private String name;

    private String description;

    private boolean done = false;

    private boolean favorite = false;

    private Date dueDate;

    public Todo() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
}
