package edu.dyanakev.finalappproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class HiScores extends AppCompatActivity {

    ListView listView;
    private DatabaseHandler newDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hi_scores);

        listView = findViewById(R.id.lvDatabase);

        newDb = new DatabaseHandler(this);


        // Reads out all the scores
        Log.i("Reading: ", "Reading all scores..");
        List<HiScore> hiScores = newDb.getAllHiScores();

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

        HiScore singleScore = newDb.getHiScore(5);
        Log.i("High Score 5 is by ", singleScore.getPlayer_name() + " with a score of " +
                singleScore.getScore());

        Log.i("divider", "========================================");

        // Calling SQL statement
        List<HiScore> top5HiScores = newDb.getTopFiveScores();
        for (HiScore hs : top5HiScores) {
            String log =
                    "Id: " + hs.getScore_id() +
                            ", Date: " + hs.getGame_date() +
                            " , Player: " + hs.getPlayer_name() +
                            " , Score: " + hs.getScore();

            // Writing HiScore to log
            Log.i("Score: ", log);
        }
        Log.i("divider", "========================================");

        HiScore hiScore = top5HiScores.get(top5HiScores.size() - 1);
        // hiScore contains the 5th highest score
        Log.i("fifth Highest score: ", String.valueOf(hiScore.getScore()) );

        Log.i("divider", "========================================");

        // Calling SQL statement
        top5HiScores = newDb.getTopFiveScores();
        List<String> scoresStr;
        scoresStr = new ArrayList<>();

        int j = 1;

        for (HiScore hs : top5HiScores) {
            String log =
                    "Id: " + hs.getScore_id() +
                            ", Date: " + hs.getGame_date() +
                            " , Player: " + hs.getPlayer_name() +
                            " , Score: " + hs.getScore();

            // This scores the score in a String Array
            scoresStr.add(j++ + " : " +
                    hs.getPlayer_name() + "\t" +
                    hs.getScore());

            // Writing HiScore to log
            Log.i("Score: ", log);
            Log.i("divider", "Scores in list <>");
            for (String ss : scoresStr){
                Log.i("Score", ss);
            }
            ArrayAdapter<String> itemsAdapter =
                    new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scoresStr);
            listView.setAdapter(itemsAdapter);
        }
    }

    // Puts you back on the main activity to start again
    public void onRestart(View view) {
        Intent intent = new Intent(view.getContext(), MainActivity.class);

        startActivity(intent);
    }
}