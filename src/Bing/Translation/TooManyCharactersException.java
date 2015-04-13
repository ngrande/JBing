package Bing.Translation;

/**
 * Created by ngrande on 4/11/15.
 */
public class TooManyCharactersException extends Exception {
    public TooManyCharactersException(String text) {
        super(text);
    }
}
