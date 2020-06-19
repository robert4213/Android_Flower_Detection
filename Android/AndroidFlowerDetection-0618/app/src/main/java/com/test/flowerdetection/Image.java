package com.test.flowerdetection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
//import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Image extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private BottomAppBar bottomAppBar;
//    static final int REQUEST_TAKE_PHOTO = 1;
//    static final int REQUEST_GALLERY_PHOTO = 2;
//    File mPhotoFile;
//    FileCompressor mCompressor;


    private static final String TAG = Image.class.getSimpleName();
    private ImageView imageView;
   // private Button btnChoose, btnUpload,bClick;
    private ProgressBar progressBar;
    Bitmap photo;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // public static String BASE_URL = "http://192.168.1.144:8888/upload.php";
//    private static final String IMGUR_CLIENT_ID = "123";
//    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static String BASE_URL = "http://192.168.1.144:5000/upload";
    static final int PICK_IMAGE_REQUEST = 1;
    static final int CAMERA_REQUEST = 1888;
    private String filePath;
    private File f;
    private Uri picUri;
    private ImageUploadTask imageUploadTask;

    private static final int REQUEST_INVITE = 1;
    private static final int REQUEST_IMAGE = 2;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;
    private static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";

    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;
    private ImageView mAddMessageImageView;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private Button btnUpload;

    //public static DataBaseHelper db;
  //  File mPhotoFile;
    Uri bitmap;
    FileCompressor mCompressor;
    FileUtils fu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Intent intent = getIntent();
        bitmap =  intent.getParcelableExtra("BitmapImage");
        imageView = (ImageView) findViewById(R.id.imageView);

        //db = new DataBaseHelper(this);
        //db.queryData();
        Glide.with(Image.this)
                .load(bitmap)
                .apply(new RequestOptions().centerCrop()
                        .circleCrop()
                        .placeholder(R.drawable.ic_launcher_background))
                .into(imageView);
        setUpBottomAppBar();
//        ButterKnife.bind(this);
     //   mCompressor = new FileCompressor(this);
        //click event over FAB
//        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Toast.makeText(MainActivity.this, "FAB Clicked.", Toast.LENGTH_SHORT).show();
//                selectImage();
//            }
//        });


        // Set default username is anonymous.
//        mUsername = ANONYMOUS;
        fu = new FileUtils(Image.this);
//        // Initialize Firebase Auth
//        mFirebaseAuth = FirebaseAuth.getInstance();
//        mFirebaseUser = mFirebaseAuth.getCurrentUser();
//        if (mFirebaseUser == null) {
//            // Not signed in, launch the Sign In activity
//            startActivity(new Intent(this, SignInActivity.class));
//            finish();
//            return;
//        } else {
//            mUsername = mFirebaseUser.getDisplayName();
//            System.out.println("mUsername :" + mUsername);
//        }
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
//                .addApi(Auth.GOOGLE_SIGN_IN_API)
//                .build();


     //   btnChoose = (Button) findViewById(R.id.button_choose);
        btnUpload = (Button) findViewById(R.id.button_upload);
      //  bClick = (Button)findViewById(R.id.Bclick);

        //For camera
//        bClick.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                picUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
//                startActivityForResult(cameraIntent,CAMERA_REQUEST);
//            }
//        });


