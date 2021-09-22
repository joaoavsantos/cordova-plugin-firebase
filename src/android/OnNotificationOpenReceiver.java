package org.apache.cordova.firebase;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.app.ActivityManager;
import android.net.Uri;

import java.util.List;

public class OnNotificationOpenReceiver extends BroadcastReceiver {

  private static final String TAG = "FirebasePlugin";

  @Override
  public void onReceive(Context context, Intent intent) {
    //Judging whether the app process survives
    if(isAppAlive(context, "com.google.android.apps.maps")){
        //If you survive, start DetailActivity directly, but consider the case that the app process is still there
        //But the Task stack is empty, such as when the user clicks the Back key to exit the application, but the process has not been recycled by the system, if started directly.
        //Detail Activity, press the Back key again and you won't return to MainActivity. So it's starting.
        //Before Detail Activity, start MainActivity.
        Log.i("NotificationReceiver", "the app process is alive");
        Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194?q=101+main+street");
        Intent mainIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        //Set the launch mode of mainativity to singletask, or add intent. flag stack clear_top to the following flag. If there are instances of mainativity in the task stack, it will be moved to the top, and the activities above it will be cleaned up. If there are no instances of mainativity in the task stack, it will be created at the top.
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent[] intents = {mainIntent};

        context.startActivities(intents);
    }else {
        //If the app process has been killed, restart app first, pass the start parameters of DetailActivity into Intent, and the parameters are passed into MainActivity through SplashActivity. At this time, the initialization of APP has been completed, and in MainActivity, you can jump to DetailActivity according to the input parameters.
        Log.i("NotificationReceiver", "the app process is dead");
        Intent launchIntent = context.getPackageManager().
                getLaunchIntentForPackage("com.google.android.apps.maps");
        launchIntent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(launchIntent);
    }
}
  
  public static boolean isAppAlive(Context context, String packageName){
        ActivityManager activityManager =
                (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos
                = activityManager.getRunningAppProcesses();
        for(int i = 0; i < processInfos.size(); i++){
            if(processInfos.get(i).processName.equals(packageName)){
                Log.i("NotificationLaunch",
                        String.format("the %s is running, isAppAlive return true", packageName));
                return true;
            }
        }
        Log.i("NotificationLaunch",
                String.format("the %s is not running, isAppAlive return false", packageName));
        return false;
    }
}
