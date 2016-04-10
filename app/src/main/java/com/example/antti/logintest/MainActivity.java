package com.example.antti.logintest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static final String APP_ID = "C9435C02-8205-26CC-FF63-3067244CAF00";
    private static final String SECRET_KEY="D1A3A4A0-BD38-37CB-FF5E-D582B1E47600";

    private EditText mPasswordView;
    private EditText mEmailView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String appVersion = "v1";
        Backendless.initApp(this, APP_ID, SECRET_KEY, appVersion);

        mEmailView = (EditText)findViewById(R.id.email_login);
        mPasswordView = (EditText)findViewById(R.id.password);

        Button registerButton = (Button) findViewById( R.id.registerButton );

        View.OnClickListener registerButtonClickListener = createRegisterButtonClickListener();

        registerButton.setOnClickListener( registerButtonClickListener );

        Button loginButton = (Button) findViewById( R.id.loginButton );
        loginButton.setOnClickListener(createLoginButtonListener());

        Button fbLoginButton = (Button) findViewById(R.id.fbLoginButton);
        fbLoginButton.setOnClickListener(createFBLoginButtonListner());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
    public void register_user(View view){
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if(is_email_valid(email)){
            BackendlessUser user = new BackendlessUser();
            user.setEmail(email);
            user.setPassword(password);

            Backendless.UserService.register(user, new BackendlessCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser backendlessUser) {
                    Log.i("Registration", backendlessUser.getEmail() + " successfully registered");
                }
            });
        }else if(!is_email_valid(email)){
            mEmailView.setError("Invalid email");
        }
    }
    */

    public void registerUser(  String email, String password,
                              AsyncCallback<BackendlessUser> registrationCallback )
    {
        BackendlessUser user = new BackendlessUser();
        user.setEmail(email);
        user.setPassword(password);


        //Backendless handles password hashing by itself, so we don't need to send hash instead of plain text
        Backendless.UserService.register(user, registrationCallback);
    }

    public void loginUser( String email, String password, AsyncCallback<BackendlessUser> loginCallback )
    {
        Backendless.UserService.login(email, password, loginCallback);
    }

    public void sign_in(View view){
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        BackendlessUser user = new BackendlessUser();

        user = Backendless.UserService.login( email, password );
        Log.i("Sign in", email);


    }

    public LoadingCallback<BackendlessUser> createRegistrationCallback()
    {
        return new LoadingCallback<BackendlessUser>( this, getString( R.string.registration) )
        {
            @Override
            public void handleResponse( BackendlessUser registeredUser )
            {
                super.handleResponse( registeredUser );
                Toast.makeText( MainActivity.this, String.format( getString( R.string.info_registered ), registeredUser.getObjectId() ), Toast.LENGTH_LONG).show();
            }
        };
    }

    private boolean is_email_valid(String email){
        return email.contains("@");
    }

    public View.OnClickListener createRegisterButtonClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                String email = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();

                if( is_email_valid(email) )
                {
                    LoadingCallback<BackendlessUser> registrationCallback = createRegistrationCallback();

                    registrationCallback.showLoading();
                    registerUser(email, password, registrationCallback);
                }
            }
        };
    }

    public View.OnClickListener createLoginButtonListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick( View v ) {
                String email = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();


                if( isLoginValid(email, password) ) {
                    LoadingCallback<BackendlessUser> loginCallback = createLoginCallback();

                    loginCallback.showLoading();
                    loginUser(email, password, loginCallback);
                }
            }
        };
    }

    /**
     * Creates a listener, which proceeds with login with Facebook on button click.
     *
     * @return a listener, handling login with Facebook button click
     */
    public View.OnClickListener createFBLoginButtonListner()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                LoadingCallback<BackendlessUser> loginCallback = createLoginCallback();

                loginCallback.showLoading();
                loginFacebookUser( loginCallback );
            }
        };
    }

    /**
     * Sends a request to Backendless to log in user with Facebook account.
     * Fetches Facebook user's name and saves it on Backendless.
     *
     * @param loginCallback a callback, containing actions to be executed on request result
     */
    public void loginFacebookUser( AsyncCallback<BackendlessUser> loginCallback )
    {
        Map<String, String> fieldsMappings = new HashMap<>();
        fieldsMappings.put( "name", "name" );
        Backendless.UserService.loginWithFacebook( this, null, fieldsMappings, Collections.<String>emptyList(), loginCallback );
    }

    /**
     * Creates a callback, containing actions to be executed on login request result.
     *
     * @return a callback, containing actions to be executed on login request result
     */

    public LoadingCallback<BackendlessUser> createLoginCallback()
    {
        return new LoadingCallback<BackendlessUser>( this, getString( R.string.loading_login ) )
        {
            @Override
            public void handleResponse( BackendlessUser loggedInUser )
            {
                super.handleResponse(loggedInUser);
                Toast.makeText( MainActivity.this, String.format( getString( R.string.info_logged_in ), loggedInUser.getObjectId() ), Toast.LENGTH_LONG ).show();

                Intent userActionIntent = new Intent( MainActivity.this, UserActivity.class );
                startActivity( userActionIntent );
                finish();
            }
        };
    }




    public boolean isLoginValid(String email,String password){
        return Validator.isLoginValid();
    }
}
