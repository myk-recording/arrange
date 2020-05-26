package com.readboy.newcurriculum.AlarmService;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;

import com.readboy.newcurriculum.Course;
import com.readboy.newcurriculum.DB.CurriculumDatebase;
import com.readboy.newcurriculum.MainActivity;
import com.readboy.newcurriculum.R;

import java.util.Calendar;
import java.util.Locale;

public class CourseAlarmService extends Service {
    private CurriculumDatebase dbHelper;

    private static final String TAG = "CourseAlarmService";
    private static boolean CLOSE_FLAG;
    private static int ID = 1;
    private static final String ALARM_TABLE = "tb_alarm";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = CurriculumDatebase.getInstance(getApplication(), "curriculum.db", null, 1);
        CLOSE_FLAG = false;
        Log.d(TAG, "onCreate: ++++++" + CLOSE_FLAG);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SQLiteDatabase db = dbHelper.openDb();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        synchronized (this) {
            if (("ADD".equals(intent.getAction()) || "UPDATE".equals(intent.getAction())) &&
                    intent.getStringExtra("day") != null) {
                Log.d(TAG, "onStartCommand: ADD++++++UPDATE");
                String day = intent.getStringExtra("day");
                String hourMinuteTime = intent.getStringExtra("time");
                String subject = intent.getStringExtra("subject");
                String courseName = intent.getStringExtra("courseName");
                String tackle = intent.getStringExtra("tackle");

                ContentValues values = new ContentValues();
                values.put("alarm_time", day + hourMinuteTime);
                values.put("subject", subject);
                values.put("course_name", courseName);
                if (tackle != null && !tackle.equals("")) values.put("tackle", tackle);


                Cursor numCursor = db.query(ALARM_TABLE,new String[]{"count(id)"},"set_up_flag = 1",null,null
                        ,null,null);
                numCursor.moveToFirst();
                int upCount = numCursor.getInt(0);
                if (upCount > 0) {
                    //存在已启动的课程提醒
                    int up = 0;
                    Cursor cursor = db.query(ALARM_TABLE, new String[]{"subject", "course_name"}, "set_up_flag = 1" +
                            " and alarm_time >= ?", new String[]{day + hourMinuteTime}, null, null, null);
                    if (cursor.moveToFirst()) {
                        //新的课程安排时间最早
                        up = 1;
                        do {
                            String upSubject = cursor.getString(0);
                            String upCourseName = cursor.getString(1);
//                            Log.d(TAG, "onStartCommand: ++++" + upSubject + "+++++++" + upCourseName);
                            alarmCancle(alarmManager,upSubject,upCourseName);

                            long num = updateUpCourse(db,upSubject,upCourseName,0);
//                        Log.d(TAG, "onStartCommand: db.update++++++" + num);
                        } while (cursor.moveToNext());

                        addAlarm(alarmManager,day,hourMinuteTime,subject,courseName,tackle);
                    }

                    cursor.close();

                    values.put("set_up_flag", String.valueOf(up));
                    if ("ADD".equals(intent.getAction())) {

                        long num = db.insert(ALARM_TABLE,null ,values);
//                        Log.d(TAG, "onStartCommand: db.insert+++++++" + num + "++++" + up);
                    }

                    if ("UPDATE".equals(intent.getAction())) {
                        boolean nextFlag = false;
                        String oldSubject = intent.getStringExtra("old_subject");
                        String oldCourseName = intent.getStringExtra("old_course_name");
                        Cursor downCursor = db.query(ALARM_TABLE,new String[] {"set_up_flag"},"subject = ? and " +
                                "course_name = ?",new String[] {oldSubject,oldCourseName},null,null
                                ,null);
                        if (downCursor.moveToFirst()) {
                            //原课程安排存在
                            String upFlag = downCursor.getString(0);
                            if (up == 0 && upFlag.equals("1")) {
                                //原课程安排延迟
                                alarmCancle(alarmManager,oldSubject,oldCourseName);
                                nextFlag = true;
                            }

                            long num = db.update(ALARM_TABLE,values,"subject = ? and course_name = ?"
                                    ,new String[] {oldSubject,oldCourseName});
                            if (nextFlag && upCount == 1) {
                                //只有一个课程安排未提醒，重新启动
                                upNextAlarm(db,alarmManager);
                            }
//                            Log.d(TAG, "onStartCommand: UPDATE++++++" + num);

                        }

                        else {
                            //记录已删除
                            long num = db.insert(ALARM_TABLE,null ,values);
//                            Log.d(TAG, "onStartCommand: db.insert+++++++UPDATE" + num);
                        }

                        downCursor.close();
                    }
                }

                else {
                    //表为空
                    addAlarm(alarmManager,day,hourMinuteTime,subject,courseName,tackle);
                    values.put("set_up_flag", "1");
                    db.insert(ALARM_TABLE,null ,values);
                }
                numCursor.close();
            }

            else if ("DELETE".equals(intent.getAction()) && intent.getStringExtra("subject") != null) {
                String subject = intent.getStringExtra("subject");
                String courseName = intent.getStringExtra("courseName");

                Cursor downCursor = db.query(ALARM_TABLE,new String[] {"set_up_flag"},"subject = ? and " +
                                "course_name = ?",new String[] {subject,courseName},null,null,null);
                if (downCursor.moveToFirst()) {
                    String upFlag = downCursor.getString(0);
                    if ("1".equals(upFlag)) {
                        //课程安排已启动提醒
                        alarmCancle(alarmManager,subject,courseName);
                        upNextAlarm(db,alarmManager);
                    }

                    long num = deleteUpCourse(db,subject,courseName);
                    Log.d(TAG, "onStartCommand: DELETE++++++" + num);
                }
            }

            else if (!"DELETE".equals(intent.getAction()) && (intent.getStringExtra("day" ) == null ||
                    "".equals(intent.getStringExtra("day")))) {
//                Log.d(TAG, "onStartCommand: ALARM+++++++++");
                Intent mintent = new Intent(this, MainActivity.class);
                PendingIntent mainPendingIntent = PendingIntent.getActivity(this, 0, mintent, 0);
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification.Builder builder = new Notification.Builder(this);

                String time = intent.getStringExtra("time");
                String tackle = intent.getStringExtra("tackle");
                String subject = intent.getStringExtra("subject");
                String courseName = intent.getStringExtra("courseName");

                SharedPreferences preferences = getSharedPreferences("language", Context.MODE_PRIVATE);
                boolean flag = preferences.getBoolean("flag",true);
                if (!flag) {
                    Configuration configuration = getResources().getConfiguration();
                    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                    configuration.setLocale(Locale.ENGLISH);
                    getResources().updateConfiguration(configuration,displayMetrics);
                }

                if (tackle == null || tackle.equals("")) {
                    tackle = getString(R.string.app_notification_tackle_empty);
                } else {
                    tackle = getString(R.string.app_notification_tackle_head) + tackle +
                            getString(R.string.app_notification_tackle_tail);
                }
                builder.setContentTitle(getString(R.string.app_notification_title) + time + "—" + subject + "—" +
                        courseName)
                        .setContentText(tackle)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                        .setContentIntent(mainPendingIntent)
                        .setAutoCancel(true)
                        .setVibrate(new long[]{0, 300, 200, 300, 200, 300})
                        .setDefaults(Notification.DEFAULT_ALL);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //8.0
                    NotificationChannel notificationChannel = new NotificationChannel("CourseAlarmServiceId"
                            , "CourseAlarmServiceName", NotificationManager.IMPORTANCE_HIGH);
                    notificationChannel.enableLights(true);

                    notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                    notificationChannel.canShowBadge();
                    notificationChannel.enableVibration(true);
                    notificationChannel.setVibrationPattern(new long[]{0, 300, 200, 300, 200, 300});
                    notificationChannel.shouldShowLights();
                    notificationManager.createNotificationChannel(notificationChannel);
                    builder.setChannelId("CourseAlarmServiceId");
                }

                notificationManager.notify(ID++, builder.build());
                mintent = null;

                upNextAlarm(db,alarmManager);

                long num = deleteUpCourse(db,subject,courseName);
//                Log.d(TAG, "onStartCommand: deleteUpCourse++++++++" + num);

            }
        }

        dbHelper.closeDb(db);
        if (CLOSE_FLAG) stopSelf();

        return super.onStartCommand(intent, flags, startId);
    }

    private void upNextAlarm(SQLiteDatabase db,AlarmManager alarmManager) {

        db.beginTransaction();
        Cursor minCursor = db.query(ALARM_TABLE,new String[]{"min(alarm_time)"},"set_up_flag = ?"
                ,new String[]{"0"},null,null,null);
        if (minCursor.moveToFirst()) {
            String minTime = minCursor.getString(0);
            Log.d(TAG, "onStartCommand: minTime+++++++" + minTime);
            if (minTime != null) {
                //表中有记录
                Cursor upCursor = db.query(ALARM_TABLE,new String[]{"alarm_time","subject","course_name","tackle"}
                        ,"set_up_flag = ? and alarm_time = ?",new String[]{"0",minTime},null,null
                        ,null);
                if (upCursor.moveToFirst()) {
                    do {
                        String upDayAndTime = upCursor.getString(0);
                        String upSubject = upCursor.getString(1);
                        String upCourseName = upCursor.getString(2);
                        String upTackle = upCursor.getString(3);

//                            String upTime = upDayAndTime.substring(8);

                        addAlarm(alarmManager,upDayAndTime.substring(0,8),upDayAndTime.substring(8,13),upSubject
                                ,upCourseName,upTackle);
                        long num = updateUpCourse(db,upSubject,upCourseName,1);
                    Log.d(TAG, "onStartCommand: updateUpCourse+++++++" + num);

                    } while (upCursor.moveToNext());
                }

                upCursor.close();
            }
            else {
                //不存在未提醒的课程安排
                CLOSE_FLAG = true;
            }
//                    Log.d(TAG, "onStartCommand: minTime++++++" + minTime);
        }
        minCursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private long deleteUpCourse(SQLiteDatabase db, String subject, String courseName) {
        return db.delete(ALARM_TABLE,"subject = ? and course_name = ?",new String[] {subject,courseName});
    }

    private void addAlarm(AlarmManager alarmManager, String day, String hourMinuteTime, String subject, String courseName
            , String tackle) {
        Intent addIntent = new Intent(this, CourseAlarmService.class);
        addIntent.setAction(subject + courseName);
        addIntent.putExtra("time",hourMinuteTime);
        addIntent.putExtra("subject",subject);
        addIntent.putExtra("courseName",courseName);
        addIntent.putExtra("tackle",tackle);

        Log.d(TAG, "addAlarm: ++++++" + subject + "+++++++" + courseName);

        Calendar calendar = Calendar.getInstance();
        String[] hourAndMinute = hourMinuteTime.split(":");

        calendar.set(Calendar.YEAR, Integer.parseInt(day.substring(0, 4)));
        calendar.set(Calendar.MONTH, Integer.parseInt(day.substring(4, 6)) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day.substring(6, 8)));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourAndMinute[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(hourAndMinute[1]) - 5);

        PendingIntent addPendingIntent = PendingIntent.getService(this, 0, addIntent
                , PendingIntent.FLAG_CANCEL_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //6.0
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), addPendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), addPendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), addPendingIntent);
        }
    }

    private long updateUpCourse(SQLiteDatabase db,String upSubject, String upCourseName, int flag) {
        ContentValues values = new ContentValues();
        values.put("set_up_flag",flag);
        return db.update(ALARM_TABLE,values,"subject = ? and course_name = ?",new String[]{upSubject
                ,upCourseName});
    }

    private void alarmCancle(AlarmManager alarmManager,String upSubject,String upCourseName) {
        Intent upIntent = new Intent(this, CourseAlarmService.class);
        upIntent.setAction(upSubject + upCourseName);
        PendingIntent upPendingIntent = PendingIntent.getService(this, 0, upIntent
                , PendingIntent.FLAG_NO_CREATE);
        if (upPendingIntent != null) {
//            Log.d(TAG, "insettAlarm: pi+++++++++" + upPendingIntent);
            alarmManager.cancel(upPendingIntent);
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: +++++++++++++");
        
        super.onDestroy();
    }
}
