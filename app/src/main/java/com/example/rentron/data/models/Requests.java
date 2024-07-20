package com.example.rentron.data.models;

import androidx.annotation.NonNull;

import com.example.rentron.utils.Preconditions;
import com.example.rentron.utils.Response;
import com.example.rentron.utils.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Requests implements Comparator<Request> {
    private Map<String, Request> requests;

    public Requests(){
        this.requests = new HashMap<>();
    }

    public void setRequests(@NonNull Map<String, Request> requestsData) {
        this.requests = requestsData;
    }

    public Response addRequest(@NonNull Request newRequest) {
        // guard-clause
        // property must have a valid id which will be used as a key
        if (Preconditions.isNotEmptyString(newRequest.getRequestID())) {
            // check if property already exists
            if (this.requests.get(newRequest.getRequestID()) != null) {
                return new Response(false, "Property with same ID already exists! Use updateProperty to update an existing property");
            }
            // add the new property
            this.requests.put(newRequest.getRequestID(), newRequest);
            // return success
            return new Response(true);
        } else {
            return new Response(false, "Property does not have a valid ID");
        }
    }

    public Result<Request, String> getRequest(@NonNull String requestID) {
        // guard-clause
        if (Preconditions.isNotEmptyString(requestID)) {
            // check if property exists
            if (this.requests.get(requestID) != null) {
                return new Result<>(this.requests.get(requestID), null);
            } else {
                return new Result<>(null, "Could not find any property for the provided property ID");
            }
        } else {
            return new Result<>(null, "Invalid property ID provided");
        }
    }

    /**
     * Method to retrieve a list containing all pending Requests by the Landlord
     * @return a List containing Request objects
     */
    public List<Request> getPendingRequests() {

        // Variable Declaration
        ArrayList<Request> pendingList = new ArrayList<>();

        // Process: looping through requests
        for (Request request : this.requests.values()) {

            // Process: checking if request is pending to be accepted OR accepted but not completed (not completed and not rejected)
            if (request.getIsPending()) {

                pendingList.add(request); //adding to list

            }

        }

        // Process: sorting the list by date placed
        Collections.sort(pendingList, this);

        // Output
        return pendingList;

    }

    /**
     * Method to retrieve a list containing all pending Requests of a Client
     * @return a List containing Request objects
     */
    public List<Request> getClientsPendingRequests() {

        // Variable Declaration
        ArrayList<Request> pendingList = new ArrayList<>();

        // Process: looping through requests
        for (Request request : this.requests.values()) {

            // Process: checking if request is pending to be accepted OR accepted but not completed (not completed and not rejected)
            if (request.getIsPending() || (!request.getIsPending() && !request.getIsCompleted() && !request.getIsRejected())) {

                pendingList.add(request); //adding to list

            }

        }

        // Process: sorting the list by date placed
        Collections.sort(pendingList, this);

        // Output
        return pendingList;

    }

    /**
     * Method to retrieve a list containing all Requests in progress by the Landlord
     * @return a List containing Request objects
     */
    public List<Request> getRequestsInProgress() {
        // Variable Declaration
        ArrayList<Request> requestsInProgress = new ArrayList<>();

        // Process: looping through requests
        for (Request request : this.requests.values()) {

            // Process: checking if completed
            if (!request.getIsCompleted() && !request.getIsRejected() && !request.getIsPending()) {

                requestsInProgress.add(request); //adding to list
            }
        }

        // Process: sorting the list by date placed
        Collections.sort(requestsInProgress, this);

        // Output
        return requestsInProgress;
    }

    /**
     * Method to retrieve a list containing all completed Requests by the Landlord
     * @return a List containing Request objects
     */
    public List<Request> getCompletedRequests() {
        // Variable Declaration
        ArrayList<Request> completedList = new ArrayList<>();

        // Process: looping through requests
        for (Request request : this.requests.values()) {

            // Process: checking if completed
            if (request.getIsCompleted()) {

                completedList.add(request); //adding to list

            }

        }

        // Process: sorting the list by date placed
        Collections.sort(completedList, this);

        // Output
        return completedList;
    }

    public Response removeRequest(@NonNull String requestId) {
        // guard-clause
        if (Preconditions.isNotEmptyString(requestId)) {
            // check if property exists
            if (this.requests.get(requestId) != null) {
                // remove the property
                this.requests.remove(requestId);
                // return operation success
                return new Response(true);
            } else {
                return new Response(false, "Could not find any property for the provided request ID");
            }
        } else {
            return new Response(false, "Invalid request ID provided");
        }
    }

    // update pending status and completed status from landlord
    public void updateRequest(Request request) {

        if (Preconditions.isNotNull(request)){

            Request request1 = this.requests.get(request.getRequestID());
            request1.setIsCompleted(request.getIsCompleted());
            request1.setIsRejected(request.getIsRejected());
            request1.setIsPending(request.getIsPending());

        }
    }

    /**
     * this method compares the requests by the dates they were placed
     * @param request1 the first request
     * @param request2 the request the first is being compared to
     * @return 0 if same date; -1 is request2 was placed first; 1 if request1 was placed first
     */
    @Override
    public int compare(Request request1, Request request2) {
        // Output
        return request2.getRequestDate().compareTo(request1.getRequestDate());
    }

}
