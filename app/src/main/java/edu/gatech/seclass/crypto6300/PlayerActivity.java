package edu.gatech.seclass.crypto6300;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        final long player_id = getIntent().getLongExtra("player_id", -1);

        if (player_id != -1) {
            DatabaseHelper db = new DatabaseHelper(this);
            Player player = db.getPlayer(player_id);

            if (player != null) {
                TextView player_fullname_text = findViewById(R.id.player_fullname_text);
                player_fullname_text.setText(String.format("%s %s", player.firstName, player.lastName));
            }

            Button crypto_list_button = findViewById(R.id.cryptolist_player);
            crypto_list_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    Intent intent = new Intent(PlayerActivity.this, PlayerCryptoListActivity.class);
                    intent.putExtra("player_id", player_id);
                    startActivity(intent);
                }
            });

            Button player_list_button = findViewById(R.id.playerlist_player);
            player_list_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    Intent intent = new Intent(PlayerActivity.this, PlayerPlayerList.class);
                    startActivity(intent);
                }
            });
        }
    }
}
