import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class DatabaseHelper {
    FirebaseFirestore db;


    public DatabaseHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public boolean login(String username, String password){
        //db.collection("users").get().



        return false;
    }
}
