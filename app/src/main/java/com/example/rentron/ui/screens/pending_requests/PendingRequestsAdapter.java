package com.example.rentron.ui.screens.pending_requests;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class PendingRequestsAdapter extends ArrayAdapter<Request> {

    /**
     * Constructor
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public PendingRequestsAdapter(@NonNull Context context, int resource, @NonNull List<Request> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Variable Declaration: getting request data item for given position
        Request request = getItem(position);
        String propertyNames = "";
        String quantities = "";
        String emailContents = "";

        // Process: checking if existing view is being reused
        if (convertView == null) { //must inflate view

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_pending_requests_list_item, parent, false);
        }

        // Process: traversing entire properties map
        for (PropertyInfo pI : request.getProperties().values()) {

            propertyNames += pI.getAddress() + "\n";
            quantities += pI.getQuantity() + "\n";
            emailContents += pI.getQuantity() + " " + pI.getAddress();

        }

        // Variable Declaration
        final String EMAIL_CONTENTS = emailContents; //constant property names & quantities


        ((TextView) convertView.findViewById(R.id.userNameText)).setText("Client: " + request.getClientInfo().getClientName());

        // EMAIL FORMATTING ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
        // NOTE: the image will not appear for Outlook because of Microsoft settings

        // Process: Sends email to client if their property has been REJECTED
        ((Button) convertView.findViewById(R.id.rejectButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Process: updating request to not pending and rejected
                request.setIsPending(false); //no longer pending
                request.setIsRejected(true); //rejected

                App.REQUEST_HANDLER.dispatch(RequestHandler.dbOperations.UPDATE_REQUEST, request, App.getAppInstance().getPendingRequestsScreen()); //updating in Firebase

                // Process: sending email to client that request has been rejected
                new SendMailTask().execute("rentronprojectgroup4@gmail.com", "zzzbziucedxljweu",
                        request.getClientInfo().getClientEmail(),
                        "REJECTED RENTRON Request #: " + request.getRequestID().substring(0, 6),
                        "Hello " + request.getClientInfo().getClientName() + ",<br><br>" +
                                "We regret to inform you that the following request has been rejected by Landlord " + request.getLandlordInfo().getLandlordName()
                                + ".<br><br>" +
                                EMAIL_CONTENTS +
                                "<br><br><br>"
                                + "Thank you for understanding.<br><br>" +
                               "<br>RENTRON Team");
            }
        });

        // Process: Sends email to client if their property has been ACCEPTED
        ((Button) convertView.findViewById(R.id.acceptButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Process: updating request to not pending and rejected
                request.setIsPending(false); //no longer pending
                request.setIsRejected(false); //not rejected

                App.REQUEST_HANDLER.dispatch(RequestHandler.dbOperations.UPDATE_REQUEST, request, App.getAppInstance().getPendingRequestsScreen()); //updating in Firebase

                // Process: sending email to client that request has been accepted
                new SendMailTask().execute("reemaalwadi427@gmail.com", "zzzbziucedxljweu",
                        request.getClientInfo().getClientEmail(),
                        "ACCEPTED RENTRON Request #: " + request.getRequestID().substring(0, 6),
                        "Hello " + request.getClientInfo().getClientName() + ",<br><br>" +
                                "The following request has been accepted by Landlord " + request.getLandlordInfo().getLandlordName() +
                                ".<br><br>" +
                                "You will receive another email notification when your request is ready for pick-up at " +
                                request.getLandlordInfo().getLandlordAddress().getStreetAddress() + ", " +
                                request.getLandlordInfo().getLandlordAddress().getCity() + " " +
                                request.getLandlordInfo().getLandlordAddress().getPostalCode() +
                                ".<br><br><br>" +
                                EMAIL_CONTENTS +
                                "<br><br><br>" +
                                "Thank you for placing your request through the RENTRON app!" +
                                "<br>" +
                                "If you have any questions about your request, please email us directly at " +
                                "reemaalwadi427@gmail.com with your request number provided in the subject line." +
                                "<br><br><br>" + "<br>" +
                                "RENTRON Team");
            }
        });
        // Process: setting the request info to appear on the screen
        ((TextView) convertView.findViewById(R.id.propertyNameText2)).setText("\n" + propertyNames);
        ((TextView) convertView.findViewById(R.id.quantityText2)).setText("(#)\n" + quantities);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd\nhh:mm:ss");
        ((TextView) convertView.findViewById(R.id.dateText2)).setText("Date:\n" + dateFormat.format(request.getRequestDate()));

        return convertView;
    }
}
