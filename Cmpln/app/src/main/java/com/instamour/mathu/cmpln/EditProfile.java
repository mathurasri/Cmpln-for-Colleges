package com.instamour.mathu.cmpln;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jibble.simpleftp.SimpleFTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class EditProfile extends ActionBarActivity {

    String email = null, university = null, filename = null;
    String avatarString = null, userName = null, bio = null;
    String imageUri = null, username = null, Bio = null;
    String filePath = null;
    ByteArrayOutputStream baos;
    Bitmap bmp = null;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1777;
    private static final int REQUEST_CROP_ICON = 1778;
    Dialog dialog;
    ImageView imageView;
    Uri mImageUri;
    int counter = 0;
    Bitmap photo = null;
    EditText profileName, userBio;
    TextView saveTV;
    JSONParser jsonParser = new JSONParser();
    private static final int SELECTED_PICTURE = 1;
    private static String url_store_user_profile = "http://cmpln.com/Scripts/StoreUserProfile.php";
    private static String url_download_avatar = "http://cmpln.com/Scripts/DownloadAvatar.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Intent intent = getIntent();
        if(intent != null)
        {
            university = (String)intent.getExtras().get("university");
            email = (String)intent.getExtras().get("email");
        }
        imageView = (ImageView) findViewById(R.id.profilePic);
        profileName = (EditText) findViewById(R.id.userNameProfile);
        userBio = (EditText) findViewById(R.id.editTextBio);
        saveTV = (TextView) findViewById(R.id.saveProfile);
        new DownloadProfile().execute();
    }
    @Override
    public void onBackPressed()
    {

    }

    public void CancelEdit(View view)
    {
        Intent intent = new Intent(getApplicationContext(), Comment.class);
        intent.putExtra("email", email);
        intent.putExtra("university", university);
        startActivity(intent);
    }
    public void ShowPhotoScreen(View view)
    {
        saveTV.setTextColor(Color.parseColor("#00CED1"));
        saveTV.setClickable(true);
        dialog = new Dialog(EditProfile.this);
        dialog.setContentView(R.layout.activity_photo);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        Button cancelPhoto = (Button) dialog.findViewById(R.id.cancelPhoto);
        cancelPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        Button takePhoto = (Button) dialog.findViewById(R.id.takePhoto);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureImage();
            }
        });
        Button existingPhoto = (Button) dialog.findViewById(R.id.existingPhoto);
        existingPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
