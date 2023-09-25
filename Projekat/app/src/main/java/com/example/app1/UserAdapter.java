package com.example.app1;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        // Postavljanje pozicije (broja ili bedža)
        if (position < 3) {
            // Prva tri korisnika dobijaju bedževe
            holder.positionTextView.setVisibility(View.INVISIBLE); // Sakrij broj
            holder.badgeImageView.setVisibility(View.VISIBLE); // Prikazi bedž
            // Postavite ikonu za bedž prema poziciji (prvo, drugo, treće mesto)
            if (position == 0) {
                holder.badgeImageView.setImageResource(R.drawable.first_place);
            } else if (position == 1) {
                holder.badgeImageView.setImageResource(R.drawable.second_place);
            } else {
                holder.badgeImageView.setImageResource(R.drawable.third_place);
            }
        } else {
            // Ostali korisnici dobijaju brojeve
            holder.positionTextView.setVisibility(View.VISIBLE); // Prikazi broj
            holder.badgeImageView.setVisibility(View.INVISIBLE); // Sakrij bedž
            holder.positionTextView.setText((position + 1) + "."); // Postavi redni broj
        }

        holder.usernameTextView.setText(user.getFullName());
        holder.pointsTextView.setText(String.valueOf(user.getPoints()));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView pointsTextView;
        TextView positionTextView;
        ImageView badgeImageView;

        UserViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username_textView);
            pointsTextView = itemView.findViewById(R.id.points_textView);
            positionTextView = itemView.findViewById(R.id.position_textView);
            badgeImageView = itemView.findViewById(R.id.badge_imageView);
        }
    }
}
