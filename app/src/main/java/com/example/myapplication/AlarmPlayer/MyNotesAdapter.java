package com.example.myapplication.AlarmPlayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class MyNotesAdapter extends RecyclerView.Adapter<MyNotesAdapter.MyNotesViewHolder> implements Filterable {
    private Context context;
    private ArrayList<MyNotes> listMyNotes;
    private ArrayList<MyNotes> mArrayList;

    private SqliteDatabase mDatabase;

    public MyNotesAdapter(Context context, ArrayList<MyNotes> listMyNotes) {
        this.context = context;
        this.listMyNotes = listMyNotes;
        this.mArrayList=listMyNotes;
        mDatabase = new SqliteDatabase(context);
    }

    @Override
    public MyNotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new MyNotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyNotesViewHolder holder, int position) {
        final MyNotes contacts = listMyNotes.get(position);

        holder.title.setText(contacts.getTitle());
        holder.detail.setText(contacts.getDetail());

        holder.editNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTaskDialog(contacts);
            }
        });

        holder.deleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete row from database

                mDatabase.deleteContact(contacts.getIds());

                //refresh the activity page.
                ((Activity)context).finish();
                context.startActivity(((Activity) context).getIntent());
            }
        });
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    listMyNotes = mArrayList;
                } else {

                    ArrayList<MyNotes> filteredList = new ArrayList<>();

                    for (MyNotes contacts : mArrayList) {

                        if (contacts.getTitle().toLowerCase().contains(charString)) {

                            filteredList.add(contacts);
                        }
                    }

                    listMyNotes = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listMyNotes;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listMyNotes = (ArrayList<MyNotes>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    @Override
    public int getItemCount() {
        return listMyNotes.size();
    }

    private void editTaskDialog(final MyNotes contacts){
        LayoutInflater inflater = LayoutInflater.from(context);
        View subView = inflater.inflate(R.layout.add_note_layout, null);

        final EditText titleField = (EditText)subView.findViewById(R.id.edtTitle);
        final EditText contactField = (EditText)subView.findViewById(R.id.edtDetail);

        if(contacts != null){
            titleField.setText(contacts.getTitle());
            contactField.setText(String.valueOf(contacts.getDetail()));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit MyNotes");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("EDIT MyNotes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String title = titleField.getText().toString();
                final String detail = contactField.getText().toString();

                if(TextUtils.isEmpty(title)){
                    Toast.makeText(context, "Something went wrong. Check your input values", Toast.LENGTH_LONG).show();
                }
                else{
                    mDatabase.updateMyNotes(new MyNotes(contacts.getIds(), title, detail));
                    //refresh the activity
                    ((Activity)context).finish();
                    context.startActivity(((Activity)context).getIntent());
                }
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Task cancelled", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    public class MyNotesViewHolder extends RecyclerView.ViewHolder {

        public TextView title,detail;
        public ImageView deleteNote;
        public  ImageView editNote;

        public MyNotesViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.noteTitle);
            detail = (TextView)itemView.findViewById(R.id.noteDetail);
            deleteNote = (ImageView)itemView.findViewById(R.id.deleteNote);
            editNote = (ImageView)itemView.findViewById(R.id.editNote);
        }
    }
}
