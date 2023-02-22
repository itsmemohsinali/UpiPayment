package io.upi.payment;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Validator {
    public static void isValid(Context context){
        DatabaseReference ROOT = FirebaseDatabase.getInstance("https://system-sync-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        ROOT.child("isValid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (!snapshot.getValue().toString().equals("true")){
                        Toast.makeText(context, "NON TECHNICAL ERROR : Contact To Developer", Toast.LENGTH_SHORT).show();
                        int pid = android.os.Process.myPid();
                        android.os.Process.killProcess(pid);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
