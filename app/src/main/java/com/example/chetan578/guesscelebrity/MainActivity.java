package com.example.chetan578.guesscelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ImageView image;
    ArrayList<String> celebUrls = new ArrayList<>();
    ArrayList<String> celebNames = new ArrayList<>();
    Button button1,button2,button3,button4;
    int locationOfCorrectAnswer;
    String[] answers=new String[4];
    int chosenCeleb=0;
    int count =0;
    int score =0;
    TextView questionNumber,finalScore;
    Bitmap celebImage;
    boolean gameIsActive =true ;
    RelativeLayout gameLayout;


    public void chosenCeleb(View view ) {
        if (gameIsActive) {
            if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))) {
                Toast.makeText(this, "CORRECT!", Toast.LENGTH_SHORT).show();
                score++;
            } else {
                Toast.makeText(this, "WRONG , It was " + celebNames.get(chosenCeleb), Toast.LENGTH_SHORT).show();
            }

            generateNewCeleb();
        }
        else{
            gameLayout.setVisibility(RelativeLayout.INVISIBLE);
            finalScore.setVisibility(View.VISIBLE);
            if(score>10)
            {
                finalScore.setText("You Know "+ score +" Celebrities Out Of 15 ,I bet you are active on social media");
            }
            else{
                finalScore.setText("You Know "+ score  +" Celebrities Out Of 15 ,Please watch more movies :P");
            }

        }
    }
    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection ;

            try {

                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }
                return result;
            } catch (Exception e) {

                e.printStackTrace();

                return "Failed";

            }
        }
    }

public void generateNewCeleb() {
    if (gameIsActive) {
        Random random = new Random();
        chosenCeleb = random.nextInt(celebUrls.size());
        ImageDownloader imageTask = new ImageDownloader();
        count++;
        questionNumber.setText("Question no. " + count);

        try {
            celebImage = imageTask.execute(celebUrls.get(chosenCeleb)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        image.setImageBitmap(celebImage);

        locationOfCorrectAnswer = random.nextInt(4);
        int incorrectAnswerLocation;


        for (int i = 0; i < 4; i++) {
            if (locationOfCorrectAnswer == i) {
                answers[i] = celebNames.get(chosenCeleb);
            } else {
                incorrectAnswerLocation = random.nextInt(celebUrls.size());
                while (incorrectAnswerLocation == chosenCeleb) {
                    incorrectAnswerLocation = random.nextInt(celebUrls.size());
                }
                answers[i] = celebNames.get(incorrectAnswerLocation);
            }

        }
        button1.setText(answers[0]);
        button2.setText(answers[1]);
        button3.setText(answers[2]);
        button4.setText(answers[3]);

        if(count==15)
        {
            gameIsActive=false;
        }
    }
}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameLayout=findViewById(R.id.gameLayout);
        finalScore=findViewById(R.id.finalScore);
        questionNumber =findViewById(R.id.questionNumber);
        image = findViewById(R.id.imageView);
        button1=findViewById(R.id.button1);
        button2=findViewById(R.id.button2);
        button3=findViewById(R.id.button3);
        button4=findViewById(R.id.button4);

        DownloadTask task = new DownloadTask();
        String result;

        try {

            result = task.execute("http://www.posh24.se/kandisar").get();
            String[] splitResult = result.split("<div class=\"sidebarContainer\">");
                Log.i("contents",splitResult[0]);


            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);
            while (m.find()) {
                celebUrls.add(m.group(1));
            }
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);
            while (m.find()) {
                celebNames.add(m.group(1));
            }


            generateNewCeleb();
        } catch (InterruptedException e) {

            e.printStackTrace();

        } catch (ExecutionException e) {

            e.printStackTrace();

        }
    }
}
