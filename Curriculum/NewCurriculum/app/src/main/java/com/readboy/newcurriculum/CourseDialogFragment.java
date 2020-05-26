package com.readboy.newcurriculum;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.readboy.newcurriculum.DB.CurriculumDatebase;
import com.readboy.newcurriculum.Utils.SkinEngine;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CourseDialogFragment extends DialogFragment {
    private TextView titleTextView;
    private EditText subjectEditText;
    private EditText classroomEditText;
    private EditText courseNameEditText;
    private EditText dayEditText;
    private EditText startTimeEditText;
    private EditText endTimeEditText;
    private EditText teacherEditText;
    private EditText tackleEditText;
    private TextView addCancelTextView;
    private TextView addConfirmTextView;
    private TextView editCancelTextView;
    private TextView editDeleteTextView;
    private TextView editSaveTextView;
    private ConstraintLayout addConstraintLayout;
    private ConstraintLayout editConstraintLayout;

    private Handler handler;
    private OnDialogInforCourse onDialogInforCourse;
    private Course showCourse;
    private ExecutorService executorService;
    private Runnable exitRunnable;

    private static final int ADD_CONFIRM = 1;
    private static final int EDIT_CONFIRM = 2;
    private static final int EDIT_DELETE = 3;
    private static final long DOUBLE_TIME = 1000;
    private static long lastClickTime = 0;

    private static final String TAG = "CourseDialogFragment";

    public CourseDialogFragment(Course course) {
        showCourse = course;
    }

    public CourseDialogFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.app_dialog_fragment_course_edit, container);
        titleTextView = view.findViewById(R.id.tv_title);
        subjectEditText = view.findViewById(R.id.et_edit_subject);
        classroomEditText = view.findViewById(R.id.et_edit_classroom);
        courseNameEditText = view.findViewById(R.id.et_edit_course_name);
        dayEditText = view.findViewById(R.id.et_edit_day);
        startTimeEditText = view.findViewById(R.id.et_edit_start_time);
        endTimeEditText = view.findViewById(R.id.et_edit_end_time);
        teacherEditText = view.findViewById(R.id.et_edit_teacher);
        tackleEditText = view.findViewById(R.id.et_edit_tackle);
        addConstraintLayout = view.findViewById(R.id.cl_add);
        editConstraintLayout = view.findViewById(R.id.cl_edit);

        getDialog().setCanceledOnTouchOutside(false);

        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        int KEEP_ALIVE_TIME = 1;
        TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();

        executorService = new ThreadPoolExecutor(NUMBER_OF_CORES,
                NUMBER_OF_CORES << 1,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                taskQueue);

        exitRunnable = new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(150);
                dismiss();
            }
        };

        if ("add".equals(getTag())) {
            titleTextView.setText("添加课程");
            addConstraintLayout.setVisibility(View.VISIBLE);
            editConstraintLayout.setVisibility(View.GONE);

            addCancelTextView = view.findViewById(R.id.tv_add_cancel);
            addConfirmTextView = view.findViewById(R.id.tv_add_confirm);


            addConfirmTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addConfirmTextView.setBackgroundColor(getResources().getColor(R.color.app_dialog_et_and_tv_color));
                    addConfirmTextView.setTextColor(Color.parseColor("#FFFFFF"));

                    editCourse(ADD_CONFIRM);
                }
            });

            addCancelTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addCancelTextView.setBackgroundColor(getResources().getColor(R.color.app_dialog_et_and_tv_color));
                    addCancelTextView.setTextColor(Color.parseColor("#FFFFFF"));

                    executorService.execute(exitRunnable);
                }
            });
        } else if ("edit".equals(getTag())) {
            titleTextView.setText("修改当前课程");
            addConstraintLayout.setVisibility(View.GONE);
            editConstraintLayout.setVisibility(View.VISIBLE);

            editCancelTextView = view.findViewById(R.id.tv_edit_cancle);
            editDeleteTextView = view.findViewById(R.id.tv_edit_delect);
            editSaveTextView = view.findViewById(R.id.tv_edit_save);

            subjectEditText.setText(showCourse.getSubject());
            classroomEditText.setText(showCourse.getClassroom());
            courseNameEditText.setText(showCourse.getCourseName());
            dayEditText.setText(showCourse.getDay());
            startTimeEditText.setText(showCourse.getClassStart());
            endTimeEditText.setText(showCourse.getClassEnd());
            teacherEditText.setText(showCourse.getTeacher());
            String tackle = showCourse.getTackle();
            if (tackle != null && !tackle.equals("")) {
                tackleEditText.setText(tackle);
            }

            editDeleteTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editDeleteTextView.setBackgroundColor(getResources().getColor(R.color.app_dialog_et_and_tv_color));
                    editDeleteTextView.setTextColor(Color.parseColor("#FFFFFF"));

                    onDialogInforCourse.editDialogInforCourse(false, null);
                    onDialogInforCourse = null;
                    executorService.execute(exitRunnable);
                }
            });

            editSaveTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editSaveTextView.setBackgroundColor(getResources().getColor(R.color.app_dialog_et_and_tv_color));
                    editSaveTextView.setTextColor(Color.parseColor("#FFFFFF"));

                    editCourse(EDIT_CONFIRM);
                }
            });

            editCancelTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editCancelTextView.setBackgroundColor(getResources().getColor(R.color.app_dialog_et_and_tv_color));
                    editCancelTextView.setTextColor(Color.parseColor("#FFFFFF"));
                    executorService.execute(exitRunnable);
                }
            });
        }

        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ADD_CONFIRM:
                        addConfirmTextView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        addConfirmTextView.setTextColor(getResources().getColor(R.color.app_dialog_et_and_tv_color));
                        break;

                    case EDIT_CONFIRM:
                        editSaveTextView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        editSaveTextView.setTextColor(getResources().getColor(R.color.app_dialog_et_and_tv_color));
                        break;

                    case EDIT_DELETE:
                        editDeleteTextView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        editDeleteTextView.setTextColor(getResources().getColor(R.color.app_dialog_et_and_tv_color));
                        break;
                }
            }
        };
        return view;
    }

    private void editCourse(final int type) {
        Runnable failureRunnable = new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(150);

                Message message = new Message();
                if (type == ADD_CONFIRM) {
                    message.what = ADD_CONFIRM;
                } else if (type == EDIT_CONFIRM) {
                    message.what = EDIT_CONFIRM;
                }

                handler.sendMessage(message);
            }
        };

        String subjectName = subjectEditText.getText().toString();
        String classroomName = classroomEditText.getText().toString();
        String courseName = courseNameEditText.getText().toString();
        String day = dayEditText.getText().toString();
        String startTime = startTimeEditText.getText().toString();
        String endTime = endTimeEditText.getText().toString();
        String teacher = teacherEditText.getText().toString();
        if (subjectName != null && !subjectName.equals("") &&
                classroomName != null && !classroomName.equals("") &&
                courseName != null && !courseName.equals("") &&
                day != null && !day.equals("") &&
                startTime != null && !startTime.equals("") &&
                endTime != null && !endTime.equals("") &&
                teacher != null && !teacher.equals("")) {

            SimpleDateFormat hourMinuteSimpleDateFormat = new SimpleDateFormat("HH:mm");
            try {
                Date startHourMinute = hourMinuteSimpleDateFormat.parse(startTime);
                Date endHourMinute = hourMinuteSimpleDateFormat.parse(endTime);
                if (startHourMinute.after(endHourMinute)) {
                    Toast.makeText(getActivity(), "开始时间是不能晚于结束时间的", Toast.LENGTH_SHORT).show();
                    executorService.execute(failureRunnable);
                } else {
                    SimpleDateFormat daySimpleDateFormat = new SimpleDateFormat("yyyyMMddHH:mm");
                    Date startDay = daySimpleDateFormat.parse(day + startTime);
                    Date nowDate = new Date(System.currentTimeMillis());
                    String dayString = daySimpleDateFormat.format(nowDate);
                    nowDate = daySimpleDateFormat.parse(dayString);

                    if (nowDate.after(startDay)) {
                        Toast.makeText(getActivity(), "设置的时间已经是过去式，我们要着眼未来"
                                , Toast.LENGTH_SHORT).show();
                        executorService.execute(failureRunnable);
                    } else {
                        if (type == ADD_CONFIRM) {
                            CurriculumDatebase dbHelper = CurriculumDatebase.getInstance(getActivity(), "curriculum.db"
                                    , null, 1);
                            SQLiteDatabase db = dbHelper.openDb();
                            Cursor cursor = db.query("tb_alarm",new String[]{"id"},"subject = ? and course_name " +
                                    "= ?",new String[] {subjectName,courseName},null,null,null);
                            if (cursor.moveToFirst()) {
                                Toast.makeText(getActivity(),subjectName + "的" + courseName + "课程已经被安排过了"
                                        ,Toast.LENGTH_SHORT).show();
                                executorService.execute(failureRunnable);
                                cursor.close();
                                dbHelper.closeDb(db);
                            } else {
                                Course course = new Course(subjectName, courseName, teacher, classroomName
                                        , tackleEditText.getText().toString(), day, startTime, endTime);
                                onDialogInforCourse.inputDialogInforCourse(course);
                                onDialogInforCourse = null;
                                cursor.close();
                                dbHelper.closeDb(db);
                                executorService.execute(exitRunnable);
                            }


                        }else if (type == EDIT_CONFIRM) {
                            Course course = new Course(subjectName, courseName, teacher, classroomName
                                    , tackleEditText.getText().toString(), day, startTime, endTime);
                            onDialogInforCourse.editDialogInforCourse(true, course);
                            onDialogInforCourse = null;
                            executorService.execute(exitRunnable);
                        }


                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            //必填项没有完成
            Toast.makeText(getActivity(), "信息不完整我记不住的", Toast.LENGTH_SHORT).show();
            executorService.execute(failureRunnable);
        }
    }

    public void setOnDialogInforCourse(OnDialogInforCourse onDialogInforCourse) {
        this.onDialogInforCourse = onDialogInforCourse;
    }


    @Override
    public void onResume() {
        super.onResume();
//        Log.d(TAG, "onResume: +++++++++");

        Calendar calendar;
        int showYear = 0;
        int showMonth = 0;
        int showDay = 0;
        int showHour = 0;
        int showMinute = 0;

        calendar = Calendar.getInstance();
        showYear = calendar.get(Calendar.YEAR);
        showMonth = calendar.get(calendar.MONTH);
        showDay = calendar.get(calendar.DAY_OF_MONTH);
        showHour = calendar.get(Calendar.HOUR_OF_DAY);
        showMinute = calendar.get(Calendar.MINUTE);

        final int finalShowYear = showYear;
        final int finalShowMonth = showMonth;
        final int finalShowDay = showDay;
        final int finalShowHour = showHour;
        final int finalShowMinute = showMinute;

        dayEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            StringBuilder builder = new StringBuilder();
                            builder.append(year);
                            month++;
                            if (month < 10) builder.append(0);
                            builder.append(month);
                            if (dayOfMonth < 10) builder.append(0);
                            builder.append(dayOfMonth);
                            dayEditText.setText(builder.toString());
                        }
                    }
                            , finalShowYear
                            , finalShowMonth
                            , finalShowDay).show();
                }
            }
        });

        dayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(year);
                        month++;
                        if (month < 10) builder.append(0);
                        builder.append(month);
                        if (dayOfMonth < 10) builder.append(0);
                        builder.append(dayOfMonth);
                        dayEditText.setText(builder.toString());
                    }
                }
                        , finalShowYear
                        , finalShowMonth
                        , finalShowDay).show();
            }
        });


        startTimeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            StringBuilder builder = new StringBuilder();
                            if (hourOfDay < 10) {
                                builder.append(0);
                            }
                            builder.append(hourOfDay);
                            builder.append(":");
                            if (minute < 10) {
                                builder.append(0);
                            }
                            builder.append(minute);
                            startTimeEditText.setText(builder.toString());
                        }
                    }
                            , finalShowHour
                            , finalShowMinute, true).show();
                }
            }
        });

        startTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        StringBuilder builder = new StringBuilder();
                        if (hourOfDay < 10) {
                            builder.append(0);
                        }
                        builder.append(hourOfDay);
                        builder.append(":");
                        if (minute < 10) {
                            builder.append(0);
                        }
                        builder.append(minute);
                        startTimeEditText.setText(builder.toString());
                    }
                }
                        , finalShowHour
                        , finalShowMinute, true).show();
            }
        });

        endTimeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            StringBuilder builder = new StringBuilder();
                            if (hourOfDay < 10) {
                                builder.append(0);
                            }
                            builder.append(hourOfDay);
                            builder.append(":");
                            if (minute < 10) {
                                builder.append(0);
                            }
                            builder.append(minute);
                            endTimeEditText.setText(builder.toString());
                        }
                    }
                            , finalShowHour
                            , finalShowMinute, true).show();
                }
            }
        });

        endTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        StringBuilder builder = new StringBuilder();
                        if (hourOfDay < 10) {
                            builder.append(0);
                        }
                        builder.append(hourOfDay);
                        builder.append(":");
                        if (minute < 10) {
                            builder.append(0);
                        }
                        builder.append(minute);
                        endTimeEditText.setText(builder.toString());
                    }
                }
                        , finalShowHour
                        , finalShowMinute, true).show();
            }
        });
    }
}
