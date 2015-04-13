package Bing;

import Bing.Translation.LangCode;
import Bing.Translation.TokenObject;
import Bing.Translation.TokenRequest;
import Bing.Translation.Translate;

import java.util.Objects;
import java.util.Scanner;

/**
 * Created by ngrande on 4/10/15.
 */
public class TestIt {

    public static void main(String[] args) {
        TokenRequest bingTokenRequest = null;
        try {
            bingTokenRequest = new TokenRequest("<Client-ID>", "<Client-Secret>");
            TokenObject tokenObject = bingTokenRequest.requestTokenObject();

            String token = tokenObject.getAccessToken();
            String type = tokenObject.getTokenType();
            String expires_in = tokenObject.getExpiresIn();
            String scope = tokenObject.getScope();

            System.out.println(String.format("access_token: %s\ntoken_type: %s\nexpires_in: %s\nscope: %s",
                    token, type, expires_in, scope));

            Translate translation = new Translate(tokenObject);
            Scanner in = new Scanner(System.in);
            String textToTranslate = "";
            while (!Objects.equals(textToTranslate.toLowerCase(), "exit")) {
                if (!translation.getTokenObject().isExpired()) {
                    System.out.println("Type text to translate:");
                    textToTranslate = in.nextLine();

                    LangCode to = LangCode.KLINGON;

                    String translated = translation.translateText(textToTranslate, LangCode.GERMAN, to);
                    System.out.println(translated);
                    translated = translation.translateText(textToTranslate, to);
                    System.out.println("Detected: " + translated);
                    String[] translatedArray = translation.translateTextArray(new String[]{textToTranslate,
                            "Das ist ein anderer Text", "Das ist ein schwierger Text"}, LangCode.GERMAN, to);

                    for (String result : translatedArray) {
                        System.out.println(result);
                    }

                    translatedArray = translation.translateTextArray(new String[]{textToTranslate, textToTranslate, textToTranslate}, to);

                    for (String result : translatedArray) {
                        System.out.println("Detected: " + result);
                    }
                } else {
                    translation.setTokenObject(bingTokenRequest.requestTokenObject());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
