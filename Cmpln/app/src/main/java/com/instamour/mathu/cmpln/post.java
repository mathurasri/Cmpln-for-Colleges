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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class post extends ActionBarActivity {

    String username = null, university = null, avatar = null, email = null;
    ArrayList<String> keywordsList = new ArrayList<String>();
    ArrayList<String> uploadKeywordsList = new ArrayList<String>();
    JSONParser jsonParser = new JSONParser();
    int avatarPos = 0;
    String untransformedSentence = null, transformedSentence = null;
    private static String url_store_keywords = "http://cmpln.com/Scripts/StoreKeywords.php";
    private static String url_store_comments = "http://cmpln.com/Scripts/StoreComment.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(intent != null)
        {
            username = (String)intent.getExtras().get("username");
            university = (String)intent.getExtras().get("university");
            avatar = (String)intent.getExtras().get("avatar");
            keywordsList = (ArrayList<String>)intent.getExtras().get("keywords");
            email = (String)intent.getExtras().get("email");
        }

        setContentView(R.layout.activity_post);
        ImageView imgPost =  (ImageView) findViewById(R.id.imageViewAvatarInPost);
        TextView tvUsername = (TextView) findViewById(R.id.textViewUserName);
        if((username == null || username.equals("Unknown")) ){
            tvUsername.setText("Unknown");
            //
            Toast.makeText(getApplicationContext(), "Enter username in EditProfile menu", Toast.LENGTH_SHORT).show();
        }
        else
        {
            tvUsername.setText((CharSequence)username);

        }
        if((avatar == null || avatar.isEmpty())){
            imgPost.setImageResource(R.drawable.defaultavatar);
        }
        else{
            Picasso.with(this).load("http://cmpln.com/images/user/"+avatar).into(imgPost);
        }
        String university_text = "#" + university;
        TextView tvUniversity = (TextView) findViewById(R.id.textViewUniversity);
        tvUniversity.setText((CharSequence)university_text);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post, menu);
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
    public void Cancel(View view)
    {
        Intent intent = new Intent(getApplicationContext(), Comment.class);
        intent.putExtra("email", email);
        intent.putExtra("university", university);
        startActivity(intent);
    }
    public void BuildKeywordList() {
        String[] strArray = untransformedSentence.split("[, ' ' ; - . ! ? \\t \\n \\r \\f]");
        for (int i = 0; i < strArray.length; i++) {
            if (strArray[i].charAt(0) == '#') {
                char[] charArray = strArray[i].toCharArray();
                StringBuilder sb = new StringBuilder();
                for (int j = 1; j < charArray.length; j++) {
                    sb.append(charArray[j]);
                }
                uploadKeywordsList.add(sb.toString());
                keywordsList.add(sb.toString());
            }
        }
    }
    public void ReplaceApostrophe()
    {
        transformedSentence = transformedSentence.replace("'","\\'");
    }
    @Override
    public void onBackPressed()
    {

    }
    public void TransformComment() {
        String[] strArray = untransformedSentence.split("[, ' ' ; - . ! ? \\t \\n \\r \\f]");
        for (int index = 0; index < strArray.length; index++) {
            if (strArray[index].charAt(0) != '#') {
                if (keywordsList.contains(strArray[index])) {

                    String target = " " + strArray[index] + " ";
                    String replacement = " " + "#" + strArray[index] + " ";

                    transformedSentence = transformedSentence.replaceAll(target, replacement);

                    target = "^" + strArray[index] + " ";
                    replacement = "#" + strArray[index] + " ";
                    transformedSentence = transformedSentence.replaceAll(target, replacement);
                    target = "^" + strArray[index];
                    replacement = "#" + strArray[index];
                    transformedSentence = transformedSentence.replaceAll(target, replacement);
                    target = " " + strArray[index] + " ";
                    replacement = " " + "#" + strArray[index] + " ";
                    transformedSentence = transformedSentence.replaceAll(target, replacement);
                    target = " "+strArray[index]+"!";
                    replacement = " "+"#" + strArray[index] + "!";
                    transformedSentence = transformedSentence.replaceAll(target, replacement);

                    target = "^"+strArray[index]+"!";
                    replacement = "#" + strArray[index] + "!";
                    transformedSentence = transformedSentence.replaceAll(target, replacement);

                    target = " " + strArray[index] + "\\?";
                    replacement = " " + "#" + strArray[index] + "?";
                    transformedSentence = transformedSentence.replaceAll(target, replacement);
                    target = "^" + strArray[index] + "?";
                    replacement = "#" + strArray[index] + "?";
                    transformedSentence = transformedSentence.replaceAll(target, replacement);
                    target = " " + strArray[index] + "\\.";
                    replacement = " " + "#" + strArray[index] + ".";
                    transformedSentence = transformedSentence.replaceAll(target, replacement);
                    target = strArray[index] + "\n ";
                    replacement = "#" + strArray[index] + "\n ";
                    transformedSentence = transformedSentence.replaceAll(target, replacement);
                    target = " " + strArray[index] + "$";
                    replacement = " " + "#" + strArray[index] + ".";
                    transformedSentence = transformedSentence.replaceAll(target, replacement);
                    target = "," + strArray[index] + " ";
                    replacement = "," + "#" + strArray[index] + " ";
                    transformedSentence = transformedSentence.replace(target, replacement);
                    target = " " + strArray[index] + ",";
                    replacement = " " + "#" + strArray[index] + ",";
                    transformedSentence = transformedSentence.replace(target, replacement);
                    target = "," + strArray[index] + ",";
                    replacement = "," + "#" + strArray[index] + ",";
                    transformedSentence = transformedSentence.replace(target, replacement);
                    target = ";" + strArray[index] + " ";
                    replacement = ";" + "#" + strArray[index] + " ";
                    transformedSentence = transformedSentence.replace(target, replacement);
                    target = " " + strArray[index] + ";";
                    replacement = " " + "#" + strArray[index] + ";";
                    transformedSentence = transformedSentence.replace(target, replacement);
                    target = ";" + strArray[index] + ";";
                    replacement = ";" + "#" + strArray[index] + ";";
                    transformedSentence = transformedSentence.replace(target, replacement);
                    target = " " + strArray[index] + "-";
                    replacement = " " + "#" + strArray[index] + "-";
                    transformedSentence = transformedSentence.replace(target, replacement);
                    target = "-" + strArray[index] + " ";
                    replacement = "-" + "#" + strArray[index] + " ";
                    transformedSentence = transformedSentence.replace(target, replacement);
                    target = "-" + strArray[index] + "-";
                    replacement = "-" + "#" + strArray[index] + "-";
                    transformedSentence = transformedSentence.replace(target, replacement);
                    target = "." + strArray[index] + " ";
                    replacement = "." + "#" + strArray[index] + " ";
                    transformedSentence = transformedSentence.replace(target, replacement);
                    target = "^" + strArray[index]+"\n";
                    replacement = "#" + strArray[index] + "\n";
                    transformedSentence = transformedSentence.replaceAll(target, replacement);
                    target = "\n"+strArray[index]+"\n";
                    replacement = "\n"+"#" + strArray[index] + "\n";
                    transformedSentence = transformedSentence.replaceAll(target, replacement);
                }
            }
            else{

            }
        }
    }
    class StoreKeywordsComments extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // Log.d("StoreKeywordsComments", "Pre-execute");
        }


        protected String doInBackground(String... args) {

            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (int i = 0; i < uploadKeywordsList.size(); i++) {
                params.add(new BasicNameValuePair("keyword[]", uploadKeywordsList.get(i)));
            }
            // getting JSON Object

            //Log.d("StoreKeywordsComments", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_store_keywords, "POST", params);

            // check log cat fro response
            //Log.d("StoreKeywordsComments", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");
                if (success == 1) {
              //      Log.d("StoreKeywordsCommentsSuccess", "keywords inserted successfully");
                    status = "Inserted";
                } else {

                //    Log.d("StoreKeywordsCommentsFail", "could not log insert keywords successfully");
                    status = "Not Inserted";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return status;
        }


        protected void onPostExecute(String status) {

            if (status.equals("Inserted")) {

                new AddComment().execute();
            } else {

            }
        }

    }

    class AddComment extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Log.d("AddComment", "Pre-execute");
        }


        protected String doInBackground(String... args) {
            String status = null;
            String comment_id = UUID.randomUUID().toString();
            // Building Parametersisset($_POST['comment'])
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("comment_id", comment_id));
            params.add(new BasicNameValuePair("comment", transformedSentence));
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("university", university));
            params.add(new BasicNameValuePair("username", username));
            //Log.d("AddComment", transformedSentence);
            //Log.d("AddComment", Integer.toString(transformedSentence.length()));
            // getting JSON Object

            //Log.d("AddComment", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_store_comments, "POST", params);

            // check log cat fro response
            //Log.d("AddComment", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");

                if (success == 1) {
              //      Log.d("SuccessAddComment", "comment added");
                    status = "Inserted";

                } else {

                //    Log.d("FailAddComment", "could not add comment");
                    status = "Not Inserted";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return status;
        }


        protected void onPostExecute(String status) {

            if (status.equals("Inserted")) {

                Intent intent = new Intent(getApplicationContext(), Comment.class);
                intent.putExtra("email", email);
                intent.putExtra("university", university);
                startActivity(intent);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "The maximum length of comment is 500 chars", Toast.LENGTH_LONG);
                toast.show();
            }
        }

    }

    public void Post_Comment(View view)
    {
        EditText comment = (EditText) findViewById(R.id.editTextCommentInPost);
        untransformedSentence = comment.getText().toString();
        if((untransformedSentence != null ) || untransformedSentence.isEmpty()) {
            //Log.d("untransformedSentence", untransformedSentence);
            comment.setText("");
            transformedSentence = untransformedSentence;
            BuildKeywordList();
            TransformComment();
            ReplaceApostrophe();
            if((username == null || username.equals("Unknown"))) {
                Toast.makeText(getApplicationContext(), "Enter username in EditProfile menu", Toast.LENGTH_SHORT).show();

            }
            else{
                if (uploadKeywordsList.size() > 0) {
                    new StoreKeywordsComments().execute();
                } else {
                    new AddComment().execute();
                }
            }
        }
    }
}
