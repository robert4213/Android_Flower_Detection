package com.test.flowerdetection;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
//import android.media.Image;
import android.graphics.Matrix;
import android.media.ExifInterface;
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
import java.io.FileOutputStream;
import java.io.IOException;
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

    private static String BASE_URL = "http://192.168.1.144:5000/upload";
    static final int PICK_IMAGE_REQUEST = 1;
    static final int CAMERA_REQUEST = 1888;
    public static String filePath;
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
    private ProgressBar pgsBar;
    //public static DataBaseHelper db;
  //  File mPhotoFile;
    Uri bitmap;
    FileCompressor mCompressor;
    FileUtils fu;
    //String filePath = null;
    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Intent intent = getIntent();
        bitmap =  intent.getParcelableExtra("BitmapImage");
        imageView = (ImageView) findViewById(R.id.imageView);
        pgsBar = (ProgressBar) findViewById(R.id.progressBar_cyclic);
        pgsBar.setVisibility(View.GONE);
        //db = new DataBaseHelper(this);
        //db.queryData();
        Glide.with(Image.this)
                .load(bitmap)
                .apply(new RequestOptions().centerCrop()
                        .placeholder(R.drawable.ic_launcher_background))
                .into(imageView);
        setUpBottomAppBar();

        fu = new FileUtils(Image.this);



     //   btnChoose = (Button) findViewById(R.id.button_choose);
        btnUpload = (Button) findViewById(R.id.button_upload);


        if(bitmap != null) {
            filePath = fu.getPath(bitmap);
            System.out.println("Image file path is: " + filePath);
            // mPhotoFile = mCompressor.compressToFile(new File(getRealPathFromUri(selectedImage)));
            Bitmap bmp = null;
            Bitmap rotate_bmp = null;
            Bitmap resize_bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), bitmap);
                System.out.println("Original bmp size: width " + bmp.getWidth() + ", height " + bmp.getHeight());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bmp != null) {
                System.out.println("Start rotate bitmap");
                resize_bmp = getResizedBitmap(bmp);
                //rotate_bmp = rotateImage(resize_bmp, filePath);
            }

            if (filePath.contains("JPEG_")) {

                rotate_bmp = rotateImage(resize_bmp, filePath);
                File dest = null;

                try {
                    dest = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (dest != null) {
                    try {
                        FileOutputStream out = new FileOutputStream(dest);
                        rotate_bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                        out.flush();
                        out.close();
                        filePath = dest.getPath();
                        System.out.println("Image file path after rotate is: " + filePath + "Width: " + rotate_bmp.getWidth() + "Height: " + rotate_bmp.getHeight());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                rotate_bmp = resize_bmp;
                File file = new File(filePath);
                if (file.exists()) {
                    file.delete();
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (file != null) {
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        rotate_bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                        out.flush();
                        out.close();
                        filePath = file.getPath();
                        System.out.println("Image file path after rotate is: " + filePath + "Width: " + rotate_bmp.getWidth() + "Height: " + rotate_bmp.getHeight());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        btnUpload = findViewById(R.id.button_upload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pgsBar.setVisibility(view.VISIBLE);

                if (bitmap != null) {

                    f = new File(filePath);

                    try {

                        //上传图片
                        //Toast.makeText(Image.this,"Begin uploading"+f.getAbsolutePath(),Toast.LENGTH_LONG).show();
                        imageUploadTask = new ImageUploadTask(Image.this, f, bitmap, filePath);
                        //pgsBar.setVisibility(view.VISIBLE);
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

    private Bitmap rotateImage(Bitmap bitmap, String ImageLocation) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(ImageLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        System.out.println("Orientation: " + orientation);
        Matrix matrix = new Matrix();
        Bitmap rotatedBitmap = null;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                System.out.println("Orientation is 90");
                matrix.postRotate(90);
                rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                break;
            default:
                rotatedBitmap = bitmap;
        }

        return rotatedBitmap;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String mFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mFile = File.createTempFile(mFileName, ".jpg", storageDir);
        return mFile;
    }

    public Bitmap getResizedBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        if(width > 600 || height > 600) {
            int newWidth;
            int newHeight;
            if (height > width) {
                newHeight = 600;
                newWidth = width * 600 / height;
            } else {
                newWidth = 600;
                newHeight = height * 600 / width;
            }
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(
                    bm, 0, 0, width, height, matrix, false);
            bm.recycle();
            return resizedBitmap;
        }
        return bm;
    }


    /**
     * Get real file path from URI
     */
    public String getRealPathFromUri(Uri contentUri) {
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


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



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
//                    case R.id.sign_out_menu:
//                        mFirebaseAuth.signOut();
//                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
//                        mUsername = ANONYMOUS;
//                        startActivity(new Intent(Image.this, SignInActivity.class));
//                        finish();
//                        return true;
//                    case R.id.action_notification:
//                        Toast.makeText(Image.this, "Notification clicked.", Toast.LENGTH_SHORT).show();
//                        break;
                }
                return false;
            }
        });

        //click event over navigation menu like back arrow or hamburger icon
        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open bottom sheet
                User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                BottomSheetDialogFragment bottomSheetDialogFragment = com.bottom.appbar.demo.BottomSheetNavigationFragment.newInstance(user.getEmail());
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