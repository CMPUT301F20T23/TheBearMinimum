package com.example.bearminimum;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.ViewHolder>{

    //holds books to display
    private ArrayList<User> usersDisplayed;

    private FirebaseFirestore db;
    private String username;

    //item click listener
    private OnUserClickListener mOnUserClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView username;
        private ImageView profileImg;

        //declare listener
        OnUserClickListener onUserClickListener;

        public ViewHolder(@NonNull View itemView, OnUserClickListener onUserClickListener) {
            super(itemView);

            //get textViews
            username = itemView.findViewById(R.id.search_username);
            profileImg = itemView.findViewById(R.id.search_user_img);

            this.onUserClickListener = onUserClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onUserClickListener.onUserClick(getAdapterPosition());
        }
    }

    public interface OnUserClickListener {
        void onUserClick(int position);
    }

    //constructor
    //needs a list of users to display
    public UserSearchAdapter(ArrayList<User> userList, OnUserClickListener onUserClickListener) {
        usersDisplayed = userList;
        this.mOnUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public UserSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_user_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v, mOnUserClickListener);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User currentUser = usersDisplayed.get(position);

        holder.username.setText(currentUser.getUsername());
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://thebearminimum-adecf.appspot.com/user_profile_images/" + currentUser.getUid());
        Log.d("MyDebug", currentUser.getUid());
        //load profile image
        Glide.with(holder.profileImg.getContext())
                .load(storageRef)
                .placeholder(R.drawable.logo_books)
                .apply(new RequestOptions().override(holder.profileImg.getHeight()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.profileImg);
    }

    @Override
    public int getItemCount() {
        if (usersDisplayed != null) {
            return usersDisplayed.size();
        } else {
            return 0;
        }
    }

   public void filteredList(ArrayList<User> filteredList) {
        usersDisplayed = filteredList;
        notifyDataSetChanged();
   }
}
