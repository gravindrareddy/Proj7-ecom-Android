package com.rapidbizapps.android.pickAPic;

/**
 * Created by AdityaSinghParmar on 9/29/2016 at 11:49 AM
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class ImageAPI extends AppCompatActivity implements GlobalConstants{

    private Button btnCamera, btnGallery;
    private TextView tvDialogTitle;
    private RelativeLayout rlDialogLayout;

    private static final String TAG = "ImageAPI";

    public static OnImageSelect onImageSelect;

    private int scalingValue;
    private String dialogTitle;
    private int dialogBackgroundColor;
    private int dialogTitleTextColor;
    private int buttonTextColor;
    private String directoryName = DEFAULT_DIRECTORY_NAME;
    private int imageHeight;
    private int imageWidth;

    private File capturedImagePath;

    private final int READ_FROM_CAMERA = 9;
    private final int READ_FROM_GALLERY = 3;

    private final int CAMERA_INTENT = 1;
    private final int GALLERY_INTENT = 2;

    private static Bitmap imgBitmap = null;
    private static String imgFilePath = null;


    // OnCreate default method..
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_selection_dialog );
        init();
        getDataFromIntent();
        decorateDialog();
    }

    /**
     * initializing views, setting up onClickListeners for the dialog.
     * Once, that is done, we can start decorating the dialog and unboxing
     * intent.
     */
    private void init() {

        rlDialogLayout = (RelativeLayout) findViewById(R.id.library_layout);
        tvDialogTitle = (TextView)findViewById(R.id.dialog_title_tv);
        btnCamera = (Button)findViewById(R.id.btn_camera);
        btnGallery = (Button)findViewById(R.id.btn_gellery);


        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**
                 * if we are requesting a permission, we can not start camera yet.
                 * We wait for CAMERA,WRITE_STORAGE permission first.
                 */
                if (isRequestingPermission(READ_FROM_CAMERA,
                        Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    return;
                }
                onCameraSelect();
            }
        });
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**
                 * if we are requesting a permission, we can not open gallery yet.
                 * We wait for READ permission first.
                 */
                if (isRequestingPermission(READ_FROM_GALLERY,
                        Manifest.permission.READ_EXTERNAL_STORAGE)){
                    return;
                }
                onGallerySelect();
            }
        });
    }


    /**
     * Called after initializing views. Here we set up, dialog title, text colors
     * etc for the dialog.
     */
    private void decorateDialog() {

        rlDialogLayout.setBackgroundColor(dialogBackgroundColor);
        tvDialogTitle.setText(dialogTitle);
        tvDialogTitle.setTextColor(dialogTitleTextColor);

        btnCamera.setTextColor(buttonTextColor);
        btnGallery.setTextColor(buttonTextColor);
    }


    /**
     * Un box all the data from the intent and save to private variables.
     * Only the callback is not sent this way. It is initialized as a static
     * variable by calling class.
     */
     private void getDataFromIntent() {
         Intent intent = getIntent();

         scalingValue = intent.getIntExtra(KEY_SCALING_VALUE,DEFAULT_SCALING_VALUE);
         dialogTitle = intent.getStringExtra(KEY_DIALOG_TITLE);
         dialogBackgroundColor = intent.getIntExtra(KEY_DIALOG_BACKGROUND_COLOUR,DEFAULT_BACKGROUND_COLOR);
         dialogTitleTextColor = intent.getIntExtra(KEY_DIALOG_TITLE_TEXT_COLOUR,DEFAULT_DIALOG_TITLE_TEXT_COLOR);
         buttonTextColor = intent.getIntExtra(KEY_DIALOG_BUTTON_TEXT_COLOUR,DEFAULT_DIALOG_BUTTON_TEXT_COLOR);
         directoryName = intent.getStringExtra(KEY_DIRECTORY_NAME);
         imageHeight = intent.getIntExtra(KEY_BITMAP_HEIGHT,DEFAULT_BITMAP_HEIGHT);
         imageWidth = intent.getIntExtra(KEY_BITMAP_WIDTH,DEFAULT_BITMAP_WIDTH);
     }


