package edu.gatech.seclass.crypto6300;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, "Login.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table users(id INTEGER primary key autoincrement, username text, password text, firstname text, lastname text, level text, cryptos_won INTEGER, cryptos_lost INTEGER)");
        db.execSQL("create index users_login_index on users (username, password);");

        db.execSQL("create table cryptograms ( id INTEGER primary key autoincrement, name text, solution text, easyAttempts INTEGER, normalAttempts INTEGER, hardAttempts INTEGER );");
        db.execSQL("create index cryptogram_cryptogram_name_index on cryptograms (name);");

        db.execSQL("create table cryptogram_instances (id INTEGER primary key autoincrement, cryptogram_id INTEGER, player_id INTEGER, encrypted_phrase TEXT, attempts_remaining INTEGER, solution_status TEXT);");
        db.execSQL("create index cryptogram_instances_cryptogram_id_index on cryptogram_instances (cryptogram_id);");
        db.execSQL("create index cryptogram_instances_player_id_index on cryptogram_instances (player_id)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    void syncPlayer(Player player){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues=new ContentValues();
        contentValues.put("username",player.username);
        contentValues.put("password",player.password);
        contentValues.put("firstname",player.firstName);
        contentValues.put("lastname",player.lastName);
        contentValues.put("level", player.difficultyCategory.toString());
        contentValues.put("cryptos_won",player.cryptosWon);
        contentValues.put("cryptos_lost",player.cryptosLost);

        if (player.isInDatabase()) {
            db.update("users", contentValues, "id = ?", new String[]{String.valueOf(player.id)});
        }
        else {
            Player existingPlayer = getPlayer(player.username);

            if (existingPlayer == null) {
                player.id = db.insert("users", null, contentValues);
            }
        }
    }

    //check username and password
    public Player usernamepassword(String username, String password){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("Select * from users where username=? and password=?", new String[]{username,password});
        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            return playerFromCursor(cursor);
        }else{
            return null;
        }
    }

    Player getPlayer(String username) {
        SQLiteDatabase db=this.getReadableDatabase();

        Cursor result = db.query( "users", null, "username = ?", new String[]{username}, null, null, null);

        if (result.getCount() == 1) {
            result.moveToFirst();
            return playerFromCursor(result);
        }
        else {
            return null;
        }
    }

    /* gets a player object with the given id or returns null if one does not exist)*/
    Player getPlayer(long id) {
        SQLiteDatabase db=this.getReadableDatabase();

        Cursor result = db.query( "users", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);

        if (result.getCount() == 1) {
            result.moveToFirst();
            return playerFromCursor(result);

        }
        else {
            return null;
        }
    }

    /* Creates a new cryptogram in the database, or updates an existing one. */
    void syncCryptogram(Cryptogram cryptogram) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", cryptogram.name);
        contentValues.put("solution", cryptogram.solution);
        contentValues.put("easyAttempts", cryptogram.easyAttempts);
        contentValues.put("normalAttempts", cryptogram.normalAttempts);
        contentValues.put("hardAttempts", cryptogram.hardAttempts);

        if (cryptogram.isInDatabase()) {
            db.update("cryptograms", contentValues, "id = ?", new String [] {String.valueOf(cryptogram.id)});
        }
        else {
            Cryptogram existing = getCryptogramByName(cryptogram.name);

            if (existing == null) {
                cryptogram.id = db.insert("cryptograms", null, contentValues);
            }
        }
    }

    List<Player> getPlayers() {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.query( "users", null, null, null, null, null, null);

        ArrayList<Player> players = new ArrayList<>();

        while(cursor.moveToNext()) {
            players.add(playerFromCursor(cursor));
        }

        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                return Integer.valueOf((int)o2.cryptosWon).compareTo((int)o1.cryptosWon);
            }
        });

        return players;
    }

    List<Cryptogram> getCryptograms() {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.query( "cryptograms", null, null, null, null, null, null);

        ArrayList<Cryptogram> cryptograms = new ArrayList<>();

        while(cursor.moveToNext()) {
            cryptograms.add(cryptogramFromCursor(cursor));
        }

        return cryptograms;
    }

    Cryptogram getCryptogram(long cryptogram_id) {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.query( "cryptograms", null, "id = ?", new String[]{String.valueOf(cryptogram_id)}, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cryptogramFromCursor(cursor);
        }
        else {
            return null;
        }
    }

    Cryptogram getCryptogramByName(String cryptogram_name) {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.query( "cryptograms", null, "name = ?", new String[]{cryptogram_name}, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cryptogramFromCursor(cursor);
        }
        else {
            return null;
        }
    }

    void syncCryptogramInstance(CryptogramInstance cryptogramInstance) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("cryptogram_id", cryptogramInstance.cryptogramId);
        contentValues.put("player_id", cryptogramInstance.playerId);
        contentValues.put("encrypted_phrase", cryptogramInstance.encryptedPhrase);
        contentValues.put("attempts_remaining", cryptogramInstance.attemptsRemaining);
        contentValues.put("solution_status", cryptogramInstance.solutionStatus.toString());

        if (cryptogramInstance.isInDatabase()) {
            db.update("cryptogram_instances", contentValues, "id = ?", new String [] {String.valueOf(cryptogramInstance.id)});
        }
        else {
            cryptogramInstance.id = db.insert("cryptogram_instances",null,contentValues);
        }
    }

    List<CryptogramInstance> getCryptogramInstances(long player_id) {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.query( "cryptogram_instances", null, "player_id = ?", new String[]{String.valueOf(player_id)}, null, null, null);

        ArrayList<CryptogramInstance> cryptogram_instances = new ArrayList<>();

        while (cursor.moveToNext()) {
            cryptogram_instances.add(cryptogramInstanceFromCursor(cursor));
        }

        return  cryptogram_instances;
    }

    CryptogramInstance getCryptogramInstance(long instance_id) {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.query( "cryptogram_instances", null, "id = ?", new String[]{String.valueOf(instance_id)}, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cryptogramInstanceFromCursor(cursor);
        }
        else {
            return null;
        }
    }

    private Player playerFromCursor(Cursor cursor) {
        return new Player(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                DifficultyCategory.valueOf(cursor.getString(5)),
                cursor.getInt(6),
                cursor.getInt(7));
    }

    private Cryptogram cryptogramFromCursor(Cursor cursor) {
        return new Cryptogram(cursor.getLong(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getInt(4),
                cursor.getInt(5)
                );
    }

    private CryptogramInstance cryptogramInstanceFromCursor(Cursor cursor) {
        return new CryptogramInstance(cursor.getLong(0),
                cursor.getLong(1),
                cursor.getLong(2),
                cursor.getString(3),
                cursor.getInt(4),
                SolutionStatus.valueOf(cursor.getString(5)));
    }
}
