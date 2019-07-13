package edu.therealbranik.therealflower.homescreen.home;


import android.content.Context;
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

import edu.therealbranik.therealflower.R;


public class CardsAdapter extends ArrayAdapter<CardModel> {
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

        CardModel model = getItem(position);

        holder.name.setText(model.getNameId());
        holder.timestamp.setText(model.getTimestampID());
        holder.description.setText(model.getDescriptionId());
        holder.post_img1.setImageResource(model.getPostImage1ID());

        StorageReference avatarRef = mStorage.getReference("images/avatars/" +  model.getUserID() + ".jpg");
        avatarRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri.toString()).into(holder.profile_img);
                    }
                });

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
}