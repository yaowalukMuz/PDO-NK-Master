package com.mist.it.pod_nk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

import static com.mist.it.pod_nk.MyConstant.projectString;
import static com.mist.it.pod_nk.MyConstant.serverString;
import static com.mist.it.pod_nk.MyConstant.urlSaveImagePerStore;
import static com.mist.it.pod_nk.MyConstant.urlUploadPicture;

public class DetailJobActivity extends AppCompatActivity {

    @BindView(R.id.txtDJAStore)
    TextView storeTextView;
    @BindView(R.id.txtDJAArrivalTime)
    TextView arrivalTimeTextView;
    @BindView(R.id.txtDJADate)
    TextView dateTextView;
    @BindView(R.id.lisDJAInvoiceList)
    ListView invoiceListView;
    @BindView(R.id.imgDJAOne)
    ImageView firstImageView;
    @BindView(R.id.imgDJATwo)
    ImageView secondImageView;
    @BindView(R.id.imgDJAThree)
    ImageView thirdImageView;
    @BindView(R.id.imgDJAFour)
    ImageView fourthImageView;
    @BindView(R.id.btnDJAArrive)
    Button arriveButton;
    @BindView(R.id.btnDJASavePic)
    Button savePicButton;
    @BindView(R.id.btnDJASignature)
    Button signatureButton;
    @BindView(R.id.btnDJAConfirm)
    Button confirmButton;

