package org.fundsofhope.androidapp.activities;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.fundsofhope.androidapp.R;

import java.util.List;

/**
 * Created by Anip on 12/3/2015.
 */
class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private String[] ti;
    private String[] desc;
    private Bitmap image;
    String TAG=null;

    public ContactAdapter(String[] title,String[] des ) {
        Log.i(TAG,"Entered contact adapter" );
        ti=title;
        desc=des;
        System.out.println(ti[0]);
    }

    @Override
    public int getItemCount() {
        return ti.length;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        //CustInfo ci = contactList.get(i);
        Log.i(TAG,"Entered on Bind View" );
        Log.i(TAG,ti[i]);
        contactViewHolder.title.setText(ti[i]);
        contactViewHolder.desc.setText(desc[i]);

        //contactViewHolder.vSurname.setText(ci.surname);
        //contactViewHolder.vEmail.setText(ci.email);
        //contactViewHolder.vTitle.setText(ci.name + " " + ci.surname);
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.activity_list, viewGroup, false);

        return new ContactViewHolder(itemView);
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView title;
        protected TextView desc;
        protected ImageView proj;
        protected TextView vTitle;

        public ContactViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
             desc = (TextView)  v.findViewById(R.id.desc);
             proj=(ImageView)   v.findViewById(R.id.img);
            //  vEmail = (TextView)  v.findViewById(R.id.txtEmail);
            //  vTitle = (TextView) v.findViewById(R.id.title);
        }

    }
}


