package Translate;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;

import java.util.ArrayList;
import java.util.List;

public class AwsTranslate {
    

    public static String translate(String textToTranslate , String TargetLanguage){

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(ACCESS_KEY , SECRET_KEY);
        TranslateClient translateClient = TranslateClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(Region.AP_SOUTH_1)
                .build();
        TranslateTextRequest translateTextRequest = TranslateTextRequest.builder()
                .text(textToTranslate)
                .sourceLanguageCode("auto")
                .targetLanguageCode(TargetLanguage)
                .build();
        long starttime = System.currentTimeMillis();
        TranslateTextResponse response = translateClient.translateText(translateTextRequest);
        long endtime = System.currentTimeMillis();
        System.out.println("Length of string:- " + textToTranslate.length() + "and " + "Time taken is:- " + (endtime - starttime));
        translateClient.close();
        return  response.translatedText() ;

    }

    public static void main(String[] args) {
        List<String> translationSentences = TranslationData.getTranslationSentences();
        List<String> awsResults = new ArrayList<>() ;
        for (String fact : translationSentences) {
            awsResults.add(translate(fact , "en"));
        }
        for (String fact : awsResults) {
            System.out.println(fact);
            System.out.println();
        }
    }
}
