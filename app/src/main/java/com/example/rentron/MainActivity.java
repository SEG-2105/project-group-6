package com.example.rentron;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private LinearLayout loginForm, registerForm;
    private Button switchButton, loginButton, registerButton;
    private Button viewPropertyDetailsButton, assignRandomClientButton, invitePropertyManagerButton, replacePropertyManagerButton, addPropertyButton, viewAvailablePropertiesButton, searchPropertiesButton, submitRentalRequestButton, viewRequestsButton, createTicketButton, viewTicketsButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    private static final String USER_ROLE = "userRole";
    private static final String PREFERENCES = "RentronPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginForm = findViewById(R.id.loginForm);
        registerForm = findViewById(R.id.registerForm);
        switchButton = findViewById(R.id.switchButton);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        viewPropertyDetailsButton = findViewById(R.id.viewPropertyDetailsButton);
        assignRandomClientButton = findViewById(R.id.assignRandomClientButton);
        invitePropertyManagerButton = findViewById(R.id.invitePropertyManagerButton);
        replacePropertyManagerButton = findViewById(R.id.replacePropertyManagerButton);
        addPropertyButton = findViewById(R.id.addPropertyButton);
        viewAvailablePropertiesButton = findViewById(R.id.viewAvailablePropertiesButton);
        searchPropertiesButton = findViewById(R.id.searchPropertiesButton);
        submitRentalRequestButton = findViewById(R.id.submitRentalRequestButton);
        viewRequestsButton = findViewById(R.id.viewRequestsButton);
        createTicketButton = findViewById(R.id.createTicketButton);
        viewTicketsButton = findViewById(R.id.viewTicketsButton);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginForm.getVisibility() == View.VISIBLE) {
                    loginForm.setVisibility(View.GONE);
                    registerForm.setVisibility(View.VISIBLE);
                    switchButton.setText("Switch to Login");
                } else {
                    registerForm.setVisibility(View.GONE);
                    loginForm.setVisibility(View.VISIBLE);
                    switchButton.setText("Switch to Register");
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailInput = findViewById(R.id.loginEmail);
                EditText passwordInput = findViewById(R.id.loginPassword);

                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {
                    loginUser(email, password);
                } else {
                    Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText firstNameInput = findViewById(R.id.registerFirstName);
                EditText lastNameInput = findViewById(R.id.registerLastName);
                EditText emailInput = findViewById(R.id.registerEmail);
                EditText passwordInput = findViewById(R.id.registerPassword);
                EditText addressInput = findViewById(R.id.registerAddress);
                EditText userTypeInput = findViewById(R.id.registerUserType);

                String firstName = firstNameInput.getText().toString();
                String lastName = lastNameInput.getText().toString();
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                String address = addressInput.getText().toString();
                String userType = userTypeInput.getText().toString();

                if (!firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && !password.isEmpty() && !address.isEmpty() && !userType.isEmpty()) {
                    registerUser(firstName, lastName, email, password, address, userType);
                } else {
                    Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewPropertyDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String propertyId = "propertyId"; // Replace with actual property ID
                showPropertyDetailsDialog(propertyId);
            }
        });

        assignRandomClientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignRandomClient();
            }
        });

        invitePropertyManagerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInvitePropertyManagerDialog();
            }
        });

        replacePropertyManagerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReplacePropertyManagerDialog();
            }
        });

        addPropertyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddPropertyDialog();
            }
        });

        viewAvailablePropertiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAvailableProperties();
            }
        });

        searchPropertiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchPropertiesDialog();
            }
        });

        submitRentalRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSubmitRentalRequestDialog();
            }
        });

        viewRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewRequests();
            }
        });

        createTicketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateTicketDialog();
            }
        });

        viewTicketsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewTickets();
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                db.collection("users").document(user.getUid())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful() && task.getResult() != null) {
                                                    String userType = task.getResult().getString("userType");
                                                    sharedPreferences.edit().putString(USER_ROLE, userType).apply();
                                                    checkUserRole(userType);
                                                } else {
                                                    Toast.makeText(MainActivity.this, "Failed to get user details", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void registerUser(final String firstName, final String lastName, final String email, final String password, final String address, final String userType) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            saveUserDetails(userId, firstName, lastName, email, address, userType);
                        } else {
                            Toast.makeText(MainActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserDetails(String userId, String firstName, String lastName, String email, String address, String userType) {
        Map<String, Object> user = new HashMap<>();
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("email", email);
        user.put("address", address);
        user.put("userType", userType);

        db.collection("users").document(userId)
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            sharedPreferences.edit().putString(USER_ROLE, userType).apply();
                            checkUserRole(userType);
                            Toast.makeText(MainActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to save user details: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkUserRole(String userRole) {
        if (!userRole.equals("Landlord")) {
            viewPropertyDetailsButton.setEnabled(false);
            assignRandomClientButton.setEnabled(false);
            invitePropertyManagerButton.setEnabled(false);
            replacePropertyManagerButton.setEnabled(false);
            addPropertyButton.setEnabled(false);
            viewAvailablePropertiesButton.setEnabled(true);
            searchPropertiesButton.setEnabled(true);
            submitRentalRequestButton.setEnabled(true);
            viewRequestsButton.setEnabled(true);
            createTicketButton.setEnabled(true);
            viewTicketsButton.setEnabled(true);
        } else {
            viewPropertyDetailsButton.setEnabled(true);
            assignRandomClientButton.setEnabled(true);
            invitePropertyManagerButton.setEnabled(true);
            replacePropertyManagerButton.setEnabled(true);
            addPropertyButton.setEnabled(true);
            viewAvailablePropertiesButton.setEnabled(false);
            searchPropertiesButton.setEnabled(false);
            submitRentalRequestButton.setEnabled(false);
            viewRequestsButton.setEnabled(false);
            createTicketButton.setEnabled(false);
            viewTicketsButton.setEnabled(false);
        }
    }

    private void showPropertyDetailsDialog(String propertyId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_property_details, null);
        builder.setView(dialogView);

        TextView propertyNameTextView = dialogView.findViewById(R.id.propertyName);
        TextView propertyAddressTextView = dialogView.findViewById(R.id.propertyAddress);
        TextView propertyRentTextView = dialogView.findViewById(R.id.propertyRent);
        TextView propertyManagerEmailTextView = dialogView.findViewById(R.id.propertyManagerEmail);
        Button editPropertyRentButton = dialogView.findViewById(R.id.editPropertyRentButton);
        Button replacePropertyManagerButton = dialogView.findViewById(R.id.replaceManagerButton);

        db.collection("properties").document(propertyId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            propertyNameTextView.setText(document.getString("name"));
                            propertyAddressTextView.setText(document.getString("address"));
                            propertyRentTextView.setText(document.getString("rent"));
                            propertyManagerEmailTextView.setText(document.getString("managerEmail"));
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to load property details", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        editPropertyRentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditRentDialog(propertyId);
            }
        });

        replacePropertyManagerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReplacePropertyManagerDialog();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showEditRentDialog(String propertyId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_edit_rent, null);
        builder.setView(dialogView);

        EditText newRentEditText = dialogView.findViewById(R.id.newRent);
        Button saveNewRentButton = dialogView.findViewById(R.id.saveNewRentButton);

        saveNewRentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newRent = newRentEditText.getText().toString();
                if (!newRent.isEmpty()) {
                    updatePropertyRent(propertyId, newRent);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a new rent amount", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updatePropertyRent(String propertyId, String newRent) {
        db.collection("properties").document(propertyId)
                .update("rent", newRent)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Property rent updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to update rent: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showInvitePropertyManagerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_invite_property_manager, null);
        builder.setView(dialogView);

        EditText managerEmailEditText = dialogView.findViewById(R.id.managerEmail);
        Button inviteManagerButton = dialogView.findViewById(R.id.inviteManagerButton);

        inviteManagerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String managerEmail = managerEmailEditText.getText().toString();
                if (!managerEmail.isEmpty()) {
                    invitePropertyManager(managerEmail);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter manager's email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void invitePropertyManager(String managerEmail) {
        Map<String, Object> manager = new HashMap<>();
        manager.put("email", managerEmail);
        manager.put("status", "invited");

        db.collection("propertyManagers").add(manager)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d("PropertyManager", "Property manager invited: " + managerEmail);
                            Toast.makeText(MainActivity.this, "Property manager invited", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("PropertyManager", "Failed to invite property manager: ", task.getException());
                            Toast.makeText(MainActivity.this, "Failed to invite property manager: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showReplacePropertyManagerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_replace_property_manager, null);
        builder.setView(dialogView);

        EditText newManagerEmailEditText = dialogView.findViewById(R.id.newManagerEmail);
        Button replaceManagerButton = dialogView.findViewById(R.id.replaceManagerButton);

        replaceManagerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newManagerEmail = newManagerEmailEditText.getText().toString();
                if (!newManagerEmail.isEmpty()) {
                    replacePropertyManager(newManagerEmail);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter new manager's email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void replacePropertyManager(String newManagerEmail) {
        db.collection("propertyManagers").whereEqualTo("status", "invited")
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            DocumentSnapshot managerDoc = task.getResult().getDocuments().get(0);
                            managerDoc.getReference().update("email", newManagerEmail)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("PropertyManager", "Property manager replaced with: " + newManagerEmail);
                                                Toast.makeText(MainActivity.this, "Property manager replaced", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.e("PropertyManager", "Failed to replace property manager: ", task.getException());
                                                Toast.makeText(MainActivity.this, "Failed to replace property manager: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(MainActivity.this, "No invited property manager found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void assignRandomClient() {
        String dummyClientId = "dummyClient123";
        Map<String, Object> update = new HashMap<>();
        update.put("clientId", dummyClientId);

        db.collection("properties").document("propertyId") // Replace with actual property ID
                .update(update)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("ClientAssignment", "Random client assigned");
                            Toast.makeText(MainActivity.this, "Random client assigned", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("ClientAssignment", "Failed to assign random client: ", task.getException());
                            Toast.makeText(MainActivity.this, "Failed to assign random client: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showAddPropertyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_add_property, null);
        builder.setView(dialogView);

        EditText propertyNameEditText = dialogView.findViewById(R.id.propertyName);
        EditText propertyAddressEditText = dialogView.findViewById(R.id.propertyAddress);
        EditText propertyRentEditText = dialogView.findViewById(R.id.propertyRent);
        Button addPropertyButton = dialogView.findViewById(R.id.addPropertyButton);

        addPropertyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String propertyName = propertyNameEditText.getText().toString();
                String propertyAddress = propertyAddressEditText.getText().toString();
                String propertyRent = propertyRentEditText.getText().toString();

                if (!propertyName.isEmpty() && !propertyAddress.isEmpty() && !propertyRent.isEmpty()) {
                    addProperty(propertyName, propertyAddress, propertyRent);
                } else {
                    Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addProperty(String propertyName, String propertyAddress, String propertyRent) {
        Map<String, Object> property = new HashMap<>();
        property.put("name", propertyName);
        property.put("address", propertyAddress);
        property.put("rent", propertyRent);
        property.put("managerEmail", "");

        db.collection("properties").add(property)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Property added", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to add property: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void viewAvailableProperties() {
        db.collection("properties").whereEqualTo("occupied", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                String address = document.getString("address");
                                String type = document.getString("type");
                                double rent = document.getDouble("rent");
                                Log.d("Property", "Address: " + address + ", Type: " + type + ", Rent: " + rent);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to load properties", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showSearchPropertiesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_search_properties, null);
        builder.setView(dialogView);

        EditText typeEditText = dialogView.findViewById(R.id.propertyType);
        EditText minRoomsEditText = dialogView.findViewById(R.id.minRooms);
        EditText minBathroomsEditText = dialogView.findViewById(R.id.minBathrooms);
        EditText minAreaEditText = dialogView.findViewById(R.id.minArea);
        EditText minRentEditText = dialogView.findViewById(R.id.minRent);
        EditText maxRentEditText = dialogView.findViewById(R.id.maxRent);
        Button searchButton = dialogView.findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = typeEditText.getText().toString();
                int minRooms = !minRoomsEditText.getText().toString().isEmpty() ? Integer.parseInt(minRoomsEditText.getText().toString()) : 0;
                int minBathrooms = !minBathroomsEditText.getText().toString().isEmpty() ? Integer.parseInt(minBathroomsEditText.getText().toString()) : 0;
                int minArea = !minAreaEditText.getText().toString().isEmpty() ? Integer.parseInt(minAreaEditText.getText().toString()) : 0;
                double minRent = !minRentEditText.getText().toString().isEmpty() ? Double.parseDouble(minRentEditText.getText().toString()) : 0.0;
                double maxRent = !maxRentEditText.getText().toString().isEmpty() ? Double.parseDouble(maxRentEditText.getText().toString()) : Double.MAX_VALUE;

                searchProperties(type, minRooms, minBathrooms, minArea, minRent, maxRent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void searchProperties(String type, int minRooms, int minBathrooms, int minArea, double minRent, double maxRent) {
        db.collection("properties").whereEqualTo("occupied", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                String propertyType = document.getString("type");
                                int numRooms = document.getLong("numRooms").intValue();
                                int numBathrooms = document.getLong("numBathrooms").intValue();
                                int area = document.getLong("area").intValue();
                                double rent = document.getDouble("rent");

                                if ((type.isEmpty() || propertyType.equalsIgnoreCase(type)) &&
                                        numRooms >= minRooms &&
                                        numBathrooms >= minBathrooms &&
                                        area >= minArea &&
                                        rent >= minRent && rent <= maxRent) {
                                    String address = document.getString("address");
                                    Log.d("Property", "Address: " + address + ", Type: " + propertyType + ", Rent: " + rent);
                                }
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to search properties", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showSubmitRentalRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_submit_rental_request, null);
        builder.setView(dialogView);

        EditText propertyAddressEditText = dialogView.findViewById(R.id.propertyAddress);
        Button submitButton = dialogView.findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String propertyAddress = propertyAddressEditText.getText().toString();
                submitRentalRequest(propertyAddress);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void submitRentalRequest(String propertyAddress) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Map<String, Object> rentalRequest = new HashMap<>();
            rentalRequest.put("clientEmail", user.getEmail());
            rentalRequest.put("propertyAddress", propertyAddress);
            rentalRequest.put("resolved", false);
            rentalRequest.put("rejected", false);

            db.collection("rentalRequests").add(rentalRequest)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Rental request submitted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to submit rental request: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void viewRequests() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("rentalRequests").whereEqualTo("clientEmail", user.getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    String propertyAddress = document.getString("propertyAddress");
                                    boolean resolved = document.getBoolean("resolved");
                                    boolean rejected = document.getBoolean("rejected");

                                    if (!resolved && !rejected) {
                                        Log.d("Request", "Property: " + propertyAddress + " - Status: Active");
                                    } else if (rejected) {
                                        Log.d("Request", "Property: " + propertyAddress + " - Status: Rejected");
                                    }
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void showCreateTicketDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_create_ticket, null);
        builder.setView(dialogView);

        EditText propertyAddressEditText = dialogView.findViewById(R.id.propertyAddress);
        EditText ticketTypeEditText = dialogView.findViewById(R.id.ticketType);
        EditText messageEditText = dialogView.findViewById(R.id.message);
        EditText urgencyEditText = dialogView.findViewById(R.id.urgency);
        Button createTicketButton = dialogView.findViewById(R.id.createTicketButton);

        createTicketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String propertyAddress = propertyAddressEditText.getText().toString();
                String type = ticketTypeEditText.getText().toString();
                String message = messageEditText.getText().toString();
                int urgency = Integer.parseInt(urgencyEditText.getText().toString());

                createTicket(propertyAddress, type, message, urgency);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createTicket(String propertyAddress, String type, String message, int urgency) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Map<String, Object> ticket = new HashMap<>();
            ticket.put("propertyAddress", propertyAddress);
            ticket.put("type", type);
            ticket.put("message", message);
            ticket.put("urgency", urgency);
            ticket.put("clientEmail", user.getEmail());
            ticket.put("resolved", false);

            db.collection("tickets").add(ticket)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Ticket created successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to create ticket: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void viewTickets() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("tickets").whereEqualTo("clientEmail", user.getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    String propertyAddress = document.getString("propertyAddress");
                                    String type = document.getString("type");
                                    boolean resolved = document.getBoolean("resolved");

                                    if (!resolved) {
                                        Log.d("Ticket", "Property: " + propertyAddress + ", Type: " + type + " - Status: Active");
                                    } else {
                                        Log.d("Ticket", "Property: " + propertyAddress + ", Type: " + type + " - Status: Closed");
                                    }
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to load tickets", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
