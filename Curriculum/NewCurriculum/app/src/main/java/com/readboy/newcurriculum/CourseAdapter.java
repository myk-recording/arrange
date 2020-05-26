package com.readboy.newcurriculum;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.readboy.newcurriculum.Utils.SkinEngine;

import java.util.ArrayList;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.MyViewHolder> {
    private List<Course> courseList = new ArrayList<>();
    private Context context;
    private static final String TAG = "CourseAdapter";

    public CourseAdapter(List<Course> list) {
        courseList = list;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.app_recycle_item_card,parent,false);
        final MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    public interface onItemClickListener {
        void onClidk(View view,int pos,Course course);
    }
    private onItemClickListener mOnItemClickListener;

    public void setmOnItemClickListener(CourseAdapter.onItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final Course course = courseList.get(position);
        if (course.getSubject() != null) {
//            Log.d(TAG, "onBindViewHolder+++++++: " + position);
            holder.subjectTextView.setText(course.getSubject());
            holder.subjectTextView.setTextColor(SkinEngine.getInstance().getColor(R.color.app_subject_tv_color));
            holder.courseTextView.setText(course.getCourseName());
            holder.startTextView.setText(course.getClassStart());
            holder.startTextView.setTextColor(SkinEngine.getInstance().getColor(R.color.app_class_start_tv_color));
            holder.endTextView.setText(course.getClassEnd());
            holder.classroomTextView.setText(course.getClassroom());
            holder.teacherTextView.setText(course.getTeacher());

        }
        holder.courseCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onClidk(v,position,course);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder{
        CardView courseCardView;
        TextView subjectTextView;
        TextView courseTextView;
        TextView startTextView;
        TextView endTextView;
        TextView classroomTextView;
        TextView teacherTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            courseCardView = (CardView)itemView;
            subjectTextView = itemView.findViewById(R.id.tv_subject);
            courseTextView = itemView.findViewById(R.id.tv_course);
            startTextView = itemView.findViewById(R.id.tv_start);
            endTextView = itemView.findViewById(R.id.tv_end);
            classroomTextView = itemView.findViewById(R.id.tv_classroom);
            teacherTextView = itemView.findViewById(R.id.tv_teacher);
        }
    }
}
