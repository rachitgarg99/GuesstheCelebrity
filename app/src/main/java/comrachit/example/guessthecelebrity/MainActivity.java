package comrachit.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
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

    ArrayList<String> celebURLs=new ArrayList<String>();
    ArrayList<String> celebNames=new ArrayList<String>();
    int chosenCeleb =0;
    ImageView imageView;
    int locationOfCorrwectAns=0;
    String[] answers=new String[4];
    Button b0,b1,b2,b3;

    public void celebChosen(View view){

        if(view.getTag().toString().equals(Integer.toString(locationOfCorrwectAns))){
            Toast.makeText(getApplicationContext(),"Correct ! ",Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(getApplicationContext(),"Wrong! It was : "+celebNames.get(chosenCeleb),Toast.LENGTH_SHORT).show();
        }
        createNewQuest();
    }


    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url= new URL(urls[0]);

                HttpURLConnection connection= (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream=connection.getInputStream();

                Bitmap myBitmap= BitmapFactory.decodeStream(inputStream);

                return myBitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String ,Void , String>{

        @Override
        protected String doInBackground(String... urls) {

            StringBuilder result= new StringBuilder();
            URL url;
            HttpURLConnection urlConnection=null;

            try{
                url=new URL(urls[0]);
                urlConnection= (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
              /*  InputStreamReader reader= new InputStreamReader(in);

                int data=reader.read();

                while(data != -1){

                    char current =(char)data;

                    result.append(current);

                    data=reader.read();     */
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line = reader.readLine();
                while (line != null){

                    result.append(line);
                    result.append("\n");
                    line = reader.readLine();

                }

                return result.toString();

            }
            catch (Exception e){
                e.printStackTrace();
                return "Failed";
            }




        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imageView=findViewById(R.id.imageView);
        b0=findViewById(R.id.button1);
        b1=findViewById(R.id.button2);
        b2=findViewById(R.id.button3);
        b3=findViewById(R.id.button4);

        DownloadTask task=new DownloadTask();
        String result="Hi";

        try {
            result=task.execute("https://www.imdb.com/list/ls052283250/").get();

            String[] splitResult=result.split("<div class=\"aux-content-widget-2 list-activity-widget\">");

            Pattern p = Pattern.compile("9\"\nsrc=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while(m.find()){

                celebURLs.add(m.group(1));
                //System.out.println(m.group(1));
            }

            p = Pattern.compile("<img alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);

            while(m.find()){

                celebNames.add(m.group(1));
                //System.out.println(m.group(1));
            }





           //Log.i("  Contents of URL  ",result);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        createNewQuest();


    }

    public void createNewQuest(){
        Random random=new Random();
        chosenCeleb= random.nextInt(celebURLs.size());

        ImageDownloader imageTask = new ImageDownloader();

        Bitmap celebImage;

        try {
            celebImage=imageTask.execute(celebURLs.get(chosenCeleb)).get();

            imageView.setImageBitmap(celebImage);


            locationOfCorrwectAns=random.nextInt(4);

            int incorrectAnsLocation;

            for(int i=0;i<4;i++){
                if(i==locationOfCorrwectAns){
                    answers[i]=celebNames.get(chosenCeleb);
                }
                else{
                    incorrectAnsLocation=random.nextInt(celebURLs.size());
                    while(incorrectAnsLocation==chosenCeleb){
                        incorrectAnsLocation=random.nextInt(celebURLs.size());
                    }
                    answers[i]=celebNames.get(incorrectAnsLocation);
                }
            }

            b0.setText(answers[0]);
            b1.setText(answers[1]);
            b2.setText(answers[2]);
            b3.setText(answers[3]);


        } catch (Exception e) {
            e.printStackTrace();
        }




    }
}
