package com.example.antti.logintest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.UserService;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class UserActivity extends AppCompatActivity {

    private static final String APP_ID = "C9435C02-8205-26CC-FF63-3067244CAF00";
    private static final String SECRET_KEY="D1A3A4A0-BD38-37CB-FF5E-D582B1E47600";
    public TextView userText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        String appVersion = "v1";
        com.backendless.Backendless.initApp(this, APP_ID, SECRET_KEY, appVersion);

        userText = (TextView)findViewById(R.id.currentUser);
        if (userText != null) {
            userText.setText("Hello "+getCurrent().getProperty("name").toString());
        }

        Button loginButton = (Button) findViewById( R.id.logout_button );
        loginButton.setOnClickListener(createLogoutButtonListener());
    }

    public BackendlessUser getCurrent(){
        BackendlessUser user;
        user=Backendless.UserService.CurrentUser();
        return user;
    }
    public View.OnClickListener createLogoutButtonListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick( View v ) {
                Backendless.UserService.logout(new AsyncCallback<Void>() {
                    @Override
                    public void handleResponse(Void aVoid) {

                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {

                    }
                });
            }
        };
    }

}
