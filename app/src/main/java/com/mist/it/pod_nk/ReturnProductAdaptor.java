package com.mist.it.pod_nk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yaowaluk on 18/07/2560.
 */

public class ReturnProductAdaptor extends BaseAdapter {
    //Explicit
    private Context context;
    private String[] modelStrings, detailStrings, amountStrings, returnQtyStrings;
    ReturnListViewHolder productListViewHolder;

    public ReturnProductAdaptor(Context context, String[] modelStrings, String[] detailStrings, String[] amountStrings, String[] returnQtyStrings) {
        this.context = context;
        this.modelStrings = modelStrings;
        this.detailStrings = detailStrings;
        this.amountStrings = amountStrings;
        this.returnQtyStrings = returnQtyStrings;
    }

    @Override
    public int getCount() {
        return modelStrings.length;
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
            // create view holder for remember data in list view
            convertView = LayoutInflater.from(context).inflate(R.layout.return_listview, null);
            productListViewHolder = new ReturnListViewHolder(convertView);
            convertView.setTag(productListViewHolder);
        } else {
            productListViewHolder = (ReturnListViewHolder) convertView.getTag();
        }

        String text = convertView.getResources().getString(R.string.Model) + " : " + modelStrings[position];
        productListViewHolder.modelTextView.setText(text);
        text = convertView.getResources().getString(R.string.Desc) + " : " + detailStrings[position];
        productListViewHolder.detailTextView.setText(text);
        text = convertView.getResources().getString(R.string.Quantity) + " : " + amountStrings[position];
        productListViewHolder.amountTextView.setText(text);
        text = convertView.getResources().getString(R.string.RetQuantity) + " : " + returnQtyStrings[position];
        productListViewHolder.returnAmtTextView.setText(text);


        return convertView;
    }

    class ReturnListViewHolder {
        @BindView(R.id.textView)
        TextView modelTextView;
        @BindView(R.id.textView2)
        TextView detailTextView;
        @BindView(R.id.textView3)
        TextView amountTextView;
        @BindView(R.id.textView4)
        TextView returnAmtTextView;

        ReturnListViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
