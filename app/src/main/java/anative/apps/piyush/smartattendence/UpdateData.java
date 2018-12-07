package anative.apps.piyush.smartattendence;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class UpdateData extends AsyncTask<String, Integer, String> {
    String msg = "hello";
    protected String doInBackground(String... urls){
        String content = "", line;
        try{
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while ((line = rd.readLine()) != null) {
                content += line + "\n";
            }
            msg = "Attendance Marked";
            return content;
        }catch(Exception ex){
            msg = ex.toString();
            if(msg.contains("attendence_found")){
                msg = "Attendance found for Today";
            }else if(msg.contains("GR_Number_Not_Valid")){
                msg = "GR_Number not found on record or is invalid";
            }else{
                msg = "Not_Valid_Request_OR_Multiple_GR_Number_Found";
            }
            return content;
        }
    }
    protected void onProgressUpdate(Integer... progress) {
        MainActivity.txtView.setText("Please wait...");
    }
    protected void onPostExecute(String result) {
        // this is executed on the main thread after the process is over
        // update your UI here
       MainActivity.txtView.setText("GR"+MainActivity.grNumber.substring(2)+"\n\n"+msg);
    }
}