//    /**
//     * Checks if the user has allowed the particular permission or not. If not allowed,
//     * starts the requestPermission process.
//     * @param PERMISSION, the permission we need to check
//     * @return true if requesting permission, false otherwise.
//     */
//    private boolean isRequestingPermission(@NonNull final String PERMISSION) {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(PERMISSION) != PackageManager.PERMISSION_GRANTED) {
//
//                switch (PERMISSION) {
//                    case Manifest.permission.CAMERA :
//                        requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},READ_FROM_CAMERA);
//                        break;
//
//                    case Manifest.permission.READ_EXTERNAL_STORAGE :
//                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_FROM_GALLERY);
//                        break;
//
//                    default:
//                        //  To be on the safe side
//                        requestPermissions(new String[]{PERMISSION},1001);
//                }
//                // we requested for permission access just now, return true
//                return true;
//            }
//            else {
//                // we already have permission.
//                return false;
//            }
//        }
//        // if less than marshmallow, we don't need to ask for permission.
//        return false;
//    }

    /**
     * Checks if the user has granted permissions or not. If its is less than marshmallow, we
     * can directly return false.
     *
     * For SDKs lesser than M, we check for every permission that is passed and save
     * denied ones to further take for requesting permission.
     *
     * @param requestCode, to be passed to requestPermissions() call
     * @param PERMISSIONS, that are required for the functionality
     * @return true if requesting permission, and false otherwise
     */
    private boolean isRequestingPermission(int requestCode,@NonNull final String...PERMISSIONS) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            List<String> denied = new ArrayList<>();
            boolean areAllPermissionsAllowed = true;
            for (String PERMISSION : PERMISSIONS) {
                areAllPermissionsAllowed = areAllPermissionsAllowed &&
                        checkSelfPermission(PERMISSION) == PackageManager.PERMISSION_GRANTED;

                if (!areAllPermissionsAllowed)
                    denied.add(PERMISSION);
            }

            if (!areAllPermissionsAllowed){
                requestPermissions(denied.toArray(new String[0]), requestCode);
            }
            return !areAllPermissionsAllowed;

        }
        return false;


//        // being requested by gallery
//        if (permission_count == 1) {
//            boolean
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            check
//            if (checkSelfPermission(PERMISSION) != PackageManager.PERMISSION_GRANTED) {
//
//                switch (PERMISSION) {
//                    case Manifest.permission.CAMERA :
//                        requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},READ_FROM_CAMERA);
//                        break;
//
//                    case Manifest.permission.READ_EXTERNAL_STORAGE :
//                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_FROM_GALLERY);
//                        break;
//
//                    default:
//                        //  To be on the safe side
//                        requestPermissions(new String[]{PERMISSION},1001);
//                }
//                // we requested for permission access just now, return true
//                return true;
//            }
//            else {
//                // we already have permission.
//                return false;
//            }
//        }
//        // if less than marshmallow, we don't need to ask for permission.
//        return false;
    }


    /**
     * This is used to send the data back to calling entity.
     * @param bitmap - Bitmap of the selected/captured image
     * @param FilePath - File path of the selected/captured image in device
     */
    private void sendImageDetails(Bitmap bitmap,String FilePath){
        if(bitmap != null && FilePath != null && onImageSelect != null)
            onImageSelect.onImageSelectionSuccess(FilePath,bitmap);
       else
            errorCall(getResources().getString(R.string.noImageRead));
    }


    /** This sends the Error message to the developer.
     * @param err - Error message which is sent to the developer
     */
    private void errorCall(String err) {
        if (onImageSelect != null)
            onImageSelect.onSelectionError(err);
        finish();
    }


    /**
     * Process starts by checking if directory where the image must be stored
     * exists or not. If it does not, we create directories and also an empty file
     * to store taken image. This file is converted into URI and passed to camera app
     * in the intent.
     */
    private  void onCameraSelect(){
        Log.d(TAG, "onCameraSelect: ");
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File root = new File(Environment.getExternalStorageDirectory()
                + FORWARD_SLASH +directoryName+ FORWARD_SLASH);//equivalent to "/"+pik-a-pic+"/"

        if(!root.exists())
            root.mkdirs();

        String imageName = UUID.randomUUID().toString() + EXTENSION_PNG;
        Uri photoUri = createUriForCapturedImage(root, imageName);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
        startActivityForResult(captureIntent,CAMERA_INTENT);
    }

    // Executes when user selects GALLERY for picking images..
    private  void onGallerySelect(){
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, getResources().getString(R.string.SelectUsing)),GALLERY_INTENT);
    }

    /**
     * First we check if the permissions are granted, if not then the error response is same
     * in any case. if true, then we check the request code and proceed accordingly.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {


        /*
            if any of the permission is denied, we show error message,
            the code below is doing just that.
         */
        int granted = 0;

        Log.d(TAG, "onRequestPermissionsResult: ");
        
        for (int res : grantResults) {
            if (res != PackageManager.PERMISSION_GRANTED){
                granted = PackageManager.PERMISSION_DENIED;
                break;
            }
        }

        switch (granted) {
            case PackageManager.PERMISSION_GRANTED :
                switch (requestCode) {
                    case READ_FROM_CAMERA :
                        Log.d(TAG, "onRequestPermissionsResult: yes");
                        onCameraSelect();
                        break;

                    case READ_FROM_GALLERY :
                        Log.d(TAG, "onRequestPermissionsResult: yes");
                        onGallerySelect();
                        break;
                }
                break;

            default:
                Log.d(TAG, "onRequestPermissionsResult: no");
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.NoPermission),Toast.LENGTH_SHORT).show();
        }

