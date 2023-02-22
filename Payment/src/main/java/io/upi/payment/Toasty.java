package io.upi.payment;

import android.content.Context;
import android.widget.Toast;

public class Toasty {

    public static void Toaster(Context context, String Msg){
        Toast.makeText(context, Msg, Toast.LENGTH_SHORT).show();
    }
}
