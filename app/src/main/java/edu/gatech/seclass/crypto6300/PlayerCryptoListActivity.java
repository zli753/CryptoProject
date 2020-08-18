package edu.gatech.seclass.crypto6300;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerCryptoListActivity extends AppCompatActivity {
    // Holds player specific cryptogram data that is used to drive the List View.
    private List<CryptogramData> playerCryptogramData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_crypto_list);
        setTitle("Available Cryptograms");
    }

    @Override
    protected void onResume(){
        super.onResume();

        final PlayerCryptoListActivity self = this;
        final long player_id = getIntent().getLongExtra("player_id", -1);

        if (player_id != -1) {
            playerCryptogramData = getCryptogramData(player_id);

            ListView cryptogram_list = findViewById(R.id.player_crypto_list);
            cryptogram_list.setAdapter(new CryptogramListViewAdapter(this, R.layout.crypto_list_item, playerCryptogramData));

            cryptogram_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    self.onListItemClick(player_id, position);
                }
            });
        }
    }

    /*
    When the user selects an item from the cryptogram list, we must first check to see if there is already an associated cryptogram instance.
    If the user has not attempted to start the selected crytpogram, then we need to create a new instance of it before moving to the solve cryptogram activity.
     */
    private void onListItemClick(long player_id, int position) {
        CryptogramData status = playerCryptogramData.get(position);

        if (status.cryptogram_instance == null) {
            DatabaseHelper db = new DatabaseHelper(this);
            Player player = db.getPlayer(player_id);

            status.cryptogram_instance = status.cryptogram.createInstance(player);
            status.cryptogram_instance.solutionStatus = SolutionStatus.InProgress;
            db.syncCryptogramInstance(status.cryptogram_instance);
        }

        Intent intent = new Intent(PlayerCryptoListActivity.this, SolveCryptoActivity.class);
        intent.putExtra("instance_id", status.cryptogram_instance.id);
        startActivity(intent);
    }

    /*
    This method pulls the required data from the database and builds the players cryptogram list view.
    For Every cryptogram in the system, we check to see if there is an associated cryptogram instance for that user.
    If there is no instance, then the status of that particular cryptogram is unstarted.
    If there is a cryptogram instance that has not been completed, then it is in progress
    If there is a completed Cryptogram instance, then the Cryptogram is omitted from the list.
    The resulting list created by this method is used to drive click events for the Cryptogram List view.
     */
    private List<CryptogramData> getCryptogramData(long player_id) {
        DatabaseHelper db = new DatabaseHelper(this);

        List<Cryptogram> cryptograms = db.getCryptograms();

        List<CryptogramInstance>  cryptogram_instances = db.getCryptogramInstances(player_id);
        HashMap<Long, CryptogramInstance> cryptogram_instance_dict = new HashMap<>();

        for (CryptogramInstance cryptogram_instance : cryptogram_instances) {
            cryptogram_instance_dict.put(cryptogram_instance.cryptogramId, cryptogram_instance);
        }

        List<CryptogramData> cryptogramDataItems = new ArrayList<>();

        for(Cryptogram cryptogram : cryptograms) {
            CryptogramInstance cryptogram_instance = null;
            if (cryptogram_instance_dict.containsKey(cryptogram.id)) {
                cryptogram_instance = cryptogram_instance_dict.get(cryptogram.id);

                if (cryptogram_instance.solutionStatus == SolutionStatus.Complete) {
                    continue;
                }
            }

            cryptogramDataItems.add(new CryptogramData(cryptogram, cryptogram_instance));
        }

        return cryptogramDataItems;
    }

    /*
    This is a utility class used to hold a cryptogram and its associated instance for a Player.
    The custom list view adapter uses this class to display Cryptograms available to the user.
     */
    private class CryptogramData {
        CryptogramData(Cryptogram cryptogram, CryptogramInstance cryptogram_instance) {
            this.cryptogram = cryptogram;
            this.cryptogram_instance = cryptogram_instance;
        }

        Cryptogram cryptogram;
        CryptogramInstance cryptogram_instance;
    }

    // Custom list view adapter that renders cells based on the CryptogramData class.
    public class CryptogramListViewAdapter extends ArrayAdapter<CryptogramData> {
        private Context context;
        int resource_id;

        CryptogramListViewAdapter(Context context, int resource_id, List<CryptogramData> items) {
            super(context, resource_id, items);
            this.context = context;
            this.resource_id = resource_id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(resource_id, null);
            }

            CryptogramData cryptogram = getItem(position);

            TextView crypto_name = convertView.findViewById(R.id.crypto_item_name);
            TextView crypto_status = convertView.findViewById(R.id.crypto_item_status);

            crypto_name.setText(cryptogram.cryptogram.name);

            if (cryptogram.cryptogram_instance != null) {
                crypto_status.setText("in progress");
            }
            else {
                crypto_status.setText("unstarted");
            }

            return convertView;
        }
    }
}
