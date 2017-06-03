package com.mihailproductions.jobapplier;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mihailproductions.jobapplier.Handlers.DatabaseHandler;
import com.mihailproductions.jobapplier.adapter.StudentAdapter;
import com.mihailproductions.jobapplier.model.Student;

import java.util.ArrayList;
import java.util.List;

import static com.mihailproductions.jobapplier.R.string.noEntries;

public class StudentsList extends AppCompatActivity {
    private ListView mStudentsListView;

    private List<Student> mStudents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentslist);
        TextView noEntriesTV = (TextView)findViewById(R.id.noEntries);
        noEntriesTV.setVisibility(View.GONE);
        Button addStudent = (Button) findViewById(R.id.addStudent);
        mStudentsListView = (ListView) findViewById(R.id.studentsList);
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance(this);
        if(databaseHandler.getStudentsCount()>0)
        {
            mStudents = new ArrayList<>();
            mStudents=databaseHandler.getAllStudents();
            mStudentsListView.setAdapter(new StudentAdapter(mStudents, StudentsList.this));
        }
        else {
            findViewById(R.id.studentsList).setVisibility(View.GONE);
            noEntriesTV.setVisibility(View.VISIBLE);
            noEntriesTV.setText(getResources().getString(noEntries));
             }

        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentsList.this, AddStudent.class));
            }
        });
    }
}
