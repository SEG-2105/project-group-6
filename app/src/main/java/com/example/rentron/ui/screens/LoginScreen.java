package com.example.rentron.ui.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.rentron.app.App;
import com.example.rentron.R;
import com.example.rentron.data.handlers.UserHandler;
import com.example.rentron.data.models.User;
import com.example.rentron.data.models.UserRoles;
import com.example.rentron.ui.core.StatefulView;
import com.example.rentron.ui.core.UIScreen;
import com.example.rentron.utils.Preconditions;
import com.example.rentron.utils.Response;

public class LoginScreen extends UIScreen implements StatefulView {
    boolean loginInProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // variable to track if login is in progress, initially set to false
        setLoginInProcess(false);

        Button loginBtn = (Button) findViewById(R.id.loginSubmitBtn);
        loginBtn.setOnClickListener(v -> {

            // if login already in progress, don't start process again
            if (loginInProcess) {
                displayErrorToast("Login in process already! Please wait!");
                return;
            }

            // handle login
            Response loginResponse = loginSubmissionHandler();
            if (loginResponse.isSuccess()) {
                displaySuccessToast(loginResponse.getSuccessMessage());
                // set login to be in process & display loading spinner
                setLoginInProcess(true);
            } else {
                displayErrorToast(loginResponse.getErrorMessage());
                setLoginInProcess(false);
            }
        });

        Button signupBtn = findViewById(R.id.goSignUpBtn);
        signupBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignupScreen.class);
            startActivity(intent);
        });
        ImageButton backBtn = findViewById(R.id.back_btn4);
        backBtn.setOnClickListener(v -> finish());
    }

    private Response loginSubmissionHandler() {

        EditText textEmail = findViewById(R.id.loginUsername);
        String email = textEmail.getText().toString();
        if (email.equals("")) {
            return new Response(false, "Please enter your email address");
        }

        EditText textPassword = findViewById(R.id.loginPassword);
        String password = textPassword.getText().toString();
        if (password.equals("")) {
            return new Response(false, "Please enter your password");
        }

        // initiate login process (async process, no blocking return)
        App.USER_HANDLER.logInUser(this, email, password);

        return new Response(true, "Login submitted, please wait");
    }

    @Override
    public void showNextScreen() {
        // this method gets called when login completed
        User currentUser = App.getAppInstance().getUser();
        setLoginInProcess(false);
        if (currentUser.getRole() == UserRoles.PROPERTY_MANAGER){
            Intent intent = new Intent(getApplicationContext(), PropertyManagerScreen.class);
            startActivity(intent);
        } else if (currentUser.getRole() == UserRoles.LANDLORD){
            Intent intent = new Intent(getApplicationContext(), LandlordScreen.class);
            startActivity(intent);
        } else if (currentUser.getRole() == UserRoles.CLIENT){
            Intent intent = new Intent(getApplicationContext(), ClientScreen.class);
            startActivity(intent);
        } else {
            displayErrorToast("Unable to log in. Invalid user role!");
        }
    }

    @Override
    public void updateUI() {
        if (loginInProcess) {
            // display loading spinner
        } else {
            // hide loading spinner
        }
    }

    /**
     * Method to handle success of a DB operation
     */
    @Override
    public void dbOperationSuccessHandler(Object dbOperation, Object payload) {
        if (dbOperation == UserHandler.dbOperations.USER_LOG_IN) {
            // show next screen
            showNextScreen();
        }
    };

    /**
     * Method to handle failure of a DB operation
     */
    @Override
    public void dbOperationFailureHandler(Object dbOperation, Object payload) {
        if (dbOperation == UserHandler.dbOperations.USER_LOG_IN) {
            // login process failed
            setLoginInProcess(false);
            displayErrorToast((String) payload);
        }
    };

    /**
     * Method to set login process status indicator
     * @param inProcess status of login process
     */
    private void setLoginInProcess(boolean inProcess) {
        this.loginInProcess = inProcess;
        updateUI();
    }

    /**
     * Method called when a Landlord who logs in is currently suspended
     * @param landlordSuspensionDate date until which landlord is suspended
     * @param landlordName name of the Landlord for displaying message
     */
    public void handleSuspendedLandlordLogin(String landlordSuspensionDate, String landlordName) {

        setLoginInProcess(false);

        // if we have a valid suspension date
        if (Preconditions.isNotEmptyString(landlordSuspensionDate)){
            // pass data to Welcome screen so a message can be shown
            Intent intent = new Intent(getApplicationContext(), WelcomeScreen.class);
            // set intent data
            intent.putExtra(WelcomeScreen.LANDLORD_SUSPENSION_DATE_KEY, landlordSuspensionDate);
            intent.putExtra(WelcomeScreen.LANDLORD_NAME_KEY, landlordName);
            startActivity(intent);
        } else {
            Log.e("handleSuspendedLandlord", "Received invalid suspension date");
            displayErrorToast("Cannot process login request");
        }
    }
}
