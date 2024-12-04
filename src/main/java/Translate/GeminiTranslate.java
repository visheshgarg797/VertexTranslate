package Translate;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GeminiTranslate {

    private static final int THREAD_POOL_SIZE = 1;

    public static void main(String[] args) throws IOException {
        String projectId = "geminisprinklr";
        String location = "us-central1";
        String modelName = "gemini-1.5-flash-001";
        String targetLanguage = "en"; // Example: Hindi
        List<String> translationSentences = TranslationData.getTranslationSentences();
        long startTime = System.currentTimeMillis();
        List<String> translatedTexts = executeTranslationTasks(projectId, location, modelName, targetLanguage, translationSentences);
        long endTime = System.currentTimeMillis();
        translatedTexts.forEach(System.out::println);
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
    }

    public static List<String> executeTranslationTasks(String projectId, String location, String modelName, String targetLanguage, List<String> sentences) {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<Callable<String>> tasks = createTranslationTasks(projectId, location, modelName, targetLanguage, sentences);
        List<String> translatedTexts = new ArrayList<>();

        try {
            List<Future<String>> results = executorService.invokeAll(tasks);
            for (Future<String> result : results) {
                try {
                    translatedTexts.add(result.get());
                } catch (Exception e) {
                    translatedTexts.add("Error during translation.");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  // Restore interrupt status
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

        return translatedTexts;
    }

    public static List<Callable<String>> createTranslationTasks(String projectId, String location, String modelName, String targetLanguage, List<String> sentences) {
        List<Callable<String>> tasks = new ArrayList<>();
        for (String sentence : sentences) {
            tasks.add(() -> translateText(projectId, location, modelName, sentence, targetLanguage));
        }
        return tasks;
    }

    public static String translateText(String projectId, String location, String modelName, String textToTranslate, String targetLanguage) throws IOException {
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            GenerativeModel model = new GenerativeModel(modelName, vertexAI);
            String prompt = "Just to the following and nothing else :- Translate the following text into " + targetLanguage + ": " + textToTranslate;
            long startTimeApi = System.currentTimeMillis();
            GenerateContentResponse response = model.generateContent(ContentMaker.fromString(prompt));
            long endTimeApi = System.currentTimeMillis();
            System.out.println( "Length of string is " + textToTranslate.length() + " and " + "Time taken is: " + (endTimeApi - startTimeApi) + "ms");
            return response.getCandidates(0).getContent().getParts(0).getText();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error during translation for text: " + textToTranslate, e);
        }
    }

}
