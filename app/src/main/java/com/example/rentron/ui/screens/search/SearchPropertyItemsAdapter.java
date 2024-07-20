package com.example.rentron.ui.screens.search;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rentron.R;
import com.example.rentron.data.models.properties.Property;
import com.example.rentron.data.models.requests.LandlordInfo;
import com.example.rentron.ui.screens.RequestScreen;

import java.util.List;

public class SearchPropertyItemsAdapter extends ArrayAdapter<SearchPropertyItem> {

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public SearchPropertyItemsAdapter(@NonNull Context context, int resource, @NonNull List<SearchPropertyItem> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        SearchPropertyItem sMItem = getItem(position);
        // retrieve the property
        Property property = sMItem.getProperty();
        // retrieve LandlordInfo
        LandlordInfo landlordInfo = sMItem.getLandlord();
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            // using activity_properties_list_item view
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_search_property_item, parent, false);
        }
        // Populate the property data
        ((TextView) convertView.findViewById(R.id.smPropertyId)).setText(property.getPropertyID());
        ((TextView) convertView.findViewById(R.id.smPropertyName)).setText(property.getAddress());
        ((TextView) convertView.findViewById(R.id.smLandlord)).setText(landlordInfo.getLandlordName());
        ((RatingBar) convertView.findViewById(R.id.smLandlordRating)).setRating((float) landlordInfo.getLandlordRating());
        // attach on click listener to the property item
        LinearLayout propertyItemContainer = convertView.findViewById(R.id.smItemContainer);
        // Cache row position inside the button using `setTag`
        propertyItemContainer.setTag(position);
        // attach the click event handler
        propertyItemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                // Use the cached row position number inside LinearLayout's tag
                SearchPropertyItem sMItemData = getItem(position);

                Intent requestScreenIntent = new Intent(getContext(), RequestScreen.class);
                requestScreenIntent.putExtra(RequestScreen.SEARCH_PROPERTY_ITEM_ARG_KEY, sMItemData);

                // show request screen, passing it the search property item data which contains property and landlord info
                startActivity(getContext(), requestScreenIntent, null);
            }
        });
        return convertView;
    }
}
