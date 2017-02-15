package com.csit.smsserver.server;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by DotNet on 2/8/2017.
 */

public class Send extends AsyncTask<String, Void, Long> {
    private String Email = "", Name = "", Date = "", FirebaseRegID = "",Message = "";

    public void setMessage(String message){Message = message;}

    public String getMessage(){return Message;}

    public void setName(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }

//    Send send = new Send();
    @Override
    protected Long doInBackground(String... params) {

        //populating static data for testing
        Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        Random rand = new Random();
        int selected = rand.nextInt(100);
       // Email = "atiartalu"+selected+"kdar@gmail.com";

        Name = getName();

        // Fetches reg id from shared preferences
        /*SharedPreferences pref = context.getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        if (!TextUtils.isEmpty(regId))
            Name = regId;*/


        //Put those data in HashMap as key value pair.
        Map<String,String> dataToSend = new HashMap<>();
        dataToSend.put("email", Email);
       // dataToSend.put("fcm_token", getName().toString());
        dataToSend.put("name", Name);
        dataToSend.put("date", Date);

        Log.e("get data in Send", "Firebase reg id: " + getName());

        //Encoded String - we will have to encode string by our custom method written in bellow
        String encodedStr = getEncodedData(dataToSend);

        //Will be used if we want to read some data from server
        BufferedReader reader = null;

        //Connection Handling
        try {
            //Converting address String to URL
            URL url = new URL("http://bopbd.org/smsServerUID.php");
            //URL url = new URL("http://crystalgrade.com/cssms/sms_insert.php");

            //Opening the connection (Not setting or using CONNECTION_TIMEOUT)
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //Post Method
            con.setRequestMethod("POST");
            //To enable inputting values using POST method
            //(Basically, after this we can write the dataToSend to the body of POST method)
            con.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());

            //Writing dataToSend to outputstreamwriter
            writer.write(encodedStr);

            //Sending the data to the server - This much is enough to send data to server
            //But to read the response of the server, you will have to implement the procedure below
            writer.flush();

            //Data Read Procedure - Basically reading the data comming line by line
            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while((line = reader.readLine()) != null) { //Read till there is something available
                sb.append(line + "\n");     //Reading and saving line by line - not all at once
            }
            line = sb.toString();           //Saving complete data received in string, you can do it differently

            //Just check to the values received in Logcat
            Log.i("custom_check","The values received in the store part are as follows:");
            Log.i("custom_check",line);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(reader != null) {
                try {
                    reader.close();     //Closing the
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



        return null;
    }

    private String getEncodedData(Map<String,String> data) {
        StringBuilder sb = new StringBuilder();
        for(String key : data.keySet()) {
            String value = null;
            try {
                value = URLEncoder.encode(data.get(key),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if(sb.length()>0)
                sb.append("&");

            sb.append(key + "=" + value);
        }
        return sb.toString();
    }
}
