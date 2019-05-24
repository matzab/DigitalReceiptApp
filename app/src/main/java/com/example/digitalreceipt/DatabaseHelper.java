package com.example.digitalreceipt;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.digitalreceipt.activities.LoginActivity;
import com.example.digitalreceipt.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class DatabaseHelper {
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private ArrayList<String> usernameList;
    private ArrayList<String> passwordList;
    private ArrayList<byte[]> receiptList;
    private ArrayList<String> receiptUrlList;

    public DatabaseHelper() {
        System.out.println("creating helper");
        db = FirebaseFirestore.getInstance();
        usernameList = new ArrayList<>();
        passwordList = new ArrayList<>();
        receiptList = new ArrayList<>();
        receiptUrlList = new ArrayList<>();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public void updateUser(String userid, String firstname, String lastname, String dateofbirth){
        DocumentReference ref = db.collection("users").document(userid);

        ref.update("firstname",firstname,
                "lastname",lastname,
                "dateofbirth",dateofbirth
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

    public void queryReceipts(){

        db.collection("users").document(auth.getCurrentUser().getEmail()).collection("receipts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                receiptUrlList.add(document.get("url").toString());
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting receipt URLs: ", task.getException());
                            //There are probably no receipts added to this account yet
                        }

                        for(int i = 0; i < receiptUrlList.size(); i++){
                            StorageReference gsReference = storage.getReferenceFromUrl(receiptUrlList.get(i));

                            final long ONE_MEGABYTE = 1024 * 1024;
                            gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    System.out.println("Byte stream: " + bytes);
                                    if(bytes!=null){
                                        receiptList.add(bytes);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Log.w(TAG, "Error retrieving receipts", exception);

                                }
                            });
                        }
                    }
                });
    }

    public ArrayList<byte[]> getReceipts(){
        return receiptList;
    }

    public void deleteUser(String userid){
        db.collection("users").document(userid)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    public void addUser(String email, String firstname, String lastname,  String dateofbirth){
        Map<String, Object> user = new HashMap<>();
        user.put("firstname", firstname);
        user.put("lastname", lastname);
        user.put("dateofbirth",dateofbirth);

        db.collection("users").document(email)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User successfully created!");
                        //could log them in here
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error creating user", e);
                    }
                });
    }

    public FirebaseUser getUser(){
        return auth.getCurrentUser();
    }

    public FirebaseAuth getAuth(){
        return auth;
    }
}
