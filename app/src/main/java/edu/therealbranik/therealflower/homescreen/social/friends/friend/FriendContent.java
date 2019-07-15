package edu.therealbranik.therealflower.homescreen.social.friends.friend;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.therealbranik.therealflower.post.ShowPostActivity;
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
    public static final List<String> ITEMS = new ArrayList<String>();
    private static Map<String, Object> friends=new Map<String, Object>() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean containsKey(@Nullable Object key) {
            return false;
        }

        @Override
        public boolean containsValue(@Nullable Object value) {
            return false;
        }

        @Nullable
        @Override
        public Object get(@Nullable Object key) {
            return null;
        }

        @Nullable
        @Override
        public Object put(@NonNull String key, @NonNull Object value) {
            return null;
        }

        @Nullable
        @Override
        public Object remove(@Nullable Object key) {
            return null;
        }

        @Override
        public void putAll(@NonNull Map<? extends String, ?> m) {

        }

        @Override
        public void clear() {

        }

        @NonNull
        @Override
        public Set<String> keySet() {
            return null;
        }

        @NonNull
        @Override
        public Collection<Object> values() {
            return null;
        }

        @NonNull
        @Override
        public Set<Entry<String, Object>> entrySet() {
            return null;
        }


    };
    /**
     * A map of sample (dummy) items, by ID.
     */
    //public static final Map<String, User> ITEM_MAP = new HashMap<String, User>();

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

    private static void addItem(String item, String id) {
        ITEMS.add(item);

        //ITEM_MAP.put(id, item);
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
        friends.clear();
//        ITEM_MAP.clear();

        db.collection("friends").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            ITEMS.clear();
                            friends.clear();
                            if (task.getResult().getData() == null)
                                return;
                            friends = (Map<String, Object>) task.getResult().getData();

                            for (Map.Entry<String,Object> entry:friends.entrySet()
                                 ) {
                                ITEMS.add(entry.getKey());

                            }

                        }

                    }
                });
    }


    public void onChangeFriends () {

    }
}
