package com.example.bearminimum;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class isbn_search_book extends AppCompatActivity {
    /******************ISBN add BOOK START ******************************/
    public EditText ISBNNum;
    public Button ISBNSearchButton;
    public String isbnValue;

    /******************ISBN add BOOK END ******************************/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.isbn_search_book);
        super.onCreate(savedInstanceState);
        /******************ISBN add BOOK START ******************************/
        ISBNSearchButton = findViewById(R.id.ISBNSearchButton);
        ISBNNum = findViewById(R.id.ISBNSearch);

        /******************ISBN add BOOK END ******************************/
        /******************ISBN add BOOK START ******************************/

        ISBNSearchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isbnValue = ISBNNum.getText().toString(); // get the isbn on click
                Log.i("Riky",isbnValue);
                if (isbnValue.length() < 10) {
                    Toast.makeText(isbn_search_book.this, "ISBN must be 10 or 13 digits", Toast.LENGTH_SHORT).show();
                } else {


                    String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbnValue;
                    //Do http request
                    Log.i("Riky",url);

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                            url,null, // here
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {


                                    String[] result = jsonParser(response);
                                    if (result[0] != null) {

                                        Intent intent = new Intent(isbn_search_book.this,isbnAddBook.class);

                                        intent.putExtra("name",result[0]);
                                        intent.putExtra("des",result[1]);
                                        intent.putExtra("author",result[2]);
                                        intent.putExtra("isbn",isbnValue);
                                        startActivityForResult(intent,0);

                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    AppController.getInstance(isbn_search_book.this).addToRequestQueue(request);
                }
            }
        });

        /******************ISBN add BOOK END ******************************/
    }
    /******************ISBN add BOOK START ******************************/
    public String[] jsonParser(JSONObject response) {
        String[] result = new String[5]; // volume information holder
        try {
            String totalItems = response.optString("totalItems");
            if (totalItems.equalsIgnoreCase("0")) {

                Toast.makeText(isbn_search_book.this, "Invalid ISBN", Toast.LENGTH_LONG).show();
            } else {
                JSONArray jsonArray = response.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject items = jsonArray.getJSONObject(i);

                    // get title info
                    String title = items.getJSONObject("volumeInfo").optString("title");
                    String subtitle = items.getJSONObject("volumeInfo").optString("subtitle");
                    result[0] = title + " : " + subtitle;

                    // get author info
                    result[1] = items.getJSONObject("volumeInfo").optString("description");

                    // get category and page count info
                    result[2] = items.getJSONObject("volumeInfo").optString("authors");
                    result[3] = items.getJSONObject("volumeInfo").optString("identifier");

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }


    /******************ISBN add BOOK END ******************************/
}