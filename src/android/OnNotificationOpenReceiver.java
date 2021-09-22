package org.apache.cordova.firebase;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

public class OnNotificationOpenReceiver extends BroadcastReceiver {

  private static final String TAG = "FirebasePlugin";

  @Override
  public void onReceive(Context context, Intent intent) {
    //Judging whether the app process survives
    if(SystemUtils.isAppAlive(context, "com.google.android.apps.maps")){
        //If you survive, start DetailActivity directly, but consider the case that the app process is still there
        //But the Task stack is empty, such as when the user clicks the Back key to exit the application, but the process has not been recycled by the system, if started directly.
        //Detail Activity, press the Back key again and you won't return to MainActivity. So it's starting.
        //Before Detail Activity, start MainActivity.
        Log.i("NotificationReceiver", "the app process is alive");
        Intent mainIntent = new Intent(context, MainActivity.class);
        //Set the launch mode of mainativity to singletask, or add intent. flag stack clear_top to the following flag. If there are instances of mainativity in the task stack, it will be moved to the top, and the activities above it will be cleaned up. If there are no instances of mainativity in the task stack, it will be created at the top.
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent detailIntent = new Intent(context, DetailActivity.class);
        detailIntent.putExtra("name", "Rice cooker");
        detailIntent.putExtra("price", "58  dollars");
        detailIntent.putExtra("detail", "This is a good pot. This is where the app process exists. Start Activity directly.");

        Intent[] intents = {mainIntent, detailIntent};

        context.startActivities(intents);
    }else {
        //If the app process has been killed, restart app first, pass the start parameters of DetailActivity into Intent, and the parameters are passed into MainActivity through SplashActivity. At this time, the initialization of APP has been completed, and in MainActivity, you can jump to DetailActivity according to the input parameters.
        Log.i("NotificationReceiver", "the app process is dead");
        Intent launchIntent = context.getPackageManager().
                getLaunchIntentForPackage("com.google.android.apps.maps");
        launchIntent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        Bundle args = new Bundle();
        args.putString("name", "Rice cooker");
        args.putString("price", "58 dollars");
        args.putString("detail", "This is a good pot. This is where the app process exists. Start Activity directly.");
        launchIntent.putExtra(Constants.EXTRA_BUNDLE, args);
        context.startActivity(launchIntent);
    }
}
}
