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
import com.example.rubel.u2uchat.model.UserConnection;

import java.util.List;

/**
 * Created by rubel on 4/14/2017.
 */

public class ChatsListAdapter extends RecyclerView.Adapter<ChatsListAdapter.ChatsViewHolder> {

    List<UserConnection> mUserConnections = null;
    Context mContext;

    private ChatOnItemClickListener listener;

    public ChatsListAdapter(List<UserConnection> mUserConnections, Context mContext) {
        this.mUserConnections = mUserConnections;
        this.mContext = mContext;
    }

    // method to set ChatItemClickListener to this adapter from activity/fragment using it
    public void setChatOnItemClickListener(ChatOnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ChatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View currentView = LayoutInflater.from(mContext).inflate(R.layout.chat_list_item, parent, false);
        return new ChatsViewHolder(currentView);
    }

    @Override
    public void onBindViewHolder(ChatsViewHolder holder, int position) {
        UserConnection connection = mUserConnections.get(position);

        Glide.with(mContext)
                .load(connection.getPhotoUrl())
                .into(holder.getIvProfile());

        if (connection.isOnline())
            holder.getIvOnline().setVisibility(View.VISIBLE);
        else holder.getIvOnline().setVisibility(View.GONE);

        holder.getTvSenderName().setText(connection.getName());
        holder.getTvLastMessage().setText(connection.getLastMessage());
        holder.getTvTimestamps().setText(connection.getTimestamps());
    }

    @Override
    public int getItemCount() {
        return mUserConnections.size();
    }

    // chat item click listener interface
    public interface ChatOnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public class ChatsViewHolder extends RecyclerView.ViewHolder {
        TextView tvSenderName;
        TextView tvLastMessage;
        TextView tvTimestamps;
        ImageView ivProfile;
        ImageView ivOnline;

        public ChatsViewHolder(final View view) {
            super(view);
            this.tvSenderName = (TextView) view.findViewById(R.id.text_view_chat_item_name);
            this.tvLastMessage = (TextView) view.findViewById(R.id.text_view_chat_item_message);
            this.tvTimestamps = (TextView) view.findViewById(R.id.text_view_chat_item_time);
            this.ivProfile = (ImageView) view.findViewById(R.id.image_view_chat_item_thumb);
            this.ivOnline = (ImageView) view.findViewById(R.id.image_view_chat_item_online);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                        listener.onItemClick(view, getAdapterPosition());
                    }
                }
            });
        }

        public TextView getTvSenderName() {
            return tvSenderName;
        }

        public void setTvSenderName(TextView tvSenderName) {
            this.tvSenderName = tvSenderName;
        }

        public TextView getTvLastMessage() {
            return tvLastMessage;
        }

        public void setTvLastMessage(TextView tvLastMesage) {
            this.tvLastMessage = tvLastMesage;
        }

        public TextView getTvTimestamps() {
            return tvTimestamps;
        }

        public void setTvTimestamps(TextView tvTimestamps) {
            this.tvTimestamps = tvTimestamps;
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
