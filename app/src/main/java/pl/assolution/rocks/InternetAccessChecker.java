package pl.assolution.rocks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Andrzej on 2016-09-08. (Ready)
 */
public class InternetAccessChecker extends BroadcastReceiver {

    public static InternetAccessListener internetAccesslistener;

    public InternetAccessChecker() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(internetAccesslistener != null) {
            internetAccesslistener.onNetworkConnectionChanged(isConnected);
        }
    }

    public static boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) RocksApplication.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    public interface InternetAccessListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }

    public static boolean checkInternetConnection(View layout, boolean connectionStatus) {
        boolean isInternetConnection = true;

        if(!connectionStatus) {
            String msg = "Brak połączenia z internetem";
            Snackbar snackbar = Snackbar.make(layout, msg ,Snackbar.LENGTH_INDEFINITE);
            View snackBarView = snackbar.getView();

            if(layout instanceof CoordinatorLayout) {
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackBarView.getLayoutParams();
                params.gravity = (Gravity.TOP);
                snackBarView.setLayoutParams(params);
            }
            if(layout instanceof FrameLayout) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();
                params.gravity = (Gravity.TOP);
                snackBarView.setLayoutParams(params);
            }
            if(layout instanceof LinearLayout) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();
                params.gravity = (Gravity.TOP);
                snackBarView.setLayoutParams(params);
            }
            TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.setActionTextColor(Color.WHITE);
            snackBarView.setBackgroundColor(Color.GRAY);

            snackbar.setAction("X", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }

            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            snackbar.show();
            isInternetConnection = false;
        }
        return isInternetConnection;
    }




}
