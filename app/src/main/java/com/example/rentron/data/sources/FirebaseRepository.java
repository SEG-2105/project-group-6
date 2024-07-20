package com.example.rentron.data.sources;

import com.example.rentron.data.sources.actions.AuthActions;
import com.example.rentron.data.sources.actions.InboxActions;
import com.example.rentron.data.sources.actions.PropertyActions;
import com.example.rentron.data.sources.actions.RequestActions;
import com.example.rentron.data.sources.actions.UserActions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseRepository {

    public UserActions USER;
    public AuthActions AUTH;
    public InboxActions INBOX;
    public PropertyActions PROPERTIES;
    public RequestActions REQUESTS;

    public FirebaseRepository(FirebaseAuth mAuth) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.USER = new UserActions(db);
        this.AUTH = new AuthActions(mAuth, db, this);
        this.INBOX = new InboxActions(db, this);
        this.PROPERTIES = new PropertyActions(db);
        this.REQUESTS = new RequestActions(db);
    }

}
