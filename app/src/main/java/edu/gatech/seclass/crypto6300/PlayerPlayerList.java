package edu.gatech.seclass.crypto6300;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class PlayerPlayerList extends AppCompatActivity {
    private List<Player> playerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_player_list);
        setTitle("Player List");
    }

    @Override
    protected void onResume() {
        super.onResume();

        final PlayerPlayerList self = this;

        playerData = getPlayerData();

        ListView player_list = findViewById(R.id.player_player_list);
        player_list.setAdapter(new PlayerListViewAdapter(this, R.layout.player_player_list_item, playerData));
    }

    private List<Player> getPlayerData() {
        DatabaseHelper db = new DatabaseHelper(this);
        List<Player> players = db.getPlayers();
        return players;
    }

    public class PlayerListViewAdapter extends ArrayAdapter<Player> {
        private Context context;
        int resource_id;

        PlayerListViewAdapter(Context context, int resource_id, List<Player> items) {
            super(context, resource_id, items);
            this.context = context;
            this.resource_id = resource_id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(resource_id, null);
            }

            Player player = getItem(position);

            TextView first_name = convertView.findViewById(R.id.playerlist_firstname);
            TextView won_number = convertView.findViewById(R.id.playerlist_won);
            TextView lost_number = convertView.findViewById(R.id.playerlist_lost);

            first_name.setText(player.firstName);
            won_number.setText(String.valueOf(player.cryptosWon));
            lost_number.setText(String.valueOf(player.cryptosLost));

            return convertView;
        }
    }
}
