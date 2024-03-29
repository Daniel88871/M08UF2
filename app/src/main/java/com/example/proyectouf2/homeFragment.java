package com.example.proyectouf2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class homeFragment extends Fragment {

    NavController navController;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        view.findViewById(R.id.gotoNewPostFragmentButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.newPost);
            }
        });

        RecyclerView postsRecyclerView = view.findViewById(R.id.postsRecyclerView);

        Query query = FirebaseFirestore.getInstance().collection("posts").limit(50);

        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post.class).setLifecycleOwner(this).build();

        postsRecyclerView.setAdapter(new PostsAdapter(options));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    class PostsAdapter extends FirestoreRecyclerAdapter<Post, PostsAdapter.PostViewHolder> {
        public PostsAdapter(@NonNull FirestoreRecyclerOptions<Post> options) {super(options);}

        @NonNull
        @Override
        public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_post, parent, false));
        }

        @Override
        protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull final Post post) {
            Glide.with(getContext()).load(post.authorPhotoUrl).circleCrop().into(holder.authorPhotoImageView);
            holder.authorTextView.setText(post.author);
            holder.contentTextView.setText(post.content);

        // Gestion de likes
            final String postKey = getSnapshots().getSnapshot(position).getId();
            final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            if(post.likes.containsKey(uid))
                holder.likeImageView.setImageResource(R.drawable.like_on);

            else
                holder.likeImageView.setImageResource(R.drawable.like_off);
                holder.numLikesTextView.setText(String.valueOf(post.likes.size()));
                holder.likeImageView.setOnClickListener(view -> {
                    FirebaseFirestore.getInstance().collection("posts")
                            .document(postKey)
                            .update("likes." + uid, post.likes.containsKey(uid) ?
                                    FieldValue.delete() : true);
                });


            // Gestion de retweets
            if(post.retweets.containsKey(uid))
                holder.retweetImageView.setImageResource(R.drawable.rt_on);

            else
                holder.retweetImageView.setImageResource(R.drawable.rt_off);
                holder.numRetweetsTextView.setText(String.valueOf(post.retweets.size()));
                holder.retweetImageView.setOnClickListener(view -> {
                    FirebaseFirestore.getInstance().collection("posts")
                            .document(postKey)
                            .update("retweets." + uid, post.retweets.containsKey(uid) ?
                                    FieldValue.delete() : true);
                });

            holder.papeleraImageView.setVisibility(View.VISIBLE);
            holder.papeleraImageView.setOnClickListener(view -> {
                FirebaseFirestore.getInstance().collection("posts")
                        .document(postKey)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Post deleted successfully", String.valueOf(aVoid));
                        })
                        .addOnFailureListener(e -> {
                            Log.w("Error deleting post", e);
                        });
            });
        }

        class PostViewHolder extends RecyclerView.ViewHolder {
            ImageView authorPhotoImageView, likeImageView, retweetImageView, comentariosImageView, papeleraImageView;
            TextView authorTextView, contentTextView, numLikesTextView, numRetweetsTextView, numComentariosTextView;

            PostViewHolder(@NonNull View itemView) {
                super(itemView);

                authorPhotoImageView = itemView.findViewById(R.id.authorphotoImageView);
                authorTextView = itemView.findViewById(R.id.authorTextView);
                contentTextView = itemView.findViewById(R.id.contentTextView);
                likeImageView = itemView.findViewById(R.id.likeImageView);
                numLikesTextView = itemView.findViewById(R.id.numLikesTextView);
                numRetweetsTextView = itemView.findViewById(R.id.numRetweetsTextView);
                retweetImageView = itemView.findViewById(R.id.retweetImageView);
                papeleraImageView = itemView.findViewById(R.id.papeleraImageView);
            }
        }
    }
}