//        switch (requestCode) {
//            case READ_FROM_CAMERA :
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Log.d(TAG, "onRequestPermissionsResult: yes");
//                    onCameraSelect();
//
//                } else {
//                    Log.d(TAG, "onRequestPermissionsResult: no");
//                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.NoPermission),Toast.LENGTH_SHORT).show();
//                }
//             break;
//            case READ_FROM_GALLERY :
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Log.d(TAG, "onRequestPermissionsResult: yes");
//                    onGallerySelect();
//
//                } else {
//                    Log.d(TAG, "onRequestPermissionsResult: no");
//                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.NoPermission),Toast.LENGTH_SHORT).show();
//                }
//             break;
//
//        }
    }

    // Getting the Result from Camera or Gallery , and processing it further..
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: data :"+data+" "+requestCode+" "+resultCode);

        switch (resultCode) {
            case RESULT_OK :
                switch (requestCode) {
                    case CAMERA_INTENT :
                        Log.d(TAG, "onActivityResult: inside ");
                        handleCameraResponse();
                        break;

                    case GALLERY_INTENT :
                        Log.d(TAG, "onActivityResult: inside ");
                        handleGalleryResponse(data);
                        break;
                }
                break;
            case RESULT_CANCELED :
                errorCall(getResources().getString(R.string.ProcessCancelled));
                finish();
        }
    }

    /**
     * decodes the image clicked using camera with appropriate sample size.
     * later we scale down the bitmap and check the orientation of the image.
     * In the end, we send appropriate response to calling activity / fragment.
     */
    private void handleCameraResponse() {


        if (!capturedImagePath.exists()) {
            errorCall("File deleted");
            return;
        }

        imgBitmap = decodeMyImage(imgFilePath);

        if (imgBitmap == null){
            errorCall(getResources().getString(R.string.CameraError));
            return;
        }

        imgBitmap = scaleDownBitmap(imgBitmap,scalingValue,this);
        imgBitmap = imageOrientation(imgBitmap,imgFilePath);
        if( imgBitmap != null && imgFilePath != null)
            sendImageDetails(imgBitmap,imgFilePath);
        else
            errorCall(getResources().getString(R.string.CameraError));

        finish();

    }

    /**
     * Here we handle the gallery response. first we get the Uri of selected image and store the
     * image as bitmap. Then, we convert URI into image path.
     * Finally, image is scaled down, orientation verified and then sent to calling activity.
     * @param data, containing the info about selected image.
     */
    private void handleGalleryResponse(Intent data) {

        if (data == null) return;

        Uri selectedImage = data.getData();
        try {
            imgBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);  // Getting the Image Bitmap ..
        } catch (IOException e) {
            e.printStackTrace();
            errorCall(getString(R.string.InvalidImageSelected));
            return;
        }

        if (imgBitmap == null){
            errorCall(getString(R.string.InvalidImageSelected));
            return;
        }

            /* Getting the path of the Image File.. */
        String path = getImagePathFromURI(selectedImage);
        if (path == null) {
            errorCall(getString(R.string.ImageNotAvailable));
            return;
        }


        imgBitmap = scaleDownBitmap(imgBitmap,scalingValue,this);
        Log.d(TAG, "onActivityResult: new image bitmap 1"+imgBitmap);
        imgBitmap = imageOrientation(imgBitmap,path);
        Log.d(TAG, "onActivityResult: new image bitmap"+imgBitmap);

        if(imgBitmap != null)
            sendImageDetails(imgBitmap,path);
        else
            errorCall(getResources().getString(R.string.ImageNotAvailable));
        finish();

    }


    /**************************************** Helper methods **************************************/

    /**
     * First we create an empty file to store the image. Later it is converted
     * into URI and returned.
     *
     * For nougat devices, we need a different process to generate URI, else
     * a fileUriExposedException is thrown.
     * @param root ,root directory where file must be created,
     * @param imageName ,name of file
     * @return ,URI for created image.
     */
    private Uri createUriForCapturedImage(File root, String imageName) {

        capturedImagePath = new File(root, imageName);
        imgFilePath = capturedImagePath.toString();

        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String extension = capturedImagePath.getName().substring(
                capturedImagePath.getName().lastIndexOf(".")+1);
        String type = mime.getMimeTypeFromExtension(extension);

        Uri photoUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            photoUri = FileProvider.getUriForFile(
                    this,getApplicationContext().getPackageName() +".provider",capturedImagePath);
        }
        else {
            photoUri = Uri.fromFile(capturedImagePath);
        }

        return photoUri;
    }

    /**
     * Compares the URI with already defined path keys. If match found, we return
     * the particular path, if no match found returns null
     * @param selectedImage, Uri to be checked
     * @return  path or null
     */
    private String getImagePathFromURI(Uri selectedImage) {

        // Test one
        String path = selectedImage.getPath();
        if (doesStringContainsKey(path,
                new String[]{EXTENSION_JPEG,EXTENSION_JPG,EXTENSION_PNG})) {
            Log.d(TAG, "onActivityResult: one");
            return path;
        }

        // Test two
        try {
            path = getRealPathFromURI(selectedImage);
        }
        catch (IllegalArgumentException | NullPointerException e){
            e.printStackTrace();
            return null;
        }
        if (path != null) {
            Log.d(TAG, "onActivityResult: two");
            return path;
        }

        //Test three
        path = selectedImage.toString();
        if (doesStringContainsKey(path,
                new String[]{"content://com.android.providers"})) {
            Log.d(TAG, "onActivityResult: three");
            path = getRealPathFromURI_API19(this,selectedImage);
            return path;
        }

        //Test four
        path = selectedImage.toString();
        if (doesStringContainsKey(path,
                new String[]{"content://com.android.externalstorage.documents/document/"})) {
            Log.d(TAG, "onActivityResult: four");

//          String wholeID = null;         changing from null to :
            String wholeID = ":";       //  null would surely cause a crash in SDK < 19

            if (Build.VERSION.SDK_INT >= 19) {
                wholeID = DocumentsContract.getDocumentId(selectedImage);
            }

            String remainingPath = wholeID.split(":")[1];
            int lastIndexOfForwardSlash = path.lastIndexOf(FORWARD_SLASH) + 1;
            int indexOfPercentile = path.indexOf("%");

            String storageLocation = selectedImage.toString().substring(
                    lastIndexOfForwardSlash,
                    indexOfPercentile);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (Objects.equals(storageLocation,"primary"))
                    path = "/storage/emulated/0/" + remainingPath;
                else
                    path = "/storage/" + storageLocation + '/' + remainingPath;
            }

            Log.d(TAG, "onActivityResult: new Path: " + path);
            return path;
        }

        return null;


