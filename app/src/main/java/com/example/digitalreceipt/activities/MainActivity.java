package com.example.digitalreceipt.activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.digitalreceipt.R;
import com.example.digitalreceipt.fragments.QRCodeDisplay;
import com.example.digitalreceipt.fragments.ReceiptFragment;
import com.example.digitalreceipt.model.ReceiptPDF;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class MainActivity extends AppCompatActivity implements ReceiptFragment.OnListFragmentInteractionListener {
    private String userName;
    private ArrayList<ReceiptPDF> receipts;
    private Fragment fragment;
    private FragmentTransaction transaction;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //  mTextMessage.setText(R.string.title_home);
/*                    receipts = new ArrayList<>();
                    ArrayList<byte[]> receiptList = LoginActivity.databaseHelper.getReceipts();
                    for (int i = 0; i < receiptList.size(); i++) {
                        receipts.add(new ReceiptPDF(receiptList.get(i), "myReceipt(" + i + ")"));
                    }*/
                    getSupportActionBar().setTitle("Home");
                    fragment = new ReceiptFragment();
                    changeFragment(fragment);

                    return true;
                case R.id.navigation_dashboard:
                    getSupportActionBar().setTitle("My QR Code");
                    fragment = new QRCodeDisplay();
                    changeFragment(fragment);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        db.collection("users").document(auth.getCurrentUser().getEmail()).collection("receipts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        System.out.println("Event happened!");
                        receipts = new ArrayList<>();
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        final ArrayList<String> receiptUrlList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : value) {
                            receiptUrlList.add(document.get("url").toString());
                            Log.d(ContentValues.TAG, document.getId() + " => " + document.getData());
                        }

                        for (int i = 0; i < receiptUrlList.size(); i++) {
                            final StorageReference gsReference = storage.getReferenceFromUrl(receiptUrlList.get(i));

                            final long ONE_MEGABYTE = 1024 * 1024;
                            gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    if (bytes != null) {
                                        receipts.add(new ReceiptPDF(bytes, gsReference.getName()));
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Log.w(ContentValues.TAG, "Error retrieving receipts", exception);

                                }
                            });
                        }

                        Toast.makeText(MainActivity.this, "Receipt received!", Toast.LENGTH_SHORT).show();

                    }
                });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = LoginActivity.databaseHelper.getUser();

        userName = user.getEmail();

        FragmentManager fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        fragment = new QRCodeDisplay();
        transaction.add(R.id.fragment_container, fragment);
       // transaction.addToBackStack(null);

        transaction.commit();


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_dashboard);
    }

    private void changeFragment(Fragment fragment) {
        transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, fragment);
       // transaction.addToBackStack(null);

        transaction.commit();
    }

    @Override
    public void onListFragmentInteraction(ReceiptPDF item) {
        Intent login = new Intent(MainActivity.this, PdfDetailActivity.class);
        login.putExtra("pdf", item);
        startActivity(login);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.log_out:
                displayAlertDialog();
                break;
            case R.id.update_inf:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Log out");
        builder.setMessage("Do you wish to log out");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                            Intent login = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(login);
                            finish();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public String getUserName() {
        return userName;
    }

    public ArrayList<ReceiptPDF> getReceipts() {
        return receipts;
    }
}