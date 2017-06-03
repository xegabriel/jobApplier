package com.mihailproductions.jobapplier.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mihailproductions.jobapplier.AddStudent;
import com.mihailproductions.jobapplier.R;
import com.mihailproductions.jobapplier.StudentsList;
import com.mihailproductions.jobapplier.model.Student;

import java.util.List;

public class StudentAdapter extends BaseAdapter {
    private List<Student> mStudents;
    private Context mContext;
    private static final int RECORD_REQUEST_CODE = 1;
    private static final String PHONE_PERMISSION = android.Manifest.permission.CALL_PHONE;
    public StudentAdapter(List<Student> mStudents, Context mContext) {
        this.mStudents = mStudents;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return (mStudents == null) ? 0 : mStudents.size();
    }

    @Override
    public Object getItem(int position) {
        return (mStudents == null) ? null : mStudents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        final Student student = (Student) getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.student_listitem, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.mName.setText(student.getLastName() + " " + student.getFirstName());
        viewHolder.mCollege.setText(student.getCollege());
        viewHolder.mProfile.setImageResource(R.drawable.user_profile_icon);
        viewHolder.mPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(mContext, PHONE_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((StudentsList)mContext, new String[]{PHONE_PERMISSION}, RECORD_REQUEST_CODE);
                }
                else{
                    mContext.startActivity(new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + student.getPhone())));
                }
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddStudent.class);
                intent.putExtra("show",student.getId());
                mContext.startActivity(intent);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(mContext, AddStudent.class);
                intent.putExtra("edit",student.getId());
                mContext.startActivity(intent);
                return false;
            }
        });
        return view;
    }

    class ViewHolder {
        protected TextView mName;
        protected TextView mCollege;
        protected ImageView mProfile;
        protected ImageView mPhone;

        public ViewHolder(View view) {
            mName = (TextView) view.findViewById(R.id.tv_name);
            mCollege = (TextView) view.findViewById(R.id.tv_college);
            mProfile = (ImageView) view.findViewById(R.id.iv_knowledge);
            mPhone = (ImageView) view.findViewById(R.id.iv_phone);
        }
    }
}