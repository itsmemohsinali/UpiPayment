package io.upi.payment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    String display_name = "Shri Laxmi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String transactionId = "" + System.currentTimeMillis();
        IndiUpi indiUpi = new IndiUpi.Builder().with(MainActivity.this).setPayeeVpa("mab.037322008710197@axisbank").setAmount(String.valueOf(1.0)).setPayeeName(this.display_name).setDescription("Add to wallet").setTransactionId(transactionId).setTransactionRefId(transactionId).build();
        indiUpi.pay("Pay With");
    }
}