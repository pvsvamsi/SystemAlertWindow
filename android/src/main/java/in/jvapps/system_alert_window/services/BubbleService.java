package in.jvapps.system_alert_window.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Person;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.HashMap;

import in.jvapps.system_alert_window.BubbleActivity;
import in.jvapps.system_alert_window.R;

import static in.jvapps.system_alert_window.utils.Constants.INTENT_EXTRA_PARAMS_MAP;
import static in.jvapps.system_alert_window.utils.Constants.KEY_HEIGHT;

public class BubbleService extends Service {

    private Context mContext;

    private static final String CHANNEL_ID = "1237";
    private int BUBBLE_NOTIFICATION_ID = 1237;
    private static NotificationManager notificationManager;
    private static String TAG = "BubbleService";

    @Override
    public void onCreate() {
        mContext = this;
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        if (intent.getExtras() != null) {
            @SuppressWarnings("unchecked")
            HashMap<String, Object> params = (HashMap<String, Object>) intent.getSerializableExtra(INTENT_EXTRA_PARAMS_MAP);
            assert params != null;
            Intent target = new Intent(mContext, BubbleActivity.class);
            target.putExtra(INTENT_EXTRA_PARAMS_MAP, params);
            PendingIntent bubbleIntent =
                    PendingIntent.getActivity(mContext, 0, target, PendingIntent.FLAG_UPDATE_CURRENT);

            // Create bubble metadata
            Notification.BubbleMetadata bubbleData =
                    new Notification.BubbleMetadata.Builder()
                            .setDesiredHeight((int) params.get(KEY_HEIGHT))
                            .setIcon(Icon.createWithResource(mContext, R.drawable.ic_notification))
                            .setIntent(bubbleIntent)
                            .setAutoExpandBubble(true)
                            .setSuppressNotification(true)
                            .build();

            Person chatBot = new Person.Builder()
                    .setBot(true)
                    .setName("BubbleBot")
                    .setImportant(true)
                    .build();

            Notification.Builder builder =
                    new Notification.Builder(mContext, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_notification)
                            .setCategory(Notification.CATEGORY_CALL)
                            .setBubbleMetadata(bubbleData)
                            .addPerson(chatBot);

            notificationManager.notify(BUBBLE_NOTIFICATION_ID, builder.build());

            startForeground(BUBBLE_NOTIFICATION_ID, builder.build());
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void createNotificationChannel() {
        CharSequence name = mContext.getString(R.string.channel_name);
        String description = mContext.getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        channel.setAllowBubbles(true);
        initNotificationManager();
        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initNotificationManager() {
        if (notificationManager == null) {
            if (mContext == null) {
                Log.e(TAG, "Context is null. Can't show the System Alert Window");
                return;
            }
            notificationManager = mContext.getSystemService(NotificationManager.class);
        }
    }

}
