package com.instamour.mathu.cmpln;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Reply extends ActionBarActivity implements AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {

    String comment_id = null, username = null, untransformedSentence = null, transformedSentence = null;
    String email = null, university = null, commentSentence = null, avatar = null;
    int start = 0, count = 5;
    ArrayList<String> uploadKeywordsList = new ArrayList<String>();
    ArrayList<String> keywordsList = new ArrayList<String>();
    JSONParser jsonParser = new JSONParser();
    JSONArray commentsArray = null, keywordsArray = null;
    String[][] commentInfo = null;
    ListView replies;
    View itemView;
    List<ReplyInfo> myReplies;
    ArrayAdapter<ReplyInfo> adapter;
    SwipeRefreshLayout swipeLayout;
    private static String url_store_keywords = "http://cmpln.com/Scripts/StoreKeywords.php";
    private static String url_store_replies = "http://cmpln.com/Scripts/StoreReply.php";
    private static String url_download_replies = "http://cmpln.com/Scripts/DownloadReplies.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        Intent intent = getIntent();
        if(intent != null)
        {
            username = (String)intent.getExtras().get("username");
            keywordsList = (ArrayList<String>)intent.getExtras().get("keywords");
            comment_id = (String) intent.getExtras().get("comment_id");
            email = (String) intent.getExtras().get("email");
            university = (String) intent.getExtras().get("university");
            avatar = (String) intent.getExtras().get("avatar");
        }
        myReplies =  new ArrayList<ReplyInfo>();
        adapter = new MyListAdapter();
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container_reply);
        swipeLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener)this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        replies = (ListView) findViewById(R.id.listViewReplies);
        replies.setOnScrollListener((android.widget.AbsListView.OnScrollListener) this);
        replies.setAdapter(adapter);
        new DownloadReplies().execute();
    }

    /*@Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, Comment.class);
        intent.putExtra("email", email);
        intent.putExtra("university", university);
        startActivity(intent);
        finish();
    }*/
    public Bitmap ConvertImgURIToBitmap(String imageUri)
    {
        byte[] decodedByte = Base64.decode(imageUri, 0);
        //byte[] decodedByte = imageUri.getBytes();
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
    private class MyListAdapter extends ArrayAdapter<ReplyInfo> {
        public MyListAdapter()
        {
            super(Reply.this, R.layout.listview_reply, myReplies);
        }
        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent)
        {
            itemView = convertView;

            if(itemView == null)
            {
                itemView = getLayoutInflater().inflate(R.layout.listview_reply, parent, false);

            }
            final ReplyInfo currentReply = myReplies.get(position);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageViewReplyUser);
            if((currentReply.avatarPos).isEmpty() || currentReply.avatarPos == null) {
                imageView.setImageResource(R.drawable.defaultavatar);
            }
            else{
                Picasso.with(getApplicationContext()).load("http://cmpln.com/images/user/"+currentReply.avatarPos).into(imageView);
            }

            TextView tvUsername = (TextView) itemView.findViewById(R.id.textViewReplyUserName);
            tvUsername.setTypeface(null, Typeface.BOLD);
            tvUsername.setText(currentReply.user);

            TextView tvComment = (TextView) itemView.findViewById(R.id.textViewReply);
            tvComment.setText(Html.fromHtml(currentReply.reply));

            TextView tvTime = (TextView) itemView.findViewById(R.id.tvReplyTime);
            tvTime.setText(currentReply.commentTime);


            return itemView;
        }
    }

    public void BuildKeywordList() {
        String[] strArray = untransformedSentence.split("[, ' ' ; - . \\t \\n \\r \\f]");
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
    public void TransformComment() {
        String[] strArray = untransformedSentence.split("[, ' ' ; - . \\t \\n \\r \\f]");
        for (int index = 0; index < strArray.length; index++) {
            if (strArray[index].charAt(0) != '#') {
                if (keywordsList.contains(strArray[index])) {
                    //strArray[index] = "#" + strArray[index];
                    String target = " " + strArray[index] + " ";
                    String replacement = " " + "#" + strArray[index] + " ";
                    //transformedSentence = transformedSentence.replace(target, replacement);
                    transformedSentence = transformedSentence.replaceAll(target, replacement);
                    target = "^" + strArray[index] + " ";
                    replacement = "#" + strArray[index] + " ";
                    transformedSentence = transformedSentence.replaceAll(target, replacement);
                    target = " " + strArray[index] + " ";
                    replacement = " " + "#" + strArray[index] + " ";
                    transformedSentence = transformedSentence.replaceAll(target, replacement);
                    target = " " + strArray[index] + ".";
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
        }
    }
    class StoreKeywordsReplies extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("StoreKeywordsReplies", "Pre-execute");
        }

        protected String doInBackground(String... args) {

            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (int i = 0; i < uploadKeywordsList.size(); i++) {
                params.add(new BasicNameValuePair("keyword[]", uploadKeywordsList.get(i)));
            }
            // getting JSON Object

           // Log.d("StoreKeywordsReplies", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_store_keywords, "POST", params);

            // check log cat fro response
            //Log.d("StoreKeywordsReplies", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");
                if (success == 1) {
              //      Log.d("StoreKeywordsRepliesSuccess", "keywords inserted successfully");
                    status = "Inserted";
                } else {

                //    Log.d("StoreKeywordsRepliesFail", "could not log insert keywords successfully");

                    status = "Not Inserted";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return status;
        }


        protected void onPostExecute(String status) {

            if (status.equals("Inserted")) {
                new AddReply().execute();
            } else {

            }
        }

    }

    class AddReply extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Log.d("AddReply", "Pre-execute");
        }


        protected String doInBackground(String... args) {
            String status = null;
            // Building Parametersisset($_POST['comment'])
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("comment_id", comment_id));
            params.add(new BasicNameValuePair("reply", transformedSentence));
            params.add(new BasicNameValuePair("username", username));
            //Log.d("AddReply", transformedSentence);
            //Log.d("AddReply", Integer.toString(transformedSentence.length()));
            // getting JSON Object

            //Log.d("AddReply", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_store_replies, "POST", params);

            // check log cat fro response
            //Log.d("AddReply", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");

                if (success == 1) {

              //      Log.d("SuccessAddReply", "Reply added");
                    status = "Inserted";

                } else {
                //    Log.d("FailAddReply", "could not add Reply");
                    status = "Not Inserted";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return status;
        }


        protected void onPostExecute(String status) {
            // dismiss the dialog once done
            if (status.equals("Inserted")) {
                Toast toast = Toast.makeText(getApplicationContext(), "Reply added", Toast.LENGTH_LONG);
                toast.show();

                swipeLayout.setRefreshing(false);
                myReplies = new ArrayList<ReplyInfo>();
                //adapter.notifyDataSetChanged();
                adapter = new MyListAdapter();
                replies.setAdapter(adapter);
                start = 0;
                new DownloadReplies().execute();

            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "The maximum length of Reply is 500 chars", Toast.LENGTH_LONG);
                toast.show();
            }
        }

    }

    public void PostReply(View view){
        EditText reply = (EditText) findViewById(R.id.editTextReplyComment);
        untransformedSentence = reply.getText().toString();
        if(untransformedSentence != null || untransformedSentence.isEmpty()) {
            //Log.d("untransformedSentence", untransformedSentence);
            reply.setText("");
            transformedSentence = untransformedSentence;
            BuildKeywordList();
            TransformComment();
            ReplaceApostrophe();
            if((username == null || username.equals("Unknown"))) {
                Toast.makeText(getApplicationContext(), "Enter username in EditProfile menu", Toast.LENGTH_SHORT).show();
            }
            else {
                if (uploadKeywordsList.size() > 0) {
                    new StoreKeywordsReplies().execute();
                } else {
                    new AddReply().execute();
                }
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reply, menu);
        return true;
    }
    public void TransformCommentKeys()
    {
        String[] strArray = commentSentence.split("[, ' ' ; - . \\t \\n \\r \\f]");
        commentSentence =  commentSentence.replace(" ", "&nbsp;");
        commentSentence = commentSentence.replace("\n", "<br/>");
        for(int i = 0; i < strArray.length; i++)
        {
            if(strArray[i].length() > 0 && strArray[i].charAt(0) == '#') {
                String target = "&nbsp;" + strArray[i] + "&nbsp;";
                String replacement = "&nbsp;" + "<font color='#9933cc'>" + strArray[i] + "</font>" + "&nbsp;";
                commentSentence = commentSentence.replaceAll(target, replacement);
                target = "^" + strArray[i] + "&nbsp;";
                replacement = "<font color='#9933cc'>" + strArray[i] + "</font>" + "&nbsp;";
                commentSentence = commentSentence.replaceAll(target, replacement);
                target = "&nbsp;" + strArray[i] + "&nbsp;";
                replacement = "&nbsp;" + "<font color='#9933cc'>" + strArray[i] + "</font>" + "&nbsp;";
                commentSentence = commentSentence.replaceAll(target, replacement);
                target = "<br/>" + strArray[i] + "<br/>";
                replacement = "<br/>" + "<font color='#9933cc'>" + strArray[i] + "</font>" + "<br/>";
                commentSentence = commentSentence.replaceAll(target, replacement);
                target = "&nbsp;" + strArray[i] + "$";
                replacement = "&nbsp;" + "<font color='#9933cc'>" + strArray[i] + "</font>" + ".";
                commentSentence = commentSentence.replaceAll(target, replacement);
                target = "&nbsp;" + strArray[i] + ".";
                replacement = "&nbsp;" + "<font color='#9933cc'>" + strArray[i] + "</font>" + ".";
                commentSentence = commentSentence.replaceAll(target, replacement);
                target = "," + strArray[i] + "&nbsp;";
                replacement = "," + "<font color='#9933cc'>" + strArray[i] + "</font>" + "&nbsp;";
                commentSentence = commentSentence.replace(target, replacement);
                target = "&nbsp;" + strArray[i] + ",";
                replacement = "&nbsp;" + "<font color='#9933cc'>" + strArray[i] + "</font>" + ",";
                commentSentence = commentSentence.replace(target, replacement);
                target = "," + strArray[i] + ",";
                replacement = "," + "<font color='#9933cc'>" + strArray[i] + "</font>" + ",";
                commentSentence = commentSentence.replace(target, replacement);
                target = ";" + strArray[i] + "&nbsp;";
                replacement = ";" + "<font color='#9933cc'>" + strArray[i] + "</font>" + "&nbsp;";
                commentSentence = commentSentence.replace(target, replacement);
                target = "&nbsp;" + strArray[i] + ";";
                replacement = "&nbsp;" + "<font color='#9933cc'>" + strArray[i] + "</font>" + ";";
                commentSentence = commentSentence.replace(target, replacement);
                target = ";" + strArray[i] + ";";
                replacement = ";" + "<font color='#9933cc'>" + strArray[i] + "</font>" + ";";
                commentSentence = commentSentence.replace(target, replacement);
                target = "&nbsp;" + strArray[i] + "-";
                replacement = "&nbsp;" + "<font color='#9933cc'>" + strArray[i] + "</font>" + "-";
                commentSentence = commentSentence.replace(target, replacement);
                target = "-" + strArray[i] + "&nbsp;";
                replacement = "-" + "<font color='#9933cc'>" + strArray[i] + "</font>" + "&nbsp;";
                commentSentence = commentSentence.replace(target, replacement);
                target = "-" + strArray[i] + "-";
                replacement = "-" + "<font color='#9933cc'>" + strArray[i] + "</font>" + "-";
                commentSentence = commentSentence.replace(target, replacement);
                target = "." + strArray[i] + "&nbsp;";
                replacement = "." + "<font color='#9933cc'>" + strArray[i] + "</font>" + "&nbsp;";
                commentSentence = commentSentence.replace(target, replacement);
                target = strArray[i] + "<br/>";
                replacement = "<font color='#9933cc'>" + strArray[i] + "</font>" + "<br/>";
                commentSentence = commentSentence.replace(target, replacement);

            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        /*Log.d("scroll", "scroll");
        Log.d("outside firstVisibleITem", Integer.toString(firstVisibleItem));
        Log.d("outside visibleItemCount", Integer.toString(visibleItemCount));
        Log.d("outside totalItemCount", Integer.toString(totalItemCount));*/

        if(totalItemCount > 0) {
            if (firstVisibleItem + visibleItemCount == totalItemCount) { //check if we've reached the bottom
                /*Log.d("firstVisibleITem", Integer.toString(firstVisibleItem));
                Log.d("visibleItemCount", Integer.toString(visibleItemCount));
                Log.d("totalItemCount", Integer.toString(totalItemCount));*/
                // viewIndex = firstVisibleItem;
                if(totalItemCount > start) {
                    start = totalItemCount;
                    new DownloadReplies().execute();
                    Toast.makeText(getApplicationContext(), "Loading", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    public void onRefresh()
    {
        swipeLayout.setRefreshing(false);
        myReplies = new ArrayList<ReplyInfo>();

        adapter = new MyListAdapter();
        replies.setAdapter(adapter);
        start = 0;
        new DownloadReplies().execute();
    }
    public void PopulateComments() {
        int length_index = commentInfo.length;
        int length = commentInfo.length;
        if (length_index > count) {
            length_index = length_index - 1;
        }
        for (int index = 0; index < length_index; index++) {
            commentSentence = commentInfo[index][0];

            TransformCommentKeys();


            String avaPos = commentInfo[index][1];
            String username = commentInfo[index][2];
            String commentTime = commentInfo[index][3];
            String comment_id = commentInfo[index][4];
            //New code
            myReplies.add(new ReplyInfo(commentSentence, avaPos, username, commentTime, comment_id));
            adapter.notifyDataSetChanged();
        }
    }

    class DownloadReplies extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Log.d("DownloadReplies", "Pre-execute");
        }


        protected String doInBackground(String... args)  {

            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("comment_id", comment_id));
            //Log.d("RepliesCommentId", comment_id);

            params.add(new BasicNameValuePair("start",Integer.toString(start)));
            params.add(new BasicNameValuePair("count",Integer.toString(count)));
            // getting JSON Object

            //Log.d("DownloadReplies", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_download_replies,"POST", params);

            // check log cat fro response
            //Log.d("DownloadReplies", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");
                if(success == 1)
                {
              //      Log.d("SuccessDownloadReplies", "Downloaded Replies successfully");
                    try {
                        commentsArray = json.getJSONArray("userreplies");
                        commentInfo = new String[commentsArray.length()][5];
                        for (int index = 0; index < commentsArray.length(); index++) {
                            JSONObject obj = commentsArray.getJSONObject(index);
                            String comment = obj.getString("comment");
                            String avatarPos = obj.getString("avatar");
                            String user = obj.getString("username");
                            String commentTime = obj.getString("time");

                            String comment_id = obj.getString("comment_id");

                            commentInfo[index][0] = comment;
                            commentInfo[index][1] = avatarPos;
                            commentInfo[index][2] = user;
                            commentInfo[index][3] = commentTime;
                            commentInfo[index][4] = comment_id;
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
                //    Log.d("FailDownloadReplies", "could not log in user activity");
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
                PopulateComments();

            }
            else {

            }
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
