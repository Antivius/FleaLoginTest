package com.example.antti.logintest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;

public class UserRegistration extends AppCompatActivity {

    private EditText fname;
    private EditText lname;
    private EditText email;
    private EditText pwd;
    private EditText pwd_confirm;

    private static final String APP_ID = "C9435C02-8205-26CC-FF63-3067244CAF00";
    private static final String SECRET_KEY="D1A3A4A0-BD38-37CB-FF5E-D582B1E47600";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        String appVersion = "v1";
        com.backendless.Backendless.initApp(this, APP_ID, SECRET_KEY, appVersion);

        fname = (EditText)findViewById(R.id.editText_FName);
        lname= (EditText)findViewById(R.id.editText_Lname);
        email= (EditText)findViewById(R.id.editText_email);
        pwd = (EditText)findViewById(R.id.editText_password);
        pwd_confirm= (EditText)findViewById(R.id.editText_confirm_pwd);

        Button registerButton = (Button) findViewById( R.id.button_register );
        View.OnClickListener registerButtonClickListener = createRegisterButtonClickListener();
        registerButton.setOnClickListener(registerButtonClickListener);
    }

    public View.OnClickListener createRegisterButtonClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                String firstname =  fname.getText().toString();
                String lastname = lname.getText().toString();
                String mail = email.getText().toString();
                String password = pwd.getText().toString();
                String password_conf = pwd_confirm.getText().toString();

                if( is_email_valid(mail)){
                    if(!password.isEmpty() && !password_conf.isEmpty() && is_pwd_valid(password,password_conf)){
                        LoadingCallback<BackendlessUser> registrationCallback = createRegistrationCallback();

                        registrationCallback.showLoading();
                        registerUser(firstname, lastname, mail, password, registrationCallback);

                    }
                }

            }
        };
    }

    private boolean is_pwd_valid(String password, String password_conf) {
        if(password.equals(password_conf)){
            return true;
        }
        else{
            Toast.makeText(UserRegistration.this,"Password does not match",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public LoadingCallback<BackendlessUser> createRegistrationCallback()
    {
        return new LoadingCallback<BackendlessUser>( this, getString( R.string.registration) )
        {
            @Override
            public void handleResponse( BackendlessUser registeredUser )
            {
                super.handleResponse( registeredUser );
                Toast.makeText( UserRegistration.this, String.format( getString( R.string.info_registered ), registeredUser.getObjectId() ), Toast.LENGTH_LONG).show();
            }
        };
    }

    public void registerUser( String fname, String lname, String email, String password,
                               AsyncCallback<BackendlessUser> registrationCallback )
    {
        BackendlessUser user = new BackendlessUser();
        user.setProperty("name",fname);
        user.setProperty("lastname",lname);
        user.setEmail(email);
        user.setPassword(password);


        //Backendless handles password hashing by itself, so we don't need to send hash instead of plain text
        Backendless.UserService.register(user, registrationCallback);
    }

    private boolean is_email_valid(String email) {
        if (email.contains("@")) {
            return true;
        } else {
            Toast.makeText(UserRegistration.this, "Invalid email address", Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
