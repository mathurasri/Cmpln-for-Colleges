package com.instamour.mathu.cmpln;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.PushService;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WhoIFollowComments extends Fragment implements AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, ActionBar.TabListener{

    String email, university, noofreplies = null;
    AlertDialogManager alert = new AlertDialogManager();
    AsyncTask<Void, Void, Void> mRegisterTask;
    ConnectionDetector cd;
    View itemView;
    PopupWindow popupWindow=null;
    int likePosition=0, startWhoIFollow, count = 5, pushNotificationFlag = 0, parseStatusFlag=1, startFlag = 0;
    int listViewPosition = 0;
    JSONParser jsonParser = new JSONParser();
    JSONArray commentsArray = null, keywordsArray = null;
    String[][] commentInfo = null;
    String imageUri, nooflikes = null, likeClickFlag = null, likeInsertedFlag=null;
    static String name;
    ArrayList<String> uploadKeywordsList = null;
    ArrayList<String> keywordsList = new ArrayList<String>();
    ArrayList<String> commentids = new ArrayList<String>();
    ArrayList<String> usernames = new ArrayList<String>();
    String transformedSentence = null;
    String untransformedSentence = null;
    String commentSentence = null;
    EditText comment;
    ViewGroup container1;
    CommentInfo myCurrentCommentInfo;
    View popupview;
    TextView cmplntText, tvLikes;
    int viewIndex = 0, refreshFlag = 0, textViewFlag =0, replyFlag = 0, replyPosition = 0;
    LinearLayout l1;
    ListView listViewSlidingWindow;
    String username = null, toBeDeleteCommentId = null;
    ScrollView sv;
    LayoutInflater inflater1;
    List<CommentInfo> myComments;
    SwipeRefreshLayout swipeLayout;
    LayoutInflater layoutInflater;
    ArrayAdapter<CommentInfo> adapter;
    String[] spinnerValues;
    View g_itemView, view;ListView l;
    private static String url_download_avatar = "http://cmpln.com/Scripts/DownloadAvatar.php";
    private static String url_download_comments = "http://cmpln.com/Scripts/DownloadComments.php";
    private static String url_download_keywords = "http://cmpln.com/Scripts/DownloadKeywords.php";
    private static String url_store_keywords = "http://cmpln.com/Scripts/StoreKeywords.php";
    private static String url_store_comments = "http://cmpln.com/Scripts/StoreComment.php";
    private static String url_likes = "http://cmpln.com/Scripts/likes.php";
    private static String url_getcurrent_likes = "http://cmpln.com/Scripts/CommentIdLikes.php";
    private static String url_getcurrent_replies = "http://cmpln.com/Scripts/CommentIdReplies.php";
    private static String url_delete_commentsby_commentid = "http://cmpln.com/Scripts/DeleteCommentsByCommentId.php";
    private static String url_delete_repliesby_commentid = "http://cmpln.com/Scripts/DeleteRepliesByCommentId.php";
    private static String url_delete_likesby_commentid = "http://cmpln.com/Scripts/DeleteLikesByCommentId.php";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
     view = inflater.inflate(R.layout.fragment_whoifollow, container, false);
     inflater1 = inflater;
     container1 = container;
     Bundle bundle = this.getArguments();
     if (bundle != null) {
         email = bundle.getString("email");
         university = bundle.getString("university");
     }
     //Log.d("wif", "oncreate");
     layoutInflater = inflater;
     if(startFlag == 0) {
         startWhoIFollow = 0;
         startFlag = 1;
     }
     myComments = new ArrayList<CommentInfo>();
     swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
     swipeLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener)this);
     swipeLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
     adapter = new MyListAdapter();
     l = (ListView) view.findViewById(R.id.listView);
    /* l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
             final CharSequence[] items = { "Delete" };
             AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
             builder.setItems(items, new DialogInterface.OnClickListener() {

                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     // TODO Auto-generated method stub
                     if(which == 0){
                         listViewPosition = position;
                         myComments.remove(position);
                         adapter.notifyDataSetChanged();
                         new DeleteCommentsByCommentId().execute();
                     }
                     dialog.cancel();

                 }
             });
             AlertDialog alert = builder.create();
             alert.show();
         }
     });*/
     l.setOnScrollListener((android.widget.AbsListView.OnScrollListener) this);
     l.setAdapter(adapter);
     return view;
    }

    @Override
    public void onStart() {
        super.onStart();
       /* Log.d("AllCommentsRefresh", "onStart");
        Log.d("wif", "onstart");
        Log.d("AllCommentsRefresh", Integer.toString(replyFlag));*/
        if(replyFlag == 1) {
            replyFlag = 0;
            new GetRepliesForCommentId().execute();
        }

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Log.d("wif", "onActivityCreated");
        new DownloadAvatarKeywordsComments().execute();
    }


    class DeleteCommentsByCommentId extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          //  Log.d("DeleteCommentsByCommentId", "Pre-execute");
        }


        protected String doInBackground(String... args)  {
            String status = null;
            toBeDeleteCommentId = commentids.get(listViewPosition);
            commentids.remove(listViewPosition);
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("comment_id", toBeDeleteCommentId));

            // getting JSON Object
            //Log.d("DeleteCommentsByCommentId", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_delete_commentsby_commentid,"POST", params);

            // check log cat fro response
            //Log.d("DeleteCommentsByCommentId", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");
                if(success == 1)
                {
                    status = "Inserted";
                }
                else
                {
              //      Log.d("DeleteCommentsByCommentId", "could not delete comments by comment_id");
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
                Toast.makeText(getActivity(), "The comment is deleted", Toast.LENGTH_SHORT).show();
                new DeleteRepliesByCommentId().execute();
            }
            else {
            }
        }

    }

    class DeleteRepliesByCommentId extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Log.d("DeleteRepliesByCommentId", "Pre-execute");
        }


        protected String doInBackground(String... args) {

            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("comment_id", toBeDeleteCommentId));

            // getting JSON Object

            //Log.d("DeleteRepliesByCommentId", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_delete_repliesby_commentid, "POST", params);

            // check log cat fro response
            //Log.d("DeleteRepliesByCommentId", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");
                if (success == 1) {

                    status = "Inserted";
                } else {

              //      Log.d("DeleteRepliesByCommentId", "could not replies comments by comment_id");
                    status = "Not Inserted";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return status;
        }


        protected void onPostExecute(String status) {

            if (status.equals("Inserted")) {
                new DeleteLikesByCommentId().execute();
            } else {

            }
        }
    }


    class DeleteLikesByCommentId extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Log.d("DeleteLikesByCommentId", "Pre-execute");
        }


        protected String doInBackground(String... args) {
            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("comment_id", toBeDeleteCommentId));

            // getting JSON Object


           // Log.d("DeleteLikesByCommentId", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_delete_likesby_commentid, "POST", params);

            // check log cat fro response
            //Log.d("DeleteLikesByCommentId", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");
                if (success == 1) {

                    status = "Inserted";
                } else {

//                    Log.d("DeleteLikesByCommentId", "could not delete likes by comment_id");
                    status = "Not Inserted";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return status;
        }


        protected void onPostExecute(String status) {

            if (status.equals("Inserted")) {

            } else {
            }
        }
    }

    public void PopulateComments() {
        int length_index = commentInfo.length;

        if (length_index > count) {
            length_index = length_index - 1;
        }
        for (int index = 0; index < length_index; index++) {
            commentSentence = commentInfo[index][0];




            String avaPos = commentInfo[index][1];
            String username = commentInfo[index][2];
            String commentTime = commentInfo[index][3];
            String likes = commentInfo[index][4];
            String likesflag = commentInfo[index][6];
            String noofreplies = commentInfo[index][7];
            if(avaPos != null && username != null && commentTime != null && likes != null && likesflag != null && noofreplies != null) {
                TransformCommentKeys();
                //New code
                myComments.add(new CommentInfo(avaPos, commentSentence, username, commentTime, likes, likesflag, "1", noofreplies));

                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

  /*      Log.d("scroll", "scroll");
        Log.d("outside firstVisibleITem", Integer.toString(firstVisibleItem));
        Log.d("outside visibleItemCount", Integer.toString(visibleItemCount));
        Log.d("outside totalItemCount", Integer.toString(totalItemCount));
        Log.d("wif", "onScroll");*/

        if(popupWindow != null)
        {
            popupWindow.dismiss();

            popupWindow = null;
        }
        if(totalItemCount > 0) {
            if (firstVisibleItem + visibleItemCount == totalItemCount) { //check if we've reached the bottom
                /*Log.d("firstVisibleITem", Integer.toString(firstVisibleItem));
                Log.d("visibleItemCount", Integer.toString(visibleItemCount));
                Log.d("totalItemCount", Integer.toString(totalItemCount));*/
                // viewIndex = firstVisibleItem;
                if (totalItemCount > startWhoIFollow) {
                    startWhoIFollow = totalItemCount;
                   // Log.d("wifStart", Integer.toString(startWhoIFollow));
                    new DownloadComments().execute();
                }
            }
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Log.d("WhoIFollowRefresh", "onResume");
        //Log.d("wif", "onResume");
      /* swipeLayout.setRefreshing(false);
        refreshFlag = 1;
        myComments = new ArrayList<CommentInfo>();
        adapter = new MyListAdapter();
        l.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        startWhoIFollow = 0;
        new DownloadComments().execute();*/
    }
    public void RefreshPage(){
        swipeLayout.setRefreshing(false);
        //Log.d("wif", "RefreshPage");
        refreshFlag = 1;
        commentids = new ArrayList<String>();
        usernames = new ArrayList<String>();
        startWhoIFollow = 0;
        myComments = new ArrayList<CommentInfo>();
        adapter = new MyListAdapter();

        l.setAdapter(adapter);
        //adapter.notifyDataSetChanged();

        new DownloadComments().execute();
    }

    @Override
    public void onPause() {
        //Log.d("wif", "onpause");
        super.onPause();

    }

    class GetRepliesForCommentId extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          //  Log.d("GetRepliesForCommentId", "Pre-execute");
        }


        protected String doInBackground(String... args)  {
            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("comment_id", commentids.get(replyPosition)));

            // getting JSON Object

            //Log.d("GetRepliesForCommentId", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_getcurrent_replies,"POST", params);

            // check log cat fro response
            //Log.d("GetRepliesForCommentId", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");
                if(success == 1)
                {
                    status = "Inserted";
                    noofreplies = json.getString("replies");
                }
                else
                {
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
            if(status.equals("Inserted")) {
              //  Log.d("GetRepliesInserted", "Inserted");
                //Log.d("GetRepliesInserted", noofreplies);
                int firstPosition = l.getFirstVisiblePosition() - l.getHeaderViewsCount();
                int wantedChild = replyPosition - firstPosition;
                View theView = l.getChildAt(wantedChild);
                TextView tvReplies = (TextView) theView.findViewById(R.id.tvReplies);
                tvReplies.setText(noofreplies);
            }
            else {
            }
        }

    }


    class StoreKeywordsComments extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Log.d("StoreKeywordsComments", "Pre-execute");
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
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("avatar", imageUri));
            params.add(new BasicNameValuePair("comment", transformedSentence));

            // getting JSON Object

            //Log.d("AddComment", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_store_comments, "POST", params);

            // check log cat fro response
            //Log.d("AddComment", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");

                if (success == 1) {
                    // successfully created product
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

            } else {
                Toast toast = Toast.makeText(getActivity(), "The maximum length of comment is 200 chars", Toast.LENGTH_LONG);
                toast.show();
            }
        }

    }

    class DownloadAvatarKeywordsComments extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Log.d("DownloadAvatarKeywordsComments", "Pre-execute");
        }


        protected String doInBackground(String... args)  {

            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));
            //Log.d("emailDownloadAvatar", email);
            // getting JSON Object

            //Log.d("DownloadAvatarKeywordsComments", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_download_avatar,"POST", params);

            // check log cat fro response
            //Log.d("DownloadAvatarKeywordsComments", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");

                if (success == 1) {
                    // successfully created product
              //      Log.d("SuccessDownloadAvatarKeywordsComments", "Downloaded Avatar");

                    imageUri = json.getString("imageUri");
                    if(imageUri.isEmpty()) {
                //        Log.d("Avatar", imageUri);
                    }
                    username = json.getString("username");
                    name = username;
                  //  Log.d("Username", username);
                    //Log.d("imageUri", imageUri);
                    status = "Inserted";

                } else {

                    //Log.d("Fail", "could not log in user activity");

                    imageUri = null;
                    username = "Unknown";
                    name = username;
                    status = "Not Inserted";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return status;
        }


        protected void onPostExecute(String status) {

            new DownloadKeywords().execute();
        }

    }

    class DownloadKeywords extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Log.d("DownloadKeywords", "Pre-execute");
        }


        protected String doInBackground(String... args) {

            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON Object

  //          Log.d("DownloadKeywords", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_download_keywords, "POST", params);

            // check log cat fro response
    //        Log.d("DownloadKeywords", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");
                if (success == 1) {
      //              Log.d("DownloadKeywordsSuccess", "Downloaded Keywords successfully");
                    try {
                        try {
                            keywordsArray = json.getJSONArray("keywords");
                            for (int index = 0; index < keywordsArray.length(); index++) {
                                JSONObject obj = keywordsArray.getJSONObject(index);
                                String keyword = obj.getString("keyword");
                                keywordsList.add(keyword);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    status = "Inserted";
                } else {
        //            Log.d("DownloadKeywordsFail", "could not download keywords");
                    status = "Not Inserted";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return status;
        }


        protected void onPostExecute(String status) {

            new DownloadComments().execute();
            if (status.equals("Inserted")) {

            } else {

            }
        }
    }

    class DownloadComments extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          //  Log.d("DownloadComments", "Pre-execute");
        }


        protected String doInBackground(String... args)  {
            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("university", university));
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("start", Integer.toString(startWhoIFollow)));
            params.add(new BasicNameValuePair("count", Integer.toString(count)));
            // getting JSON Object
            /*Log.d("DownloadCommentsStart", Integer.toString(startWhoIFollow));
            Log.d("DownloadCommentsCount", Integer.toString(count));

            Log.d("DownloadComments", "Before-HttpRequest");*/
            JSONObject json = jsonParser.makeHttpRequest(url_download_comments,"POST", params);


            //Log.d("DownloadComments", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");
                if(success == 1)
                {
              //      Log.d("SuccessDownloadComments", "Downloaded comments successfully");
                    try {
                        commentsArray = json.getJSONArray("usercomments");
                        commentInfo = new String[commentsArray.length()][8];
                        for (int index = 0; index < commentsArray.length(); index++) {
                            JSONObject obj = commentsArray.getJSONObject(index);
                            String comment = obj.getString("comment");
                            String avatarPos = obj.getString("avatar");
                            String user = obj.getString("username");
                            String commentTime = obj.getString("time");
                            String likes = obj.getString("likes");
                            String comment_id = obj.getString("comment_id");
                            int likesflag = obj.getInt("likesflag");
                            int replies = obj.getInt("noofreplies");
                            //int avaPos = Integer.parseInt(avatarPos);
                            if(!commentids.contains(comment_id)) {
                                commentInfo[index][0] = comment;
                                commentInfo[index][1] = avatarPos;
                                commentInfo[index][2] = user;
                                commentInfo[index][3] = commentTime;
                                commentInfo[index][4] = likes;
                                commentInfo[index][5] = comment_id;
                                commentInfo[index][6] = Integer.toString(likesflag);
                                commentInfo[index][7] = Integer.toString(replies);
                                commentids.add(comment_id);
                                usernames.add(user);

                            }
                            else{
                                commentInfo[index][0] = null;
                                commentInfo[index][1] = null;
                                commentInfo[index][2] = null;
                                commentInfo[index][3] = null;
                                commentInfo[index][4] = null;
                                commentInfo[index][5] = null;
                                commentInfo[index][6] = null;
                                commentInfo[index][7] = null;
                            }
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
                //    Log.d("FailDownloadComments", "could not log in user activity");
                    ///Log.d("imageUri", imageUri);
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


    class Likes extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Log.d("likes", "Pre-execute");
        }


        protected String doInBackground(String... args)  {

            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("comment_id", commentids.get(likePosition)));
            //Log.d("LikesCommentID",commentids.get(likePosition) );
            params.add(new BasicNameValuePair("username", username));
            //Log.d("LikesCommentUsername", username);

            // getting JSON Object

            //Log.d("Likes", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_likes,"POST", params);

            // check log cat fro response
            //Log.d("Likes", "After-HttpRequest");

            // check for success tag
            try {
                int success = 0;
                try {
                    success = json.getInt("success");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                if(success == 1)
                {
                    try {
                        likeClickFlag = json.getString("likeflag");
                        status = "Inserted";

                        if(json.getString("message").trim().equals("New like to the comment")){
                            likeInsertedFlag = "1";

                        }
                        else {
                            likeInsertedFlag = "0";
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
                else
                {
                    status = "Not Inserted";
                    likeInsertedFlag="0";
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
              //  Log.d("Likes", "Inserted");
                new GetLikesForCommentId().execute();

            }
            else {

            }
        }

    }

    class GetLikesForCommentId extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Log.d("GetLikesForCommentId", "Pre-execute");
        }


        protected String doInBackground(String... args)  {

            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("comment_id", commentids.get(likePosition)));

            // getting JSON Object

            //Log.d("GetLikesForCommentId", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_getcurrent_likes,"POST", params);

            // check log cat fro response
            //Log.d("GetLikesForCommentId", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");
                if(success == 1)
                {
                    status = "Inserted";
                    nooflikes = json.getString("likes");
                }
                else
                {
                    ///Log.d("imageUri", imageUri);
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

            if(status.equals("Inserted")) {
              //  Log.d("GetLikesInserted", "Inserted");
                //Log.d("GetLikesInseted", nooflikes);
                int firstPosition = l.getFirstVisiblePosition() - l.getHeaderViewsCount();
                int wantedChild = likePosition - firstPosition;
                View theView = l.getChildAt(wantedChild);
                myCurrentCommentInfo.likes = nooflikes;
                ImageView ivlikes1 = (ImageView) theView.findViewById(R.id.likeBtn);
                if (likeClickFlag.equals("1")) {
                    myCurrentCommentInfo.likesflag = likeClickFlag;
                    ivlikes1.setImageResource(R.drawable.likebtnclicked);
                } else if (likeClickFlag.equals("0")) {
                    myCurrentCommentInfo.likesflag = likeClickFlag;
                    ivlikes1.setImageResource(R.drawable.likebtn);
                }

                TextView tvlikes1 = (TextView) theView.findViewById(R.id.tvLikes);
                tvlikes1.setText(nooflikes);
                /*cd = new ConnectionDetector(getApplicationContext());
                // Check if Internet present
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(Comment.this,
                            "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    // stop executing code by return
                }

                // Check if GCM configuration is set
                else if (SERVER_URL == null || SENDER_ID == null || SERVER_URL.length() == 0
                        || SENDER_ID.length() == 0) {
                    // GCM sernder id / server url is missing
                    alert.showAlertDialog(Comment.this, "Configuration Error!",
                            "Please set your Server URL and GCM Sender ID", false);
                    // stop executing code by return
                } else {


                    GCMRegistrar.checkDevice(getApplicationContext());

                    // Make sure the manifest was properly set - comment out this line
                    // while developing the app, then uncomment it when it's ready.
                    GCMRegistrar.checkManifest(getApplicationContext());
                    registerReceiver(mHandleMessageReceiver, new IntentFilter(
                            DISPLAY_MESSAGE_ACTION));
                    final String regId = GCMRegistrar.getRegistrationId(getApplicationContext());
                    if (regId.equals("")) {
                        // Registration is not present, register now with GCM
                        GCMRegistrar.register(getApplicationContext(), SENDER_ID);
                    } else {
                        // Device is already registered on GCM
                        if (GCMRegistrar.isRegisteredOnServer(getApplicationContext())) {
                            // Skips registration.
                            Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
                        } else {
                            // Try to register again, but not in the UI thread.
                            // It's also necessary to cancel the thread onDestroy(),
                            // hence the use of AsyncTask instead of a raw thread.
                            final Context context = getApplicationContext();
                            mRegisterTask = new AsyncTask<Void, Void, Void>() {

                                @Override
                                protected Void doInBackground(Void... params) {
                                    // Register on our server
                                    // On server creates a new user
                                    ServerUtilities.register(context, name, regId);
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void result) {
                                    mRegisterTask = null;
                                }

                            };
                            mRegisterTask.execute(null, null, null);
                        }
                    }
                }*/
                if(likeInsertedFlag.equals("1") ) {
                    /*Parse.initialize(getApplicationContext(), "Vm2SZDsZB0VG625ExulASNO3UFgojXTH0jlocuK7", "rQDDw2KUWnZ2HAjBxdZs1flgBREIILmgVmyURdKS");
                    ParsePush.subscribeInBackground("", new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                            } else {
                                Log.e("com.parse.push", "failed to subscribe for push", e);
                            }
                        }
                    });*/
                    cd = new ConnectionDetector(getActivity());
                    if (cd.isConnectingToInternet() && parseStatusFlag == 1) {
                        // Internet Connection is not present
                        Parse.initialize(getActivity(), "Vm2SZDsZB0VG625ExulASNO3UFgojXTH0jlocuK7", "rQDDw2KUWnZ2HAjBxdZs1flgBREIILmgVmyURdKS");
                        ParsePush.subscribeInBackground("", new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                  //                  Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                                } else {
                    //                Log.e("com.parse.push", "failed to subscribe for push", e);
                                }
                            }
                        });
                        PushService.setDefaultPushCallback(getActivity(),Comment.class);
                        ParsePush pp = new ParsePush();
                        TextView tvUsername = (TextView) theView.findViewById(R.id.tvUserName);
                        String message = username + " " + "likes " +  tvUsername.getText().toString();
                        pp.setMessage(message);
                        pp.setPushToAndroid(true);
                        pp.sendInBackground();
                        // stop executing code by return
                    }
                    likeInsertedFlag = "0";

                }

            }
            else {
                //Toast toast = Toast.makeText(getApplicationContext(), "Could not populate the comment screen", Toast.LENGTH_LONG);
                //toast.show();
            }
        }

    }


    private class MyListAdapter extends ArrayAdapter<CommentInfo> {
        public MyListAdapter()
        {
            super(getActivity(), R.layout.listview_xml, myComments);
        }
        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent)
        {
            itemView = convertView;
            Bundle bundle = new Bundle();
            if(itemView == null)
            {
                itemView = layoutInflater.inflate(R.layout.listview_xml, parent, false);
                final CommentInfo currentComment = myComments.get(position);

                ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
                if((currentComment.avaPos).isEmpty()) {
                    imageView.setImageResource(R.drawable.defaultavatar);
                }
                else{
                    Picasso.with(getActivity()).load("http://cmpln.com/images/user/"+currentComment.avaPos).into(imageView);
                    //Picasso.with(getActivity()).load("http://cmpln.com/images/user/"+currentComment.avaPos).into(imageView);
                }

                TextView tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
                tvUsername.setTypeface(null, Typeface.BOLD);
                tvUsername.setText(currentComment.userName);

                TextView tvComment = (TextView) itemView.findViewById(R.id.tvComment);
                tvComment.setText(Html.fromHtml(currentComment.commentSentence));

                TextView tvTime = (TextView) itemView.findViewById(R.id.tvTime);
                tvTime.setText(currentComment.time);

                TextView tvReplies = (TextView) itemView.findViewById(R.id.tvReplies);
                tvReplies.setText(currentComment.noofreplies);

                final TextView tvLikes = (TextView) itemView.findViewById(R.id.tvLikes);
                tvLikes.setText(currentComment.likes);

                final ImageView ivLikes = (ImageView) itemView.findViewById(R.id.likeBtn);
                if(currentComment.likesflag.equals("1"))
                {
                    ivLikes.setImageResource(R.drawable.likebtnclicked);
                }
                else if(currentComment.likesflag.equals("0")){
                    ivLikes.setImageResource(R.drawable.likebtn);
                }
               /* ivLikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likePosition = position;
                        myCurrentCommentInfo = currentComment;
                        g_itemView = itemView;
                        new Likes().execute();

                    }
                });*/
                ImageView reply = (ImageView) itemView.findViewById(R.id.replyBtn);
                reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        replyFlag = 1;
                        replyPosition = position;
                        Intent intent = new Intent(getActivity(), Reply.class);
                        intent.putExtra("comment_id", commentids.get(position));
                        intent.putExtra("username", username);
                        intent.putExtra("keywords", keywordsList);
                        intent.putExtra("email", email);
                        intent.putExtra("avatar", imageUri);
                        intent.putExtra("university", university);
                        startActivity(intent);
                    }
                });
                ImageView follows = (ImageView) itemView.findViewById(R.id.followUserBtn);
                if(currentComment.followed_flag.equals("1")){
                    follows.setVisibility(View.GONE);
                }
                RelativeLayout relativeView = (RelativeLayout) itemView.findViewById(R.id.relative);

                return itemView;
            }
            else{
                final CommentInfo currentComment = myComments.get(position);

                ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
                if((currentComment.avaPos).isEmpty()) {
                    imageView.setImageResource(R.drawable.defaultavatar);
                }
                else{
                    Picasso.with(getActivity()).load("http://cmpln.com/images/user/"+currentComment.avaPos).skipMemoryCache().into(imageView);
                    //Picasso.with(getActivity()).load("http://cmpln.com/images/user/"+currentComment.avaPos).into(imageView);
                }

                TextView tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
                tvUsername.setTypeface(null, Typeface.BOLD);
                tvUsername.setText(currentComment.userName);

                TextView tvComment = (TextView) itemView.findViewById(R.id.tvComment);
                tvComment.setText(Html.fromHtml(currentComment.commentSentence));

                TextView tvTime = (TextView) itemView.findViewById(R.id.tvTime);
                tvTime.setText(currentComment.time);

                TextView tvReplies = (TextView) itemView.findViewById(R.id.tvReplies);
                tvReplies.setText(currentComment.noofreplies);

                final TextView tvLikes = (TextView) itemView.findViewById(R.id.tvLikes);
                tvLikes.setText(currentComment.likes);

                final ImageView ivLikes = (ImageView) itemView.findViewById(R.id.likeBtn);
                if(currentComment.likesflag.equals("1"))
                {
                    ivLikes.setImageResource(R.drawable.likebtnclicked);
                }
                else if(currentComment.likesflag.equals("0")){
                    ivLikes.setImageResource(R.drawable.likebtn);
                }
                ivLikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ((username == null || username.equals("Unknown")) ) {
                            Toast.makeText(getActivity(), "Enter username in EditProfile menu", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            likePosition = position;
                            myCurrentCommentInfo = currentComment;
                            g_itemView = itemView;
                            new Likes().execute();
                        }
                    /*if(textViewFlag == 1) {
                        TextView tvlikes1 = (TextView) convertView.findViewById(R.id.tvLikes);
                        tvlikes1.setText("999");
                        textViewFlag = 0;
                    }*/
                        //ExecuteLikes();

                    }
                });
                ImageView reply = (ImageView) itemView.findViewById(R.id.replyBtn);
                reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        replyFlag = 1;
                        replyPosition = position;
                        Intent intent = new Intent(getActivity(), Reply.class);
                        intent.putExtra("comment_id", commentids.get(position));
                        intent.putExtra("username", username);
                        intent.putExtra("keywords", keywordsList);
                        intent.putExtra("email", email);
                        intent.putExtra("university", university);
                        startActivity(intent);
                    }
                });
                ImageView follows = (ImageView) itemView.findViewById(R.id.followUserBtn);
                if(currentComment.followed_flag.equals("1")){
                    follows.setVisibility(View.GONE);
                }
                RelativeLayout relativeView = (RelativeLayout) itemView.findViewById(R.id.relative);

                return itemView;
            }
        }
    }
    @Override
    public void onRefresh()
    {
        //Log.d("wif", "onRefresh");
        swipeLayout.setRefreshing(false);
        refreshFlag = 1;
        commentids = new ArrayList<String>();
        usernames = new ArrayList<String>();
        myComments = new ArrayList<CommentInfo>();
        //adapter.notifyDataSetChanged();
        adapter = new MyListAdapter();
        l.setAdapter(adapter);
        startWhoIFollow = 0;
        new DownloadComments().execute();
    }
    public void TransformCommentKeys()
    {
        String[] strArray = commentSentence.split("[, ' ' ; - .! ? \\t \\n \\r \\f]");
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
                target = "^" + strArray[i];
                replacement = "<font color='#9933cc'>" + strArray[i] + "</font>";
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
                target = "&nbsp;" + strArray[i] + "!";
                replacement = "&nbsp;" + "<font color='#9933cc'>" + strArray[i] + "</font>" + "!";
                commentSentence = commentSentence.replaceAll(target, replacement);
                target = "&nbsp;" + strArray[i] + "\\?";
                replacement = "&nbsp;" + "<font color='#9933cc'>" + strArray[i] + "</font>" + "?";
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
}
