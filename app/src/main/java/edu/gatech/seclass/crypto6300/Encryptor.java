package edu.gatech.seclass.crypto6300;

import java.util.ArrayList;
import java.util.Collections;

/*
Encruption Specification from Assignment 5:

The encrypted phrase for the cryptogram will be generated for each player starting a new cryptogram by:
Replacing each letter with another letter randomly, so that all of any particular letter are replaced with the same other letter, such as all A’s becoming C’s, and every letter is paired with a unique encrypted letter.
Preserving the capitalization in the original phrase.
Preserving any non-alphabetic characters (such as punctuation or white space) unaltered.\
*/

public class Encryptor {
    private static ArrayList<Character> src_characters;

    static {
        src_characters = new ArrayList<>();
        for (char ch = 'a'; ch <= 'z'; ch++) {
            src_characters.add(ch);
        }
    }
    public static String encrypt(String input) {
        ArrayList<Character> encrypted_characters = new ArrayList<>(src_characters);
        Collections.shuffle(encrypted_characters);

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char src_char = input.charAt(i);
            boolean src_is_upper_case = Character.isUpperCase(src_char);

            src_char = Character.toLowerCase(input.charAt(i));

            if (src_char >= 'a' && src_char <= 'z'){
                Character dest_char = encrypted_characters.get(src_char - 'a');
                stringBuilder.append(src_is_upper_case ? Character.toUpperCase(dest_char) : dest_char);
            }
            else {
                stringBuilder.append(src_char);
            }
        }

        return stringBuilder.toString();
    }
}
