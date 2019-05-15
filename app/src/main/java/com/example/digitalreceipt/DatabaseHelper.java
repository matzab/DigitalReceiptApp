package com.example.digitalreceipt;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DatabaseHelper {
    private FirebaseFirestore db;
    private ArrayList<String> usernameList;
    private ArrayList<String> passwordList;



    public DatabaseHelper() {
        System.out.println("creating helper");
        db = FirebaseFirestore.getInstance();
        usernameList = new ArrayList<>();
        passwordList = new ArrayList<>();
        getAllUsers();
    }

    public boolean login(String username, String password){
        for(int i = 0; i<usernameList.size(); i++){
            if(usernameList.get(i).equals(username) && passwordList.get(i).equals(password)){
                return true;
            }
        }
        return false;
    }

    public void getAllUsers(){
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                usernameList.add(document.getId());
                                passwordList.add(document.get("password").toString());

                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    public void updateUser(String userid, String firstname, String lastname, String password, String dateofbirth){
        DocumentReference ref = db.collection("users").document(userid);

        ref.update("firstname",firstname,
                "lastname",lastname,
                "password",password,"dateofbirth",dateofbirth
        ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully updated!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });




    }

    public ArrayList<Blob> getReceipts(){


        return null;
    }
}
