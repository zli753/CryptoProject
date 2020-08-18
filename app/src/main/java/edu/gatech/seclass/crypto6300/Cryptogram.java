package edu.gatech.seclass.crypto6300;

class Cryptogram {

    /* Createes a new Cryptogram that is not yet in the database */
    Cryptogram(String name, String solution, int easyAttempts, int normalAttempts, int hardAttempts) {
        this(-1, name, solution, easyAttempts, normalAttempts, hardAttempts);
    }

    /* Creates a new Cryptogram that is already in the database */
    Cryptogram(long id, String name, String solution, int easyAttempts, int normalAttempts, int hardAttempts) {
        this.id = id;
        this.name = name;
        this.solution = solution;
        this.easyAttempts = easyAttempts;
        this.normalAttempts = normalAttempts;
        this.hardAttempts = hardAttempts;
    }

    boolean isInDatabase(){
        return id != -1;
    }

    CryptogramInstance createInstance(Player player) {
        int attempts = 0;
        switch (player.difficultyCategory){
            case Easy:
                attempts = easyAttempts;
                break;
            case Normal:
                attempts = normalAttempts;
                break;
            case Difficult:
                attempts = hardAttempts;
                break;
        }

        return new CryptogramInstance(this.id, player.id, Encryptor.encrypt(solution), attempts, SolutionStatus.Unstarted);
    }

    long id;
    String name;
    String solution;
    int easyAttempts;
    int normalAttempts;
    int hardAttempts;
}
