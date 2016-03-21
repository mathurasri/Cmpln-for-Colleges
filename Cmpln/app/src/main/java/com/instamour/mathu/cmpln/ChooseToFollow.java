package com.instamour.mathu.cmpln;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ChooseToFollow extends ActionBarActivity {

    private static String url_get_followed = "http://cmpln.com/Scripts/SelectToFollow.php";
    private static String url_delete_followed = "http://cmpln.com/Scripts/DeleteFollowed.php";
    private static String url_store_followed = "http://cmpln.com/Scripts/StoreFollowed.php";
    String university = null, username = null, email = null, followed_person = null;
    JSONParser jsonParser = new JSONParser();
    JSONArray followedArray = null;
    String[][] followedInfo = null;
    List<FollowedInfo> myFollowedInfos;
    ArrayAdapter<FollowedInfo> adapter;
    ListView listViewFollow;
    View itemView;
    FollowedInfo currentFollowedInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_to_follow);
        Intent intent = getIntent();
        if(intent != null) {
            username = (String) intent.getExtras().get("username");
        }
        if(intent != null) {
            university = (String) intent.getExtras().get("university");
        }
        if(intent != null) {
            email = (String) intent.getExtras().get("email");
        }
        myFollowedInfos = new ArrayList<FollowedInfo>();
        new DownloadFollowed().execute();

    }


    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, Comment.class);
        intent.putExtra("university", university);
        intent.putExtra("email", email);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_to_follow, menu);
        return true;
    }

    class DownloadFollowed extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          //  Log.d("DownloadFollowed", "Pre-execute");
        }

        protected String doInBackground(String... args)  {

            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("university", university));
            params.add(new BasicNameValuePair("username", username));

            // getting JSON Object
            //Log.d("DownloadFollowed", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_get_followed,"POST", params);

            // check log cat fro response
            //Log.d("DownloadFollowed", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");
                if(success == 1)
                {
              //      Log.d("SuccessDownloadFollowed", "Downloaded followed successfully");
                    try {
                        followedArray = json.getJSONArray("usercomments");
                        followedInfo = new String[followedArray.length()][2];
                        for (int index = 0; index < followedArray.length(); index++) {
                            JSONObject obj = followedArray.getJSONObject(index);
                            String user = obj.getString("username");
                            String followflag = obj.getString("followflag");
                //            Log.d("Followed user", user);
                            followedInfo[index][0] = user;
                            followedInfo[index][1] = followflag;
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    status = "Inserted";
                }
                else
                {
                  //  Log.d("FailDownloadFollowed", "could not log in user activity");
                    status = "Not Inserted";
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return status;
        }

        protected void onPostExecute(String status) {
            if(status.equals("Inserted"))
            {
                PopulateFollowed();
                adapter = new MyListAdapter();
                listViewFollow = (ListView) findViewById(R.id.listViewChooseFollow);
                listViewFollow.setAdapter(adapter);

            }
            else {

            }
        }

    }

    class DeleteFollowed extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Log.d("DeleteFollowed", "Pre-execute");
        }


        protected String doInBackground(String... args)  {

            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("followed", followed_person));
            params.add(new BasicNameValuePair("username", username));

            // getting JSON Object
            //Log.d("DeleteFollowed", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_delete_followed,"POST", params);

            // check log cat fro response
            //Log.d("DeleteFollowed", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");
                if(success == 1)
                {

                    status = "Inserted";
                }
                else
                {
              //      Log.d("FailDownloadFollowed", "could not log in user activity");
                    status = "Not Inserted";
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return status;
        }

        protected void onPostExecute(String status) {
            if(status.equals("Inserted"))
            {
                Toast.makeText(getApplicationContext(), "The person is unfollowed", Toast.LENGTH_SHORT).show();
            }
            else {

            }
        }

    }

    class StoreFollowed extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Log.d("StoreFollowed", "Pre-execute");
        }


        protected String doInBackground(String... args)  {

            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("followed", followed_person));
            params.add(new BasicNameValuePair("username", username));

            // getting JSON Object
            //Log.d("StoreFollowed", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_store_followed,"POST", params);

            // check log cat fro response
            //Log.d("StoreFollowed", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");
                if(success == 1)
                {

                    status = "Inserted";
                }
                else
                {
              //      Log.d("FailDownloadFollowed", "could not log in user activity");
                    status = "Not Inserted";
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return status;
        }


        protected void onPostExecute(String status) {

            if(status.equals("Inserted"))
            {
                Toast.makeText(getApplicationContext(), "The person is followed", Toast.LENGTH_SHORT).show();
            }
            else {

            }
        }

    }

    private class MyListAdapter extends ArrayAdapter<FollowedInfo> {
        public MyListAdapter()
        {
            super(ChooseToFollow.this, R.layout.listview_follow, myFollowedInfos);
        }
        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent)
        {
            itemView = convertView;

            if(itemView == null)
            {
                itemView = getLayoutInflater().inflate(R.layout.listview_follow, parent, false);

            }
            //currentFollowedInfo = myFollowedInfos.get(position);

            final ImageView imageView = (ImageView) itemView.findViewById(R.id.imageViewFollow);
            if(myFollowedInfos.get(position).followflag.equals("1"))
            {
                imageView.setImageResource(R.drawable.followusersuccesssbtn);
            }
            else{
                imageView.setImageResource(R.drawable.followusericon);
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((username == null || username.equals("Unknown"))) {
                        Toast.makeText(getApplicationContext(), "Enter username in EditProfile menu", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (myFollowedInfos.get(position).followflag.equals("1")) {
                            imageView.setImageResource(R.drawable.followusericon);
                            myFollowedInfos.get(position).followflag = "0";
                            followed_person = myFollowedInfos.get(position).username;
                            Toast.makeText(getApplicationContext(), followed_person, Toast.LENGTH_SHORT).show();
                            new DeleteFollowed().execute();
                        } else {
                            imageView.setImageResource(R.drawable.followusersuccesssbtn);
                            myFollowedInfos.get(position).followflag = "1";
                            followed_person = myFollowedInfos.get(position).username;
                            Toast.makeText(getApplicationContext(), followed_person, Toast.LENGTH_SHORT).show();
                            new StoreFollowed().execute();
                        }
                    }
                }
            });
            TextView tvUsername = (TextView) itemView.findViewById(R.id.textViewFollowedName);
            tvUsername.setText(myFollowedInfos.get(position).username);


            return itemView;
        }
    }
    public void PopulateFollowed() {
        int length_index = followedInfo.length;
        for (int index = 0; index < length_index; index++) {

            String user = followedInfo[index][0];
            String followFlag = followedInfo[index][1];

            //New code
            myFollowedInfos.add(new FollowedInfo(followFlag, user));

            //adapter.notifyDataSetChanged();
        }
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
}
