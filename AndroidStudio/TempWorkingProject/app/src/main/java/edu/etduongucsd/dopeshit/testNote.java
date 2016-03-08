package edu.etduongucsd.dopeshit;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v4.app.DialogFragment;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//LectureList page
public class testNote extends AppCompatActivity {

    public static Professor currentProfessor;

    private static LectureListAdapter lecAdapter;
    public ListView lecListView;
    public static List<Lecture> lecList = HomeScreen.selectedProfessor.lectures;

    public static Lecture lecSel;
    public static String lecName;

    Button button;
    Button button2;

    public static int daySel;
    public static int monthSel;
    public static int yearSel;

    public static String daySelString = String.valueOf(daySel);
    public static String monthSelString = String.valueOf(monthSel);
    public static String yearSelString = String.valueOf(yearSel);

    static final int Dialog_ID = 0;

    final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_note);

        button = (Button) findViewById(R.id.addMyClassBut);
        button2 = (Button) findViewById(R.id.newLecBut);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.findViewById(R.id.toolbar_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(testNote.this, HomeScreen.class));
            }
        });
        toolbar.findViewById(R.id.toolbar_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(testNote.this, SettingsPage.class));
            }
        });

        currentProfessor = HomeScreen.selectedProfessor;
        TextView lecTitle = (TextView) findViewById(R.id.lectureTitle);
        lecTitle.setText(currentProfessor.getName());

        createLectureList();

        addToMyClassesOnClick();

        daySel = calendar.get(Calendar.DAY_OF_MONTH);
        monthSel = calendar.get(Calendar.MONTH);
        yearSel = calendar.get(Calendar.YEAR);

        newLecOnClick();
    }


    public void addToMyClassesOnClick(){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Professor newProf = HomeScreen.selectedProfessor;
                Course newCourse = newProf.parentCourse;
                if (UserProfile.myCourses.contains(newCourse)) {
                    Toast.makeText(testNote.this, HomeScreen.selectedDepart.getName() + " " + HomeScreen.selectedCourse.getName() + " is already in your list of classes!", Toast.LENGTH_LONG).show();
                } else {
                    UserProfile.myCourses.add(newCourse);
                    Toast.makeText(testNote.this, HomeScreen.selectedDepart.getName() + " " + HomeScreen.selectedCourse.getName() + " has been added to your classes!", Toast.LENGTH_LONG).show();
                }

                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);
            }
        });
    }

    //Adda Lecture
    public void newLecOnClick(){
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog(Dialog_ID);

                /*
                Lecture newLec = new Lecture(HomeScreen.selectedProfessor.dataBaseRef+HomeScreen.selectedProfessor.name+"/", HomeScreen.selectedProfessor.numberOfLectures + 1, monthSel, daySel);
                //System.out.println(monthSel + "/" + daySel);
                newLec.parentProfessor = HomeScreen.selectedProfessor;

                if(HomeScreen.selectedProfessor.lectures.contains(newLec)) {
                    Toast.makeText(testNote.this, monthSel + "/" + daySel + " Lecture is already available!", Toast.LENGTH_LONG).show();
                }
                else {
                    HomeScreen.selectedProfessor.lectures.add(newLec);
                    HomeScreen.selectedProfessor.numberOfLectures++;
                    newLec.addLectureToFirebase();

                    Intent intent = getIntent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    startActivity(intent);
                }*/


            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == Dialog_ID){
            return new DatePickerDialog(this, datePickListener, yearSel, monthSel, daySel);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            yearSel = year;
            monthSel = monthOfYear + 1;
            daySel = dayOfMonth;
            Toast.makeText(testNote.this, monthSel + "/" + daySel + " Lecture has been added!", Toast.LENGTH_LONG).show();

            Lecture newLec = new Lecture(HomeScreen.selectedProfessor.dataBaseRef+HomeScreen.selectedProfessor.name+"/", HomeScreen.selectedProfessor.numberOfLectures + 1, monthSel, daySel);
            //System.out.println(monthSel + "/" + daySel);
            newLec.parentProfessor = HomeScreen.selectedProfessor;

            if(HomeScreen.selectedProfessor.lectures.contains(newLec)) {
                Toast.makeText(testNote.this, monthSel + "/" + daySel + " Lecture is already available!", Toast.LENGTH_LONG).show();
            }
            else {
                HomeScreen.selectedProfessor.lectures.add(newLec);
                HomeScreen.selectedProfessor.numberOfLectures++;
                newLec.addLectureToFirebase();

                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);
            }
        }
    };


    //Create the list of courses that is displayed on the screen
    void createLectureList(){

        lecListView = (ListView)findViewById(R.id.lectureListView);
        lecAdapter = new LectureListAdapter(testNote.this, 0, lecList);
        lecListView.setAdapter(lecAdapter);
        lecListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                HomeScreen.selectedLecture = HomeScreen.selectedProfessor.lectures.get(position);
                lecSel = (Lecture) lecListView.getItemAtPosition(position);
                lecName = lecSel.toString();
                Intent selectedIntent = new Intent(testNote.this, MainActivity.class);
                startActivity(selectedIntent);

            }
        });
    }

}
