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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DatabaseHelper {
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private ArrayList<String> usernameList;
    private ArrayList<String> passwordList;
    private ArrayList<byte[]> receiptList;


    public DatabaseHelper() {
        System.out.println("creating helper");
        db = FirebaseFirestore.getInstance();
        usernameList = new ArrayList<>();
        passwordList = new ArrayList<>();
        receiptList = new ArrayList<>();
        getAllUsers();
        storage = FirebaseStorage.getInstance();
    }

    public boolean login(String username, String password){
        for(int i = 0; i<usernameList.size(); i++){
            if(usernameList.get(i).equals(username) && passwordList.get(i).equals(password)){
                queryReceipts(username);
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

    public void queryReceipts(String userid){
        
            StorageReference storageRef = storage.getReference();
            StorageReference islandRef = storageRef.child(userid + "/test.txt");

            final long ONE_MEGABYTE = 1024 * 1024;
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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
}