package com.example.rentron.ui.screens;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rentron.R;
import com.example.rentron.app.App;
import com.example.rentron.data.handlers.RequestHandler;
import com.example.rentron.data.models.Request;
import com.example.rentron.data.models.requests.PropertyInfo;
import com.example.rentron.utils.SendMailTask;

import java.text.SimpleDateFormat;
import java.util.List;

public class RequestsInProgressAdapter extends ArrayAdapter<Request>{

    /**
     * Constructor
     *
     * @param context
     * @param resource
     * @param objects
     */
    public RequestsInProgressAdapter(@NonNull Context context, int resource, @NonNull List<Request> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // get landlord's requests in progress
        Request request = getItem(position);

        // get request data item for given position
        String propertyNames = "";
        String quantities = "";
        String emailContents = "";

        // check if an existing view is being reused, otherwise, inflate
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.requests_in_progress, parent, false);
        }

        // Process: traversing entire properties map
        for (PropertyInfo pI : request.getProperties().values()) {

            propertyNames += pI.getAddress() + "\n";
            quantities += pI.getQuantity() + "\n";
            emailContents += pI.getQuantity() + "   " + pI.getAddress() + "<br>";

        }

        // EMAIL FORMATTING ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
        // NOTE: the image will not appear for Outlook because of Microsoft settings

        // Variable Declaration
        final String EMAIL_CONTENTS = emailContents; //constant property names & quantities

        // on click on completed button
        // once this button is clicked, the user will receive an email
        ((Button) convertView.findViewById(R.id.doneButton)).setOnClickListener(v -> {
            request.setIsCompleted(true);
            App.REQUEST_HANDLER.dispatch(RequestHandler.dbOperations.UPDATE_REQUEST, request,  App.getAppInstance().getRequestsInProgressScreen());

            // Process: sending email to client that request has been rejected
            String str = "Hello " + request.getClientInfo().getClientName() + ","
                    + "<br><br>" +
                    "The following request is ready for pick-up at " +
                    request.getLandlordInfo().getLandlordAddress().getStreetAddress() + ", " +
                    request.getLandlordInfo().getLandlordAddress().getCity() + " " +
                    request.getLandlordInfo().getLandlordAddress().getPostalCode() + "."
                    + "<br><br>" +
                    EMAIL_CONTENTS
                    + "<br>" +
                    "If you have any questions about pick-up, please contact us at rentronprojectgroup4@gmail.com" +
                    " with your request number provided in the subject line."
                    + "<br><br><br>" + "<img src = https://raw.githubusercontent.com/uOttawaSEGA2022/project-group-4/master/app/src/main/res/drawable-v24/rentron.png width=\"100\" height=\"100\">"
                    + "<br>" +
                    "RENTRON Team";

            new SendMailTask().execute("rentronprojectgroup4@gmail.com", "zzzbziucedxljweu",
                    request.getClientInfo().getClientEmail(),
                    "READY-FOR-PICK-UP RENTRON Request #: " + request.getRequestID().substring(0, 6),
                    str);
        });


        // populate data
        ((TextView) convertView.findViewById(R.id.clientText)).setText("Client: " + request.getClientInfo().getClientName());
        ((TextView) convertView.findViewById(R.id.propertyNameText)).setText("Property(s): " + "\n" + propertyNames);
        ((TextView) convertView.findViewById(R.id.quantityOfPropertyInProgress)).setText("(#)\n" + quantities);

        // format the date string
        String stringDate = new SimpleDateFormat("yyyy-MM-dd").format(request.getRequestDate());
        ((TextView) convertView.findViewById(R.id.dateForRequest)).setText(stringDate);


        return convertView;
    }

}
