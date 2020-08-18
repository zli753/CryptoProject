package edu.gatech.seclass.crypto6300;

import java.io.InvalidObjectException;

public class CryptogramInstance {
    /* Createes a new CryptogramInstance that is not yet in the database */
    CryptogramInstance(long cryptogramId, long playerId, String encryptedPhrase, int attemptsRemaining, SolutionStatus solutionStatus) {
        this(-1, cryptogramId, playerId, encryptedPhrase, attemptsRemaining, solutionStatus);
    }

    /* Creates a new CryptogramInstance that is already in the database */
    CryptogramInstance(long id, long cryptogramId, long playerId, String encryptedPhrase, int attemptsRemaining, SolutionStatus solutionStatus) {
        this.id = id;
        this.playerId = playerId;
        this.cryptogramId = cryptogramId;
        this.encryptedPhrase = encryptedPhrase;
        this.attemptsRemaining = attemptsRemaining;
        this.solutionStatus = solutionStatus;
    }

    boolean isInDatabase() {
        return id != -1;
    }

    boolean checkAnswer(String answer_string, DatabaseHelper db) {
        Cryptogram cryptogram = db.getCryptogram(this.cryptogramId);
        Player player = db.getPlayer(this.playerId);

        boolean answer_is_correct = answer_string.equals(cryptogram.solution);

        if (answer_is_correct) {
            player.cryptosWon += 1;
            this.solutionStatus = SolutionStatus.Complete;
        }
        else {
            attemptsRemaining -= 1;

            if (attemptsRemaining == 0) {
                player.cryptosLost += 1;
                this.solutionStatus = SolutionStatus.Complete;
            }
        }

        db.syncCryptogramInstance(this);
        db.syncPlayer(player);
        return answer_is_correct;
    }

    long id;
    long playerId;
    long cryptogramId;
    String encryptedPhrase;
    int attemptsRemaining;
    SolutionStatus solutionStatus;
}
