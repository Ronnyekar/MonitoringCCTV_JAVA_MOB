package com.example.testdashboard;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.ping.PingResult;
import com.stealthcopter.networktools.ping.PingStats;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Handler;

public class PingThread {
    private static PingThread instance;
    private Runnable pingRunnable;
    private Future pingTask;
    private List<String> ipList = new ArrayList<>();
    private List<String> ipId = new ArrayList<>();
    private boolean running = false;
    public static final String PING_CHANNEL = "ping channel";

    private DataHelper dbcenter;
    private Context context;

    private PingThread(Context context) {
        this.context = context;
        dbcenter = new DataHelper(context);
        pingRunnable = new Runnable() {
            @Override
            public void run() {
                pingProcess();
            }
        };
    }

    public static PingThread getInstance(Context context) {
        if (instance == null) {
            instance = new PingThread(context);
        }

        return instance;
    }

    public void startThread() {
        if (this.instance == null) {
            return;
        }

        running = true;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        pingTask = executorService.submit(pingRunnable);
    }

    public void stopThread() {
        if (this.instance == null) {
            return;
        }

        this.pingTask.cancel(true);
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    private void pingProcess() {
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tabletime ",null);
        cursor.moveToFirst();
        int interval = 10000;
        if (cursor.getCount()>0)
        {
            cursor.moveToPosition(0);
            interval = cursor.getInt(0);
        }
        int menit = interval * 60000;

        while (true) {
            try {
                Thread.sleep(menit);
                Log.d("PING THREAD", "Pinging CCTVs...");
                getList();
                pingExecute();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private void pingExecute() {
        for (int i = 0; i < this.ipList.size(); i++) {
            final String id = this.ipId.get(i);
            Ping.onAddress(ipList.get(i)).setTimeOutMillis(1000).setTimes(5).doPing(new Ping.PingListener() {
                @Override
                public void onResult(PingResult pingResult) {
                    SQLiteDatabase db = dbcenter.getWritableDatabase();
                    ContentValues args = new ContentValues();
                    args.put("status", "Up");
                    String whereClause = "no = " + id;
                    db.update("demodtb2", args, whereClause, null);
                }
                @Override
                public void onFinished(PingStats pingStats) {
                }
                @Override
                public void onError(Exception e) {
                    SQLiteDatabase db = dbcenter.getWritableDatabase();
                    ContentValues args = new ContentValues();
                    args.put("status", "Down");
                    String whereClause = "no = " + id;
                    db.update("demodtb2", args, whereClause, null);
                    notification();
                }
            });
        }
    }

    public void getList() {
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM demodtb2", null);
        cursor.moveToFirst();
        for (int cc = 0; cc < cursor.getCount(); cc++) {
            cursor.moveToPosition(cc);
            this.ipList.add(cursor.getString(2));
            this.ipId.add(cursor.getString(0));
        }
    }

    public void notification (){
        Preferences preferences = Preferences.getInstance(context);
        if (!preferences.isPingNotificationEnable()) {
            return;
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.context, PING_CHANNEL)
                .setSmallIcon(R.drawable.ic_message)
                .setContentTitle("Laporan")
                .setContentText("Terdapat CCTV Bermasalah");

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, mBuilder.build());
    }
}
