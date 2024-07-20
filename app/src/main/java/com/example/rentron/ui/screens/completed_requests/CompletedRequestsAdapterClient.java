package com.example.rentron.ui.screens.completed_requests;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rentron.R;
import com.example.rentron.data.models.Request;
import com.example.rentron.data.models.requests.PropertyInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class CompletedRequestsAdapterClient extends ArrayAdapter<Request> {

    /**
     * Constructor
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public CompletedRequestsAdapterClient(@NonNull Context context, int resource, @NonNull List<Request> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Variable Declaration: getting request data item for given position
        Request request = getItem(position);
        String propertyNames = "";
        String quantities = "";

        // Process: checking if existing view is being reused
        if (convertView == null) { //must inflate view

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_completed_requests_client_list_item, parent, false);

        }

        // Process: traversing entire properties map
        for (PropertyInfo pI : request.getProperties().values()) {

            propertyNames += pI.getAddress() + "\n";
            quantities += pI.getQuantity() + "\n";

        }

        ((TextView) convertView.findViewById(R.id.userNameText)).setText("Landlord: " + request.getLandlordInfo().getLandlordName());

        // Process: setting the request info to appear on the screen
        ((TextView) convertView.findViewById(R.id.propertyNameText2)).setText("\n" + propertyNames);
        ((TextView) convertView.findViewById(R.id.quantityText2)).setText("(#)\n" + quantities);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd\nhh:mm:ss");
        ((TextView) convertView.findViewById(R.id.dateText2)).setText("Date:\n" + dateFormat.format(request.getRequestDate()));

        return convertView;
    }
}
