package com.instamour.mathu.cmpln;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.astuetz.PagerSlidingTabStrip;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.parse.*;

import static com.instamour.mathu.cmpln.CommonUtilites.EXTRA_MESSAGE;
import android.content.BroadcastReceiver;

public class Comment extends FragmentActivity implements AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, ActionBar.TabListener {

    AlertDialogManager alert = new AlertDialogManager();
    AsyncTask<Void, Void, Void> mRegisterTask;
    ConnectionDetector cd;
    String email;
    View itemView;
    PopupWindow popupWindow=null;
    int likePosition=0, start = 0, count = 5, pushNotificationFlag = 0, parseStatusFlag=1;
    JSONParser jsonParser = new JSONParser();
    JSONArray commentsArray = null, keywordsArray = null;
    String[][] commentInfo = null;
    String imageUri, nooflikes = null, likeClickFlag = null, likeInsertedFlag=null;
    static String name;
    final Context context = this;
    ArrayList<String> uploadKeywordsList = null;
    ArrayList<String> keywordsList = new ArrayList<String>();
    ArrayList<String> commentids = new ArrayList<String>();
    ArrayList<String> usernames = new ArrayList<String>();
    String transformedSentence = null;
    String untransformedSentence = null;
    String university = null, commentSentence = null;
    EditText comment;
    CommentInfo myCurrentCommentInfo;
    View popupview;
    TextView cmplntText, tvLikes;
    int viewIndex = 0, refreshFlag = 0, textViewFlag =0;
    LinearLayout l1;
    ListView l, listViewSlidingWindow;
    String username = null;
    ScrollView sv;
    boolean downloadFollowComments = false, downloadWhoIFollowComments = false;
    List<CommentInfo> myComments;
    SwipeRefreshLayout swipeLayout;
    ArrayAdapter<CommentInfo> adapter;
    String[] spinnerValues;
    View g_itemView;
    ViewPager Tab;
    TabPagerAdapter tabPageAdapter;
    ActionBar actionBar;