//        btnChoose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                imageBrowse();
//            }
//        });
        btnUpload = findViewById(R.id.button_upload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap != null) {
//                    imageUpload(filePath);

                    String filePath = fu.getPath(bitmap);
                    System.out.println("Image file path is: " + filePath);
                   // mPhotoFile = mCompressor.compressToFile(new File(getRealPathFromUri(selectedImage)));
                    f = new File(filePath);
//                    try {
//                        f = mCompressor.compressToFile(new File(getRealPathFromUri(bitmap)));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    try {
                        //上传图片
                        //Toast.makeText(Image.this,"Begin uploading"+f.getAbsolutePath(),Toast.LENGTH_LONG).show();
                        imageUploadTask = new ImageUploadTask(Image.this, f, bitmap);
                        imageUploadTask.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Image not selected!", Toast.LENGTH_LONG).show();
                }
                //openNewActivity();
            }
        });
    }


    /**
     * Get real file path from URI
     */
    public String getRealPathFromUri(Uri contentUri) {
        System.out.println("contentUri path: ---------------------: " + contentUri.getPath());
        String imagePath = contentUri.getPath();
        if (imagePath != null) {
            return imagePath;
        } else {
            Cursor cursor = null;
            try {
                String[] proj = { MediaStore.Images.Media.DATA };
                cursor = getContentResolver().query(contentUri, proj, null, null, null);
                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }


    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void imageBrowse() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

//    //Inflate menu to bottom bar
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        System.out.println("!!!!!!!!!!!!!!!");
//        switch (item.getItemId()) {
//            case R.id.sign_out_menu:
//                mFirebaseAuth.signOut();
//                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
//                mUsername = ANONYMOUS;
//                startActivity(new Intent(this, SignInActivity.class));
//                finish();
//                return true;
//            case R.id.action_notification:
//                break;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//        return false;
//    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (resultCode == RESULT_OK) {
//
//            if(requestCode == PICK_IMAGE_REQUEST){
//                picUri = data.getData();
//
//
//                filePath = getPath(picUri);
//
//                Log.d("picUri", picUri.toString());
//                Log.d("filePath", filePath);
//
//                imageView.setImageURI(picUri);
//
//            }
//
//            if(requestCode == CAMERA_REQUEST) {
//                Bundle bundle  = data.getExtras();
//                final Bitmap bmp = (Bitmap) bundle.get("data");
//                imageView.setImageBitmap(bmp);
//            }

 //       }

    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == RESULT_OK) {
//
//            if(requestCode == PICK_IMAGE_REQUEST){
//                picUri = data.getData();
//
//
//                filePath = getPath(picUri);
//
//                Log.d("picUri", picUri.toString());
//                Log.d("filePath", filePath);
//
//                imageView.setImageURI(picUri);
//
//            }
//
//            if(requestCode == CAMERA_REQUEST) {
//                Bundle bundle  = data.getExtras();
//                final Bitmap bmp = (Bitmap) bundle.get("data");
//                imageView.setImageBitmap(bmp);
//            }
//
//        }
//
//    }

    private void imageUpload(final String imagePath) {

        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            String message = jObj.getString("message");

                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        smr.addFile("image", imagePath);
        MyApplication.getInstance().addToRequestQueue(smr);

    }

    public void openNewActivity(){
        Intent intent = new Intent(Image.this, ShowResult.class);
        //intent.putExtra("IMAGE_URI", picUri);
        intent.putExtra("IMAGE_URI",bitmap);
        startActivity(intent);
    }


    /*******************************************************************/

    /**
     * set up Bottom Bar
     */
    private void setUpBottomAppBar() {
        //find id
        bottomAppBar = findViewById(R.id.bottomAppBar);
        System.out.println("MenuItem selected: ");
        //set bottom bar to Action bar as it is similar like Toolbar
        setSupportActionBar(bottomAppBar);

        //click event over Bottom bar menu item
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                System.out.println("MenuItem selected: " + item.getItemId());
                switch (item.getItemId()) {
                    case R.id.ViewHis:
                        System.out.println("ViewHis Select");
                        Intent intent = new Intent(getApplicationContext(), image_list.class);
                        startActivity(intent);
                        return true;
                    case R.id.Rating:
                        System.out.println("Rating Select");
                        Toast.makeText(getApplicationContext(), "Select Rating", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.Contact:
                        Toast.makeText(getApplicationContext(), "Select Contact Us", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.test_menu:
                        Toast.makeText(Image.this, "test", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.sign_out_menu:
                        mFirebaseAuth.signOut();
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                        mUsername = ANONYMOUS;
                        startActivity(new Intent(Image.this, SignInActivity.class));
                        finish();
                        return true;
                    case R.id.action_notification:
                        Toast.makeText(Image.this, "Notification clicked.", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });

        //click event over navigation menu like back arrow or hamburger icon
        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open bottom sheet
                BottomSheetDialogFragment bottomSheetDialogFragment = com.bottom.appbar.demo.BottomSheetNavigationFragment.newInstance();
                bottomSheetDialogFragment.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
            }
        });
    }





    /**
     * method to toggle fab mode
     *
     * @param view
     */
    public void toggleFabMode(View view) {
        //check the fab alignment mode and toggle accordingly
        if (bottomAppBar.getFabAlignmentMode() == BottomAppBar.FAB_ALIGNMENT_MODE_END) {
            bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
        } else {
            bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
        }
    }
}