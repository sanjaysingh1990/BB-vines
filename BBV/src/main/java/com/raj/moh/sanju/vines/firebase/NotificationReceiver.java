package com.raj.moh.sanju.vines.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.raj.moh.sanju.vines.activity.PlayerActivity;
import com.raj.moh.sanju.vines.other.Config;
import com.rajmoh.allvines.R;

/**
 * Created by android on 5/12/16.
 */

public class NotificationReceiver extends WakefulBroadcastReceiver {
    private static final
    String MESSAGE =
            "gcm.notification.body";
    String TITLE =
            "gcm.notification.title";
     String VIDEO_ID = "gcm.notification.video_id";
    private String mVideoId;
    private int NOTIID;

    @Override
    public void onReceive(Context context, Intent data) {



        try {
            mVideoId = data.getExtras().get(VIDEO_ID).toString();


        } catch (Exception ex) {
            Log.e("pushdata", ex.getMessage() + "");


        }


        NOTIID = (int) System.currentTimeMillis();
        //   Logger.e("notiid",NOTIID+"");

        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(Config.VIDEO_ID,mVideoId);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, NOTIID, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("All-Vines").setContentText(data.getStringExtra(MESSAGE)).setAutoCancel(true).setSound(defaultSoundUri).setContentIntent(pendingIntent);
        notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder));

        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIID, notificationBuilder.build());

        //to abort default notificaiton receiver of firebase
        abortBroadcast();


    }


    /**
     * ************************ notification icon below and above lollipop **********************
     *
     * @param notificationBuilder
     * @return
     */
    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = Color.parseColor("#EF6A4A");
            notificationBuilder.setColor(color);
            return R.drawable.ic_app_logo;

        } else {
            return R.drawable.ic_app_logo;
        }
    }


}
