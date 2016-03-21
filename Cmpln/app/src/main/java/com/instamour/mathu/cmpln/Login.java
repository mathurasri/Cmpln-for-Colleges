package com.instamour.mathu.cmpln;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Login extends ActionBarActivity {

    EditText loginEmail;
    EditText loginPwd;
    String login_email;
    String login_pwd;
    private static String url_login_credential_check = "http://cmpln.com/Scripts/LoginCredentialCheck.php";
    JSONParser jsonParser = new JSONParser();
    int success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
        String university = null;
        char[] emailChars = login_email.toCharArray();
        if(dotIndex > atIndex) {
            StringBuilder sb = new StringBuilder();
            for (int i = atIndex + 1; i < dotIndex; i++) {
                sb.append(emailChars[i]);
            }

            university = sb.toString();
        }
        else
        {
            StringBuilder sb = new StringBuilder();
            for(int i = atIndex+1; i < emailChars.length; i++)
            {
                if(emailChars[i]!='.')
                {
                    sb.append(emailChars[i]);
                }
                else
                {
                    break;
                }
            }
            university = sb.toString();

        }
        return  university;
    }
    public void SignIn(View view)
    {
        loginEmail = (EditText)findViewById(R.id.editTextLoginEmail);
        loginPwd = (EditText) findViewById(R.id.editTextLoginPassword);
        login_email = loginEmail.getText().toString();
        login_pwd = loginPwd.getText().toString();
        //Log.d("Image Path", Integer.toString(R.drawable.avatar1));
        new CredentialCheck().execute();
    }
    class CredentialCheck extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          //  Log.d("CredentialCheck", "pre-execute");
        }

       protected String doInBackground(String... args) {

            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", login_email));
            params.add(new BasicNameValuePair("password", login_pwd));

            // getting JSON Object

            //Log.d("CredentialCheck", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_login_credential_check, "POST", params);
            // check log cat fro response
            //Log.d("CredentialCheck", "After-HttpRequest");

            // check for success tag
            try {
                success = json.getInt("success");

                if (success == 1) {
              //      Log.d("CredentialCheck", "user account exists");
                    status = "success";

                } else {

                //    Log.d("CredentialCheck", "user account not exists");
                    status="fail";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return status;
        }


        protected void onPostExecute(String status) {

            if(status.equals("success"))
            {
                String university = FindUniversity(login_email);
                SharedPreferences userAccount = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = userAccount.edit();
                editor.putString("username", login_email);
                editor.putString("university", university);
                editor.apply();
                //Log.d("Login", "Success");
                Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), Comment.class);
                loginEmail.setText("");
                loginPwd.setText("");
                //intent.putExtra("email", login_email);
                //String universty = FindUniversity(login_email);
                //intent.putExtra("university", university);
                startActivity(intent);
            }
            else
            {
                //Log.d("Login", "Error");
                Toast.makeText(getApplicationContext(), "Login error", Toast.LENGTH_LONG).show();
            }
        }
    }
}

