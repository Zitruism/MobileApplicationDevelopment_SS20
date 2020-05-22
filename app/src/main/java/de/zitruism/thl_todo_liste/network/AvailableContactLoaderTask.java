package de.zitruism.thl_todo_liste.network;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import de.zitruism.thl_todo_liste.database.model.Contact;
import de.zitruism.thl_todo_liste.database.model.ContactDetailElement;
import de.zitruism.thl_todo_liste.interfaces.IMainActivity;
import de.zitruism.thl_todo_liste.ui.adapters.ContactListAdapter;

public class AvailableContactLoaderTask extends AsyncTask<Void, Void, ArrayList<Contact>> {


    //Contacts that are already part of the item. Ignore them.
    private List<String> ids;
    private ContactListAdapter adapter;
    private IMainActivity mListener;


    public AvailableContactLoaderTask(IMainActivity mListener, List<String> ids, ContactListAdapter adapter) {
        this.ids = ids;
        this.adapter = adapter;
        this.mListener = mListener;
    }

    @Override
    protected ArrayList<Contact> doInBackground(Void... voids) {

        ArrayList<Contact> contacts = new ArrayList<>();

        ContentResolver cr = mListener.getActivityContentResolver();
        String[] projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                projection, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {

                String contactId = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));


                if(ids != null && ids.contains(contactId)){
                    continue;
                }

                //  Get all phone numbers.
                Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                List<ContactDetailElement> numbers = new ArrayList<>();
                if((phones != null ? phones.getCount() : 0) > 0){
                    while (phones.moveToNext()) {
                        String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        String label = mListener.getActivity().getString(ContactsContract.CommonDataKinds.Phone.getTypeLabelResource(type));
                        numbers.add(new ContactDetailElement(number, label));
                    }
                }
                if(phones != null)
                    phones.close();

                //Get all mail addresses
                Cursor mails = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
                List<ContactDetailElement> addresses = new ArrayList<>();
                if((mails != null ? mails.getCount() : 0) > 0){
                    while (mails.moveToNext()) {
                        String email = mails.getString(mails.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                        int type = mails.getInt(mails.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                        String label = mListener.getActivity().getString(ContactsContract.CommonDataKinds.Email.getTypeLabelResource(type));
                        addresses.add(new ContactDetailElement(email, label));
                    }
                }
                if(mails != null)
                    mails.close();

                Contact contact = new Contact(
                        contactId,
                        cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)),
                        numbers,
                        addresses
                );

                contacts.add(contact);

                /*if(ids != null){
                    if(ids.contains(contact.getId()) && include){
                        contacts.add(contact);
                    }else if (!ids.contains(contact.getId()) && !include){
                        contacts.add(contact);
                    }
                }else{
                    contacts.add(contact);
                }*/
            }
        }
        if(cur!=null){
            cur.close();
        }
        return contacts;
    }

    @Override
    protected void onPostExecute(ArrayList<Contact> contacts) {
        //in case the adapter isn't there anymore.
        if(adapter != null){
            adapter.setData(contacts);
        }
    }
}
