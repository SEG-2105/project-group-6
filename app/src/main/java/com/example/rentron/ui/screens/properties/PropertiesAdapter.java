package com.example.rentron.ui.screens.properties;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rentron.R;
import com.example.rentron.data.models.properties.Property;
import com.example.rentron.ui.screens.PropertyInfoScreen;

import java.util.List;

public class PropertiesAdapter extends ArrayAdapter<Property> {

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public PropertiesAdapter(@NonNull Context context, int resource, @NonNull List<Property> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Property property = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            // using activity_properties_list_item view
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_properties_list_item, parent, false);
        }
        // Populate the property data
        ((TextView) convertView.findViewById(R.id.plPropertyId)).setText(property.getPropertyID());
        ((TextView) convertView.findViewById(R.id.plPropertyAddress)).setText(property.getAddress());
        ((TextView) convertView.findViewById(R.id.plOffered)).setText(property.isOffered() ? "Yes" : "No");
        ((TextView) convertView.findViewById(R.id.plPropertyType)).setText(property.getPropertyType());
        // attach on click listener to the property item
        LinearLayout propertyItemContainer = convertView.findViewById(R.id.plItemContainer);
        // Cache row position inside the button using `setTag`
        propertyItemContainer.setTag(position);
        // attach the click event handler
        propertyItemContainer.setOnClickListener(view -> {
            int position1 = (Integer) view.getTag();
            // Use the cached row position number inside LinearLayout's tag
            Property propertyData = getItem(position1);

            Bundle extras = new Bundle();
            extras.putSerializable(PropertyInfoScreen.PROPERTY_DATA_ARG_KEY, propertyData);
            Intent propertyInfoIntent = new Intent(getContext(), PropertyInfoScreen.class);
            propertyInfoIntent.putExtras(extras);

            // show property info screen, passing it the property's data
            startActivity(getContext(), propertyInfoIntent, null);
        });
        return convertView;
    }
}
