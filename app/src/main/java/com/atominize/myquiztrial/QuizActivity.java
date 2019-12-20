package com.atominize.myquiztrial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.atominize.myquiztrial.models.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    public static final String EXTRA_SCORE = "extraScore";
    public static final long COUNTDOWN_IN_MILLIS = 30000;

    public static final String FREEZE_SCORE = "freezeScore";
    public static final String FREEZE_QUESTION_COUNT = "freezeQuestionCount";
    public static final String FREEZE_MILLIS_LEFT = "freezeMillisLeft";
    public static final String FREEZE_ANSWERED = "freezeAnswered";
    public static final String FREEZE_QUESTION_LIST = "freezeQuestionList";

    private TextView mQuestion, mScore, mQuestionCount, mCountDown;
    private RadioGroup radioGroup;
    private RadioButton rButton1, rButton2, rButton3;
    private Button mConfirmNext;

    private ArrayList<Question> questionList;
    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;

    private int score;
    private boolean answered;
    private long backPressedTime;
    private long timeLeftInMillis;

    private ColorStateList textColourDefaultRb;
    private ColorStateList mColorCountdown;

    private CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        initViews();

        textColourDefaultRb = rButton1.getTextColors();
        mColorCountdown = mCountDown.getTextColors();

        if (savedInstanceState == null) {
            QuizDbHelper dbHelper = new QuizDbHelper(this);
            questionList = dbHelper.getAllQuestions();

            questionCountTotal = questionList.size();
            Collections.shuffle(questionList);

            showNextQuestions();
        } else {
            questionList = savedInstanceState.getParcelableArrayList(FREEZE_QUESTION_LIST);
//            questionCountTotal = questionList.size();
            questionCounter = savedInstanceState.getInt(FREEZE_QUESTION_COUNT);
//            currentQuestion = questionList.get(questionCounter - 1);
            score = savedInstanceState.getInt(FREEZE_SCORE);
            timeLeftInMillis = savedInstanceState.getLong(FREEZE_MILLIS_LEFT);
            answered = savedInstanceState.getBoolean(FREEZE_ANSWERED);

            if (!answered) {
                startCountDown();
            } else {
                updateCountDownTextView();
                showSolution();
            }
        }

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

        countDownTimer.cancel();

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
                mQuestion.setText("Answer A is correct");
                break;
            case 2:
                rButton2.setTextColor(Color.GREEN);
                mQuestion.setText("Answer B is correct");
                break;
            case 3:
                rButton3.setTextColor(Color.GREEN);
                mQuestion.setText("Answer C is correct");
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

            timeLeftInMillis = COUNTDOWN_IN_MILLIS;
            startCountDown();
        } else {
            finishQuiz();
        }
    }

    private void startCountDown() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMillis = l;
                updateCountDownTextView();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountDownTextView();
                checkAnswer();
            }
        }.start();
    }

    private void updateCountDownTextView() {
        int minute = (int) ((timeLeftInMillis / 1000) / 60);
        int seconds = (int) ((timeLeftInMillis / 1000) % 60);

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minute, seconds);

        mCountDown.setText(timeFormatted);

        if (timeLeftInMillis < 10000) {
            mCountDown.setTextColor(Color.RED);
        } else {
            mCountDown.setTextColor(mColorCountdown);
        }
    }

    private void finishQuiz() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_SCORE, score);
        setResult(RESULT_OK, intent);
        finish();
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

    @Override
    public void onBackPressed() {
        backPressedTime = System.currentTimeMillis();
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finishQuiz();
        } else {
            Toast.makeText(this, "Press back again to finish", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy()    {
        super.onDestroy();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putInt(FREEZE_SCORE, score);
        outState.putInt(FREEZE_QUESTION_COUNT, questionCounter);
        outState.putLong(FREEZE_MILLIS_LEFT, timeLeftInMillis);
        outState.putBoolean(FREEZE_ANSWERED, answered);
        outState.putParcelableArrayList(FREEZE_QUESTION_LIST, questionList);
    }
}
