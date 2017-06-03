package com.mihailproductions.jobapplier;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mihailproductions.jobapplier.Handlers.DatabaseHandler;
import com.mihailproductions.jobapplier.model.Student;

import java.util.regex.Pattern;

public class AddStudent extends AppCompatActivity {
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText phone;
    private RadioGroup schoolRG;
    private Student student;
    private CheckBox cb[] = new CheckBox[4];
    private Switch sw;
    private DatabaseReference database;
    private DatabaseHandler databaseHandler;
    private Bundle extras;
    void submit() {
        int schoolIndex = schoolRG.indexOfChild(findViewById(schoolRG.getCheckedRadioButtonId()));
        //Switch is false -> Cancel current submission and go back to parent activity.
        if (!sw.isChecked()) {
            if(extras!=null)
                databaseHandler.deleteStudent(student);
            NavUtils.navigateUpFromSameTask(AddStudent.this);
            Toast.makeText(this, getResources().getString(R.string.canceled), Toast.LENGTH_SHORT).show();
        }//One or more fields are not filled
        else if (firstName == null || lastName == null || email == null || phone == null || schoolIndex == -1) {
            Toast.makeText(this, getResources().getString(R.string.incomplet), Toast.LENGTH_SHORT).show();
        }//No job experience
        else if (!cb[0].isChecked() && !cb[1].isChecked()&&!cb[2].isChecked()&&!cb[3].isChecked()) {
            Toast.makeText(this, getResources().getString(R.string.notqualified), Toast.LENGTH_SHORT).show();
        }
        else if(!Pattern.compile("^(.+)@(.+)$").matcher(email.getText().toString()).matches()){
            Toast.makeText(this, getResources().getString(R.string.invalidEmail), Toast.LENGTH_SHORT).show();
        }
        else {
            String skills="";
            for(int i=0;i<cb.length;i++)
                if(cb[i].isChecked())
                    skills=skills+getString(getResources().getIdentifier("cb"+i,"string",getApplicationContext().getPackageName()))+" ";
            //Add student
            if(extras==null){
                Student student = new Student(0, lastName.getText().toString(), firstName.getText().toString(),email.getText().toString(), ((RadioButton)schoolRG.getChildAt(schoolIndex)).getText().toString(),skills, Integer.parseInt(phone.getText().toString()) );
                databaseHandler.addStudent(student);
                Toast.makeText(this, getResources().getString(R.string.sent), Toast.LENGTH_SHORT).show();
                NavUtils.navigateUpFromSameTask(AddStudent.this);
                //Add remote
                database= FirebaseDatabase.getInstance().getReference("students");
                String studentId = database.push().getKey();
                database.child(studentId).setValue(student);
            }
            else{
                databaseHandler.updateStudent(new Student(student.getId(),  lastName.getText().toString(), firstName.getText().toString(),email.getText().toString(), ((RadioButton)schoolRG.getChildAt(schoolIndex)).getText().toString(),skills, Integer.parseInt(phone.getText().toString()) ));
                Toast.makeText(this, getResources().getString(R.string.updated), Toast.LENGTH_SHORT).show();
                NavUtils.navigateUpFromSameTask(AddStudent.this);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addstudent);
        Button submit = (Button) findViewById(R.id.submit);
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        schoolRG = (RadioGroup) findViewById(R.id.schoolRG);
        for(int i=0;i<cb.length;i++)
            cb[i]= (CheckBox) findViewById(getResources().getIdentifier("cb"+i, "id", getApplicationContext().getPackageName()));
        sw = (Switch) findViewById(R.id.sw);
        databaseHandler = DatabaseHandler.getInstance(this);
        if(getIntent().getExtras()!=null)
        {
            extras = getIntent().getExtras();
            //Start edit student if exists
            if (extras.getInt("edit")>0) {
                student = databaseHandler.getStudent(extras.getInt("edit"));
                firstName.setText(student.getFirstName());
                lastName.setText(student.getLastName());
                email.setText(student.getEmail());
                phone.setText(student.getPhone()+"");
                for(int i=0;i<schoolRG.getChildCount();i++)
                    if(((RadioButton)schoolRG.getChildAt(i)).getText().toString().equals(student.getCollege()))
                    {
                        ((RadioButton)schoolRG.getChildAt(i)).setChecked(true);
                        break;
                    }
                for(int i=0;i<cb.length;i++)
                    if(student.getSkills().contains(cb[i].getText().toString()))
                        cb[i].setChecked(true);
                sw.setChecked(true);
            }//Show student
            else if(extras.getInt("show")>0){
                student = databaseHandler.getStudent(extras.getInt("show"));
                RelativeLayout root = (RelativeLayout) findViewById(R.id.addStudentRoot);
                firstName.setVisibility(View.GONE);
                lastName.setVisibility(View.GONE);
                email.setVisibility(View.GONE);
                phone.setVisibility(View.GONE);
                schoolRG.setVisibility(View.GONE);
                findViewById(R.id.confirmationSubtitle).setVisibility(View.GONE);
                for(int i=0;i<cb.length;i++)
                    cb[i].setVisibility(View.GONE);
                sw.setVisibility(View.GONE);
                findViewById(R.id.submit).setVisibility(View.GONE);
                //Populate
                int margins = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.margin), getResources().getDisplayMetrics());
                int margins2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.margin3), getResources().getDisplayMetrics());
                // #1 info
                TextView info = new TextView(this);
                info.setText(student.getLastName()+"\n\n"+student.getFirstName()+"\n"+student.getEmail()+"\n\n"+student.getPhone());
                RelativeLayout.LayoutParams informationsParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                informationsParams.addRule(RelativeLayout.RIGHT_OF, R.id.profilePic);
                info.setLayoutParams(informationsParams);
                informationsParams.setMargins(margins,0,0,0);
                info.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.showSize));
                root.addView(info, informationsParams);
                //#2 University
                LinearLayout linearRoot = (LinearLayout)findViewById(R.id.linearContainer);
                TextView university = new TextView(this);
                LinearLayout.LayoutParams universityParams =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                university.setLayoutParams(universityParams);
                universityParams.setMargins(0,margins2,0,0);
                university.setText(student.getCollege());
                university.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.showSize));
                linearRoot.addView(university,3);
                //#3 Skills
                TextView uni = new TextView(this);
                LinearLayout.LayoutParams skillsParams =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                uni.setLayoutParams(skillsParams);
                skillsParams.setMargins(0,margins2,0,0);
                uni.setText(student.getSkills());
                uni.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.showSize));
                linearRoot.addView(uni,5);
            }
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }
}
