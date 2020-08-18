package edu.gatech.seclass.crypto6300;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class CreatePlayer extends AppCompatActivity {

    DatabaseHelper db;
    private EditText first_name;
    private EditText last_name;
    private EditText username_player;
    private EditText password_player;
    private RadioGroup level;
    private Button save_player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_player);

        db=new DatabaseHelper(this);
        first_name=(EditText)findViewById(R.id.first_name);
        last_name=(EditText)findViewById(R.id.last_name);
        username_player=(EditText)findViewById(R.id.username_player);
        password_player=(EditText)findViewById(R.id.password_player);
        save_player=(Button) findViewById(R.id.save_player);
        level = (RadioGroup) findViewById(R.id.level);

        save_player.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                int selectedId = level.getCheckedRadioButtonId();
                RadioButton radiolevelButton = findViewById(selectedId);

                String first_name_str = first_name.getText().toString();
                String last_name_str = last_name.getText().toString();
                String username_str = username_player.getText().toString();
                String password_str = password_player.getText().toString();
                String difficulty_category_str = radiolevelButton.getText().toString();

                if(username_str.isEmpty() || first_name_str.isEmpty() || last_name_str.isEmpty() || password_str.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Fields are empty", Toast.LENGTH_SHORT).show();
                }else{
                    Player player = new Player(
                            username_str,
                            password_str,
                            first_name_str,
                            last_name_str,
                            DifficultyCategory.valueOf(difficulty_category_str),
                            0, 0);

                    db.syncPlayer(player);
                    if (player.isInDatabase()) {
                        Toast.makeText(getApplicationContext(),"Player Created", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Username Existed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}
