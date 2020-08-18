package edu.gatech.seclass.crypto6300;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Admin extends AppCompatActivity {
    private Button create_player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        create_player=(Button) findViewById(R.id.create_player);
        create_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent=new Intent(Admin.this, CreatePlayer.class);
                startActivity(intent);
            }
        });

        Button create_crypto = findViewById(R.id.create_crypto);
        create_crypto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Admin.this, CreateCryptoActivity.class);
                startActivity(intent);
            }
        });

        Button playerlist_admin = findViewById(R.id.playerlist_admin);
        playerlist_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Admin.this, AdminPlayerList.class);
                startActivity(intent);
            }
        });

    }
}
