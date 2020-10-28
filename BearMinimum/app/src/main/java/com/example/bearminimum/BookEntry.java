package com.example.bearminimum;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class BookEntry extends AppCompatDialogFragment {
    private EditText edit_title;
    private EditText edit_descr;
    private EditText edit_author;
    private EditText edit_ISBN;
    private EditText edit_Owner;
    private dialoglistner listner;
    private String title;
    private String descr;
    private String author;
    private String ISBN;
    private Book thisBook;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.add_book,null);
        edit_author=view.findViewById(R.id.editAuthor);
        edit_descr=view.findViewById(R.id.editDescr);
        edit_ISBN=view.findViewById(R.id.editISBN);
        edit_title=view.findViewById(R.id.editTitle);

        if(getArguments()!=null){
            thisBook=(Book)getArguments().getSerializable("book");
        } else thisBook=null;

        if (thisBook!=null){
            edit_author.setText(thisBook.getAuthor());
            edit_descr.setText(thisBook.getDescription());
            edit_ISBN.setText(thisBook.getISBN());
            edit_title.setText(thisBook.getTitle());
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setTitle("Add/Edit Book")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Enter", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        title=edit_title.getText().toString();
                        descr=edit_descr.getText().toString();
                        ISBN=edit_ISBN.getText().toString();
                        author=edit_author.getText().toString();

                        if (thisBook==null){
                            listner.applyText(title, descr, ISBN, author);
                        } else {
                            thisBook.setAuthor(author);
                            thisBook.setDescription(descr);
                            thisBook.setISBN(ISBN);
                            thisBook.setTitle(title);
                        }
                    }


                })
                .create();

    }

    public interface dialoglistner{
        void applyText(String title, String descr, String ISBN, String author);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listner=(dialoglistner)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+" must be implemented");
        }
    }

    static BookEntry newInstance(Book thisitem){
        Bundle args;
        if (thisitem!=null){
            args=new Bundle();
            args.putSerializable("book",thisitem);

        }else args=null;

        BookEntry fragment=new BookEntry();
        fragment.setArguments(args);
        return fragment;
    }
}
