package com.instamour.mathu.cmpln;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;




import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class MainActivity extends ActionBarActivity {

    private static String url_add_name = "http://cmpln.com/Scripts/test1.php";
    private static String url_send_email = "http://cmpln.com/Scripts/AccountActivateEmail.php";
    private static String url_store_user_account = "http://cmpln.com/Scripts/StoreUserAccount.php";
    JSONParser jsonParser = new JSONParser();
    TextView termsService;
    TextView emailText;
    TextView tvPassword;
    EditText email;
    EditText pwd;
    String userEmail, userPwd;
    int success;
    Session session = null;
    GMailSender sender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        termsService = (TextView)findViewById(R.id.textViewServiceTerms);
        termsService.setText(Html.fromHtml("<font color='#FFFFFF'>By creating an account you agree to our </font> <font color='#9933FF'> Terms of Service</font> <font color='#FFFFFF'> and </font> <font color='#9933FF'>Privacy Policy</font>"));
        email = (EditText) findViewById(R.id.editTextEmail);
        pwd = (EditText) findViewById(R.id.editTextPwd);
        //new MyAsyncClass().execute();

    }

    @Override
    public void onBackPressed()
    {

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    class MyAsyncClass extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... mApi) {
            try {
                // Add subject, Body, your mail Id, and receiver mail Id.
                SendActivationEmail();


            }

            catch (Exception ex) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.cancel();
            Toast.makeText(getApplicationContext(), "Email send", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean ValidateEmail()
    {
        boolean emailFlag = false;
        EditText etEmail;
        String email;
        etEmail = (EditText)findViewById(R.id.editTextEmail);
        email = etEmail.getText().toString();
        emailText = (TextView) findViewById(R.id.textViewEmailValidator);
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.edu)$";
        //String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        emailFlag = Pattern.matches(emailPattern, email);
        if(emailFlag) {
            emailFlag = true;
        }
        else
        {
            emailText.setText("**Enter valid edu email address");
        }
        return emailFlag;
    }
    public void SendActivationEmail(){
        String to = "mathura1987@gmail.com";

        // Sender's email ID needs to be mentioned
        String from = "mathu.jobs@gmail.com";
        final String username = "mathu.jobs@gmail.com";//change accordingly
        final String password = "shreedhe";//change accordingly

        // Assuming you are sending email through relay.jangosmtp.net
        //String host = "www.cmpln.com";
        String host = "smtp.gmail.com";
        Properties props = new Properties();
        //props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465"); // 25

        // Get the Session object.
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            // Set Subject: header field
            message.setSubject("Testing Subject");

            // Now set the actual message
            message.setText("Hello, this is sample for to check send " +
                    "email using JavaMailAPI ");

            // Send message
            Transport.send(message);

          //  Log.d("email","Sent message successfully....");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean ValidatePassword()
    {
        boolean passwordFlag = false;
        EditText etPassword;
        String password;
        EditText etConfirmPassword;
        String confirmPassword;
        etPassword = (EditText) findViewById(R.id.editTextPwd);
        password = etPassword.getText().toString();
        etConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPwd);
        confirmPassword = etConfirmPassword.getText().toString();
        tvPassword = (TextView)findViewById(R.id.textViewPwdValidator);
        if(password.length() == 0)
        {
            tvPassword.setText("*Password cannot be blank and its length should be greater >= 5");
        }
        else if(password.length() < 5)
        {
            tvPassword.setText("Password should be more than length 5");
        }
        else if(!password.equals(confirmPassword))
        {
            tvPassword.setText("Password and confirm password should be equal");
        }
        else
        {
            passwordFlag =  true;
        }
        return passwordFlag;
    }
    public void SignUp(View view)
    {
        boolean emailFlag = ValidateEmail();
        boolean passwordFlag = ValidatePassword();
        if(emailFlag && passwordFlag)
        {
            userEmail = email.getText().toString();
            userPwd = pwd.getText().toString();
            new StoreUserAccount().execute();
        }
    }
    public void GoToLogin(View view)
    {
        Intent intent =  new Intent(this, Login.class);
        startActivity(intent);
    }
    class StoreUserAccount extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // Log.d("StoreUserAccount", "pre-execute");
        }


        protected String doInBackground(String... args) {

            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", userEmail));
            params.add(new BasicNameValuePair("password", userPwd));
            // getting JSON Object

            //Log.d("StoreUserAccount", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_store_user_account, "POST", params);

            // check log cat fro response
            //Log.d("StoreUserAccount", "After-HttpRequest");

            // check for success tag
            try {
                success = json.getInt("success");

                if (success == 1) {

              //      Log.d("StoreUserAccount", "User Account inserted");
                    status = "success";

                } else {

                //    Log.d("StoreUserAccount", "Could not insert the user account");
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
                Toast.makeText(getApplicationContext(),"Confirmation email sent to your email id for activating your account", Toast.LENGTH_LONG).show();

                email.setText("");
                pwd.setText("");
                EditText cfmPwd = (EditText)findViewById(R.id.editTextConfirmPwd);
                cfmPwd.setText("");
                new ActivateAccount().execute();

            }
            else
            {
                Toast.makeText(getApplicationContext(), "User Account not inserted", Toast.LENGTH_LONG).show();
            }
        }

    }

    class ActivateAccount extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Log.d("Pre", "execute");
        }


        protected String doInBackground(String... args) {

            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", userEmail.trim()));
            //Log.d("MainActivity", userEmail);

            // getting JSON Object

            //Log.d("Before: ", "HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_send_email, "POST", params);

            //Log.d("After: ", "HttpRequest");

            // check for success tag
            try {
                success = json.getInt("success");

                if (success == 1) {
              //      Log.d("Success", json.getString("message"));
                    status = "success";

                } else {

                //    Log.d("Fail", json.getString("message"));
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
                Toast.makeText(getApplicationContext(), "Email sent", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Email does not exist", Toast.LENGTH_LONG).show();
            }
        }

    }

}