//        if (selectedImage.getPath().contains(".jpeg") || selectedImage.getPath().contains(".jpg") || selectedImage.getPath().contains(".png") ) {
//            Log.d(TAG, "onActivityResult: one");
//            path = selectedImage.getPath();
//        } else if(getRealPathFromURI(selectedImage)!= null){
//            Log.d(TAG, "onActivityResult: two");
//            path = getRealPathFromURI(selectedImage);
//        }   else  if (selectedImage.toString().contains("content://com.android.providers")) {
//            Log.d(TAG, "onActivityResult: three");
//            path = getRealPathFromURI_API19(this, selectedImage);
//            Log.d(TAG, "onActivityResult: path 1 : " + path);
//        } else if (selectedImage.toString().contains("content://com.android.externalstorage.documents/document/")) {
//            Log.d(TAG, "onActivityResult: four");
//
//            String wholeID = null;
//
//            if (Build.VERSION.SDK_INT >= 19) {
//                wholeID = DocumentsContract.getDocumentId(selectedImage);
//            }
//
//            // Split at colon, use second item in the array
//            String remainingPath = wholeID.split(":")[1];
//
//            String storageLocation = selectedImage.toString().substring(selectedImage.toString().lastIndexOf("/") + 1, selectedImage.toString().indexOf("%"));
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                if (Objects.equals(storageLocation, "primary"))
//                    path = "/storage/emulated/0/" + remainingPath;
//                else
//                    path = "/storage/" + storageLocation + '/' + remainingPath;
//            }
//
//            Log.d(TAG, "onActivityResult: new Path: " + path);
//
//        }
    }


    /**
     * Checks if the source string contains any of the specified key
     * @param sourceString, string to check
     * @param keys, keys to compare
     * @return true if any key found, false otherwise.
     */
    private boolean doesStringContainsKey(String sourceString, String[] keys){
        for (String key : keys) {
            if (sourceString.contains(key))
                return true;
        }
        return false;
    }


    /** Checking the orientation of the image and sending the appropriate rotated Bitmap.
     *
     * @param bitmap - Bitmap of the image which is to be checked for it's orientation
     * @param path - Path of that image , where it is stored in the device
     * @return - Returns the appropriate Rotated Image bitmap
     */
    private static Bitmap imageOrientation(Bitmap bitmap, String path){

        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                Log.d(TAG, "onActivityResult: 90");
                bitmap = rotateImage(bitmap, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                Log.d(TAG, "onActivityResult: 180");
                bitmap = rotateImage(bitmap, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                Log.d(TAG, "onActivityResult: 270");
                bitmap = rotateImage(bitmap, 270);
                break;
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                Log.d(TAG, "onActivityResult: default");
        }
        return bitmap;
    }


    /** Scaling the size of the Bitmap.
     *
     * @param photo- Bitmap of the image to be scaled down
     * @param newHeight- Height to which it should be scaled
     * @param context- Context
     * @return - Returns a scaled bitmap with the desired height
     */
    private static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h = (int) (newHeight*densityMultiplier);
        int w = (int) (h * photo.getWidth()/((double) photo.getHeight()));

        photo = Bitmap.createScaledBitmap(photo, w, h, true);
        return photo;
    }


    /** Rotating the bitmap to an angle that makes the resultant bitmap appear straight.
     *
     * @param source - Image bitmap to be oriented
     * @param angle - Orientation angle of the image
     * @return - Returns correctly oriented image.
     */
    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source,
                0, /* x axis starting point 1st pixel */
                0, /* y axis starting point 1st pixel */
                source.getWidth(), /* width of image */
                source.getHeight(), /* height of image */
                matrix,
                true    /* should apply filter, translation */
        );
    }


    /** This gives the path of the Images where they are being stored..
     *
     * @param contentUri - Image URI for which the path is to be found..
     * @return - Path of the Image, or throws IllegalArgumentException
     */

    private String getRealPathFromURI(Uri contentUri) throws IllegalArgumentException, NullPointerException{
        String[] projection = { MediaStore.Audio.Media.DATA };
        Cursor cursor;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB){
            cursor = managedQuery(contentUri, projection, null, null, null);
        }
        else {
            cursor = getContentResolver().query(contentUri,projection,null,null,null);
        }

        int column_index;
        try {

            column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
        }
        catch (IllegalArgumentException | NullPointerException e){
            e.printStackTrace();
            throw e;
        }
        return cursor.getString(column_index);
    }


    /** This gives the path of the Images where they are being stored in devices having API level above 19..
     *
     * @param context - Context
     * @param uri - Image URI for which the path is to be found..
     * @return - Path of the Image
     */
    private static String getRealPathFromURI_API19(Context context, Uri uri){

        String filePath = "";
//        String wholeID = null;
        String wholeID = ":";

        if (Build.VERSION.SDK_INT >= 19) {
            wholeID = DocumentsContract.getDocumentId(uri);
        }

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];
        String[] projection = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String selection = MediaStore.Images.Media._ID + "=?";
        String selectionArgs[] = new String[]{id};

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs
                , null);

        if (cursor == null)
            return null;

        int columnIndex = cursor.getColumnIndex(projection[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

     /**
      * Decodes the image with bounds so that we don't get out of memory exception.
      * We need to know the size of imageView, (bounds) to make it work.
      * Default bounds are 200 X 200, unless specified.
      * Ideally we would pass it from calling activity as inputs.
      *
      * @param imageFilePath, where the image is stored in file
      * @return the decoded Bitmap as per bounds.
      */
     private Bitmap decodeMyImage(String imageFilePath) {

         InputStream is1 = null;    // one stream to decode bitmap
         InputStream is2 = null;    //  one stream to get meta data
         try {

             FileInputStream fis1 = new FileInputStream(imageFilePath);
             FileInputStream fis2 = new FileInputStream(imageFilePath);

             is1 = fis1;
             is2 = fis2;
         } catch (FileNotFoundException e) {
             e.printStackTrace();
         }

         final BitmapFactory.Options options = new BitmapFactory.Options();
         options.inJustDecodeBounds = true;
         BitmapFactory.decodeStream(is1, null, options);

         options.inSampleSize = calculateInSampleSize(options, imageWidth, imageHeight);
         options.inJustDecodeBounds = false;

         return BitmapFactory.decodeStream(is2, null, options);
     }

     /**
      * returns the sample size that we must use to decode the file into bitmap.
      * not using a sample size while decoding would cause an OutOfMemoryException.
      *
      * Based on the height and width of the image, a sample size (multiple of 2) is returned.
      *
      * @param options   BitmapFactory Options.
      * @param reqWidth  Width of the image.
      * @param reqHeight Height of the image.
      * @return Appropriate sample size.
      */
     private int calculateInSampleSize(BitmapFactory.Options options,
                                       int reqWidth,
                                       int reqHeight) {
         // Raw height and width of image
         final int height = options.outHeight;
         final int width = options.outWidth;
         int inSampleSize = 1;

         if (height > reqHeight || width > reqWidth) {

             final int halfHeight = height / 2;
             final int halfWidth = width / 2;

             // Calculate the largest inSampleSize value that is a power of 2 and keeps both
             // height and width larger than the requested height and width.
             while ((halfHeight / inSampleSize) >= reqHeight
                     && (halfWidth / inSampleSize) >= reqWidth) {
                 inSampleSize *= 2;
             }
         }
         return inSampleSize;
     }

    /**********************************************************************************************/
}
