package fun.wxy.annoy_o_tron.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fun.wxy.annoy_o_tron.R;

/**
 * Created by 0_o on 2016/4/28.
 */
public class Contact {
    // model
    public class ContactModel {
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private String name;
    }

    // generate tool for test
    public static List<ContactModel> generateSampleList() {
        List<ContactModel> result = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            ContactModel it = new Contact().new ContactModel();
            it.setName("Name - " + i);
            result.add(it);
        }
        return result;
    }

    // view holder
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nameTextView;
        public ViewHolder(View itemView){
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.tv_name);

        }
    }

    // adapter
    public class ContactsAdapter extends RecyclerView.Adapter<Contact.ViewHolder> {
        // constructer & getItemCount
        private List<ContactModel> mContacts;

        public ContactsAdapter(List<ContactModel> contacts){
            mContacts = contacts;
        }

        @Override
        public int getItemCount() {
            return mContacts.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            Context context = viewGroup.getContext();

            View contactView = LayoutInflater.from(context).inflate(R.layout.item_contact, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(contactView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            ContactModel contact = mContacts.get(position);
            TextView nameTextView = viewHolder.nameTextView;
            nameTextView.setText(contact.getName());
        }
    }
}
