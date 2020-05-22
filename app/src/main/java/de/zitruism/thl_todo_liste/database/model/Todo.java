package de.zitruism.thl_todo_liste.database.model;



import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import de.zitruism.thl_todo_liste.database.converters.DateConverter;

@Entity
@TypeConverters({DateConverter.class})
public class Todo {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private String name;

    private String description;

    private boolean done = false;

    private boolean favourite = false;

    private Long expiry;

    private List<String> contacts = new ArrayList<>();

    public Todo() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public Long getExpiry() {
        return expiry;
    }

    public void setExpiry(Long expiry) {
        this.expiry = expiry;
    }

    public List<String> getContacts() {
        return this.contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        if(getDescription().length() > 0) {
            sb.append(" - ");
            sb.append(getDescription());
        }
        return sb.toString();
    }
}
