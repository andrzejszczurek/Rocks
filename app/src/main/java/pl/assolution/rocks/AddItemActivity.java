package pl.assolution.rocks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class AddItemActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 200;
    private static final int REQUEST_IMAGE_PICK = 100;
    public static final String FILE_URI = "file_uri";
    public static final String COLOR = "color";
    public static final String TYPE = "type";
    public static final String COMPOSITION = "composition";
    public static final String STRUCTURE = "structure";
    public static final String TEXTURE = "texture";
    public static final String BINDER = "binder";
    public static final String DESIGNATION = "designation";
    public static final String OTHER = "other";
    public static final String AUTHOR= "author";
    public static final String PATH= "path";
    private static final String USER = "user";
    private static final String TAG_SUCCESS = "success";
    private static final String url_upload_server = "http://student.agh.edu.pl/~aszczure/testowyUpload.php";
    private static final String url_upload_description = "http://student.agh.edu.pl/~aszczure/itemDescriptionUpload.php";
    public Button takePhotoBtn;
    private Uri uriFile;
    private ImageView image;
    private EditText colorAddEt;
    private EditText typeAddEt;
    private EditText compositionAddEt;
    private EditText structureAddEt;
    private EditText textureAddEt;
    private EditText binderAddEt;
    private EditText designationAddEt;
    private EditText otherAddEt;
    private String textColorAddEt;
    private String textTypeAddEt;
    private String textCompositionAddEt;
    private String textStructureAddEt;
    private String textTextureAddEt;
    private String textBinderAddEt;
    private String textDesignationAddEt;
    private String textOtherAddEt;
    private Button uploadBtn;
    private String user;
    private String imagePathString;
    Boolean flag = false;
    int serverResponseCode = 0;
    JSONParser jsonParser = new JSONParser();
    public Button searchGalleryBtn;
    private String timeStamp;
    private String imageSourceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        takePhotoBtn = (Button) findViewById(R.id.take_photo_btn);
        searchGalleryBtn = (Button) findViewById(R.id.gallery_btn);
        uploadBtn = (Button) findViewById(R.id.upload_btn);

        if (uploadBtn != null) {
            uploadBtn.setClickable(false);
            uploadBtn.setEnabled(false);
            uploadBtn.setText("Dodaj wpis (Wymagane zdjęcie)");
        }

        image = (ImageView) findViewById(R.id.add_image_iv);

        colorAddEt = (EditText) findViewById(R.id.color_add_et);
        typeAddEt = (EditText) findViewById(R.id.type_add_et);
        compositionAddEt = (EditText) findViewById(R.id.composition_add_et);
        structureAddEt = (EditText) findViewById(R.id.structure_add_et);
        textureAddEt = (EditText) findViewById(R.id.texture_add_et);
        binderAddEt = (EditText) findViewById(R.id.binder_add_et);
        designationAddEt = (EditText) findViewById(R.id.designation_add_et);
        otherAddEt = (EditText) findViewById(R.id.other_add_et);

        searchGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(searchIntent, REQUEST_IMAGE_PICK);
                timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                imagePathString = "IMG_rock"+ timeStamp +".jpg";
            }
        });

        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                File imageFolder = new File(Environment.getExternalStorageDirectory(), "RockApp/Media/");

                if (!imageFolder.exists()) {
                    if (!imageFolder.mkdirs()) {
                        Log.e("FileCreateLog :: ", "Problem creating Image folder");
                    }
                }
                imagePathString = "IMG_rock"+ timeStamp +".jpg";
                File imagePath = new File(imageFolder, imagePathString);
                 uriFile = Uri.fromFile(imagePath);
                Log.d("uri::", uriFile.toString());

                intentImage.putExtra(MediaStore.EXTRA_OUTPUT, uriFile);
                startActivityForResult(intentImage, REQUEST_IMAGE_CAPTURE);
            }
        });

        if(!isSupportCamera()) {
            Toast.makeText(getApplicationContext(),"Uppss. Brak kamery",Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                try {
                    //final Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriFile);
                    final Bitmap photo2 = BitmapFactory.decodeFile(uriFile.getPath());
                    image.setVisibility(View.VISIBLE);
                    image.setImageBitmap(photo2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                uploadBtn.setClickable(true);
                uploadBtn.setEnabled(true);
                uploadBtn.setText("Dodaj wpis");
                imageSourceInfo = "camera";

                uploadBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Validation(uriFile.getPath());
                    }
                });
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Anulowano dodawanie zdjęcia", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Błąd, nie przechwycono zdjęcia", Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode == REQUEST_IMAGE_PICK) {
            if (resultCode == RESULT_OK && data != null) {

                uriFile = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(uriFile, filePathColumn, null, null, null);

                assert cursor != null;
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                final String imgDecodString = cursor.getString(columnIndex);
                cursor.close();
                image.setVisibility(View.VISIBLE);
                image.setImageBitmap(BitmapFactory.decodeFile(imgDecodString));


                uploadBtn.setClickable(true);
                uploadBtn.setEnabled(true);
                uploadBtn.setText("Dodaj wpis");
                imageSourceInfo = "gallery";

                uploadBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Validation(imgDecodString);
                    }
                });
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Anulowano dodawanie zdjęcia", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Błąd, nie przechwycono zdjęcia", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void Validation(String imgDecodString) {
        textColorAddEt = colorAddEt.getText().toString();
        textTypeAddEt = typeAddEt.getText().toString();
        textCompositionAddEt = compositionAddEt.getText().toString();
        textStructureAddEt = structureAddEt.getText().toString();
        textTextureAddEt = textureAddEt.getText().toString();
        textBinderAddEt = binderAddEt.getText().toString();
        textDesignationAddEt = designationAddEt.getText().toString();
        textOtherAddEt = otherAddEt.getText().toString();
        boolean isError = false;

        if(TextUtils.isEmpty(textColorAddEt)) {
            colorAddEt.setError("To pole jest wymagane");
            isError = true;
        }
        if(TextUtils.isEmpty(textTypeAddEt)) {
            typeAddEt.setError("To pole jest wymagane");
            isError = true;
        }
        if(TextUtils.isEmpty(textCompositionAddEt)) {
            compositionAddEt.setError("To pole jest wymagane");
            isError = true;
        }
        if(TextUtils.isEmpty(textStructureAddEt)) {
            structureAddEt.setError("To pole jest wymagane");
            isError = true;
        }
        if(TextUtils.isEmpty(textTextureAddEt)) {
            textureAddEt.setError("To pole jest wymagane");
            isError = true;
        }
        if(TextUtils.isEmpty(textBinderAddEt)) {
            binderAddEt.setError("To pole jest wymagane");
            isError = true;
        }
        if(TextUtils.isEmpty(textDesignationAddEt)) {
            designationAddEt.setError("To pole jest wymagane");
            isError = true;
        }
        if(TextUtils.isEmpty(textOtherAddEt)) {
            otherAddEt.setError("To pole jest wymagane");
            isError = true;
        }
        if(image.getVisibility() == View.GONE) {
            Toast.makeText(getApplicationContext(),"Zdjęcie jest wymagane", Toast.LENGTH_SHORT).show();
            isError = true;
        }

        if(!isError) {
            uploadItemToServer(imgDecodString,user,imageSourceInfo);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelable(FILE_URI, uriFile);
        outState.putString(COLOR, colorAddEt.getText().toString());
        outState.putString(TYPE,typeAddEt.getText().toString());
        outState.putString(COMPOSITION, compositionAddEt.getText().toString());
        outState.putString(STRUCTURE, structureAddEt.getText().toString());
        outState.putString(TEXTURE, textureAddEt.getText().toString());
        outState.putString(BINDER, binderAddEt.getText().toString());
        outState.putString(DESIGNATION, designationAddEt.getText().toString());
        outState.putString(OTHER,otherAddEt.getText().toString());
        outState.putString("user", PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(USER, null));
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        uriFile = savedInstanceState.getParcelable(FILE_URI);
        textColorAddEt = savedInstanceState.getString(COLOR);
        textTypeAddEt= savedInstanceState.getString(TYPE);
        textCompositionAddEt = savedInstanceState.getString(COMPOSITION);
        textStructureAddEt = savedInstanceState.getString(STRUCTURE);
        textTextureAddEt = savedInstanceState.getString(TEXTURE);
        textBinderAddEt = savedInstanceState.getString(BINDER);
        textDesignationAddEt = savedInstanceState.getString(DESIGNATION);
        textOtherAddEt = savedInstanceState.getString(OTHER);
        user = savedInstanceState.getString("user");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()){
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
            case R.id.action_settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_logout:
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isSupportCamera() {
        return getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    private void uploadItemToServer(String path, final String user, String imageSourceInfo) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Running backup. Do not unplug drive");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        AsyncTask<String, Integer, Boolean> task = new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(final String... strings) {
//               Thread thread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
                        HttpURLConnection conn;
                        DataOutputStream dos;
                        File sourceFile = new File(strings[0]);

                        //if(strings[2].equals("camera")) {
                            sourceFile = scaleImage(sourceFile, strings[2]);
                        //}
                        int bytesRead, bytesAvailable, bufferSize;
                        byte[] buffer;
                        int maxBufferSize = 1024 * 1024;
                        String lineEnd = "\r\n";
                        String twoHyphens = "--";
                        String boundary = "*****";
                        String imageName;
                        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        imageName = "IMG_rock"+timeStamp+".jpg";

                assert sourceFile != null;
                if (!sourceFile.isFile()) {
                            progressDialog.dismiss();
                            Log.e("uploadFile", "Source File not exist :"+sourceFile);
                        } else {
                            try {
                                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                                URL url = new URL(url_upload_server);
                                conn = (HttpURLConnection) url.openConnection();
                                conn.setDoInput(true);
                                conn.setDoOutput(true);
                                conn.setUseCaches(false);
                                conn.setRequestMethod("POST");
                                conn.setRequestProperty("Connection", "Keep-Alive");
                                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + "*****");
                                conn.setRequestProperty("uploaded_file", imageName);
                                dos = new DataOutputStream(conn.getOutputStream());
                                dos.writeBytes("--" + "*****" + "\r\n");
                                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + imageName + "\"" + "\r\n");
                                dos.writeBytes("\r\n");
                                bytesAvailable = fileInputStream.available();
                                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                                buffer = new byte[bufferSize];
                                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                                while (bytesRead > 0) {
                                    dos.write(buffer, 0, bufferSize);
                                    bytesAvailable = fileInputStream.available();
                                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                                }

                                dos.writeBytes("\r\n");
                                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                                serverResponseCode = conn.getResponseCode();
                                String serverResponseMessage = conn.getResponseMessage();
                                Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

                                if(serverResponseCode == 200){
                                    Log.d("serverR::", String.valueOf(serverResponseCode));
                                }

                                fileInputStream.close();
                                dos.flush();
                                dos.close();
                                flag = uploadDescription();
                            } catch (IOException e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();
                        }
//                    }
//                });
//                thread.start();
                return flag;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if(!aBoolean) {
                    Toast.makeText(getApplicationContext(),"dodanowpis", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(),"blad", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        };
        task.execute(path, user, imageSourceInfo);
    }

    private Boolean uploadDescription() {

        SharedPreferences sharedPreferences = getSharedPreferences(USER, MODE_PRIVATE);
        String userAdd = sharedPreferences.getString(USER, null);

        Boolean isError = true;
        HashMap<String, String> params = new HashMap<>();
        params.put(COLOR, textColorAddEt);
        params.put(TYPE, textTypeAddEt);
        params.put(COMPOSITION, textCompositionAddEt);
        params.put(STRUCTURE, textStructureAddEt);
        params.put(TEXTURE, textTextureAddEt);
        params.put(BINDER, textBinderAddEt);
        params.put(DESIGNATION, textDesignationAddEt);
        params.put(OTHER, textOtherAddEt);
        params.put(AUTHOR, userAdd);
        params.put(PATH, ("img/"+imagePathString));
        JSONObject json = jsonParser.makeHttpRequest(url_upload_description,"POST",params);

        try {
            int success = json.getInt(TAG_SUCCESS);
            isError = success != 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return isError;
    }

    private File scaleImage(File file, String source) {
        try {

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;

            FileInputStream inputStream = new FileInputStream(file);
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            final int REQUIRED_SIZE=75;
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            if(source.equals("gallery")) {

                File folder = new File(Environment.getExternalStorageDirectory(), "RockApp/Images/");
                if (!folder.exists()) {
                    if (!folder.mkdirs()) {
                        Log.e("FileCreateLog :: ", "Problem creating Image folder");
                    }
                }

                String name = file.getPath().substring(file.getPath().lastIndexOf("/") + 1);
                file = new File(folder, name);
            } else {
                final boolean newFile = file.createNewFile();
                Log.d("newFile", String.valueOf(newFile));
            }
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}