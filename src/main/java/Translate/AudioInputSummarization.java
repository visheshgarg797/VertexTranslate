package Translate;

import com.google.cloud.storage.*;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.PartMaker;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AudioInputSummarization {

    public static void main(String[] args) throws IOException {
        String projectId = "geminisprinklr";
        String location = "us-central1";
        String modelName = "gemini-1.5-flash-001";
        String localFilePath = "/Users/vishesh.garg/IdeaProjects/VertexAiSprinklr/src/main/java/org/example/audio2/echo_due_to_dual_stream_CONV_RECORD_1688992861722_12865115964717354707.mp3";
        String objectName = "audio2/echo_due_to_dual_stream_CONV_RECORD_1688992861722_12865115964717354707.mp3";
        String bucketName = "bucket_vertexapi";
        uploadFileToGCS(localFilePath, bucketName, objectName);
        summarizeAudio(projectId, location, modelName ,bucketName, objectName );
    }

    public static String summarizeAudio(String projectId, String location, String modelName , String bucketName, String objectName)
            throws IOException {
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            String audioUri = "gs://" + bucketName + "/" + objectName;
            GenerativeModel model = new GenerativeModel(modelName, vertexAI);
            long starttime = System.currentTimeMillis() ;
            GenerateContentResponse response = model.generateContent(
                    ContentMaker.fromMultiModalData(
                            "You are an audio quality analyst tasked with reviewing the provided Gemini VOIP call recordings. " +
                                    "Your objective is to identify voice quality issues encountered during the call. " +
                                    "Focus only specifically on the following four factors: overlap between agent and customer, single-party recording (just identification of following 4 issues) , " +
                                    "voicebot cracking recordings, and same-participant dual-stream issues. Follow the detailed steps below:\n\n" +
                                    "### Instructions\n" +
                                    "1. **Issue Detection and Categorization**:\n" +
                                    "   - **Overlap Between Agent and Customer**:\n" +
                                    "     - Identify sections where both agent and customer speak simultaneously due to timestamp mismatches.\n" +
                                    "   - **Single-Party Recording**:\n" +
                                    "     - Check if the audio contains only one participant (agent or customer) when both should be present or there is missing audio of one participant.\n" +
                                    "   - **Cracking Recordings**:\n" +
                                    "     - Detect any instances of audio breaking or becoming unintelligible.\n" +
                                    "   - **Echo**:\n" +
                                    "     - Identify if echo is present anywhere in the whole recording or " +
                                    "     - Identify if the same participant’s audio stream is duplicated with a delay, causing an echo effect.\n" +
                                    "Output Requirements\n"
                                    +        "Use concise and professional language.\n"
                                    +        "Ensure accuracy in issue categorization."
                                  , PartMaker.fromMimeTypeAndData("audio/mp3", audioUri)
                    ));
            String output = ResponseHandler.getText(response);
            long endtime = System.currentTimeMillis() ;
            System.out.println("time " + (endtime-starttime));
            int totalTokenCount = response.getUsageMetadata().getTotalTokenCount();
            System.out.println(output);
            System.out.println("tokenused " + totalTokenCount);
            return output;
        }
    }

    public static void uploadFileToGCS(String localFilePath, String bucketName, String objectName) throws IOException {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Path path = Paths.get(localFilePath);
        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        Blob blob = storage.create(blobInfo, Files.readAllBytes(path));
        System.out.println("File uploaded to: gs://" + bucketName + "/" + objectName);
    }
}