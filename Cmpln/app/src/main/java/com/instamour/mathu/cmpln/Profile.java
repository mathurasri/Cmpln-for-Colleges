package com.instamour.mathu.cmpln;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Profile extends ActionBarActivity {

    String email;
    String userName;
    String bio;
    PopupWindow popupWindow;
    int avatarPosition;
    int avatarPath;
    JSONParser jsonParser = new JSONParser();
    private static String url_store_user_profile = "http://cmpln.com/Scripts/StoreUserProfile.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        email = (String)intent.getExtras().get("email");
        Toast.makeText(getApplicationContext(), email, Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String FindUniversity(String login_email)
    {
        int atIndex = login_email.indexOf('@');
        int dotIndex = login_email.indexOf('.');
        char[] emailChars = login_email.toCharArray();
        StringBuilder sb = new StringBuilder();
        for(int i = atIndex + 1; i < dotIndex; i++ )
        {
            sb.append(emailChars[i]);
        }
        String university = sb.toString();
        return  university;
    }
    class UpdateProfile extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // Log.d("UpdateProfile", "pre-execute");
        }


        protected String doInBackground(String... args)  {

            String status = null;
            avatarPath = avatarPosition;
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(avatarPath);
            String imgUri = sb.toString();

            if(imgUri.equals(null) || imgUri.isEmpty()||imgUri.equals("0"))
            {
                imgUri = null;
            }
            if(userName.equals(null) || userName.isEmpty())
            {
                userName = null;
            }
            if(bio.equals(null) || bio.isEmpty())
            {
                bio = null;
            }
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("imageuri", imgUri));
            params.add(new BasicNameValuePair("username", userName));
            params.add(new BasicNameValuePair("bio", bio));

            // getting JSON Object

            //Log.d("UpdateProfile", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_store_user_profile,"POST", params);

            // check log cat fro response
            //Log.d("UpdateProfile", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");
                if (success == 1) {

              //      Log.d("UpdateProfile", "User Profile added and comments are updated");
                    status = "Inserted";

                } else {

                //    Log.d("UpdateProfile", "could not add User Profile or could not update comments");
                    status = "Not Inserted";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return status;
        }


        protected void onPostExecute(String status) {

            Intent intent = new Intent(getApplicationContext(), Comment.class);
            intent.putExtra("email", email);
            String university = FindUniversity(email);
            intent.putExtra("university", university);
            startActivity(intent);
            if(status.equals("Inserted"))
            {

            }
            else
            {

            }
        }

    }
    public void BioUpdate(View view)
    {
        EditText user = (EditText)findViewById(R.id.editTextUserName);
        userName = user.getText().toString();
        EditText etBio = (EditText) findViewById(R.id.editTextBio);
        bio = etBio.getText().toString();
        new UpdateProfile().execute();
    }
}