    String dateString, subJobNoString, tripNoString, storeString, storeIdString, arriveTimeString, pathImgFirstString, pathImgSecondString, pathImgThirdString, pathImgFourthString;
    String[] loginStrings, invoiceStrings;
    private Uri firstUri, secondUri, thirdUri, fourthUri;
    private Boolean imgFirstFlagABoolean, imgSecondFlagABoolean, imgThirdFlagABoolean, imgFourthFlagABoolean, flagSaveABoolean;
    private Bitmap imgFirstBitmap = null;
    private Bitmap imgSecondBitmap = null;
    private Bitmap imgThirdBitmap = null;
    private Bitmap imgFourthBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_job);
        ButterKnife.bind(this);

        //Set flag img
        imgFirstFlagABoolean = false;
        imgSecondFlagABoolean = false;
        imgThirdFlagABoolean = false;
        imgFourthFlagABoolean = false;


        dateString = getIntent().getStringExtra("Date");
        tripNoString = getIntent().getStringExtra("Position");
        loginStrings = getIntent().getStringArrayExtra("Login");
        subJobNoString = getIntent().getStringExtra("SubJobNo");
        storeString = getIntent().getStringExtra("Place");

        storeTextView.setText(getResources().getText(R.string.Store) + ": " + storeString);
        dateTextView.setText(getResources().getText(R.string.Date) + ": " + dateString);

        SynGetJobDetail synGetJobDetail = new SynGetJobDetail();
        synGetJobDetail.execute();


    }

    @OnItemClick(R.id.lisDJAInvoiceList)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(DetailJobActivity.this, ReturnActivity.class);
        intent.putExtra("Date", dateString);
        intent.putExtra("Position", tripNoString);
        intent.putExtra("Login", loginStrings);
        intent.putExtra("SubJobNo", subJobNoString);
        intent.putExtra("Place", storeString);
        intent.putExtra("StoreId", storeIdString);
        intent.putExtra("Invoice", invoiceStrings[position]);
        startActivity(intent);

    }

    class SynGetJobDetail extends AsyncTask<Void, Void, String> {
        public SynGetJobDetail() {
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Log.d("TAG", "Send ==> " + subJobNoString + " , " + storeString);
                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("subjob_no", subJobNoString)
                        .add("invoiceNo", storeString)
                        .add("isAdd", "true")
                        .build();
                Request request = builder.post(requestBody).url(MyConstant.urlGetJobDetail).build();
                Response response = okHttpClient.newCall(request).execute();

                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("Tag", s);
            try {
                JSONObject jsonObject = new JSONObject(s);

                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    storeIdString = jsonObject1.getString("StoreId");
                    arriveTimeString = jsonObject1.getString("ArrivalTime");

                    pathImgFirstString = jsonObject1.getString("ImgFileName_1");
                    pathImgSecondString = jsonObject1.getString("ImgFileName_2");
                    pathImgThirdString = jsonObject1.getString("ImgFileName_3");
                    pathImgFourthString = jsonObject1.getString("ImgFileName_4");



                    JSONArray jsonArray1 = jsonObject1.getJSONArray("Invoice");
                    invoiceStrings = new String[jsonArray1.length()];

                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                        invoiceStrings[j] = jsonObject2.getString("Invoice");
                    }
                }

                arrivalTimeTextView.setText(getResources().getText(R.string.ArrivalTime) + ": " + arriveTimeString);
                InvoiceListAdaptor invoiceListAdaptor = new InvoiceListAdaptor(DetailJobActivity.this, invoiceStrings);
                invoiceListView.setAdapter(invoiceListAdaptor);


                Log.d("Tag", "Image Path :::  " + serverString + projectString + "/app/CenterService/" + pathImgFirstString);

            Glide.with(DetailJobActivity.this).load(serverString + projectString + "/app/CenterService/" + pathImgFirstString).into(firstImageView);
            Glide.with(DetailJobActivity.this).load(serverString + projectString + "/app/CenterService/" + pathImgSecondString).into(secondImageView);
            Glide.with(DetailJobActivity.this).load(serverString + projectString + "/app/CenterService/" + pathImgThirdString).into(thirdImageView);
            Glide.with(DetailJobActivity.this).load(serverString + projectString + "/app/CenterService/" + pathImgFourthString).into(fourthImageView);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Tag", String.valueOf(e) + " Line: " + e.getStackTrace()[0].getLineNumber());
            }

        }
    }

    class InvoiceListAdaptor extends BaseAdapter {
        Context context;
        String[] invoiceStrings;
        InvoiceListViewHolder invoiceListViewHolder;

        public InvoiceListAdaptor(Context context, String[] invoiceStrings) {
            this.context = context;
            this.invoiceStrings = invoiceStrings;
            Log.d("Tag", String.valueOf(invoiceStrings.length));
        }

        @Override
        public int getCount() {
            return invoiceStrings.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.invoice_listview, null);
                invoiceListViewHolder = new InvoiceListViewHolder(convertView);
                convertView.setTag(invoiceListViewHolder);
            } else {
                invoiceListViewHolder = (InvoiceListViewHolder) convertView.getTag();
            }
            Log.d("Tag", invoiceStrings[position]);

            invoiceListViewHolder.invoiceTextView.setText(invoiceStrings[position]);

            return convertView;
        }

        class InvoiceListViewHolder {
            @BindView(R.id.txtILInvoice)
            TextView invoiceTextView;
            @BindView(R.id.imgILCamera)
            ImageView cameraImageView;

            InvoiceListViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }


    }


    class SynUpdateStatusArrive extends AsyncTask<Void, Void, String> {
        String latString, longString, timeString;

        public SynUpdateStatusArrive(String latString, String longString, String timeString) {
            this.latString = latString;
            this.longString = longString;
            this.timeString = timeString;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("user_name", loginStrings[5])
                        .add("dealerName", storeString)
                        .add("subjob_no", subJobNoString)
                        .add("invoiceNo", invoiceStrings[0])
                        .add("gps_lat", latString)
                        .add("gps_lon", longString)
                        .add("timeStamp", timeString)
                        .build();
                Request request = builder.url(MyConstant.urlSaveArrivedToStore).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();

                return response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Tag", String.valueOf(e) + " Line: " + e.getStackTrace()[0].getLineNumber());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Tag", s);


        }
    }


    @OnClick({R.id.btnDJAArrive, R.id.btnDJASavePic, R.id.btnDJASignature, R.id.btnDJAConfirm, R.id.imgDJAOne, R.id.imgDJATwo, R.id.imgDJAThree, R.id.imgDJAFour})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnDJAArrive:
                GPSManager gpsManager = new GPSManager(DetailJobActivity.this);
                if (gpsManager.setLatLong(0)) {
                    SynUpdateStatusArrive synUpdateStatusArrive = new SynUpdateStatusArrive(gpsManager.getLatString(), gpsManager.getLongString(), gpsManager.getDateTime());
                    synUpdateStatusArrive.execute();
                } else {
                    Toast.makeText(getBaseContext(), "Try Again", Toast.LENGTH_LONG);
                }
                break;
            case R.id.btnDJASavePic:
                if (pathImgFirstString != null) {
                    SynUploadImage synUploadImage = new SynUploadImage(DetailJobActivity.this, imgFirstBitmap, invoiceStrings[0].toString(), subJobNoString, "sto_first.png", storeIdString);
                    synUploadImage.execute();
                }
                if (pathImgSecondString != null) {
                    SynUploadImage synUploadImage = new SynUploadImage(DetailJobActivity.this, imgSecondBitmap, invoiceStrings[0].toString(), subJobNoString, "sto_second.png", storeIdString);
                    synUploadImage.execute();
                }
                if (pathImgThirdString != null) {
                    SynUploadImage synUploadImage = new SynUploadImage(DetailJobActivity.this, imgThirdBitmap, invoiceStrings[0].toString(), subJobNoString, "sto_third.png", storeIdString);
                    synUploadImage.execute();
                }
                if (pathImgFourthString != null) {
                    SynUploadImage synUploadImage = new SynUploadImage(DetailJobActivity.this, imgFourthBitmap, invoiceStrings[0].toString(), subJobNoString, "sto_fourth.png", storeIdString);
                    synUploadImage.execute();
                }


                break;
            case R.id.btnDJASignature:
                Intent intent = new Intent(DetailJobActivity.this, SignatureActivity.class);
                intent.putExtra("Date", dateString);
                intent.putExtra("Position", tripNoString);
                intent.putExtra("Login", loginStrings);
                intent.putExtra("SubJobNo", subJobNoString);
                intent.putExtra("Place", storeString);
                intent.putExtra("StoreId", storeIdString);
                startActivity(intent);
                break;
            case R.id.btnDJAConfirm:

                break;

            case R.id.imgDJAOne:
                if (!imgFirstFlagABoolean) {
                    File originalFile1 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "sto_first.png");
                    Intent cameraIntent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    firstUri = Uri.fromFile(originalFile1);
                    cameraIntent1.putExtra(MediaStore.EXTRA_OUTPUT, firstUri);
                    startActivityForResult(cameraIntent1, 1);


                }
                break;
            case R.id.imgDJATwo:
                if (!imgSecondFlagABoolean) {
                    File originalFile2 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "sto_second.png");
                    Intent cameraIntent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    secondUri = Uri.fromFile(originalFile2);
                    cameraIntent2.putExtra(MediaStore.EXTRA_OUTPUT, secondUri);
                    startActivityForResult(cameraIntent2, 2);
                }
                break;
            case R.id.imgDJAThree:
                if (!imgThirdFlagABoolean) {
                    File originalFile3 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "sto_third.png");
                    Intent cameraIntent3 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    thirdUri = Uri.fromFile(originalFile3);
                    cameraIntent3.putExtra(MediaStore.EXTRA_OUTPUT, thirdUri);
                    startActivityForResult(cameraIntent3, 3);
                }
                break;
            case R.id.imgDJAFour:
                if (!imgFourthFlagABoolean) {
                    File originalFile4 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "sto_fourth.png");
                    Intent cameraIntent4 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fourthUri = Uri.fromFile(originalFile4);
                    cameraIntent4.putExtra(MediaStore.EXTRA_OUTPUT, fourthUri);
                    startActivityForResult(cameraIntent4, 4);
                }
                break;


        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    pathImgFirstString = firstUri.getPath().toString();
                    try {
                        imgFirstBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(firstUri));
                        if (imgFirstBitmap.getHeight() < imgFirstBitmap.getWidth()) {
                            imgFirstBitmap = rotateBitmap(imgFirstBitmap);
                        }
                        firstImageView.setImageBitmap(imgFirstBitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    pathImgSecondString = secondUri.getPath().toString();
                    try {
                        imgSecondBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(secondUri));
                        if (imgSecondBitmap.getHeight() < imgSecondBitmap.getWidth()) {
                            imgSecondBitmap = rotateBitmap(imgSecondBitmap);
                        }
                        secondImageView.setImageBitmap(imgSecondBitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 3:
                if (resultCode == RESULT_OK) {
                    pathImgThirdString = thirdUri.getPath().toString();
                    try {
                        imgThirdBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(thirdUri));
                        if (imgThirdBitmap.getHeight() < imgThirdBitmap.getWidth()) {
                            imgThirdBitmap = rotateBitmap(imgThirdBitmap);
                        }
                        thirdImageView.setImageBitmap(imgThirdBitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 4:
                if (resultCode == RESULT_OK) {
                    pathImgFourthString = fourthUri.getPath().toString();
                    try {
                        imgFourthBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(fourthUri));
                        if (imgFourthBitmap.getHeight() < imgFourthBitmap.getWidth()) {
                            imgFourthBitmap = rotateBitmap(imgFourthBitmap);
                        }
                        fourthImageView.setImageBitmap(imgFourthBitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }

                break;
        }
    }


    private class SynUploadImage extends AsyncTask<Void, Void, String> {
        private Context context;
        private Bitmap bitmap;
        private String invoiceNoString, subjobNoString, mFileNameString, storeIdString;
        private UploadImageUtils uploadImageUtils;


        public SynUploadImage(Context context, Bitmap bitmap, String invoiceNoString, String subjobNoString, String mFileNameString, String storeIdString) {
            this.context = context;
            this.bitmap = bitmap;
            this.invoiceNoString = invoiceNoString;
            this.subjobNoString = subjobNoString;
            this.mFileNameString = mFileNameString;
            this.storeIdString = storeIdString;
        }

        @Override
        protected String doInBackground(Void... params) {
            uploadImageUtils = new UploadImageUtils();
            final String result = uploadImageUtils.uploadFile(mFileNameString, urlUploadPicture, bitmap, storeIdString, "P", subjobNoString, invoiceNoString);
            if (result == "NOK") {
                return "NOK";

            } else {
                try {
                    GPSManager gpsManager = new GPSManager(DetailJobActivity.this);
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody requestBody = new FormEncodingBuilder()
                            .add("isAdd", "true")
                            .add("subjob_no", subjobNoString)
                            .add("invoiceNo", invoiceNoString)
                            .add("File_Name", result)
                            .add("user_name", loginStrings[5])
                            .add("StoreId", storeIdString)
                            .add("timeStamp", gpsManager.getDateTime())
                            .build();
                    Request.Builder builder = new Request.Builder();
                    Request request = builder.post(requestBody).url(urlSaveImagePerStore).build();
                    Response response = okHttpClient.newCall(request).execute();
                    return response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("Tag", String.valueOf(e) + " Line: " + e.getStackTrace()[0].getLineNumber());
                    return null;
                }

            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Tag", "___________________" + s);
            if (s.equals("OK")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getText(R.string.save_img_success), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getText(R.string.save_img_unsuccessful), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }


    private Bitmap rotateBitmap(Bitmap src) {

        // create new matrix
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(90);
        Bitmap bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return bmp;
    }
}
