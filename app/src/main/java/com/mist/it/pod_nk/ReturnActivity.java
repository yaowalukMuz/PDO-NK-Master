package com.mist.it.pod_nk;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

import static com.mist.it.pod_nk.MyConstant.urlGetJobDetailProduct;
import static com.mist.it.pod_nk.MyConstant.urlSaveQuantityReturnByItem;

public class ReturnActivity extends AppCompatActivity {

    @BindView(R.id.txtRAInvoiceNo)
    TextView invoiceNoTextView;
    @BindView(R.id.imgRAOne)
    ImageView firstImageView;
    @BindView(R.id.imgRATwo)
    ImageView secondImageView;
    @BindView(R.id.btnRASave)
    Button saveButton;
    @BindView(R.id.btnRAReturnAll)
    Button returnAllButton;
    @BindView(R.id.btnRAConfirm)
    Button confirmButton;
    @BindView(R.id.lisRAItemList)
    ListView itemListView;


    private String[] invoiceNoStrings, imgFileNameStrings, subJobStrings;
    private String[][] modelStrings, amountStrings, detailStrings, returnAmountStrings;
    private ArrayList<ReturnItem> returnItems = new ArrayList<ReturnItem>();
    DialogViewHolder dialogViewHolder;
    AlertMessageViewHolder alertMessageViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);

        //1. Bind Widget
        ButterKnife.bind(this);
        // 2.create class for synDataAdaptor to listview
        SynJobDtlProduct synJobDtlProduct = new SynJobDtlProduct(this, "", "", returnItems);
        synJobDtlProduct.execute(urlGetJobDetailProduct);


    }// main Method


