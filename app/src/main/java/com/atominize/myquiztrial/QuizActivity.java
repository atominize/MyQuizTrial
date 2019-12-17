package com.atominize.myquiztrial;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.atominize.myquiztrial.models.Question;

import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView mQuestion, mScore, mQuestionCount, mCountDown;
    private RadioGroup radioGroup;
    private RadioButton rButton1, rButton2, rButton3;
    private Button mConfirmNext;

    private List<Question> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        initViews();

        QuizDbHelper dbHelper = new QuizDbHelper(this);
        questionList = dbHelper.getAllQuestions();
    }

    private void initViews() {
        mQuestion = findViewById(R.id.tvQuestion);
        mScore = findViewById(R.id.tvScore);
        mQuestionCount = findViewById(R.id.tvQuestionCount);
        mCountDown = findViewById(R.id.tvCountDown);
        radioGroup = findViewById(R.id.rgOptions);
        rButton1 = findViewById(R.id.rbOption1);
        rButton2 = findViewById(R.id.rbOption2);
        rButton3 = findViewById(R.id.rbOption3);
        mConfirmNext = findViewById(R.id.btConfirmNext);
    }
}
