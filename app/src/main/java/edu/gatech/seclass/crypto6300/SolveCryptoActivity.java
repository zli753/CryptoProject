package edu.gatech.seclass.crypto6300;

import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class SolveCryptoActivity extends AppCompatActivity {

    HashMap<Character, Character> text_replacements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_solve_crypto);
        setTitle("Solve Cryptogram");
    }

    @Override
    protected void onStart() {
        super.onStart();

        long instance_id = getIntent().getLongExtra("instance_id", -1);
        if (instance_id != -1) {
            text_replacements = new HashMap<>();
            DatabaseHelper db = new DatabaseHelper(this);
            CryptogramInstance instance = db.getCryptogramInstance(instance_id);

            final SolveCryptoActivity self = this;
            TextView encrypted_string = findViewById(R.id.encrypted_string);
            TextView attempts_remining = findViewById(R.id.attempts_remaining_text);

            attempts_remining.setText(String.format("Attempts Remaining: %d", instance.attemptsRemaining));
            encrypted_string.setText(instance.encryptedPhrase);
            applyReplacementCharacters();

            Button substitution_popup_button = findViewById(R.id.substitution_popup_button);

            substitution_popup_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    self.showPopup();
                }
            });

            Button submit_answer_button = findViewById(R.id.submit_answer_button);
            submit_answer_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    self.onSubmitAnswer();
                }
            });
        }
    }

    private void onSubmitAnswer() {
        DatabaseHelper db = new DatabaseHelper(this);
        long instance_id = getIntent().getLongExtra("instance_id", -1);
        CryptogramInstance cryptogram_instance = db.getCryptogramInstance(instance_id);

        if (cryptogram_instance != null) {
            TextView answer_guess_string = findViewById(R.id.answer_guess_string);
            boolean result = cryptogram_instance.checkAnswer(answer_guess_string.getText().toString(), db);

            if (result){
                Cryptogram cryptogram = db.getCryptogram(cryptogram_instance.cryptogramId);

                Toast.makeText(this, String.format("Congratulations, you solved %s", cryptogram.name), Toast.LENGTH_SHORT).show();
                finish();
            }
            else if (cryptogram_instance.attemptsRemaining > 0){
                Toast.makeText(this,"Unfortunately Your answer is incorrect. Please try again.", Toast.LENGTH_SHORT).show();
                TextView attempts_remaining = findViewById(R.id.attempts_remaining_text);
                attempts_remaining.setText(String.format("Attempts Remaining: %d", cryptogram_instance.attemptsRemaining));
            }
            else {
                Toast.makeText(this,"Unfortunately you have failed to solve this cryptogram.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void showPopup() {
        final SolveCryptoActivity self = this;

        View popup_view = LayoutInflater.from(this).inflate(R.layout.cypto_subsitution_popup, null);
        final PopupWindow popup_window = new PopupWindow(popup_view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popup_window.setElevation(20);

        Button apply_button = popup_view.findViewById(R.id.sub_char_apply_button);
        Button cancel_button = popup_view.findViewById(R.id.sub_char_cancel_button);

        final EditText src_char_text = popup_view.findViewById(R.id.src_char_text);
        final EditText sub_char_text = popup_view.findViewById(R.id.sub_char_text);

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.length() == 0) {
                    return source;
                }

                Character new_char = source.charAt(0);

                return Character.isLetter(new_char) ? new_char.toString() : "";
            }
        };

        InputFilter[] filterArray = new InputFilter[]{filter};
        src_char_text.setFilters(filterArray);
        sub_char_text.setFilters(filterArray);

        apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                popup_window.dismiss();
                self.addReplacementCharacter(src_char_text.getText().charAt(0), sub_char_text.getText().charAt(0));
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                popup_window.dismiss();
            }
        });

        popup_window.showAtLocation(popup_view, Gravity.TOP | Gravity.CENTER, 0, 0);
    }

    public void addReplacementCharacter(char encrypted_char, char sub_char){
        text_replacements.put(Character.toLowerCase(encrypted_char), Character.toLowerCase(sub_char));
        applyReplacementCharacters();
    }

    public void applyReplacementCharacters()
    {
        TextView encrypted_string = findViewById(R.id.encrypted_string);
        TextView guess_string = findViewById(R.id.answer_guess_string);

        String src_string = encrypted_string.getText().toString();

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < src_string.length(); i++) {
            Character src_char = src_string.charAt(i);
            if (Character.isLetter(src_char)) {
                Character dst_char = Character.toLowerCase(src_char);

                if (text_replacements.containsKey(dst_char)) {
                    dst_char = text_replacements.get(dst_char);

                    if (Character.isUpperCase(src_char))
                        dst_char = Character.toUpperCase(dst_char);
                } else {
                    dst_char = '?';
                }

                stringBuilder.append(dst_char);
            }
            else {
                stringBuilder.append(src_char);
            }
        }

        guess_string.setText(stringBuilder.toString());
    }
}
