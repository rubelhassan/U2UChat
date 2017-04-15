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
import com.example.rubel.u2uchat.model.User;

import java.util.List;

/**
 * Created by rubel on 4/15/2017.
 */

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.SearchViewHolder> {

    private SearchItemClickListener listener;

    private List<User> mUsersList;
    private Context mContext;

    public SearchListAdapter(List<User> mUsersList, Context mContext) {
        this.mUsersList = mUsersList;
        this.mContext = mContext;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View currentView = LayoutInflater.from(mContext).inflate(R.layout.search_list_item,
                parent, false);
        return new SearchViewHolder(currentView);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        User user = mUsersList.get(position);
        Glide.with(mContext)
                .load(user.getPhotoUrl())
                .into(holder.getIvProfile());
        if (user.isOnline())
            holder.getIvOnline().setVisibility(View.VISIBLE);
        else holder.getIvOnline().setVisibility(View.GONE);

        holder.getTvName().setText(user.getFullName());
        holder.getTvDistance().setText("12 km");
        holder.getTvProfession().setText("Computer Sciend Student");
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }

    // method to set SearchItemClickListener to this adapter from activity/fragment using it
    public void setSearchItemClickListener(SearchItemClickListener listener) {
        this.listener = listener;
    }

    public User getItem(int position) {
        return mUsersList.get(position);
    }

    // search item click listener interface
    public interface SearchItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvProfession;
        TextView tvDistance;
        ImageView ivProfile;
        ImageView ivOnline;

        public SearchViewHolder(View view) {
            super(view);
            this.tvName = (TextView) view.findViewById(R.id.text_view_search_item_name);
            this.tvProfession = (TextView) view.findViewById(R.id.text_view_search_item_profession);
            this.tvDistance = (TextView) view.findViewById(R.id.text_view_search_item_distance);
            this.ivProfile = (ImageView) view.findViewById(R.id.image_view_search_item_thumb);
            this.ivOnline = (ImageView) view.findViewById(R.id.image_view_search_item_online);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                        listener.onItemClick(v, getAdapterPosition());
                    }
                }
            });
        }

        public TextView getTvName() {
            return tvName;
        }

        public void setTvName(TextView tvName) {
            this.tvName = tvName;
        }

        public TextView getTvProfession() {
            return tvProfession;
        }

        public void setTvProfession(TextView tvProfession) {
            this.tvProfession = tvProfession;
        }

        public TextView getTvDistance() {
            return tvDistance;
        }

        public void setTvDistance(TextView tvDistance) {
            this.tvDistance = tvDistance;
        }

        public ImageView getIvProfile() {
            return ivProfile;
        }

        public void setIvProfile(ImageView ivProfile) {
            this.ivProfile = ivProfile;
        }

        public ImageView getIvOnline() {
            return ivOnline;
        }

        public void setIvOnline(ImageView ivOnline) {
            this.ivOnline = ivOnline;
        }
    }
}
