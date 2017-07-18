package com.mist.it.pod_nk;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.Voice;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mist.it.pod_nk.MyConstant.urlGetJobDetailProduct;

public class ReturnActivity extends AppCompatActivity {

    @BindView(R.id.txtRAInvoiceNo)
    TextView invoiceNoTextView;
    @BindView(R.id.lisRAItemList)
    ListView itemListView;
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

    private String[] invoiceNoStrings, imgFileNameStrings, subJobStrings;
    private String[][] modelStrings, amountStrings, detailStrings, returnAmountStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);

        //Bind Widget
        ButterKnife.bind(this);

        SynJobDtlProduct synJobDtlProduct = new SynJobDtlProduct(this, "", "");
        synJobDtlProduct.execute(urlGetJobDetailProduct);


    }// main Method

    private class SynJobDtlProduct extends AsyncTask<String, Void, String> {
        //Explicit
        private Context context;
        private String subjob_no, invoiceNo;

        public SynJobDtlProduct(Context context, String subjob_no, String invoiceNo) {
            this.context = context;
            this.subjob_no = subjob_no;
            this.invoiceNo = invoiceNo;
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

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                invoiceNoStrings = new String[jsonArray.length()];
                subJobStrings = new String[jsonArray.length()];
                imgFileNameStrings = new String[jsonArray.length()];
                modelStrings = new String[jsonArray.length()][];
                amountStrings = new String[jsonArray.length()][];
                detailStrings = new String[jsonArray.length()][];
                returnAmountStrings = new String[jsonArray.length()][];                //set Text


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
                    }


                }


                invoiceNoTextView.setText(getResources().getString(R.string.InvoiceNo)+" : " + invoiceNoStrings[0]);
                ReturnProductAdaptor returnProductAdaptor = new ReturnProductAdaptor(context, modelStrings[0], detailStrings[0], amountStrings[0], returnAmountStrings[0]);
                itemListView.setAdapter(returnProductAdaptor);
            } catch (Exception e) {
                Log.d("Tag", "onPostExecute: " + e.getStackTrace()[0].getLineNumber());
            }

        }
    }//end class synJobDetailProduct



    /*protected class ReturnAdaptor extends BaseAdapter{


        @Override
        public int getCount() {
            return 0;
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
            return null;
        }
    }
*/


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
}
