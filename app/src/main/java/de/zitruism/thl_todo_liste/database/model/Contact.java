package de.zitruism.thl_todo_liste.database.model;

import java.util.List;

public class Contact {

    private String id;
    private String name;
    private List<ContactDetailElement> numbers;
    private List<ContactDetailElement> emails;


    public Contact(String id, String name, List<ContactDetailElement> numbers, List<ContactDetailElement> emails) {
        this.id = id;
        this.name = name;
        this.numbers = numbers;
        this.emails = emails;
    }

    public Contact(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<ContactDetailElement> getNumbers() {
        return numbers;
    }

    public List<ContactDetailElement> getEmails() {
        return emails;
    }
}