// call android default gallery
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
// ******** code for crop image
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 0);
                intent.putExtra("aspectY", 0);
                intent.putExtra("outputX", 96);
                intent.putExtra("outputY", 96);

                try {

                    intent.putExtra("return-data", true);
                    startActivityForResult(Intent.createChooser(intent,
                            "Complete action using"), SELECTED_PICTURE);

                } catch (ActivityNotFoundException e) {
                }
            }
        });
        dialog.show();
    }

    public void ChangeTextColorAndClickStatus(View view)
    {
        saveTV.setTextColor(Color.parseColor("#551A8B"));
        saveTV.setClickable(true);
    }
    // Crop and resize the image for profile
    private void cropImageTakenPhoto() {
        // Use existing crop activity.
        Intent intent = new Intent("com.android.camera.action.CROP");
        String filePath = Environment.getExternalStorageDirectory()+File.separator + filename;
        File file = new File(filePath);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "image/*");

        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 96);
        intent.putExtra("outputY", 96);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_CROP_ICON);
    }


    private void cropImageExistingPhoto() {
        // Use existing crop activity.
        Intent intent = new Intent("com.android.camera.action.CROP");
        String filePath1 = Environment.getExternalStorageDirectory()+File.separator + filePath;
        File file = new File(filePath1);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "image/*");

        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 96);
        intent.putExtra("outputY", 96);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_CROP_ICON);
    }

    public void CaptureImage()
    {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        filename = timeStamp+"."+"jpg";
        File file = new File(Environment.getExternalStorageDirectory()+File.separator + filename);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        //start camera intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
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
    class DownloadProfile extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // Log.d("DownloadProfile", "Pre-execute");
        }

        protected String doInBackground(String... args)  {

            String status = null;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));
            //Log.d("emailDownloadProfile", email);
            // getting JSON Object
            //Log.d("DownloadProfile", "Before-HttpRequest");
            JSONObject json = jsonParser.makeHttpRequest(url_download_avatar,"POST", params);

            // check log cat fro response
            //Log.d("DownloadProfile", "After-HttpRequest");

            // check for success tag
            try {
                int success = json.getInt("success");

                if (success == 1) {
              //      Log.d("SuccessDownloadProfile", "Downloaded Profile");
                    imageUri = json.getString("imageUri");
                //    Log.d("DownloadProfile", imageUri);
                    username = json.getString("username");
                    Bio = json.getString("bio");

                    status = "success";

                } else {
                  //  Log.d("FailDownloadProfile", "could not download Profile");
                    status = "fail";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return status;
        }

        protected void onPostExecute(String status) {

            if(username == null || username.isEmpty() || username.equals("Unknown")) {
                profileName.setHint("Enter Username");
            }
            else
            {
                profileName.setText(username);
                //We should not allow users to update profile name
                profileName.setKeyListener(null);
            }
            if(Bio == null || Bio.isEmpty() || Bio.equals("Unknown"))
            {
                userBio.setHint("Enter Bio");
            }
            else
            {
                userBio.setText(Bio);
            }
            if(imageUri == null || imageUri.isEmpty())
            {
                imageView.setImageResource(R.drawable.defaultavatar);
            }
            else {
                new DownloadImage().execute();
                //Picasso.with(getApplicationContext()).load("http://cmpln.com/images/user/"+imageUri).into(imageView);
                //Picasso.with(getApplicationContext()).load("http://cmpln.com/images/user/"+imageUri).into(imageView);]
            }

        }

    }

    class DeleteOldImage extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Log.d("DeleteOldImage", "Pre-execute");
        }

        protected String doInBackground(String... args)  {

            String status = null;
            FTPClient client = new FTPClient();
            try {
                client.connect("ftp.cmpln.com", 21);
                if(client.login("cmpln", "trashtalk")){
              //      Log.e("ftpclient", "Loggedin");
                }
                if(client.changeWorkingDirectory("cmpln.com/images/user/")){
                //    Log.e("ftpclient", "changedworkingdirectory");
                }
                if(client.deleteFile(imageUri)){
                  //  Log.e("ftpclient","DeletedFile");
                }
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return status;
        }

        protected void onPostExecute(String status) {
            SimpleFTP ftp = new SimpleFTP();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try {
                ftp.connect("ftp.cmpln.com", 21, "cmpln", "trashtalk");
                ftp.bin();
                if (ftp.cwd("cmpln.com/images/user/")) {
                    //Log.e("Success ftp", "Success cwd");
                } else {
                    //Log.e("fail ftp", "fail cwd");
                }
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                avatarString = userName +timeStamp+ ".png";
                //avatarString = "jh1" + ".png";

                    if (ftp.stor(new ByteArrayInputStream(baos.toByteArray()), avatarString)) {
                      //  Log.e("success", "Success stor");
                    } else {
                        //Log.e("fail", "Fail stor");
                    }

                ftp.disconnect();
//                Picasso.with(getApplicationContext()).invalidate("http://cmpln.com/images/user/"+avatarString);
                //Picasso.with(getApplicationContext()).load("http://cmpln.com/images/user/"+avatarString).memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView);
            } catch (IOException e) {
                e.printStackTrace();
                //Log.e("Fail Image", "Fail Image Load");
            }
            new UpdateProfile().execute();
        }

    }
    class DownloadImage extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Log.d("DownloadImage", "Pre-execute");
        }

        protected String doInBackground(String... args)  {

           String status = null;
            URL url = null;
            try {
                url = new URL("http://cmpln.com/images/user/" + imageUri);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                url.openConnection().setDefaultUseCaches(false);
                url.openConnection().setUseCaches(false);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return status;
        }

        protected void onPostExecute(String status) {

            imageView.setImageBitmap(bmp);
        }

    }
    class UpdateProfile extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Log.d("UpdateProfile", "pre-execute");
        }

        protected String doInBackground(String... args)  {

            String status = null;
            if(avatarString == null || avatarString.isEmpty())
            {
                avatarString = null;
            }
            if(userName == null || userName.isEmpty())
            {
                userName = null;
            }
            if(bio == null || bio.isEmpty())
            {
                bio = null;
            }
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("imageuri", avatarString));
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

            if(status.equals("Inserted"))
            {
                Toast toast = Toast.makeText(getApplicationContext(), "User Profile updated", Toast.LENGTH_LONG);
                toast.show();
                Intent intent = new Intent(getApplicationContext(), Comment.class);
                intent.putExtra("email", email);
                String university = FindUniversity(email);
                intent.putExtra("university", university);
                startActivity(intent);
            }
            else
            {

            }
        }

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
    public void SaveProfile(View view)
    {
        saveTV.setClickable(true);
        userName = profileName.getText().toString();
        bio = userBio.getText().toString();

        if(userName == null || userName.isEmpty()){
            Toast.makeText(getApplicationContext(), "Enter username", Toast.LENGTH_SHORT).show();
           saveTV.setClickable(false);
        }
        else{
            saveTV.setClickable(true);
            if(baos != null){
                new DeleteOldImage().execute();
            }
            else{
                new UpdateProfile().execute();
            }
        }



    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {

            // Get the dimensions of the View
            cropImageTakenPhoto();
            dialog.cancel();

        }
        else if (requestCode == REQUEST_CROP_ICON && resultCode == RESULT_OK){
            Bundle extras2 = data.getExtras();
            if (extras2 != null) {
                Bitmap photo = extras2.getParcelable("data");
                //photo = photo.createScaledBitmap(photo, 50 , 50, false);
                baos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 0, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                avatarString = Base64.encodeToString(b, 0, b.length, 0);
                imageView.setImageBitmap(photo);
            }
        }
        else if(requestCode == SELECTED_PICTURE && resultCode == RESULT_OK){

            dialog.cancel();
            Bundle extras2 = data.getExtras();
            if (extras2 != null) {
                Bitmap photo = extras2.getParcelable("data");
                //photo = photo.createScaledBitmap(photo, 50 , 50, false);
                baos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 0, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                avatarString = Base64.encodeToString(b, 0, b.length, 0);
                imageView.setImageBitmap(photo);
            }

            final Bundle extras = data.getExtras();

            if (extras != null) {
               photo = extras.getParcelable("data");
                baos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 0, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                avatarString = Base64.encodeToString(b, 0, b.length, 0);
                //Log.d("base64length", Integer.toString(avatarString.length()));
                byte[] decodedByte = Base64.decode(avatarString, 0);

                dialog.cancel();
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length));


            }
        }
    }
}
