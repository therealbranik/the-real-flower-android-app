package edu.therealbranik.therealflower.homescreen.social.friends.friend;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.therealbranik.therealflower.user.User;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class FriendContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<User> ITEMS = new ArrayList<User>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, User> ITEM_MAP = new HashMap<String, User>();

    private static final int COUNT = 5;

//    static {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        db.collection("users")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                addItem(document.toObject(User.class).withId(document.getId()), document.getId());
//                                // drawUserMarker(document.toObject(Position.class));
//                            }
//                        }
//                    }
//                });
//
//        // Add some sample items.
////        for (int i = 1; i <= COUNT; i++) {
////            addItem(createDummyItem(i));
////        }
//    }

    private static void addItem(User item, String id) {
        ITEMS.add(item);
        ITEM_MAP.put(id, item);
    }

//    private static FriendItem createDummyItem(int position) {
//        return new FriendItem(String.valueOf(position), "Item " + position, makeDetails(position));
//    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
//    public static class FriendItem {
//        public final String id;
//        public final String content;
//        public final String details;
//
//        public FriendItem(String id, String content, String details) {
//            this.id = id;
//            this.content = content;
//            this.details = details;
//        }
//
//        @Override
//        public String toString() {
//            return content;
//        }
//    }

    public static void GetData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ITEMS.clear();
        ITEM_MAP.clear();

        db.collection("friends")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                addItem(document.toObject(User.class).withId(document.getId()), document.getId());
                                // drawUserMarker(document.toObject(Position.class));
                            }
                        }
                    }
                });
    }
}
