package com.mehedi.pchtest.adapter;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mehedi.pchtest.R;
import com.mehedi.pchtest.db.User;

import java.util.ArrayList;
import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    //interface is created to pass the edit button pressed event to mainactivity
    public interface OnEditButtonClickListener {
        void onEditButtonClicked(User user);
    }

    private List<User> data;
    private Context context;
    private LayoutInflater layoutInflater;
    private OnEditButtonClickListener onEditButtonClickListener;

    public UserAdapter(Context context, OnEditButtonClickListener listener) {
        this.data = new ArrayList<>();
        this.context = context;
        this.onEditButtonClickListener = listener;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View itemView = layoutInflater.inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        // Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        final User currentUser = data.get(position);
        holder.textViewName.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
        holder.textViewWage.setText("Daily WAGE: " + currentUser.getDailyWage());
        holder.textViewNumberOfDays.setText("Required Days to Reach Goal: " + currentUser.getNumberOfDays());
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onEditButtonClickListener != null)
                    onEditButtonClickListener.onEditButtonClicked(currentUser);
            }
        });
        String url = currentUser.getPicture();
        //load the image using glide library
        Glide.with(context)
                .load(url)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return data.size();
    }

    public void setData(List<User> newData) {
        if (data != null) {
            UserDiffCallback userDiffCallback = new UserDiffCallback(data, newData);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(userDiffCallback);
            //latest data is saved for future comparison
            data.clear();
            data.addAll(newData);
            diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            data = newData;
        }
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView textViewName;
        public TextView textViewWage;
        public TextView textViewNumberOfDays;
        public ImageButton editButton;

        public UserViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.circleView);
            textViewName = itemView.findViewById(R.id.text_view_user_name);
            textViewWage = itemView.findViewById(R.id.text_view_user_daily_wage);
            textViewNumberOfDays = itemView.findViewById(R.id.text_view_user_number_of_days);
            editButton = itemView.findViewById(R.id.edit_button);

        }

    }

    class UserDiffCallback extends DiffUtil.Callback {

        private final List<User> oldUsers, newUsers;

        public UserDiffCallback(List<User> oldUsers, List<User> newUsers) {
            this.oldUsers = oldUsers;
            this.newUsers = newUsers;
        }

        @Override
        public int getOldListSize() {
            return oldUsers.size();
        }

        @Override
        public int getNewListSize() {
            return newUsers.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldUsers.get(oldItemPosition).getId() == newUsers.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldUsers.get(oldItemPosition).equals(newUsers.get(newItemPosition));
        }
    }
}
