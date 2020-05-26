package com.readboy.newcurriculum;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.readboy.newcurriculum.AlarmService.CourseAlarmService;
import com.readboy.newcurriculum.base.BaseActivity;
import com.readboy.newcurriculum.DB.CurriculumDatebase;
import com.readboy.newcurriculum.Utils.DateUtil;
import com.readboy.newcurriculum.Utils.SkinEngine;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends BaseActivity implements View.OnClickListener, OnDialogInforCourse {
    private TextView currentDateTextView;
    private ImageView backgroundImageView;
    private RecyclerView curriculumRecyclerView;
    private TextView addCourseTextView;
    private TextView mondayTextView;
    private TextView tuesdayTextView;
    private TextView wednesdayTextView;
    private TextView thursdayTextView;
    private TextView fridayTextView;
    private TextView saturdayTextView;
    private TextView sundayTextView;
    private TextView emptyTextView;
    private ImageView lastWeekImageView;
    private ImageView nextWeekImageView;
    private TextView skinTextView;
    private LinearLayout languageLinearLayout;
    private TextView chineseTextView;
    private TextView englishTextView;

    private CourseAdapter courseAdapter;
    private CurriculumDatebase dbHelper;

    private Map<String, List<Course>> curriculumHM;
    private String today;
    private String showDay;
    private int editPos;
    private int moveWeek = 0;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    private static int tvBgColorId;
    private static final int[] imageId = {R.drawable.bg,R.drawable.appbg};
    private static boolean chineseFlag = true;

    private static final String TAG = "MainActivity";

    public static void verifyStoragePermissions(AppCompatActivity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,PERMISSIONS_STORAGE[1]);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        verifyStoragePermissions(this);

        addCourseTextView = findViewById(R.id.tv_add_course);
        skinTextView = findViewById(R.id.tv_skin);
        languageLinearLayout = findViewById(R.id.ll_language);
        chineseTextView = findViewById(R.id.tv_chinese);
        englishTextView = findViewById(R.id.tv_English);

        backgroundImageView = findViewById(R.id.iv_background);
        currentDateTextView = findViewById(R.id.tv_current_date);

        mondayTextView = findViewById(R.id.tv_monday);
        tuesdayTextView = findViewById(R.id.tv_tuesday);
        wednesdayTextView = findViewById(R.id.tv_wednesday);
        thursdayTextView = findViewById(R.id.tv_thursday);
        fridayTextView = findViewById(R.id.tv_friday);
        saturdayTextView = findViewById(R.id.tv_saturday);
        sundayTextView = findViewById(R.id.tv_sunday);
        lastWeekImageView = findViewById(R.id.iv_last_week);
        nextWeekImageView = findViewById(R.id.iv_next_week);

        emptyTextView = findViewById(R.id.tv_empty);

        curriculumRecyclerView = findViewById(R.id.recycler_view_curriculum);
        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 3);
        curriculumRecyclerView.setLayoutManager(layoutManager);

        currentDateTextView.setOnClickListener(this);
        addCourseTextView.setOnClickListener(this);
        mondayTextView.setOnClickListener(this);
        tuesdayTextView.setOnClickListener(this);
        wednesdayTextView.setOnClickListener(this);
        thursdayTextView.setOnClickListener(this);
        fridayTextView.setOnClickListener(this);
        saturdayTextView.setOnClickListener(this);
        sundayTextView.setOnClickListener(this);
        lastWeekImageView.setOnClickListener(this);
        nextWeekImageView.setOnClickListener(this);
        skinTextView.setOnClickListener(this);
        languageLinearLayout.setOnClickListener(this);

        dbHelper = CurriculumDatebase.getInstance(getApplication(), "curriculum.db", null, 1);

        curriculumHM = new HashMap<>();
        Date day = new Date(System.currentTimeMillis());
        today = getYearMonthDay(day);
        showDay = today;

        SQLiteDatabase db = dbHelper.openDb();
        if (!tableExists(db, today)) {
            creatDayCourseTable(today);
            Log.d(TAG, "onCreate: tableExists+++++++++");
        }
        dbHelper.closeDb(db);

        initLanguage();
        initSkin();
        setBackgroud();
        //当前显示日期背景色
        tvBgColorId = SkinEngine.getInstance().getColor(R.color.app_tv_bg_color);
        initWeek(day);

        List<Course> coursesList;
        if (curriculumHM.containsKey(today)){
            coursesList = curriculumHM.get(today);
            initCurrentView(coursesList);
        } else {
            curriculumRecyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        }
    }

    private void initLanguage() {
        SharedPreferences preferences = getSharedPreferences("language", Context.MODE_PRIVATE);
        boolean flag = preferences.getBoolean("flag",true);
        if (!chineseFlag || !flag){
            englishTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
            chineseTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
        }

        if (chineseFlag && !flag) {
            //设置应用语言为英语
            Configuration configuration = getResources().getConfiguration();
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            configuration.setLocale(Locale.ENGLISH);
            getResources().updateConfiguration(configuration,displayMetrics);
            chineseFlag = flag;
            recreate();
        }
    }

    private String getYearMonthDay(Date day) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(day);
    }

    private void initWeek(Date day) {
        Date date = DateUtil.getDateInWeekOfFirstDay(day);
        mondayTextView.setText(getString(R.string.app_week_monday) + "\n" + DateUtil.getDayAfterDay(date, 0));
        tuesdayTextView.setText(getString(R.string.app_week_tuesday) + "\n" + DateUtil.getDayAfterDay(date, 1));
        wednesdayTextView.setText(getString(R.string.app_week_wednesday) + "\n" + DateUtil.getDayAfterDay(date, 2));
        thursdayTextView.setText(getString(R.string.app_week_thursday) + "\n" + DateUtil.getDayAfterDay(date, 3));
        fridayTextView.setText(getString(R.string.app_week_friday) + "\n" + DateUtil.getDayAfterDay(date, 4));
        saturdayTextView.setText(getString(R.string.app_week_saturday) + "\n" + DateUtil.getDayAfterDay(date, 5));
        sundayTextView.setText(getString(R.string.app_week_sunday) + "\n" + DateUtil.getDayAfterDay(date, 6));

        mondayTextView.setTypeface(null, Typeface.NORMAL);
        tuesdayTextView.setTypeface(null, Typeface.NORMAL);
        wednesdayTextView.setTypeface(null, Typeface.NORMAL);
        thursdayTextView.setTypeface(null, Typeface.NORMAL);
        fridayTextView.setTypeface(null, Typeface.NORMAL);
        saturdayTextView.setTypeface(null, Typeface.NORMAL);
        sundayTextView.setTypeface(null, Typeface.NORMAL);

        if (moveWeek == 0)
            initShowDayView();
        else
            cancleDayView();

        Date posDate;
        String posDay;
        boolean queryFlag = true;
        for (int i = 0; i < 7; i++) {
            posDate = DateUtil.getDateAfterDay(date, i);
            posDay = getYearMonthDay(posDate);
            if (curriculumHM.containsKey(posDay)) {
                queryFlag = false;
                initCourseDay(i);
            }
        }

        if (queryFlag) {
            for (int i = 0; i < 7; i++) {
                posDate = DateUtil.getDateAfterDay(date, i);
                posDay = getYearMonthDay(posDate);
                SQLiteDatabase db = dbHelper.openDb();
                if (tableExists(db, posDay)) {
                    Cursor cursor = db.query("tb_" + posDay, new String[]{"subject", "course_name", "teacher"
                                    , "classroom", "tackle", "class_start", "class_end"}, null, null,
                            null, null, "class_start asc");
                    if (cursor.moveToFirst()) {
                        initCourseDay(i);
                        List<Course> list = new ArrayList<>();
                        do {
                            String subject = cursor.getString(0);
                            String courseName = cursor.getString(1);
                            String teacher = cursor.getString(2);
                            String classroom = cursor.getString(3);
                            String tackle = cursor.getString(4);
                            String classStart = cursor.getString(5);
                            String classEnd = cursor.getString(6);
                            Course course = new Course(subject, courseName, teacher, classroom, tackle, posDay, classStart, classEnd);

                            list.add(course);

                        } while (cursor.moveToNext());

                        curriculumHM.put(posDay, list);
                    }

                    cursor.close();
                }

                dbHelper.closeDb(db);
            }
        }


    }

    private boolean tableExists(SQLiteDatabase db, String posDay) {
        boolean result = false;
        Cursor cursor = db.query("Sqlite_master", new String[]{"count(*)"}, "type = 'table' and name = ?",
                new String[]{"tb_" + posDay}, null, null, null);
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            if (count > 0) {
                result = true;
            }
        }
        cursor.close();
        return result;
    }

    //有课程安排的日期为粗体
    private void initCourseDay(int i) {
        switch (i) {
            case 6:
                sundayTextView.setTypeface(null, Typeface.BOLD);
                break;
            case 0:
                mondayTextView.setTypeface(null, Typeface.BOLD);
                break;
            case 1:
                tuesdayTextView.setTypeface(null, Typeface.BOLD);
                break;
            case 2:
                wednesdayTextView.setTypeface(null, Typeface.BOLD);
                break;
            case 3:
                thursdayTextView.setTypeface(null, Typeface.BOLD);
                break;
            case 4:
                fridayTextView.setTypeface(null, Typeface.BOLD);
                break;
            case 5:
                saturdayTextView.setTypeface(null, Typeface.BOLD);
                break;
        }
    }

    //显示当前显示日期的背景色
    private void initShowDayView() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        try {
            Date showDate = simpleDateFormat.parse(showDay);
            int dayOfWeek = DateUtil.getDayInWeek(showDate);
            switch (dayOfWeek) {
                case 1:
                    sundayTextView.setBackgroundColor(tvBgColorId);
                    break;
                case 2:
                    mondayTextView.setBackgroundColor(tvBgColorId);
                    break;
                case 3:
                    tuesdayTextView.setBackgroundColor(tvBgColorId);
                    break;
                case 4:
                    wednesdayTextView.setBackgroundColor(tvBgColorId);
                    break;
                case 5:
                    thursdayTextView.setBackgroundColor(tvBgColorId);
                    break;
                case 6:
                    fridayTextView.setBackgroundColor(tvBgColorId);
                    break;
                case 7:
                    saturdayTextView.setBackgroundColor(tvBgColorId);
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //初始化当前日期课程表
    private void initCurrentView(List<Course> coursesList) {
        courseAdapter = new CourseAdapter(coursesList);
        courseAdapter.setHasStableIds(true);
        curriculumRecyclerView.setAdapter(courseAdapter);
        courseAdapter.setmOnItemClickListener(new CourseAdapter.onItemClickListener() {
            @Override
            public void onClidk(View view, int pos, Course course) {
                editPos = pos;
                showCourseDialog(2, course);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Date day = new Date(System.currentTimeMillis());
        today = getYearMonthDay(day);
        initCurrentDate(day);
    }

    private void setBackgroud() {
        Glide.with(MainActivity.this).load(imageId[getSkinIndex()]).into(backgroundImageView);
    }

    private void initCurrentDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        currentDateTextView.setText(dateFormat.format(date));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add_course:
                showCourseDialog(1, null);
                break;
            case R.id.tv_monday:
                updateShowCourse(0);

                mondayTextView.setBackgroundColor(tvBgColorId);
                break;
            case R.id.tv_tuesday:
                updateShowCourse(1);
                tuesdayTextView.setBackgroundColor(tvBgColorId);
                break;
            case R.id.tv_wednesday:
                updateShowCourse(2);
                wednesdayTextView.setBackgroundColor(tvBgColorId);
                break;
            case R.id.tv_thursday:
                updateShowCourse(3);
                thursdayTextView.setBackgroundColor(tvBgColorId);
                break;
            case R.id.tv_friday:
                updateShowCourse(4);
                fridayTextView.setBackgroundColor(tvBgColorId);
                break;
            case R.id.tv_saturday:
                updateShowCourse(5);
                saturdayTextView.setBackgroundColor(tvBgColorId);
                break;
            case R.id.tv_sunday:
                updateShowCourse(6);
                sundayTextView.setBackgroundColor(tvBgColorId);
                break;
            case R.id.iv_last_week:
                moveWeek--;
                updateWeek();
                break;
            case R.id.iv_next_week:
                moveWeek++;
                updateWeek();
                break;
            case R.id.tv_current_date:
                moveWeek = 0;
                showDay = today;
                cancleDayView();
                updateWeek();
                if (curriculumHM.containsKey(showDay)) {
                    initCurrentView(curriculumHM.get(showDay));
                    if (curriculumRecyclerView.getVisibility() == View.GONE) {
                        emptyTextView.setVisibility(View.GONE);
                        curriculumRecyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    curriculumRecyclerView.setVisibility(View.GONE);
                    emptyTextView.setVisibility(View.VISIBLE);
                }

                Toast.makeText(this, "这是今天的安排", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_skin:
                changeSkin(getPath());
                setBackgroud();
                tvBgColorId = SkinEngine.getInstance().getColor(R.color.app_tv_bg_color);
                initWeek(getDateYearMonthDay(showDay));
                break;
            case R.id.ll_language:
                changeLanguage();
                break;
        }
    }

    private void changeLanguage() {
        Configuration configuration = getResources().getConfiguration();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        if (chineseFlag) {
            configuration.setLocale(Locale.ENGLISH);
            englishTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
            chineseTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
        } else {
            configuration.setLocale(Locale.SIMPLIFIED_CHINESE);
            chineseTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
            englishTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
        }

        chineseFlag = !chineseFlag;
        getResources().updateConfiguration(configuration,displayMetrics);
        SharedPreferences.Editor editor = getSharedPreferences("language",Context.MODE_PRIVATE).edit();
        editor.putBoolean("flag",chineseFlag);
        editor.apply();
        recreate();
    }

    private Date getDateYearMonthDay(String showDay) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            Date date = simpleDateFormat.parse(showDay);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateWeek() {
        Date date = new Date(System.currentTimeMillis());
        Date moveDate = DateUtil.getDateAfterDay(date, moveWeek * 7);
        initWeek(moveDate);
    }

    private void updateShowCourse(int i) {
        Date date = new Date(System.currentTimeMillis());
        Date showDate = DateUtil.getDateAfterDay(DateUtil.getDateInWeekOfFirstDay(date), i + moveWeek * 7);
        showDay = getYearMonthDay(showDate);
//        Log.d(TAG, "updateShowDay: showDay++++" + showDay);
        if (curriculumHM.containsKey(showDay)) {
            emptyTextView.setVisibility(View.GONE);
            initCurrentView(curriculumHM.get(showDay));
            curriculumRecyclerView.setVisibility(View.VISIBLE);
        } else {
            curriculumRecyclerView.setVisibility(View.GONE);
            courseAdapter = null;
            emptyTextView.setVisibility(View.VISIBLE);
        }

        cancleDayView();
    }

    private void cancleDayView() {
        sundayTextView.setBackgroundResource(0);
        mondayTextView.setBackgroundResource(0);
        tuesdayTextView.setBackgroundResource(0);
        wednesdayTextView.setBackgroundResource(0);
        thursdayTextView.setBackgroundResource(0);
        fridayTextView.setBackgroundResource(0);
        saturdayTextView.setBackgroundResource(0);
    }

    private void showCourseDialog(int type, Course course) {
        switch (type) {
            case 1:
                CourseDialogFragment addDialogFragment = new CourseDialogFragment();
                addDialogFragment.setOnDialogInforCourse(this);
                addDialogFragment.show(getSupportFragmentManager(), "add");
                break;

            case 2:
                CourseDialogFragment editDialogFragment = new CourseDialogFragment(course);
                editDialogFragment.setOnDialogInforCourse(this);
                editDialogFragment.show(getSupportFragmentManager(), "edit");
                break;
        }

    }

    @Override
    public void inputDialogInforCourse(Course course) {
        String day = course.getDay();
        List<Course> courseList;
        SQLiteDatabase db = dbHelper.openDb();
        boolean updateFlag = false;
        if (!curriculumHM.containsKey(day)) {
            updateFlag = true;
            courseList = new ArrayList<>();
            courseList.add(course);

            if (!tableExists(db, day)) {
                creatDayCourseTable(day);
            }
            Log.d(TAG, "inputDialogInforCourse: updateFlag++++++++" + updateFlag);

        } else {
            courseList = curriculumHM.get(day);
            int len = courseList.size();
            Course indexCourse;
            indexCourse = courseList.get(len - 1);
            String courseTime = day + course.getClassStart();
            if (startAfterEnd(indexCourse.getDay() + indexCourse.getClassStart(),courseTime)) {
                //不是最晚时间
                for (int i = 0; i < len; i++) {
                    indexCourse = courseList.get(i);
                    if (indexCourse.getDay() != null && startAfterEnd(indexCourse.getDay() + indexCourse.getClassStart()
                            , courseTime)) {
                        courseList.add(i, course);
                        break;
                    }
                }
            }
            else {
                courseList.add(course);
            }
        }

//        Log.d(TAG, "inputDialogInforCourse: showDay");
        if (showDay.equals(day)) {
            int pos = courseList.indexOf(course);
            if (pos != courseList.size() - 1) {
                initCurrentView(courseList);
            }

            if (curriculumRecyclerView.getVisibility() == View.GONE) {
                initCurrentView(courseList);
                emptyTextView.setVisibility(View.GONE);
                curriculumRecyclerView.setVisibility(View.VISIBLE);
            }
        }

        curriculumHM.put(day, courseList);
        if (updateFlag) updateShowWeek(day);

        ContentValues values = getCourseValues(course);
        long num = db.insert("tb_" + day, null, values);
        Log.d(TAG, "inputDialogInforCourse: ++++++" + num);
        dbHelper.closeDb(db);
        updateAlarm(course, "ADD", null);
    }

    private ContentValues getCourseValues(Course course) {
        ContentValues values = new ContentValues();
        values.put("subject", course.getSubject());
        values.put("course_name", course.getCourseName());
        values.put("teacher", course.getTeacher());
        values.put("classroom", course.getCourseName());
        values.put("tackle", course.getTackle());
        values.put("day", course.getDay());
        values.put("class_start", course.getClassStart());
        values.put("class_end", course.getClassEnd());
        return values;
    }

    private void updateShowWeek(String day) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            Date showDate = simpleDateFormat.parse(showDay);
            Date startDate = DateUtil.getDateInWeekOfFirstDay(showDate);
            String startDay = simpleDateFormat.format(startDate);
            Date date = simpleDateFormat.parse(day);
//            Log.d(TAG, "updateShowWeek: startDay+++++++" + startDay + "+++++" + day + "+++++++++" + startAfterEnd(day,startDay));
            if (startDay.equals(day) || date.after(startDate)) {
                Date endDate = DateUtil.getDateAfterDay(startDate, 6);
                String endDay = simpleDateFormat.format(endDate);
//                Log.d(TAG, "updateShowWeek: endDay++++++++" + endDay);
                if (endDay.equals(day) || endDate.after(date)) initWeek(showDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void creatDayCourseTable(String day) {
        SQLiteDatabase db = dbHelper.openDb();
        db.execSQL("create table tb_" + day + "(id integer primary key autoincrement,subject text,course_name text," +
                "teacher text,classroom text,tackle text,day text,class_start text,class_end text)");
        dbHelper.closeDb(db);
    }

    private void updateAlarm(Course course, String action, Course oldCourse) {
//        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, CourseAlarmService.class);
        intent.setAction(action);
        intent.putExtra("subject", course.getSubject());
        intent.putExtra("courseName", course.getCourseName());
        if (!action.equals("DELETE")) {
            intent.putExtra("day", course.getDay());
            intent.putExtra("time", course.getClassStart());

            intent.putExtra("tackle", course.getTackle());
            if (action.equals("UPDATE")) {
                intent.putExtra("old_subject", oldCourse.getSubject());
                intent.putExtra("old_course_name", oldCourse.getCourseName());
            }
        }

        startService(intent);
    }

    private boolean startAfterEnd(String startTime, String endTime) {
        Date startDate = getDateYearMonthDay(startTime);
        Date endDate = getDateYearMonthDay(endTime);
        if (startDate.after(endDate)) {
            return true;
        }
        return false;
    }

    @Override
    public void editDialogInforCourse(boolean flagEdit, Course course) {
        List<Course> courseList = curriculumHM.get(showDay);
        Course posCourse = courseList.get(editPos);
        SQLiteDatabase db = dbHelper.openDb();
//        Log.d(TAG, "editDialogInforCourse: ++++++++++" + editPos);
        if (flagEdit) {
            updateAlarm(course, "UPDATE", posCourse);
            courseList.set(editPos, course);
            int len = courseList.size();
            boolean insertFlag = false;
            if (len > 1 && !posCourse.getClassStart().equals(course.getClassStart())) {
                //开始时间发生变化
                Date posDate = getHourMinute(posCourse.getClassStart());
                Date date = getHourMinute(course.getClassStart());
                Date indexDate;
                if (posDate.after(date)) {
                    //开始时间提前了
                    for (int i = 0; i < editPos; i++) {
                        indexDate = getHourMinute(courseList.get(i).getClassStart());
                        if (indexDate.after(date)) {
                            insertFlag = true;
                            courseList.remove(editPos);
                            courseList.add(i,course);
                            initCurrentView(courseList);
                            break;
                        }
                    }
                }

                else if (editPos != len - 1){
                    //开始时间延后了，原来不是最晚的
                    insertFlag = true;
                    boolean endFlag = true;
                    for (int i = editPos + 1;i < len;i++) {
                        indexDate = getHourMinute(courseList.get(i).getClassStart());
                        if (indexDate.after(date)) {
                            endFlag = false;
                            courseList.add(i,course);
                            courseList.remove(editPos);
                            initCurrentView(courseList);
                            break;
                        }
                    }

                    if (endFlag) {
                        courseList.remove(editPos);
                        courseList.add(course);
                    }
                }
            }

            if (!insertFlag)
                courseAdapter.notifyDataSetChanged();
            else
                initCurrentView(courseList);
            ContentValues values = getCourseValues(course);
            Log.d(TAG, "editDialogInforCourse: posCourse+++++" + posCourse.getSubject() + "++++++" + posCourse.getCourseName());
            db.update("tb_" + course.getDay(), values, "subject = ? and course_name = ?", new String[]{
                    posCourse.getSubject(), posCourse.getCourseName()});

        } else {
//            Log.d(TAG, "editDialogInforCourse:++++++++++ " + courseList.get(editPos).getSubject());
            updateAlarm(posCourse, "DELETE", null);
            courseList.remove(editPos);
            courseAdapter.notifyDataSetChanged();
            db.delete("tb_" + posCourse.getDay(), "subject = ? and course_name = ?", new String[]{
                    posCourse.getSubject(), posCourse.getCourseName()});
            if (courseList.size() == 0) {
                curriculumRecyclerView.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.VISIBLE);
                courseAdapter = null;
                curriculumHM.remove(posCourse.getDay());
                db.execSQL("drop table if exists tb_" + posCourse.getDay());
                updateShowWeek(posCourse.getDay());
            }
        }

        dbHelper.closeDb(db);
    }

    private Date getHourMinute(String classStart) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        try {
            Date date = simpleDateFormat.parse(classStart);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
