package secrets;

/** Exception som kastas när det finns något fel i en
 *  nyckel-värde-fil som används för att importera nycklar till API:er m.m.
 * */
public class SecretsParserException extends Exception {
    public SecretsParserException() {
    }

    public SecretsParserException(String message) {
        super(message);
    }
}
