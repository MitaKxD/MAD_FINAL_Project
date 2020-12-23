package edu.dyanakev.finalappproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GameOver extends AppCompatActivity {

    int score, round;
    TextView tvScore, tvRound;

    public DatabaseHandler db;
    public EditText etScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        etScore = findViewById(R.id.etScore);


        tvScore = findViewById(R.id.tvScore);
        tvRound = findViewById(R.id.tvRound);

        score = getIntent().getIntExtra("score", 0);
        round = getIntent().getIntExtra("round",0);

        tvScore.setText(String.valueOf(score));
        tvRound.setText(String.valueOf(round));


        db = new DatabaseHandler(this);
        db.emptyHiScores();
        Data();
        Log.i("Reading: ", "Reading all scores..");
        List<HiScore> hiScores = db.getAllHiScores();


        for (HiScore hs : hiScores) {
            String log =
                    "Id: " + hs.getScore_id() +
                            ", Date: " + hs.getGame_date() +
                            " , Player: " + hs.getPlayer_name() +
                            " , Score: " + hs.getScore();

            // Writing HiScore to log
            Log.i("Score: ", log);
        }

        Log.i("divider", "========================================");

        HiScore singleScore = db.getHiScore(5);
        Log.i("High Score 5 is by ", singleScore.getPlayer_name() + " with a score of " + singleScore.getScore());

        Log.i("divider", "========================================");

        // Calling SQL statement
        List<HiScore> top5HiScores = db.getTopFiveScores();
        for (HiScore hs : top5HiScores) {
            String log =
                    "Id: " + hs.getScore_id() +
                            ", Date: " + hs.getGame_date() +
                            " , Player: " + hs.getPlayer_name() +
                            " , Score: " + hs.getScore();
            // Writing HiScore to log
            Log.i("Score: ", log);
        }

        HiScore lastScore = top5HiScores.get(top5HiScores.size() - 1);
        if (score > lastScore.score) {
            Toast.makeText(this,"You Won!!! Enter your Name!!", Toast.LENGTH_LONG).show();
        }

    }

    public void Data(){
        // Inserting hi scores
        Log.i("Insert: ", "Inserting Scores...");
        db.addHiScore(new HiScore("23/1/2016", "Paul P", 1));
        db.addHiScore(new HiScore("20/12/2010", "Ridley", 4));
        db.addHiScore(new HiScore("20/01/2001", "Ganon", 6));
        db.addHiScore(new HiScore("06/10/2018", "Sephiroth", 7));
        db.addHiScore(new HiScore("14/11/2020", "Darth Vader", 111));
        db.addHiScore(new HiScore("04/02/2020", "Gandalf", 132));
    }

    public void doSubmit(View view) {
        List<HiScore> top5HiScores = db.getTopFiveScores();
        HiScore lastScore = top5HiScores.get(top5HiScores.size() - 1);

        if(score > lastScore.score && etScore.getText().toString() != ""){
            String userName = etScore.getText().toString();
            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            db.addHiScore(new HiScore(date, userName, score));
            top5HiScores = db.getTopFiveScores();
            for (HiScore hs : top5HiScores) {
                String log =
                        "Id: " + hs.getScore_id() +
                                " , Player: " + hs.getPlayer_name() +
                                " , Score: " + hs.getScore();

                // Writing HiScore to log
                Log.i("Score: ", log);
            }
        }
        else{
            Toast.makeText(this,"Your Score isn't High Enough",Toast.LENGTH_SHORT).show();
        }

        onHighScore(view);
    }

    public void onHighScore(View view) {
        Intent intent = new Intent(view.getContext(), HiScores.class);

        startActivity(intent);
        finish();
    }

    public void onRestart(View view) {
        Intent in = new Intent(view.getContext(), MainActivity.class);

        startActivity(in);
    }
}