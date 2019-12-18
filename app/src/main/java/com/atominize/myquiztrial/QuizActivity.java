package com.atominize.myquiztrial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.atominize.myquiztrial.models.Question;

import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView mQuestion, mScore, mQuestionCount, mCountDown;
    private RadioGroup radioGroup;
    private RadioButton rButton1, rButton2, rButton3;
    private Button mConfirmNext;

    private List<Question> questionList;
    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;

    private int score;
    private boolean answered;

    private ColorStateList textColourDefaultRb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        initViews();

        textColourDefaultRb = rButton1.getTextColors();

        QuizDbHelper dbHelper = new QuizDbHelper(this);
        questionList = dbHelper.getAllQuestions();

        questionCountTotal = questionList.size();
        Collections.shuffle(questionList);

        showNextQuestions();

        mConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!answered) {
                    if (rButton1.isChecked() || rButton2.isChecked() || rButton3.isChecked()) {
                        checkAnswer();
                    } else {
                        Toast.makeText(QuizActivity.this, "Please select an answer",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showNextQuestions();
                }
            }
        });
    }

    private void checkAnswer() {
        answered = true;

        RadioButton radioButtonSelected = findViewById(radioGroup.getCheckedRadioButtonId());
        int answerNumber = radioGroup.indexOfChild(radioButtonSelected) + 1;

        if (answerNumber == currentQuestion.getAnswerNumber()) {
            score++;
            mScore.setText("Score: " + score);
        }

        showSolution();
    }

    private void showSolution() {
        rButton1.setTextColor(Color.RED);
        rButton2.setTextColor(Color.RED);
        rButton3.setTextColor(Color.RED);

        switch (currentQuestion.getAnswerNumber()) {
            case 1:
                rButton1.setTextColor(Color.GREEN);
                break;
            case 2:
                rButton2.setTextColor(Color.GREEN);
                break;
            case 3:
                rButton3.setTextColor(Color.GREEN);
                break;
        }

        if (questionCounter < questionCountTotal) {
            mConfirmNext.setText("Next");
        } else {
            mConfirmNext.setText("Finish");
        }
    }

    private void showNextQuestions() {
        rButton1.setTextColor(textColourDefaultRb);
        rButton2.setTextColor(textColourDefaultRb);
        rButton3.setTextColor(textColourDefaultRb);
        radioGroup.clearCheck();

        if (questionCounter < questionCountTotal) {
            currentQuestion = questionList.get(questionCounter);

            mQuestion.setText(currentQuestion.getQuestion());
            rButton1.setText(currentQuestion.getOption1());
            rButton2.setText(currentQuestion.getOption2());
            rButton3.setText(currentQuestion.getOption3());

            questionCounter++;
            mQuestionCount.setText("Question: " + questionCounter + "/" + questionCountTotal);
            answered = false;
            mConfirmNext.setText("Confirm");
        }
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
