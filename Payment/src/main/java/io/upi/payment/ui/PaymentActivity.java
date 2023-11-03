package io.upi.payment.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.upi.payment.R;
import io.upi.payment.Singleton;
import io.upi.payment.entity.PaymentPayload;
import io.upi.payment.entity.TransactionResponse;

public final class PaymentActivity extends AppCompatActivity {
    public static final int PAYMENT_REQUEST = 1001;
    private static final String TAG = "PaymentActivity";
    private Singleton singleton;
    private String title;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        this.singleton = Singleton.getInstance();
        Intent intent = getIntent();
        PaymentPayload payment = (PaymentPayload) intent.getParcelableExtra("payment");
        if (!TextUtils.isEmpty(intent.getStringExtra("title"))) {
            this.title = intent.getStringExtra("title");
        } else {
            this.title = getString(R.string.default_text_pay_using);
        }
        Uri.Builder payUri = new Uri.Builder();
        payUri.scheme("upi").authority("pay");
        payUri.appendQueryParameter("pa", payment.getVpa());
        payUri.appendQueryParameter("pn", payment.getName());
        payUri.appendQueryParameter("tid", payment.getTxnId());
        if (payment.getPayeeMerchantCode() != null) {
            payUri.appendQueryParameter("mc", payment.getPayeeMerchantCode());
        }
        payUri.appendQueryParameter("tr", payment.getTxnRefId());
        payUri.appendQueryParameter("tn", payment.getDescription());
        payUri.appendQueryParameter("am", payment.getAmount());
        payUri.appendQueryParameter("cu", payment.getCurrency());
        try {
            if (payment.getUrl() != null) {
                Uri.Builder callbackUrl = payment.getUrl().buildUpon();
                callbackUrl.appendQueryParameter("pa", payment.getVpa());
                callbackUrl.appendQueryParameter("pn", payment.getName());
                callbackUrl.appendQueryParameter("tid", payment.getTxnId());
                if (payment.getPayeeMerchantCode() != null) {
                    callbackUrl.appendQueryParameter("mc", payment.getPayeeMerchantCode());
                }
                callbackUrl.appendQueryParameter("tr", payment.getTxnRefId());
                callbackUrl.appendQueryParameter("tn", payment.getDescription());
                callbackUrl.appendQueryParameter("am", payment.getAmount());
                callbackUrl.appendQueryParameter("cu", payment.getCurrency());
                payUri.appendQueryParameter("url", callbackUrl.build().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Uri uri = payUri.build();
        Intent paymentIntent = new Intent("android.intent.action.VIEW");
        paymentIntent.setData(uri);

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("SHRILAXMI_GAMES", Context.MODE_PRIVATE);
        String PAYMENT_APP = preferences.getString("PAYMENT_APP", "");

        if(PAYMENT_APP.equals("PHONEPE")){
            paymentIntent.setPackage("com.phonepe.app");
            if (paymentIntent.resolveActivity(getPackageManager()) != null) {
                ((Activity) PaymentActivity.this).startActivityForResult(paymentIntent, PaymentActivity.PAYMENT_REQUEST);
                return;
            }
        }else if(PAYMENT_APP.equals("PAYTM")){
            paymentIntent.setPackage("net.one97.paytm");
            if (paymentIntent.resolveActivity(getPackageManager()) != null) {
                ((Activity) PaymentActivity.this).startActivityForResult(paymentIntent, PaymentActivity.PAYMENT_REQUEST);
                return;
            }
        }else if(PAYMENT_APP.equals("GPAY")){
            paymentIntent.setPackage("com.google.android.apps.nbu.paisa.user");
            if (paymentIntent.resolveActivity(getPackageManager()) != null) {
                ((Activity) PaymentActivity.this).startActivityForResult(paymentIntent, PaymentActivity.PAYMENT_REQUEST);
                return;
            }
        }else{
            if (paymentIntent.resolveActivity(getPackageManager()) != null) {
                List<ResolveInfo> intentList = getPackageManager().queryIntentActivities(paymentIntent, 0);
                removePhonepe(intentList);
                showApps(intentList, paymentIntent);
                return;
            }
        }
        Toast.makeText(this, "No UPI Supported app found in device! Please Install to Proceed!", Toast.LENGTH_LONG).show();
    }

    /* access modifiers changed from: package-private */
    public void removePhonepe(List<ResolveInfo> list) {
        Iterator<ResolveInfo> iterator = list.iterator();
        while (iterator.hasNext()) {
            if ("com.phonepe.app".equalsIgnoreCase(iterator.next().activityInfo.packageName)) {
                iterator.remove();
                return;
            }
        }
    }

    private void showApps(List<ResolveInfo> appsList, Intent intent) {
        new AppsBottomSheet(appsList, intent, new View.OnClickListener() {
            public void onClick(View v) {
                PaymentActivity.this.callbackTransactionCancelled();
                PaymentActivity.this.finish();
            }
        }).show(getSupportFragmentManager(), this.title);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYMENT_REQUEST) {
            if (data != null) {
                //Get Response from activity intent
                String response = data.getStringExtra("response");

                if (response == null) {
                    callbackTransactionCancelled();
                    Log.d(TAG, "Response is null");

                } else {

                    TransactionResponse transactionDetails = getTransactionDetails(response);

                    //Update Listener onTransactionCompleted()
                    callbackTransactionComplete(transactionDetails);

                    //Check if success, submitted or failed
                    try {
                        if (transactionDetails.getStatus().toLowerCase().equals("success")) {
                            callbackTransactionSuccess(transactionDetails);
                        } else if (transactionDetails.getStatus().toLowerCase().equals("submitted")) {
                            callbackTransactionSubmitted();
                        } else {
                            callbackTransactionFailed();
                        }
                    } catch (Exception e) {
                        callbackTransactionCancelled();
                        callbackTransactionFailed();
                    }
                }
            } else {
                Log.e(TAG, "Intent Data is null. User cancelled");
                callbackTransactionCancelled();
            }
            finish();
        }
    }

    private Map<String, String> getQueryString(String url) {
        String[] params = url.split("&");
        Map<String, String> map = new HashMap<>();
        for (String param : params) {
            map.put(param.split("=")[0], param.split("=")[1]);
        }
        return map;
    }

    private TransactionResponse getTransactionDetails(String response) {
        Map<String, String> map = getQueryString(response);
        return new TransactionResponse(map.get("txnId"), map.get("responseCode"), map.get("ApprovalRefNo"), map.get("Status"), map.get("txnRef"));
    }

    private boolean isListenerRegistered() {
        return Singleton.getInstance().isListenerRegistered();
    }

    private void callbackTransactionSuccess(TransactionResponse transactionDetails) {
        if (isListenerRegistered()) {
            this.singleton.getListener().onTransactionSuccess(transactionDetails);
        }
    }

    private void callbackTransactionSubmitted() {
        if (isListenerRegistered()) {
            this.singleton.getListener().onTransactionSubmitted();
        }
    }

    private void callbackTransactionFailed() {
        if (isListenerRegistered()) {
            this.singleton.getListener().onTransactionFailed();
        }
    }

    /* access modifiers changed from: private */
    public void callbackTransactionCancelled() {
        if (isListenerRegistered()) {
            this.singleton.getListener().onTransactionCancelled();
        }
    }

    private void callbackTransactionComplete(TransactionResponse transactionDetails) {
        if (isListenerRegistered()) {
            this.singleton.getListener().onTransactionCompleted(transactionDetails);
        }
    }
}
