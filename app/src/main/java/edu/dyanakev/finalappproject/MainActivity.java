package edu.dyanakev.finalappproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // Adding buttons
    Button btnNorth, btnWest, btnEast, btnSouth, FB;

    // experimental values for hi and lo magnitude limits
    private final double NORTH_MOVE_FORWARD = 8;     // upper mag limit
    private final double NORTH_MOVE_BACKWARD = 5;      // lower mag limit

    private final double SOUTH_MOVE_FORWARD =  1;     // upper mag limit
    private final double SOUTH_MOVE_BACKWARD = 4;      // lower mag limit

    private final double EAST_MOVE_FORWARD = 1;     // upper mag limit
    private final double EAST_MOVE_BACKWARD = 0;      // lower mag limit

    private final double WEST_MOVE_FORWARD = -1;     // upper mag limit
    private final double WEST_MOVE_BACKWARD = 0;      // lower mag limit

    boolean highLimitNorth = false;      // detect high limit
    boolean highLimitSouth = false;      // detect high limit
    boolean highLimitEast = false;      // detect high limit
    boolean highLimitWest = false;      // detect high limit

    int counterNorth = 0;
    int counterSouth = 0;
    int counterEast = 0;
    int counterWest = 0;

    int score, round, increase;

    TextView tvx, tvy, tvz, tvNorth, tvSouth, tvEast, tvWest;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    int sequenceCount = 4, n = 0;
    int[] gameSequence = new int[120];
    int arrayIndex = 0;

    private final int NORTH = 1;
    private final int WEST = 2;
    private final int SOUTH = 3;
    private final int EAST = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnEast = findViewById(R.id.btnEast);
        btnWest = findViewById(R.id.btnWest);
        btnNorth = findViewById(R.id.btnNorth);
        btnSouth = findViewById(R.id.btnSouth);


        tvx = findViewById(R.id.tvX);
        tvy = findViewById(R.id.tvY);
        tvz = findViewById(R.id.tvZ);
        tvNorth = findViewById(R.id.tvNorth);
        tvSouth = findViewById(R.id.tvSouth);
        tvWest  = findViewById(R.id.tvWest);
        tvEast  = findViewById(R.id.tvEast);

        // we are going to use the sensor service
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        DatabaseHandler db = new DatabaseHandler(this);

        db.emptyHiScores();     // empty table if required

        // Inserting hi scores
        Log.i("Insert: ", "Inserting ..");
        db.addHiScore(new HiScore("23/1/2016", "Paul P", 1));
        db.addHiScore(new HiScore("20/12/2010", "Ridley", 4));
        db.addHiScore(new HiScore("20/01/2001", "Ganon", 6));
        db.addHiScore(new HiScore("06/10/2018", "Sephiroth", 7));
        db.addHiScore(new HiScore("01 DEC 2020", "Darth Vader", 111));
        db.addHiScore(new HiScore("02 DEC 2020", "Gandalf", 132));

        // Reading all scores
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

        Log.i("divider", "====================");

        HiScore singleScore = db.getHiScore(5);
        Log.i("High Score 5 is by ", singleScore.getPlayer_name() + " with a score of " +
                singleScore.getScore());

        Log.i("divider", "====================");

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
        Log.i("divider", "====================");

        HiScore hiScore = top5HiScores.get(top5HiScores.size() - 5);
        // hiScore contains the 5th highest score
        Log.i("fifth Highest score: ", String.valueOf(hiScore.getScore()) );

        Log.i("divider", "====================");

        // Calling SQL statement
        top5HiScores = db.getTopFiveScores();

        for (HiScore hs : top5HiScores) {
            String log =
                    "Id: " + hs.getScore_id() +
                            ", Date: " + hs.getGame_date() +
                            " , Player: " + hs.getPlayer_name() +
                            " , Score: " + hs.getScore();

            // Writing HiScore to log
            Log.i("Score: ", log);
        }

        score = getIntent().getIntExtra("score", 0);
        round = getIntent().getIntExtra("round", 1);
        increase = getIntent().getIntExtra("increase", 2);

    }

    /*
     * When the app is brought to the foreground - using app on screen
     */
    protected void onResume() {
        super.onResume();
        // turn on the sensor
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    /*
     * App running but not on screen - in the background
     */
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);    // turn off listener to save power
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        tvx.setText(String.valueOf(x));
        tvy.setText(String.valueOf(y));
        tvz.setText(String.valueOf(z));

        // North Movement
        if ((x > NORTH_MOVE_FORWARD && z > 0) && (highLimitNorth == false)) {
            highLimitNorth = true;
        }
        if ((x < NORTH_MOVE_BACKWARD && z > 0) && (highLimitNorth == true)) {
            // we have a tilt to the NORTH
            counterNorth++;
            tvNorth.setText(String.valueOf(counterNorth));
            highLimitNorth = false;

            Handler handler = new Handler();
            Runnable r = new Runnable() {
                public void run() {

                    btnNorth.setPressed(true);
                    btnNorth.invalidate();
                    btnNorth.performClick();
                    Handler handler1 = new Handler();
                    Runnable r1 = new Runnable() {
                        public void run() {
                            btnNorth.setPressed(false);
                            btnNorth.invalidate();
                        }
                    };
                    handler1.postDelayed(r1, 600);

                } // end runnable
            };
            handler.postDelayed(r, 600);


        }

        // South Movement
        if ((x < SOUTH_MOVE_FORWARD && z < 0) && (highLimitSouth == false)) {
            highLimitSouth = true;
        }
        if ((x > SOUTH_MOVE_BACKWARD && z < 0) && (highLimitSouth == true)) {
            // we have a tilt to the SOUTH
            counterSouth++;
            tvSouth.setText(String.valueOf(counterSouth));
            highLimitSouth = false;


            Handler handler = new Handler();
            Runnable r = new Runnable() {
                public void run() {

                    btnSouth.setPressed(true);
                    btnSouth.invalidate();
                    btnSouth.performClick();
                    Handler handler1 = new Handler();
                    Runnable r1 = new Runnable() {
                        public void run() {
                            btnSouth.setPressed(false);
                            btnSouth.invalidate();
                        }
                    };
                    handler1.postDelayed(r1, 600);

                } // end runnable
            };
            handler.postDelayed(r, 600);


        }

        // East Movement
        if (y > EAST_MOVE_FORWARD && highLimitEast == false) {
            highLimitEast = true;
        }
        if (y < EAST_MOVE_BACKWARD && highLimitEast == true) {
            // we have a tilt to the EAST
            counterEast++;
            tvEast.setText(String.valueOf(counterEast));
            highLimitEast = false;


            Handler handler = new Handler();
            Runnable r = new Runnable() {
                public void run() {

                    btnEast.setPressed(true);
                    btnEast.invalidate();
                    btnEast.performClick();
                    Handler handler1 = new Handler();
                    Runnable r1 = new Runnable() {
                        public void run() {
                            btnEast.setPressed(false);
                            btnEast.invalidate();
                        }
                    };
                    handler1.postDelayed(r1, 600);

                } // end runnable
            };
            handler.postDelayed(r, 600);


        }

        // West Movement
        if (y < WEST_MOVE_FORWARD && highLimitWest == false) {
            highLimitWest = true;
        }
        if (y > WEST_MOVE_BACKWARD && highLimitWest == true) {
            // we have a tilt to the WEST
            counterWest++;
            tvWest.setText(String.valueOf(counterWest));
            highLimitWest = false;


            Handler handler = new Handler();
            Runnable r = new Runnable() {
                public void run() {

                    btnWest.setPressed(true);
                    btnWest.invalidate();
                    btnWest.performClick();
                    Handler handler1 = new Handler();
                    Runnable r1 = new Runnable() {
                        public void run() {
                            btnWest.setPressed(false);
                            btnWest.invalidate();
                        }
                    };
                    handler1.postDelayed(r1, 600);

                } // end runnable
            };
            handler.postDelayed(r, 600);


        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not used
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    CountDownTimer cdtRound1 = new CountDownTimer(6000,  1500) {

        public void onTick(long millisUntilFinished) {
            //mTextField.setText("seconds remaining: " + millisUntilFinished / 1500);
            oneButton();
            //here you can have your logic to set text to edittext
        }

        @Override
        public void onFinish() {

            for (int i = 0; i< arrayIndex; i++)
                Log.d("game sequence", String.valueOf(gameSequence[i]));
            // start next activity

            // put the sequence into the next activity
            Intent i = new Intent(MainActivity.this, GameActivity.class);
            i.putExtra("sequence", gameSequence);
            i.putExtra("round", round);
            i.putExtra("score", score);
            i.putExtra("increase", increase);
            startActivity(i);

            // start the next activity
        }
    };
    CountDownTimer cdtRound2 = new CountDownTimer(9000,  1500) {

        public void onTick(long millisUntilFinished) {
            //mTextField.setText("seconds remaining: " + millisUntilFinished / 1500);
            oneButton();
            //here you can have your logic to set text to edittext
        }

        @Override
        public void onFinish() {

            for (int i = 0; i< arrayIndex; i++)
                Log.d("game sequence", String.valueOf(gameSequence[i]));
            // start next activity

            // put the sequence into the next activity
            Intent i = new Intent(MainActivity.this, GameActivity.class);
            i.putExtra("sequence", gameSequence);
            i.putExtra("round", round);
            i.putExtra("score", score);
            i.putExtra("increase", increase);
            startActivity(i);

            // start the next activity
        }
    };
    CountDownTimer cdtRound3 = new CountDownTimer(12000,  1500) {

        public void onTick(long millisUntilFinished) {
            //mTextField.setText("seconds remaining: " + millisUntilFinished / 1500);
            oneButton();
            //here you can have your logic to set text to edittext
        }

        @Override
        public void onFinish() {

            for (int i = 0; i< arrayIndex; i++)
                Log.d("game sequence", String.valueOf(gameSequence[i]));
            // start next activity

            // put the sequence into the next activity
            Intent i = new Intent(MainActivity.this, GameActivity.class);
            i.putExtra("sequence", gameSequence);
            i.putExtra("round", round);
            i.putExtra("score", score);
            i.putExtra("increase", increase);
            startActivity(i);

            // start the next activity
        }
    };
    CountDownTimer cdtRound4 = new CountDownTimer(15000,  1500) {

        public void onTick(long millisUntilFinished) {
            //mTextField.setText("seconds remaining: " + millisUntilFinished / 1500);
            oneButton();
            //here you can have your logic to set text to edittext
        }

        @Override
        public void onFinish() {

            for (int i = 0; i< arrayIndex; i++)
                Log.d("game sequence", String.valueOf(gameSequence[i]));
            // start next activity

            // put the sequence into the next activity
            Intent i = new Intent(MainActivity.this, GameActivity.class);
            i.putExtra("sequence", gameSequence);
            i.putExtra("round", round);
            i.putExtra("score", score);
            i.putExtra("increase", increase);
            startActivity(i);

            // start the next activity
        }
    };
    CountDownTimer cdtRound5 = new CountDownTimer(18000,  1500) {

        public void onTick(long millisUntilFinished) {
            //mTextField.setText("seconds remaining: " + millisUntilFinished / 1500);
            oneButton();
            //here you can have your logic to set text to edittext
        }

        @Override
        public void onFinish() {

            for (int i = 0; i< arrayIndex; i++)
                Log.d("game sequence", String.valueOf(gameSequence[i]));
            // start next activity

            // put the sequence into the next activity
            Intent i = new Intent(MainActivity.this, GameActivity.class);
            i.putExtra("sequence", gameSequence);
            i.putExtra("round", round);
            i.putExtra("score", score);
            i.putExtra("increase", increase);
            startActivity(i);

            // start the next activity
        }
    };

        public void doPlay(View view) {

            switch  (round)
            {
                case(1):
                    cdtRound1.start();
                    break;
                case(2):
                    cdtRound2.start();
                    break;
                case(3):
                    cdtRound3.start();
                    break;
                case(4):
                    cdtRound4.start();
                    break;
                case(5):
                    cdtRound5.start();
                    break;
            }
        }

        private void oneButton() {
            n = getRandom(sequenceCount);


            switch (n) {
                case 1:
                    flashButton(btnNorth);
                    gameSequence[arrayIndex++] = NORTH;
                    break;
                case 2:
                    flashButton(btnWest);
                    gameSequence[arrayIndex++] = WEST;
                    break;
                case 3:
                    flashButton(btnSouth);
                    gameSequence[arrayIndex++] = SOUTH;
                    break;
                case 4:
                    flashButton(btnEast);
                    gameSequence[arrayIndex++] = EAST;
                    break;
                default:
                    break;
            }   // end switch
        }

        // return a number between 1 and maxValue
        private int getRandom(int maxValue) {
            return ((int) ((Math.random() * maxValue) + 1));
        }

        private void flashButton(Button button) {
            FB = button;
            Handler handler = new Handler();
            Runnable r = new Runnable() {
                public void run() {

                    FB.setPressed(true);
                    FB.invalidate();
                    FB.performClick();
                    Handler handler1 = new Handler();
                    Runnable r1 = new Runnable() {
                        public void run() {
                            FB.setPressed(false);
                            FB.invalidate();
                        }
                    };
                    handler1.postDelayed(r1, 600);

                } // end runnable
            };
            handler.postDelayed(r, 600);
        }
    };