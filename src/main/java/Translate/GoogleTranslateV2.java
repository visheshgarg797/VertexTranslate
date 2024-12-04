package Translate;

import com.google.cloud.translate.*;
import com.google.cloud.translate.Translate.TranslateOption;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GoogleTranslateV2 {

    private static final String API_KEY = "AIzaSyA5AbXnDnwSAmXKwBrn_vA9CPp7l_kTpXs";

    private static Translate createTranslateService(String apiKey) {
        return TranslateOptions.newBuilder().setApiKey(apiKey).build().getService();
    }

    private static Detection translateText(Translate translate, String text) {
        Detection translation = translate.detect(text);
        return translation;
    }

    private static Translation translateText(Translate translate, String text, String targetLanguage) {
        long startTime = System.currentTimeMillis();
        Translation translation = translate.translate(text, TranslateOption.targetLanguage(targetLanguage));
        long endTime = System.currentTimeMillis();
        System.out.println("Length of string is " + text.length() + " and " + "Time taken for translation: " + (endTime - startTime) + " ms");
        return translation;
    }

    private static void translateTextList(Translate translate, List<String> texts, String targetLanguage) {
        long startTime = System.currentTimeMillis() ;
        for(String text : texts ) {
            Translation translationprint =   translateText(translate , text, targetLanguage);
            System.out.println(translationprint.getTranslatedText());
        }
        long endTime = System.currentTimeMillis() ;
        System.out.println("time taken is " + (endTime-startTime));

    }

    public static void main(String[] args) throws IOException {
        Translate translate = createTranslateService(API_KEY);
        String targetLangCode = "en";
        String moduleType = "testModule";
        List <String> results = new ArrayList<>() ;
        List <String> texts = TranslationData.getFootballFacts() ;
        for (String fact : texts) {
            results.add(translateText(translate, fact ,targetLangCode).getTranslatedText());
        }
        for (String fact : results) {
            System.out.println(fact) ;
            System.out.println() ;
        }
    }
}
