package edu.gatech.seclass.crypto6300;

import android.database.Cursor;



public class Player{
    Player(String username, String password, String firstName, String lastName, DifficultyCategory difficultyCategory, int cryptosWon, int cryptosLost) {
        this(-1, username, password, firstName, lastName, difficultyCategory, cryptosWon, cryptosLost);
    }

    Player(long id, String username, String password, String firstName, String lastName, DifficultyCategory difficultyCategory, int cryptosWon, int cryptosLost) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.difficultyCategory = difficultyCategory;
        this.cryptosWon = cryptosWon;
        this.cryptosLost = cryptosLost;
    }

    boolean isInDatabase() {
        return id != -1;
    }

    long id;
    String firstName;
    String lastName;
    String username;
    String password;
    DifficultyCategory difficultyCategory;
    long cryptosWon;
    long cryptosLost;
}