    private static String url_download_avatar = "http://cmpln.com/Scripts/DownloadAvatar.php";
    private static String url_download_comments = "http://cmpln.com/Scripts/DownloadComments.php";
    private static String url_download_keywords = "http://cmpln.com/Scripts/DownloadKeywords.php";
    private static String url_store_keywords = "http://cmpln.com/Scripts/StoreKeywords.php";
    private static String url_store_comments = "http://cmpln.com/Scripts/StoreComment.php";
    private static String url_likes = "http://cmpln.com/Scripts/likes.php";
    private static String url_getcurrent_likes = "http://cmpln.com/Scripts/CommentIdLikes.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        l1 = (LinearLayout) findViewById(R.id.linear1);
        //Intent intent = getIntent();
        /*if(intent != null) {
            email = (String) intent.getExtras().get("email");
        }
        if(intent != null) {
            university = (String) intent.getExtras().get("university");
            university = university.substring(0, 1).toUpperCase() + university.substring(1);
        }*/
        SharedPreferences userAccount = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
       // Log.d("CommentEmail", userAccount.getString("username", "username"));
        if((userAccount.getString("username", null)== null) && (userAccount.getString("university", null) == null)){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        else{
            email = userAccount.getString("username", "");
            university = userAccount.getString("university", "");
            university = Character.toUpperCase(university.charAt(0)) + university.substring(1);
            sv = (ScrollView) findViewById(R.id.scroll);
            sv.setFillViewport(true);
            cmplntText = (TextView)findViewById(R.id.textViewCmplnts);
            cmplntText.setText(university+" "+"Cmplnts");
            myComments = new ArrayList<CommentInfo>();

            adapter = new MyListAdapter();

            listViewSlidingWindow = (ListView) findViewById(R.id.drawerList);
            listViewSlidingWindow.setOnItemClickListener(this);
            //Below 2 lines are google analytics tracker
            AnalyticsTrackers.initialize(this);
            AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
            this.trackScreenView("Comment Screen");
            //To be uncommented later
            //new DownloadAvatarKeywordsComments().execute();
            final ViewPager pager = (ViewPager)findViewById(R.id.viewPager);
            pager.setOffscreenPageLimit(0);
            pager.setAdapter(new TabPagerAdapter(getSupportFragmentManager(), email, university));
            final PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
            tabs.setViewPager(pager);
            tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    Fragment fragment = null;

                    List<Fragment> allFragments = getSupportFragmentManager().getFragments();
                    if(allFragments != null){
                        for(Fragment fragment1 : allFragments){
                            if(fragment1 != null) {
                                if (fragment1.isVisible()) {
                                    if(fragment1 instanceof AllComments)
                                    {
                                        AllComments ac = (AllComments)fragment1;
                                        ac.RefreshPage();
                                    }
                                    else if(fragment1 instanceof FollowerComments)
                                    {
                                        FollowerComments fc = (FollowerComments)fragment1;
                                        fc.RefreshPage();
                                    }
                                    else if(fragment1 instanceof WhoIFollowComments)
                                    {
                                        WhoIFollowComments wif = (WhoIFollowComments)fragment1;
                                        wif.RefreshPage();
                                    }
                                }
                            }
                        }
                    }
                    switch (position){
                        case 0:
                           // Log.d("PageSelected", "AllComments");

                            break;

                        case 1:
                            //Log.d("PageSelected", "FollowerComments");

                            break;

                        case 2:
                            //Log.d("PageSelected", "WhoIFollow");
                            break;

                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            new DownloadAvatarKeywordsComments().execute();
        }

    }

    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }

    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }


    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                            .setDescription(
                                    new StandardExceptionParser(this, null)
                                            .getDescription(Thread.currentThread().getName(), e))
                            .setFatal(false)
                            .build()
            );
        }
    }

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }
    @Override
    public void onBackPressed()
    {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.d("Comment", "onResume");

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(position == 0)
        {
            Intent intent = new Intent(this, EditProfile.class);
            intent.putExtra("email", email);
            intent.putExtra("university", university);
            startActivity(intent);
        }
        if(position == 1)
        {
            Intent intent = new Intent(this, Support.class);
            intent.putExtra("email", email);
            intent.putExtra("university", university);
            intent.putExtra("username", username);
            startActivity(intent);
        }
        if(position == 2)
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            SharedPreferences userAccount = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = userAccount.edit();
            editor.putString("username", null);
            editor.putString("university", null);
            editor.apply();
            startActivity(intent);
        }
        if(position == 3){
            Intent intent =  new Intent(this, ChooseToFollow.class);
            intent.putExtra("university", university);
            intent.putExtra("username", username);
            intent.putExtra("email", email);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.d("CommentStart", "Start");
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comment, menu);
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

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());

            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */

            // Showing received message
            Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();

            // Releasing wake lock
            WakeLocker.release();
        }
    };
    public void Comment(View view) {

        Intent intent = new Intent(getApplicationContext(), post.class);
        intent.putExtra("username", username);
        //Log.d("usernamePost", username);
        intent.putExtra("university", university);
        intent.putExtra("email", email);
        intent.putExtra("avatar", imageUri);
        intent.putExtra("keywords", keywordsList);
        startActivity(intent);
    }


    public void Cancel(View view)
    {
        popupWindow.dismiss();
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
                    target = strArray[index]+"\n";
                    replacement = "#" + strArray[index] + "\n";
                    transformedSentence = transformedSentence.replaceAll(target, replacement);
                    target = strArray[index];
                    replacement = "#" + strArray[index];
                    transformedSentence = transformedSentence.replaceAll(target, replacement);
                }
            }
        }
    }

    public void GoToSettings(View view) {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.openDrawer(listViewSlidingWindow);
    }

    public void DismissPopup(View view)
    {
        if(popupWindow != null)
        {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
    public void PopulateComments() {
        int length_index = commentInfo.length;
        int length = commentInfo.length;
        if(length_index > count)
        {
            length_index = length_index - 1;
        }
        for (int index = 0; index < length_index; index++) {
            commentSentence = commentInfo[index][0];
            //Log.d("Comment", commentSentence);
            TransformCommentKeys();
            //Log.d("comment", commentSentence);

            String avaPos = commentInfo[index][1];
            String username = commentInfo[index][2];
            String commentTime = commentInfo[index][3];
            String likes = commentInfo[index][4];
            String likesflag = commentInfo[index][6];
            //New code
            //myComments.add(new CommentInfo(avaPos, commentSentence, username,commentTime, likes, likesflag ));
           /* LinearLayout l2 = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            l2.setLayoutParams(params);
            l2.setOrientation(LinearLayout.HORIZONTAL);
            l1.addView(l2);


            LinearLayout l3 = new LinearLayout(this);
            l2.addView(l3);


            ImageView i2 = new ImageView(this);
            i2.setImageResource(avaPos);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.setMargins(10, 10, 10, 10);
            i2.setLayoutParams(llp);
            l3.addView(i2);

            LinearLayout.LayoutParams l2Params = (LinearLayout.LayoutParams)l2.getLayoutParams();
            LinearLayout.LayoutParams l3Params = (LinearLayout.LayoutParams) l3.getLayoutParams();
            Log.d("l1Width", Integer.toString(l1.getWidth()));
            Log.d("l2width", Integer.toString(l2.getWidth()));
            Log.d("l3Width", Integer.toString(l3.getWidth()));
            int l4Width = l1.getWidth() - 130;
            LinearLayout l4 = new LinearLayout(this);
            l4.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(l4Width, LinearLayout.LayoutParams.WRAP_CONTENT);
            l4.setLayoutParams(params1);
            l2.addView(l4);

            RelativeLayout r1 = new RelativeLayout(this);
            RelativeLayout.LayoutParams params2 =  new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            r1.setLayoutParams(params2);
            l4.addView(r1);

            TextView t21 = new TextView(this);
            RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params3.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            t21.setLayoutParams(params3);
            t21.setTextColor(Color.BLACK);
            t21.setTypeface(null, Typeface.BOLD);
            t21.setText(username);
            r1.addView(t21);

            TextView t22 = new TextView(this);
            RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            t22.setLayoutParams(params4);
            t22.setTextColor(Color.BLACK);
            t22.setText("1h");
            r1.addView(t22);

            LinearLayout l5 = new LinearLayout(this);
            LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            l5.setLayoutParams(params5);
            l5.setOrientation(LinearLayout.VERTICAL);
            l4.addView(l5);

            TextView t23 = new TextView(this);
            t23.setTextColor(Color.BLACK);
            t23.setText(Html.fromHtml(commentSentence));
            //t23.setText((CharSequence)commentSentence);
            l5.addView(t23);

            View ruler = new View(this);
            ruler.setBackgroundColor(Color.GRAY);
            LinearLayout.LayoutParams viewParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
            viewParam.setMargins(0, 30, 0, 0);
            ruler.setLayoutParams(viewParam);
            l5.addView(ruler);*/
        }
        /*if(length > count) {
            final Button button = new Button(this);
            button.setText("Show More");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    l1.removeView(button);
                    start = start + count;
                    new DownloadComments().execute();
                }
            });
            l1.addView(button);

        }*/
        /*try {
            ArrayAdapter<CommentInfo> adapter = new MyListAdapter();
            //ListView l = (ListView) findViewById(R.id.listView);
            l.setAdapter(adapter);
            l.setOnScrollListener((android.widget.AbsListView.OnScrollListener) this);
            if(refreshFlag == 0) {
                l.setSelection(viewIndex);
            }
            else
            {
                refreshFlag = 0;
                l.setSelection(0);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        /*Log.d("scroll", "scroll");
        Log.d("outside firstVisibleITem", Integer.toString(firstVisibleItem));
        Log.d("outside visibleItemCount", Integer.toString(visibleItemCount));
        Log.d("outside totalItemCount", Integer.toString(totalItemCount));*/
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
                if(totalItemCount > start) {
                    start = totalItemCount;
                    new DownloadComments().execute();
                    Toast.makeText(getApplicationContext(), "Loading", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }
    @Override
    public void onRefresh()
    {
        swipeLayout.setRefreshing(false);
        refreshFlag = 1;
        myComments = new ArrayList<CommentInfo>();
        //adapter.notifyDataSetChanged();
        adapter = new MyListAdapter();
        l.setAdapter(adapter);
        start = 0;
        new DownloadComments().execute();
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
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

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
                // params.add(new BasicNameValuePair("keyword[]", dummyKeywordsArray[1]));
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
                    //Log.d("SuccessAddComment", "comment added");
                    status = "Inserted";
                } else {
                    //Log.d("FailAddComment", "could not add comment");
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
                Toast toast = Toast.makeText(getApplicationContext(), "The maximum length of comment is 200 chars", Toast.LENGTH_LONG);
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
              //      Log.d("SuccessDownloadAvatarKeywordsComments", "Downloaded Avatar");
                    imageUri = json.getString("imageUri");
                    if(imageUri.isEmpty()) {
                //        Log.d("Avatar", imageUri);
                    }
                    username = json.getString("username");
                    name = username;
                  /*  Log.d("Username", username);
                    Log.d("imageUri", imageUri);*/
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
            //Log.d("DownloadKeywords", "Pre-execute");
        }
        protected String doInBackground(String... args) {
            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON Object
            //Log.d("DownloadKeywords", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_download_keywords, "POST", params);

            // check log cat fro response
            //Log.d("DownloadKeywords", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");
                if (success == 1) {
              //      Log.d("DownloadKeywordsSuccess", "Downloaded Keywords successfully");
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
                //    Log.d("DownloadKeywordsFail", "could not download keywords");
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

    class DownloadComments extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Log.d("DownloadComments", "Pre-execute");
        }

        protected String doInBackground(String... args)  {
            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("university", university));
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("start", Integer.toString(start)));
            params.add(new BasicNameValuePair("count", Integer.toString(count)));
            // getting JSON Object
            /*Log.d("DownloadCommentsStart", Integer.toString(start));
            Log.d("DownloadCommentsCount", Integer.toString(count));

            Log.d("DownloadComments", "Before-HttpRequest");*/
            JSONObject json = jsonParser.makeHttpRequest(url_download_comments,"POST", params);

            // check log cat fro response
            //Log.d("DownloadComments", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");
                if(success == 1)
                {
              //      Log.d("SuccessDownloadComments", "Downloaded comments successfully");
                    try {
                        commentsArray = json.getJSONArray("usercomments");
                        commentInfo = new String[commentsArray.length()][7];
                        for (int index = 0; index < commentsArray.length(); index++) {
                            JSONObject obj = commentsArray.getJSONObject(index);
                            String comment = obj.getString("comment");
                            String avatarPos = obj.getString("avatar");
                            String user = obj.getString("username");
                            String commentTime = obj.getString("time");
                            String likes = obj.getString("likes");
                            String comment_id = obj.getString("comment_id");
                            int likesflag = obj.getInt("likesflag");

                            commentInfo[index][0] = comment;
                            commentInfo[index][1] = avatarPos;
                            commentInfo[index][2] = user;
                            commentInfo[index][3] = commentTime;
                            commentInfo[index][4] = likes;
                            commentInfo[index][5] = comment_id;
                            commentInfo[index][6] = Integer.toString(likesflag);
                            commentids.add(comment_id);
                            usernames.add(user);
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

            }
            else {
            }
        }

    }

    public void ExecuteLikes(){
        new Likes().execute();
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
              /*  Log.d("GetLikesInserted", "Inserted");
                Log.d("GetLikesInseted", nooflikes);*/
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
                    cd = new ConnectionDetector(getApplicationContext());
                    if (cd.isConnectingToInternet() && parseStatusFlag == 1) {
                        // Internet Connection is not present
                        Parse.initialize(getApplicationContext(), "Vm2SZDsZB0VG625ExulASNO3UFgojXTH0jlocuK7", "rQDDw2KUWnZ2HAjBxdZs1flgBREIILmgVmyURdKS");
                        ParsePush.subscribeInBackground("", new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    //Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                                } else {
                                    //Log.e("com.parse.push", "failed to subscribe for push", e);
                                }
                            }
                        });
                        PushService.setDefaultPushCallback(getApplicationContext(), Comment.class);
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

            }
        }

    }

    private class MyListAdapter extends ArrayAdapter<CommentInfo> {
        public MyListAdapter()
        {
            super(Comment.this, R.layout.listview_xml, myComments);
        }
        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent)
        {
            itemView = convertView;

            if(itemView == null)
            {
                itemView = getLayoutInflater().inflate(R.layout.listview_xml, parent, false);

            }
            final CommentInfo currentComment = myComments.get(position);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            if((currentComment.avaPos).isEmpty()) {
                imageView.setImageResource(R.drawable.defaultavatar);
            }
            else{

            }

            TextView tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvUsername.setTypeface(null, Typeface.BOLD);
            tvUsername.setText(currentComment.userName);

            TextView tvComment = (TextView) itemView.findViewById(R.id.tvComment);
            tvComment.setText(Html.fromHtml(currentComment.commentSentence));

            TextView tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvTime.setText(currentComment.time);

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
                    likePosition = position;
                    myCurrentCommentInfo = currentComment;
                    g_itemView = itemView;
                    new Likes().execute();
                }
            });
            ImageView reply = (ImageView) itemView.findViewById(R.id.replyBtn);
            reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Reply.class);
                    intent.putExtra("comment_id", commentids.get(position));
                    intent.putExtra("username", username);
                    intent.putExtra("keywords", keywordsList);
                    intent.putExtra("email", email);
                    intent.putExtra("university", university);
                    startActivity(intent);
                }
            });
            RelativeLayout relativeView = (RelativeLayout) itemView.findViewById(R.id.relative);
            return itemView;
        }
    }
}
