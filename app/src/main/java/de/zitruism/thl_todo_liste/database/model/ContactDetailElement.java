package de.zitruism.thl_todo_liste.database.model;

import androidx.annotation.NonNull;

public class ContactDetailElement {

    private String value;
    private String title;

    public ContactDetailElement(String value, String title) {
        this.value = value;
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public String getTitle() {
        return title;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(getTitle());
        sb.append(") ");
        sb.append(getValue());
        return sb.toString();
    }
}
