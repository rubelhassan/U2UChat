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
import com.example.rubel.u2uchat.model.Message;

import java.util.List;

/**
 * Created by rubel on 4/18/2017.
 */

public class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int TEXT_LEFT = 101, TEXT_RIGHT = 102;
    private static final int IMAGE_LEFT = 201, IMAGE_RIGHT = 202;
    private List<Message> mMessagesList;
    private Context mContext;
    private String mSenderPhotoUrl;
    private String mReceiverPhotoUrl;

    public MessageListAdapter(List<Message> mMessagesList, Context mContext, String mSenderPhotoUrl, String mReceiverPhotoUrl) {
        this.mMessagesList = mMessagesList;
        this.mContext = mContext;
        this.mSenderPhotoUrl = mSenderPhotoUrl;
        this.mReceiverPhotoUrl = mReceiverPhotoUrl;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case TEXT_LEFT: {
                View viewLeft = LayoutInflater.from(mContext).inflate(R.layout.message_item_left,
                        parent, false);
                viewHolder = new TextViewHolder(viewLeft);
                break;
            }
            case TEXT_RIGHT: {
                View viewRight = LayoutInflater.from(mContext).inflate(R.layout.message_item_right,
                        parent, false);
                viewHolder = new TextViewHolder(viewRight);
                break;
            }
            case IMAGE_LEFT: {
                View viewLeftImage = LayoutInflater.from(mContext).inflate(R.layout.message_item_left_img,
                        parent, false);
                viewHolder = new TextViewHolder(viewLeftImage);
                break;
            }
            case IMAGE_RIGHT: {
                View viewRightImage = LayoutInflater.from(mContext).inflate(R.layout.message_item_right_img,
                        parent, false);
                viewHolder = new TextViewHolder(viewRightImage);
                break;
            }
            default: {
                View viewDefault = LayoutInflater.from(mContext).inflate(R.layout.message_item_left,
                        parent, false);
                viewHolder = new TextViewHolder(viewDefault);
                break;
            }
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TEXT_RIGHT: {
                TextViewHolder rightTextViewHolder = (TextViewHolder) holder;
                configureRightTextViewHolder(rightTextViewHolder, position);
                break;
            }
            case IMAGE_LEFT: {
                ImageViewHolder leftImgViewHolder = (ImageViewHolder) holder;
                configureLeftImgViewHolder(leftImgViewHolder, position);
                break;
            }
            case IMAGE_RIGHT: {
                ImageViewHolder leftImgViewHolder = (ImageViewHolder) holder;
                configureRightImgViewHolder(leftImgViewHolder, position);
                break;
            }
            case TEXT_LEFT:
            default: {
                TextViewHolder leftTextViewHolder = (TextViewHolder) holder;
                configureLeftTextViewHolder(leftTextViewHolder, position);
                break;
            }
        }
    }


    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }

    @Override
    public int getItemViewType(int position) {

        Message msg = mMessagesList.get(position);

        // message is image/emoji
        if (!msg.isText()) {
            if (msg.isLeft())
                return IMAGE_LEFT;
            return IMAGE_RIGHT;
        }

        if (msg.isLeft())
            return TEXT_LEFT;

        return TEXT_RIGHT;
    }

    private void configureLeftTextViewHolder(TextViewHolder textViewHolder, int position) {
        Message msg = mMessagesList.get(position);
        textViewHolder.getTvMessage().setText(msg.getContent());
        Glide.with(mContext)
                .load(mReceiverPhotoUrl)
                .into(textViewHolder.getIvProfileIcon());
    }

    private void configureRightTextViewHolder(TextViewHolder textViewHolder, int position) {
        Message msg = mMessagesList.get(position);
        textViewHolder.getTvMessage().setText(msg.getContent());
        Glide.with(mContext)
                .load(mSenderPhotoUrl)
                .into(textViewHolder.getIvProfileIcon());
    }

    private void configureLeftImgViewHolder(ImageViewHolder imageViewHolder, int position) {
        Message msg = mMessagesList.get(position);
        Glide.with(mContext)
                .load(mReceiverPhotoUrl)
                .into(imageViewHolder.getIvProfileIcon());

        Glide.with(mContext)
                .load(msg.getContent())
                .into(imageViewHolder.getIvImage());
    }

    private void configureRightImgViewHolder(ImageViewHolder imageViewHolder, int position) {
        Message msg = mMessagesList.get(position);
        Glide.with(mContext)
                .load(mSenderPhotoUrl)
                .into(imageViewHolder.getIvProfileIcon());

        Glide.with(mContext)
                .load(msg.getContent())
                .into(imageViewHolder.getIvImage());
    }


    public class TextViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMessage;
        private ImageView ivProfileIcon;

        public TextViewHolder(View itemView) {
            super(itemView);
            tvMessage = (TextView) itemView.findViewById(R.id.text_view_message_content);
            ivProfileIcon = (ImageView) itemView.findViewById(R.id.image_view_message_sender);
        }

        public TextView getTvMessage() {
            return tvMessage;
        }

        public void setTvMessage(TextView tvMessage) {
            this.tvMessage = tvMessage;
        }

        public ImageView getIvProfileIcon() {
            return ivProfileIcon;
        }

        public void setIvProfileIcon(ImageView ivProfileIcon) {
            this.ivProfileIcon = ivProfileIcon;
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage;
        private ImageView ivProfileIcon;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.image_view_message_sender_img);
            ivProfileIcon = (ImageView) itemView.findViewById(R.id.image_view_message_sender_img);
        }

        public ImageView getIvImage() {
            return ivImage;
        }

        public void setIvImage(ImageView ivImage) {
            this.ivImage = ivImage;
        }

        public ImageView getIvProfileIcon() {
            return ivProfileIcon;
        }

        public void setIvProfileIcon(ImageView ivProfileIcon) {
            this.ivProfileIcon = ivProfileIcon;
        }
    }
}
