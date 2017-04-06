package com.example.rubel.u2uchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rubel.u2uchat.R;
import com.example.rubel.u2uchat.model.Friend;

import java.util.List;

/**
 * Created by rubel on 4/5/2017.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    List<Friend> mFriends = null;
    Context mContext;

    public FriendsAdapter(List<Friend> friends, Context context){
        mFriends = friends;
        this.mContext = context;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View currentView = LayoutInflater.from(mContext)
                .inflate(R.layout.friends_list_item, parent, false);
        return new FriendViewHolder(currentView);
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        Friend friend = mFriends.get(position);
        Glide.with(mContext)
                .load(friend.getPhotoUrl())
                .into(holder.getImageViewThumb());
        holder.getTextViewName().setText(friend.getName());
        if(!friend.isOnline())
            holder.getImageViewOnline().setVisibility(View.GONE);
        else holder.getImageViewOnline().setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewName;
        private ImageView imageViewThumb;
        private ImageView imageViewOnline;

        public FriendViewHolder(View view){
            super(view);
            textViewName = (TextView) view.findViewById(R.id.text_view_friend_item_name);
            imageViewOnline = (ImageView) view.findViewById(R.id.image_view_friend_item_online);
            imageViewThumb = (ImageView) view.findViewById(R.id.image_view_friend_item_thumb);
        }

        public TextView getTextViewName() {
            return textViewName;
        }

        public void setTextViewName(TextView textViewName) {
            this.textViewName = textViewName;
        }

        public ImageView getImageViewThumb() {
            return imageViewThumb;
        }

        public void setImageViewThumb(ImageView imageViewThumb) {
            this.imageViewThumb = imageViewThumb;
        }

        public ImageView getImageViewOnline() {
            return imageViewOnline;
        }

        public void setImageViewOnline(ImageView imageViewOnline) {
            this.imageViewOnline = imageViewOnline;
        }
    }
}
