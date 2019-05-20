package com.example.digitalreceipt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.digitalreceipt.R;
import com.example.digitalreceipt.fragments.QRCodeDisplay;
import com.example.digitalreceipt.fragments.ReceiptFragment;

//DB Test import
import java.util.ArrayList;
//End of DB Test import

public class MainActivity extends AppCompatActivity {
    private String userName;
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                  //  mTextMessage.setText(R.string.title_home);
                    changeFragment(new ReceiptFragment());
                    return true;
                case R.id.navigation_dashboard:
                 //   mTextMessage.setText(R.string.title_dashboard);
                    changeFragment(new QRCodeDisplay());

                    //DB Testing
                    ArrayList<byte[]> receiptList = LoginActivity.databaseHelper.getReceipts();
                    System.out.println("Receipt arraylist: " + receiptList);
                    for (int i = 0; i < receiptList.size(); i++){
                        System.out.println("Array " + i + ": " + new String(receiptList.get(i)));
                    }
                    //End of DB Testing

                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        userName = intent.getStringExtra("user_id");
        mTextMessage = (TextView) findViewById(R.id.message);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void changeFragment(Fragment fragment){
        // Create new fragment and transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);

// Commit the transaction
        transaction.commit();
    }

    public String getUserName() {
        return userName;
    }
}
