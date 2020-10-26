package com.example.bearminimum;

import android.app.Activity;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.content.ContentValues.TAG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AddDeleteBookTest {

    //sample user acc info to test with
    private String uid = "";
    private String email = "";
    private String uName = "";
    private String phone = "";
    private String password = "";

    //sample books to test with
    //book1
    String title1 = "I Want My Hat Back";
    String author1 = "Jon Klassen";
    String owner1 = uid;
    String borrower1 = null;  //can be 'unknown' or 'none'
    String description1 = "A bear almost gives up his search for his missing " +
            "hat until he remembers something important.";
    String ISBN1 = "0763655988";
    String status1 = "available";

    //book2
    String title2 = "This is Not My Hat";
    String author2 = "Jon Klassen";
    String owner2 = uid;
    String borrower2 = null;  //can be 'unknown' or 'none'
    String description2 = "A tiny minnow wearing a pale blue bowler hat has a " +
            "thing or two up his fins in this underwater light-on-dark " +
            "chase scene.";
    String ISBN2 = "0763655996";
    String status2 = "available";

    //book3
    String title3 = "We Found A Hat";
    String author3 = "Jon Klassen";
    String owner3 = uid;
    String borrower3 = null;  //can be 'unknown' or 'none'
    String description3 = "Two turtles have found a hat. The hat looks good on both of " +
            "them, but there is only one hat.";
    String ISBN3 = "0763656003";
    String status3 = "available";

    //firebase
    FirebaseFirestore db;

    //Robotium testing
    private Solo solo;


    //TODO
    // verify that we're using uid to specify owner


    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(AuthPage.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * logs in using sample user info
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());

        //click sign up button
        solo.clickOnButton("Sign in");
        solo.waitForActivity("LoginActivity");
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        //get views for input fields
        //enter login info
        solo.enterText((EditText) solo.getView(R.id.email_box), email);

        //press continue
        solo.clickOnButton("CONTINUE");
        solo.sleep(2000);

        solo.enterText((EditText) solo.getView(R.id.password_box), password);
        solo.clickOnButton("CONFIRM");  //confirm info to login

        //check if it navigates to MainActivity after login
        solo.waitForActivity("MainActivity");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    /**
     * Adds a book
     * checks if the book is there using assertTrue on ISBN and title
     */
    @Test
    public void addBook(){
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        //TODO
        // adjust as needed to add book process

        //click add book button
        solo.clickOnButton("add book");

        //get input fields for book creation
        //creating mockbook1
        //TODO
        // do we need to enter owner when creating a book
        solo.enterText((EditText) solo.getView(R.id.bookTitle), title1);
        solo.enterText((EditText) solo.getView(R.id.bookAuthor), author1);
        solo.enterText((EditText) solo.getView(R.id.bookDescription), description1);
        solo.enterText((EditText) solo.getView(R.id.bookISBN), ISBN1);
        solo.clickOnButton("Enter");  //confirm book creation

        //check if book is now in database under logged in user
        //check if logged in user matches with current user from database
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            //TODO
            // double check if .getUid() and getEmail() works
            //if user exists, check info matches
            String userID = user.getUid();
            String userEmail = user.getEmail();

            assertEquals(uid, userID);
            assertEquals(email, userEmail);

            //access Firestore
            db = FirebaseFirestore.getInstance();

            //check for book
            //(https://firebase.google.com/docs/firestore/query-data/queries)
            //query for book such that:
            //      owner = current userID
            //      title = added book
            //      ISBN  = added book
            db.collection("books")
                    .whereEqualTo("owner", userID)
                    .whereEqualTo("title", title1)
                    .whereEqualTo("ISBN", ISBN1)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                //TODO
                                // create a list of all documents found under user
                                // find the added book within the list

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());

//                                    //get data if successful to check if they are the same
//                                    String bookOwner = document.getString("owner");
//                                    String bookTitle = document.getString("title");
//                                    String bookISBN = document.getString("ISBN");
//                                    assertEquals(userID, bookOwner);
//                                    assertEquals(title1, bookTitle);
//                                    assertEquals(ISBN1, bookISBN);

                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                                fail("Couldn't get the book");
                            }
                        }
                    });
        }



    }

    /**
     * deletes a book
     * checks if the number of books under the user's name decreases
     */
    @Test
    public void deleteBook() {
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        //add books (mockbook2, mockbook3)
        //creating mockbook2
        solo.clickOnButton("add book");
        //TODO
        // do we need to enter owner when creating a book
        solo.enterText((EditText) solo.getView(R.id.bookTitle), title2);
        solo.enterText((EditText) solo.getView(R.id.bookAuthor), author2);
        solo.enterText((EditText) solo.getView(R.id.bookDescription), description2);
        solo.enterText((EditText) solo.getView(R.id.bookISBN), ISBN2);
        solo.clickOnButton("Enter");  //confirm book creation
        //creating mockbook3
        solo.clickOnButton("add book");
        solo.enterText((EditText) solo.getView(R.id.bookTitle), title3);
        solo.enterText((EditText) solo.getView(R.id.bookAuthor), author3);
        solo.enterText((EditText) solo.getView(R.id.bookDescription), description3);
        solo.enterText((EditText) solo.getView(R.id.bookISBN), ISBN3);
        solo.clickOnButton("Enter");  //confirm book creation

        //TODO
        // adjust as needed to delete book process
        // how does user select the book?

        //click delete book button
        solo.clickOnButton("delete book");



    }
}

