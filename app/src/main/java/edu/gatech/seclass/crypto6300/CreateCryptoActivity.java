package edu.gatech.seclass.crypto6300;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class CreateCryptoActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_crypto);

        final CreateCryptoActivity self = this;

        Button save_button = findViewById(R.id.save_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                self.onSaveCrypto();
            }
        });
    }

    private void onSaveCrypto() {
        EditText name_text = findViewById(R.id.name_text);
        EditText solution_text = findViewById(R.id.solution_text);
        EditText easy_int = findViewById(R.id.easy_int);
        EditText normal_int = findViewById(R.id.normal_int);
        EditText hard_int = findViewById(R.id.hard_int);

        Cryptogram newCryptogram = new Cryptogram(
                name_text.getText().toString(),
                solution_text.getText().toString(),
                Integer.valueOf(easy_int.getText().toString()),
                Integer.valueOf(normal_int.getText().toString()),
                Integer.valueOf(hard_int.getText().toString())
        );

        DatabaseHelper db = new DatabaseHelper(this);
        db.syncCryptogram(newCryptogram);

        if (!newCryptogram.isInDatabase()) {
            Toast.makeText(this, String.format("There is already an existing cryptogram named: %s", newCryptogram.name), Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, String.format("Created New Cryptogram: %s", newCryptogram.name), Toast.LENGTH_SHORT).show();

            finish();
        }
    }

}
