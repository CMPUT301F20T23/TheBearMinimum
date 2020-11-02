package com.example.bearminimum.ui.ViewBookState;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bearminimum.R;

import java.util.ArrayList;

public class MainActivity_Bookstatus extends AppCompatActivity {
    public static ListView BList;
    public static ArrayAdapter<Book> bookAdapter;
    public static ArrayList<Book> bookDataList;
    public String selectedFilter = "all";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //BList = findViewById(R.id.listview);

        String[] name = {"Mind Playter","Maybe you should talk to someone","The Silent Patient","I'm Dead, Now What?"};
        String[] author = {"NAJWA ZEBIAN","Lori Gottlieb","Alex Michaelides"," Peter Pauper Press"};
        String[] ISBN = {"12345","666666","88h8h8","loki999"};


        bookDataList = new ArrayList<>();

        for (int i = 0; i < name.length; i++) {
            Book book = new Book(name[i], author[i], ISBN[i]);
            if (i==3){
                book.setState(true);
                book.setBorrower("Riky");
            }
            else{
                book.setState(false);
                book.setBorrower("UnKnown");
            }

            bookDataList.add(book);

        }



        bookAdapter = new BookAdapter(this, bookDataList);

        BList.setAdapter(bookAdapter);
    }
    private void FilterList(String Clicked){
        selectedFilter = Clicked;
        ArrayList<Book> stateFilter = new ArrayList<Book>();
        for(Book book:bookDataList){
            if (Clicked == "UNBORROWED"){
                if (!book.getState()){
                    stateFilter.add(book);
                }
            }
            else if (Clicked == "BORROWED"){
                if (book.getState()){
                    stateFilter.add(book);
                }
            }

        }
        bookAdapter = new BookAdapter(this, stateFilter);

        BList.setAdapter(bookAdapter);

    }
    public void all_button(View view){
        bookAdapter = new BookAdapter(this, bookDataList);

        BList.setAdapter(bookAdapter);

    }

    public void UB_button(View view){
        FilterList("UNBORROWED");

    }
    public void B_button(View view){
        FilterList("BORROWED");
    }
}