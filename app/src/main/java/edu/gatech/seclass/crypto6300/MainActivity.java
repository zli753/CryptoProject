package edu.gatech.seclass.crypto6300;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login =(Button) findViewById(R.id.login);

        final MainActivity self = this;

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                self.onLoginButtonClicked();
            }
        });

    }

    private void onLoginButtonClicked() {
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        RadioGroup role = findViewById(R.id.role);
        TextView login_info =(TextView) findViewById(R.id.login_info);

        login_info.setText("");

        int login_type = role.getCheckedRadioButtonId();

        if (login_type == R.id.Admin_Button){
            validate(username.getText().toString(), password.getText().toString());
        }
        else {
            validatePlayer(username.getText().toString(), password.getText().toString());
        }

    }

    private void validate(String username, String password){
        TextView login_info = findViewById(R.id.login_info);

        if (username.equals("")||password.equals("")){
             login_info.setText("empty info");
        } else if((username.equals("Admin"))&&(password.equals("1234"))){
             Intent intent=new Intent(MainActivity.this, Admin.class);
             startActivity(intent);
        }else{
              login_info.setText(R.string.incorrect_login_message);
        }
    }

    private void validatePlayer(String username, String password) {
        DatabaseHelper db = new DatabaseHelper(this);
        TextView login_info = findViewById(R.id.login_info);

        if (username.equals("")||password.equals("")){
            login_info.setText("empty info");
        } else {
            Player player = db.usernamepassword(username,password);
            if (player != null) {
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                intent.putExtra("player_id", player.id);
                startActivity(intent);
            }
            else {
                login_info.setText(R.string.incorrect_login_message);
            }
        }
    }
}
