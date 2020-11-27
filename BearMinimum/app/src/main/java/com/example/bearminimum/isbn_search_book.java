package com.example.bearminimum;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * isbn_search_book
 *
 * Connects to Google Books Api to obtain book descriptions using
 * the ISBN when adding a new book
 *
 * Nov. 6, 2020
 */

public class isbn_search_book extends AppCompatActivity {


    /******************ISBN add BOOK START ******************************/
    public EditText ISBNNum;
    public Button ISBNSearchButton;
    public Button ISBNScannerButton;
    public String isbnValue;
    private FirebaseFirestore db;

    private static final int PERMISSIONS_REQUEST_ACCESS_CAMERA = 1;
    private static final int SCANNER_OK = 1177;
    private final static int SEARCH_INVALID = 0;
    private final static int SEARCH_OK = 1;
    private final static int SEARCH_CANCEL = 45;

    /******************ISBN add BOOK END ******************************/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.isbn_search_book);

        /******************ISBN add BOOK START ******************************/
        ISBNSearchButton = findViewById(R.id.ISBNSearchButton);
        ISBNScannerButton = findViewById(R.id.ISBN_scanner);

        ISBNNum = findViewById(R.id.ISBNSearch);
        Log.i("Riky","isbnSearch1");

        ISBNSearchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isbnValue = ISBNNum.getText().toString(); // get the isbn on click
                Log.i("Riky",isbnValue);
                if (isbnValue.length() != 10 && isbnValue.length() != 13) {
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
                                    Log.d("addbook", "api response");
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
        ISBNScannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(isbn_search_book.this,BarCodeHelper.class);
                startActivityForResult(intent4,0);
                
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent D) {
        super.onActivityResult(requestCode, resultCode, D);
        if (resultCode==1) {
            if(D != null){
                String name = D.getStringExtra("name");
                String author = D.getStringExtra("author");
                String isbn = D.getStringExtra("isbn");
                String des = D.getStringExtra("des");
                db = FirebaseFirestore.getInstance();

                HashMap<String, Object> data = new HashMap<>();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (name.length() > 0 && author.length() > 0 && isbn.length() > 0) {
                    data.put("title", name);
                    data.put("author", author);
                    data.put("isbn", isbn);
                    data.put("description", des);
                    data.put("bookid", "");
                    data.put("borrower", "~");
                    data.put("status", "available");
                    data.put("owner", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    data.put("requests", new ArrayList<String>());
                    data.put("latitude", "");
                    data.put("longitude", "");
                    db.collection("books")
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("DEBUG", "Data has been added successfully!");
                                    documentReference.update("bookid", documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("DEBUG", "Data could not be added!" + e.toString());
                                }
                            });
                    setResult(SEARCH_OK);
                    finish();


                }
            }
        } else if (resultCode == SCANNER_OK) {
            ISBNNum.setText(D.getExtras().getString("isbn"));
        }

        if (resultCode == 4){
            Log.i("Riky","Tester1");
            String isbn_num = D.getStringExtra("isbn_number");
            Log.i("Riky",isbn_num);
            ISBNNum.setText(isbn_num);
        }


    }
    /******************ISBN add BOOK START ******************************/
    public String[] jsonParser(JSONObject response) {
        String[] result = new String[5]; // volume information holder
        try {
            String totalItems = response.optString("totalItems");
            if (totalItems.equalsIgnoreCase("0")) {
                //Toast.makeText(isbn_search_book.this, "Invalid ISBN", Toast.LENGTH_LONG).show();
                Log.d("addbook", "parser got no result");
                this.setResult(SEARCH_INVALID);
                this.finish();
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

    private void getCameraPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startScanActivity();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_ACCESS_CAMERA);
        }
    }

    /**
     * Handles the result of the request for camera permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScanActivity();
                }
            }
        }

    }

    private void startScanActivity() {
        Log.i("Riky","yes");
        Intent intent = new Intent(isbn_search_book.this, BarCodeScanner.class);
        startActivityForResult(intent, SCANNER_OK);
    }

    @Override
    public void onBackPressed() {
        setResult(SEARCH_CANCEL);
        finish();
    }
}
