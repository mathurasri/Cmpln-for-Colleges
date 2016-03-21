package com.instamour.mathu.cmpln;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Support extends ActionBarActivity {

    ListView support_list;
    String email = null, university = null, username = null;
    int delCommsAndProfileFlag = 0;
    JSONParser jsonParser = new JSONParser();
    private static String url_delete_user_account = "http://cmpln.com/Scripts/DeleteAccount.php";
    private static String url_delete_user_comments = "http://cmpln.com/Scripts/DeleteComments.php";
    private static String url_delete_user_profile = "http://cmpln.com/Scripts/DeleteProfile.php";
    private static String url_delete_user_replies = "http://cmpln.com/Scripts/DeleteRepliesByUsername.php";
    private static String url_delete_user_likes = "http://cmpln.com/Scripts/DeleteLikesByUsername.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        Intent intent = getIntent();
        if(intent != null)
        {
            university = (String)intent.getExtras().get("university");
            email = (String)intent.getExtras().get("email");
            username = (String) intent.getExtras().get("username");
        }
        support_list = (ListView) findViewById(R.id.support_list);
        support_list.setAdapter(new SupportAdapter(this));
        support_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 2)
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Support.this);
                    alertDialog.setTitle("Delete Comments and Profile");
                    alertDialog.setMessage("Are you sure you want to delete comments and profile also?");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                   new DeleteCommentsAndProfile().execute();
                                }
                            });
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Support.this);
                                    alertDialog.setTitle("Delete Account");
                                    alertDialog.setMessage("Are you sure you want to delete your account?");
                                    alertDialog.setCancelable(false);
                                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            SharedPreferences userAccount = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = userAccount.edit();
                                            editor.putString("username", null);
                                            editor.putString("university", null);
                                            editor.apply();

                                            new DeleteUserAccount().execute();
                                        }
                                    });
                                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    alertDialog.show();
                                }
                            });
                    alertDialog.show();
                }
                else if(position == 0){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://android.cmpln.com"));
                    startActivity(browserIntent);
                }
                else if(position == 1){
                    Intent feedbackIntent = new Intent(getApplicationContext(), Feedback.class);
                    startActivity(feedbackIntent);
                }
                else if(position == 3){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.cmpln.com"));
                    startActivity(browserIntent);
                }
                else if(position == 4){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.cmpln.com/privacy.htm"));
                    startActivity(browserIntent);
                }
                else if(position == 5){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.cmpln.com/terms.htm"));
                    startActivity(browserIntent);
                }
            }
        });
    }

    public void GoToMenu(View view)
    {
        Intent intent =  new Intent(this, Comment.class);
        intent.putExtra("email", email);
        intent.putExtra("university", university);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_support, menu);
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
    class DeleteCommentsAndProfile extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Log.d("DeleteUserComments", "pre-execute");
        }


        protected String doInBackground(String... args) {

            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));
            // getting JSON Object

  //          Log.d("DeleteUserComments", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_delete_user_comments, "POST", params);

            // check log cat fro response
    //        Log.d("DeleteUserComments", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");

                if (success == 1) {

      //              Log.d("DeleteUSerComments", "User Comments deleted");
                    status = "success";

                } else {

        //            Log.d("DeleteUserComments", "User Comments not found");
                    status="fail";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return status;
        }


        protected void onPostExecute(String status) {

            new DeleteUserProfile().execute();
        }

    }

    class DeleteUserReplies extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          //  Log.d("DeleteUserReplies", "pre-execute");
        }


        protected String doInBackground(String... args) {

            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));
            // getting JSON Object

            //Log.d("DeleteUserReplies", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_delete_user_replies, "POST", params);

            // check log cat fro response
            //Log.d("DeleteUserReplies", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");

                if (success == 1) {

              //      Log.d("DeleteUserReplies", "User replies deleted");
                    status = "success";

                } else {

                //    Log.d("DeleteUserReplies", "User replies not found");
                    status="fail";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return status;
        }


        protected void onPostExecute(String status) {

            new DeleteUserLikes().execute();
        }

    }
    class DeleteUserLikes extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Log.d("DeleteUserLikes", "pre-execute");
        }


        protected String doInBackground(String... args) {

            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));
            // getting JSON Object

            //Log.d("DeleteUserLikes", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_delete_user_likes, "POST", params);

            // check log cat fro response
            //Log.d("DeleteUserLikes", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");

                if (success == 1) {

              //      Log.d("DeleteUserLikes", "User likes deleted");
                    status = "success";

                } else {

                //    Log.d("DeleteUserLikes", "User likes not found");
                    status="fail";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return status;
        }


        protected void onPostExecute(String status) {


        }

    }

    class DeleteUserProfile extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Log.d("DeleteUserProfile", "pre-execute");
        }


        protected String doInBackground(String... args) {

            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));
            // getting JSON Object

            //Log.d("DeleteUserProfile", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_delete_user_profile, "POST", params);

            // check log cat fro response
            //Log.d("DeleteUserProfile", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");

                if (success == 1) {

              //      Log.d("DeleteUSerProfile", "User Profile deleted");
                    status = "success";

                } else {

                //    Log.d("DeleteUserProfile", "Could not delete the user profile");
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
                Toast.makeText(getApplicationContext(), "User Profile and Comments Deleted", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Support.this);
                alertDialog.setTitle("Delete Account");
                alertDialog.setMessage("Are you sure you want to delete your account?");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        SharedPreferences userAccount = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = userAccount.edit();
                        editor.putString("username", null);
                        editor.putString("university", null);
                        editor.apply();
                        new DeleteUserAccount().execute();
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        new DeleteUserReplies().execute();
                    }
                });
                alertDialog.show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Nil UserComments or profile", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Support.this);
                alertDialog.setTitle("Delete Account");
                alertDialog.setMessage("Are you sure you want to delete your account?");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                        SharedPreferences userAccount = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = userAccount.edit();
                        editor.putString("username", null);
                        editor.putString("university", null);
                        editor.apply();
                        new DeleteUserAccount().execute();
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        }

    }

    class DeleteUserAccount extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // Log.d("DeleteUserAccount", "pre-execute");
        }


        protected String doInBackground(String... args) {

            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));
            // getting JSON Object

            //Log.d("DeleteUserAccount", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_delete_user_account, "POST", params);

            // check log cat fro response
            //Log.d("DeleteUserAccount", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");

                if (success == 1) {

              //      Log.d("DeleteUSerAccount", "User Account deleted");
                    status = "success";

                } else {

                //    Log.d("DeleteUserAccount", "Could not delete the user account");
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
                new DeleteUserReplies().execute();
                Toast.makeText(getApplicationContext(), "User Account Deleted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "User Account could not be deleted", Toast.LENGTH_SHORT).show();
            }
        }

    }
    class SupportSingleRow
    {
        String menu_item;
        int image;
        SupportSingleRow(String item, int image)
        {
            this.menu_item = item;
            this.image = image;
        }
    }
    class SupportAdapter extends BaseAdapter
    {
        ArrayList<SupportSingleRow> list;
        Context context;
        SupportAdapter(Context c)
        {
            context = c;
            list = new ArrayList<SupportSingleRow>();
            Resources res = c.getResources();
            String[] support_items = res.getStringArray(R.array.support_menu);
            int[] images = {R.drawable.arrow, R.drawable.arrow, R.drawable.arrow, R.drawable.arrow, R.drawable.arrow, R.drawable.arrow};
            for(int index = 0; index < 6; index++)
            {
                list.add(new SupportSingleRow(support_items[index], images[index]));
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.support_row, parent, false);
            TextView support_text = (TextView) row.findViewById(R.id.textSupport);
            ImageView support_image = (ImageView) row.findViewById(R.id.imageSupport);
            SupportSingleRow temp = list.get(position);
            support_text.setText(temp.menu_item);
            support_image.setImageResource(temp.image);
            return row;
        }

    }
}
