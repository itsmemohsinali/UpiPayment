package io.upi.payment.ui.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.upi.payment.R;
import io.upi.payment.ui.PaymentActivity;
import io.upi.payment.util.Validator;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.AppViewHolder> {

    private Context mContext;
    private List<ResolveInfo> mList;
    private Intent mIntent;

    public AppsAdapter(Context mContext, List<ResolveInfo> mList, Intent mIntent) {
        this.mContext = mContext;
        this.mList = mList;
        this.mIntent = mIntent;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AppViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_app, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        final ResolveInfo info = mList.get(position);
        String name = String.valueOf(info.loadLabel(mContext.getPackageManager()));
        final Drawable icon = info.loadIcon(mContext.getPackageManager());
        holder.bind(name, icon);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = mIntent;
                intent.setPackage(info.activityInfo.packageName);
                ((Activity) mContext).startActivityForResult(intent, PaymentActivity.PAYMENT_REQUEST);
                SharedPreferences.Editor editor = mContext.getSharedPreferences("SHRILAXMI_GAMES", MODE_PRIVATE).edit();
                editor.putString("PACKAGE",name).apply();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;

        AppViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.appIcon);
            name = itemView.findViewById(R.id.appName);
        }

        void bind(String app_name, Drawable app_icon) {
            name.setText(app_name);
            icon.setImageDrawable(app_icon);
        }
    }
}