//    @OnClick({R.id.button3, R.id.button2})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.button3:
//                break;
//            case R.id.button2:
//                break;
//        }
//    }


    //Set On Click Listener
    @OnClick({R.id.btnRASave, R.id.btnRAReturnAll, R.id.btnRAConfirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnRASave:
                break;
            case R.id.btnRAReturnAll:
                break;
            case R.id.btnRAConfirm:
                break;
        }
    }

    @OnItemClick(R.id.lisRAItemList)
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
       // Log.d("Tag", "onItemSelected: " + position);

        //event onclick listview
        final View view1 = View.inflate(getBaseContext(), R.layout.dialog_return, null);
        final View view2 = View.inflate(getBaseContext(), R.layout.custom_alert, null);

        dialogViewHolder = new DialogViewHolder(view1);

        AlertDialog.Builder builder = new AlertDialog.Builder(ReturnActivity.this);
        dialogViewHolder.titleTextview.setText("Return : " + returnItems.get(position).getModelString());
        dialogViewHolder.amtTxtTextview.setText("Amount : ");
        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                    if (Integer.parseInt(dialogViewHolder.amtRtnEditText.getText().toString()) < Integer.parseInt(returnItems.get(position).getAmountString())) {
                        SyncUpdateTurn syncUpdateTurn = new SyncUpdateTurn(ReturnActivity.this, returnItems.get(position).getInvoiceNoString(),
                                returnItems.get(position).getInvoiceSeqString(), returnItems.get(position).getModelString(),
                                returnItems.get(position).getRetrunAmountString());
                        syncUpdateTurn.execute(urlSaveQuantityReturnByItem);
                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ReturnActivity.this);
                       // alertMessageViewHolder.imgAlertImageView.setImageResource(R.drawable.caution);
                        alertMessageViewHolder.headerTextView.setText("Over amount");
                        alertMessageViewHolder.descriptTextView.setText("Please check return amount is over");
                        builder1.setView(view2);
                        builder1.show();
                    }


            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setView(view1);
        builder.show();


    }


    //inner class for syn data from adaptor
    protected class SynJobDtlProduct extends AsyncTask<String, Void, String> {

        private String[][] modelStrings, amountStrings, detailStrings, returnAmountStrings;
        private String[] invoiceNoStrings, subJobStrings, imgFileNameStrings;
        //Explicit
        private Context context;
        private String subjob_no, invoiceNo;
        private ArrayList<ReturnItem> returnItmes;

        public SynJobDtlProduct(Context context, String subjob_no, String invoiceNo, ArrayList<ReturnItem> returnItmes) {
            this.context = context;
            this.subjob_no = subjob_no;
            this.invoiceNo = invoiceNo;
            this.returnItmes = returnItmes;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder().add("isAdd", "true")
                        .add("subjob_no", subjob_no)
                        .add("invoiceNo", invoiceNo).build();

                Request.Builder builder = new Request.Builder();
                Request request = builder.url(urlGetJobDetailProduct).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                return "NOK";
            }


        }//end doInBack

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Tag", "onPostExecute: " + s);

            // returnItmes = new ArrayList<ReturnItem>();
            //List<Map<String ,Objects>> retrunProductItem = new ArrayList<Map<String,Objects>>();
            // Map<String, Objects> map = new Hashtable<String, Objects>();

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                invoiceNoStrings = new String[jsonArray.length()];
                subJobStrings = new String[jsonArray.length()];
                imgFileNameStrings = new String[jsonArray.length()];
                modelStrings = new String[jsonArray.length()][];
                amountStrings = new String[jsonArray.length()][];
                detailStrings = new String[jsonArray.length()][];
                returnAmountStrings = new String[jsonArray.length()][];


                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    invoiceNoStrings[i] = jsonObject1.getString("Invoice");
                    subJobStrings[i] = jsonObject1.getString("SubJobNo");
                    imgFileNameStrings[i] = jsonObject1.getString("ImgPath");

                    JSONArray productArray = jsonObject1.getJSONArray("Product");

                    modelStrings[i] = new String[productArray.length()];
                    amountStrings[i] = new String[productArray.length()];
                    detailStrings[i] = new String[productArray.length()];
                    returnAmountStrings[i] = new String[productArray.length()];

                    for (int j = 0; j < productArray.length(); j++) {
                        JSONObject jsonObject2 = productArray.getJSONObject(j);
                        modelStrings[i][j] = jsonObject2.getString("Product");
                        amountStrings[i][j] = jsonObject2.getString("Quantity");
                        detailStrings[i][j] = jsonObject2.getString("Product1");
                        returnAmountStrings[i][j] = jsonObject2.getString("QTYRT");

                        ReturnItem returnItem = new ReturnItem();
                        returnItem.setModelString(jsonObject2.getString("Product"));
                        returnItem.setRetrunAmountString(jsonObject2.getString("QTYRT"));
                        returnItem.setAmountString(jsonObject2.getString("Quantity"));
                        returnItmes.add(returnItem);
                        Log.d("Tag", "returnItem: " + returnItmes);


                    }


                }

                //set adapter Listview
                invoiceNoTextView.setText(getResources().getString(R.string.InvoiceNo) + " : " + invoiceNoStrings[0]);
                // ReturnProductAdaptor returnProductAdaptor = new ReturnProductAdaptor(context, modelStrings[0], detailStrings[0], amountStrings[0], returnAmountStrings[0]);
                ReturnProductAdaptor returnProductAdaptor = new ReturnProductAdaptor(context, returnItmes);

                itemListView.setAdapter(returnProductAdaptor);


            } catch (Exception e) {
                Log.d("Tag", "onPostExecute: " + e + " Line: " + e.getStackTrace()[0].getLineNumber());
            }

        }


    }//end class synJobDetailProduct

    private class SyncUpdateTurn extends AsyncTask<String, Void, String> {
        private Context context;
        private String invoiceNoString, invoiceSeqString, modelString, amountReturnString;

        public SyncUpdateTurn(Context context, String invoiceNoString, String invoiceSeqString, String modelString, String amountReturnString) {
            this.context = context;
            this.invoiceNoString = invoiceNoString;
            this.invoiceSeqString = invoiceSeqString;
            this.modelString = modelString;
            this.amountReturnString = amountReturnString;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("Tag", "onPostExecute:->return save return product:::::  " + s);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("invoiceNo", invoiceNoString)
                        .add("model", modelString)
                        .add("amountReturn", amountReturnString).build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.post(requestBody).url(urlSaveQuantityReturnByItem).build();
                Response response = okHttpClient.newCall(request).execute();


                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }


        }

    }


    static class DialogViewHolder {
        //        @Nullable
        @BindView(R.id.editText)
        EditText amtRtnEditText;
        @BindView(R.id.textView5)
        TextView amtTxtTextview;
        @BindView(R.id.textView6)
        TextView titleTextview;


        DialogViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class AlertMessageViewHolder {
        @BindView(R.id.imgCAAlert)
        ImageView imgAlertImageView;
        @BindView(R.id.txtCAHeader)
        TextView headerTextView;
        @BindView(R.id.txtCADescript)
        TextView descriptTextView;

        AlertMessageViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}





