package edu.therealbranik.therealflower.homescreen.home;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.post.FullscreenPostActivity;
import edu.therealbranik.therealflower.post.ShowPostActivity;
import edu.therealbranik.therealflower.user.UserProfileActivity;


public class CardsAdapter extends ArrayAdapter<CardModel> {
    CardModel model;

    public CardsAdapter(Context context) {
        super(context, R.layout.fragment_home);
    }

    FirebaseStorage mStorage=FirebaseStorage.getInstance();

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.fragment_home, parent, false);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        model = getItem(position);

        holder.name.setText(model.getNameId());
        holder.timestamp.setText(model.getTimestampID());
        holder.description.setText(model.getDescriptionId());

        StorageReference avatarRef = mStorage.getReference("images/avatars/" +  model.getUserID() + ".jpg");
        avatarRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri.toString()).into(holder.profile_img);
                    }
                });
        StorageReference postRef = mStorage.getReference("images/posts/" +  model.getPostImage1ID() + ".jpg");
        postRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri.toString()).into(holder.post_img1);
                    }
                });
        holder.profile_img.setOnClickListener(onClickListenerAvatar);
        holder.post_img1.setOnClickListener(onClickListenerPost);
        return convertView;

    }

    static class ViewHolder {
        ImageView profile_img,post_img1;
        TextView name,description,timestamp;

        ViewHolder(View view) {
            profile_img = (ImageView) view.findViewById(R.id.profile_avatar);
            name = (TextView) view.findViewById(R.id.profile_name);
            timestamp = (TextView) view.findViewById(R.id.post_time);
            description = (TextView) view.findViewById(R.id.post_description);
            post_img1 = (ImageView) view.findViewById(R.id.post_img);
        }
    }

    View.OnClickListener onClickListenerAvatar=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i=new Intent(getContext(), UserProfileActivity.class);
            i.putExtra("id", model.getUserID());
            getContext().startActivity(i);
        }
    };
    View.OnClickListener onClickListenerPost=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i =new Intent(getContext(), ShowPostActivity.class);
            i.putExtra("id",model.getPostImage1ID());
            getContext().startActivity(i);
        }
    } ;
}