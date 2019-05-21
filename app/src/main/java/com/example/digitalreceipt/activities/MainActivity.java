package com.example.digitalreceipt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.digitalreceipt.R;
import com.example.digitalreceipt.fragments.QRCodeDisplay;
import com.example.digitalreceipt.fragments.ReceiptFragment;
import com.example.digitalreceipt.model.ReceiptPDF;

import java.util.ArrayList;

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
                    receipts = new ArrayList<>();
                    ArrayList<byte[]> receiptList = LoginActivity.databaseHelper.getReceipts();
                    for (int i = 0; i < receiptList.size(); i++) {
                        receipts.add(new ReceiptPDF(receiptList.get(i), "myReceipt(" + i + ")"));
                    }
                    getSupportActionBar().setTitle("Home");
                    fragment = new ReceiptFragment();
                    changeFragment(fragment);

                    return true;
                case R.id.navigation_dashboard:
                    getSupportActionBar().setTitle("Code display");
                    fragment = new QRCodeDisplay();
                    changeFragment(fragment);
                    return true;
                case R.id.navigation_notifications:
                    getSupportActionBar().setTitle("Notifications");
                    System.out.println("Notificationf ragment ");
                    changeFragment(fragment);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receipts = new ArrayList<>();
        ArrayList<byte[]> receiptList = LoginActivity.databaseHelper.getReceipts();
        for (int i = 0; i < receiptList.size(); i++) {
            receipts.add(new ReceiptPDF(receiptList.get(i), "myReceipt(" + i + ")"));
        }

        Intent intent = getIntent();
        userName = intent.getStringExtra("user_id");
        FragmentManager fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        fragment = new QRCodeDisplay();
        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);

        transaction.commit();


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_dashboard);
    }

    private void changeFragment(Fragment fragment) {
        // Create new fragment and transaction
        transaction = getSupportFragmentManager().beginTransaction();
// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);

// Commit the transaction
        transaction.commit();
    }

    @Override
    public void onListFragmentInteraction(ReceiptPDF item) {
        Intent login = new Intent(MainActivity.this, PdfDetailActivity.class);
        login.putExtra("pdf", item);
        startActivity(login);

    }

    public String getUserName() {
        return userName;
    }

    public ArrayList<ReceiptPDF> getReceipts() {
        return receipts;
    }
}