package com.instamour.mathu.cmpln;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class Feedback extends ActionBarActivity {
    EditText feedback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        feedback = (EditText) findViewById(R.id.editTextFeedback);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
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
    public void SendFeedback(View view){
        if(feedback.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter some feedback before pressing Send", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mathura1987@gmail.com", "Me@jasonsherman.org"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Cmpln Feedback");
            emailIntent.putExtra(Intent.EXTRA_TEXT, feedback.getText().toString());
            feedback.setText("");
            emailIntent.setType("message/rfc822");
            startActivity(Intent.createChooser(emailIntent, "Choose email client..."));
        }
    }
}
