package com.example.rentron;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserService {
    private DatabaseReference database;
    private FirebaseAuth auth;

    public UserService() {
        this.database = FirebaseDatabase.getInstance().getReference("Users");
        this.auth = FirebaseAuth.getInstance();
    }

    public void registerUser(User user, AuthCallback callback) {
        if (!ValidationUtils.isValidEmail(user.getEmail()) || !ValidationUtils.isValidPassword(user.getPassword())) {
            callback.onFailure("Invalid email or password.");
            return;
        }

        auth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userId = auth.getCurrentUser().getUid();
                user.setId(userId);
                database.child(userId).setValue(user).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        callback.onSuccess(auth.getCurrentUser(), user.getRole());
                    } else {
                        callback.onFailure(task1.getException().getMessage());
                    }
                });
            } else {
                callback.onFailure(task.getException().getMessage());
            }
        });
    }

    public void loginUser(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser currentUser = auth.getCurrentUser();
                if (currentUser != null) {
                    database.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            callback.onSuccess(currentUser, user.getRole());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            callback.onFailure(databaseError.getMessage());
                        }
                    });
                } else {
                    callback.onFailure("User not found.");
                }
            } else {
                callback.onFailure(task.getException().getMessage());
            }
        });
    }

    public void logoutUser() {
        auth.signOut();
    }

    public interface AuthCallback {
        void onSuccess(FirebaseUser user, String role);
        void onFailure(String message);
    }
}
