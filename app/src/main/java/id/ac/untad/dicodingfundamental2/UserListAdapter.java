package id.ac.untad.dicodingfundamental2;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ListViewHolder> {
    private ArrayList<User> users;

    public UserListAdapter(ArrayList<User> list) {
        this.users = list;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListViewHolder holder, final int position) {
        final User user = users.get(position);
        holder.nama.setText(user.getLogin());
        holder.type.setText(user.getOrganizations_url());
        Glide.with(holder.itemView.getContext())
                .load(user.getAvatar_url())
                .apply(new RequestOptions().override(460, 460))
                .into(holder.imgUser);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                intent.putExtra(DetailActivity.EXTRA_USER, user.getLogin());
                intent.putExtra(FollowersFragment.EXTRA_FOLLOWER, user.getLogin());
                intent.putExtra(FollowingFragment.EXTRA_FOLLOWING, user.getLogin());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView nama, type;
        ImageView imgUser;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            nama = itemView.findViewById(R.id.username_list);
            type = itemView.findViewById(R.id.company);
        }
    }

}
