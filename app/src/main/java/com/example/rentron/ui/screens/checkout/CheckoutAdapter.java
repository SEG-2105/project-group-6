package com.example.rentron.ui.screens.checkout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rentron.R;
import com.example.rentron.data.models.requests.RequestItem;

import java.text.DecimalFormat;
import java.util.List;

/**
 * This class will be used to change the text of each item in the cart based on its information
 * (property name + price + quantity -> the item's quantity can still be changed in the checkout screen)
 */
public class CheckoutAdapter extends ArrayAdapter<RequestItem> {

    // format price to two decimal places
    private static final DecimalFormat df = new DecimalFormat("0.00");

    /**
     *
     * @param context
     * @param resource
     * @param objects
     */
    public CheckoutAdapter(@NonNull Context context, int resource, @NonNull List<RequestItem> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Get data for item in cart
        RequestItem item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            // using checkout_item view
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.checkout_item, parent, false);

        }

        // Populate the data
        ((TextView) convertView.findViewById(R.id.item_property_title)).setText(item.getSearchPropertyItem().getProperty().getAddress());
        double price = item.getSearchPropertyItem().getProperty().getPrice();
        ((TextView) convertView.findViewById(R.id.item_price)).setText("$ " + df.format(price));
        TextView quantity = ((TextView) convertView.findViewById(R.id.item_quantity));
        quantity.setText(String.valueOf(item.getQuantity()));

        return convertView;
    }
}
