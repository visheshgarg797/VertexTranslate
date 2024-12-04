package org.example;

import com.google.cloud.storage.*;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.PartMaker;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import io.grpc.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AudioInputSummarization {

    public static void main(String[] args) throws IOException {
        String projectId = "geminisprinklr";
        String location = "us-central1";
        String modelName = "gemini-1.5-pro-001";
        String localFilePath = "/Users/vishesh.garg/IdeaProjects/VertexAiSprinklr/src/main/java/org/example/audio2/customer_voice_drops943141721311494- DIAced7955add5e85bfad307350377b21ff.mp3";
        String objectName = "audio2/sample.mp3";
        String bucketName = "bucket_vertexapi";
        AudioInputSummarization FileUploader;
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
                            "You are a voice quality analyst tasked with reviewing the provided VOIP call audio. " +
                                    "Your goal is to identify, categorize, and summarize any voice quality issues " +
                                    "encountered during the call. Follow the detailed steps below:\n" +
                                    "Instructions\n" +        "Metadata Extraction:\n" +
                                    "Identify and document basic details of the call (e.g., duration, participants, context).\n"
                                    +        "Issue Detection and Categorization:Listen to the call and identify any instances of the following voice quality issues:\n"
                                    +        "Echo: Repeated sounds from one or both participants.\n"
                                    +        "Jitter: Choppy or scrambled audio.\n"
                                    +        "Latency: Delays between speaking and response.\n"
                                    +        "Packet Loss: Missing words, syllables, or silent gaps.\n"
                                    +        "Background Noise: Environmental noise masking the conversation.\n"
                                    +        "Distortion: Warping or robotic-sounding voices.\n"
                                    +        "Volume Issues: Uneven loudness or excessively high/low audio levels.\n"
                                    +        "Missing Audio: check if both agent and caller are audible in the call\n"
                                    +        "Timestamped Examples:\n"
                                    +        "For each identified issue, provide specific time markers where it occurs (e.g., 01:23-01:45).\n"
                                    +        "Severity Assessment:\n"
                                    +        "Rate each issue as minor, moderate, or critical, based on its impact on the call's intelligibility.\n"
                                    +        "Root Cause Hypotheses:\n"
                                    +        "Suggest potential causes for the identified issues based on the audio context (e.g., network interruptions, device malfunctions).\n"
                                    +        "Actionable Recommendations:\n"
                                    +        "Propose specific troubleshooting steps to address the issues, such as optimizing bandwidth or upgrading hardware.\n"
                                    +        "Summary Structure:\n" +        "Call Metadata: Include call ID, duration, date/time, and context.\n"
                                    +        "Issues and Categories: List each identified issue with a brief description.\n"
                                    +        "Examples with Timestamps: Provide clear examples of when each issue occurs.\n"
                                    +        "Severity and Causes: Assess the impact and hypothesize root causes.\n"
                                    +        "Recommendations: Offer steps for resolution or further escalation.\n"
                                    +        "Output Requirements\n"
                                    +        "Format the output in structured sections with headings (e.g., Metadata, Issues, Timestamps, Recommendations).\n"
                                    +        "Use concise and professional language.\n"
                                    +        "Ensure accuracy in timestamping and issue categorization.",

                            PartMaker.fromMimeTypeAndData("audio/mp3", audioUri)
                    ));
            String output = ResponseHandler.getText(response);
            long endtime = System.currentTimeMillis() ;
            System.out.println("time" + (endtime-starttime));
            int totalTokenCount = response.getUsageMetadata().getTotalTokenCount();
            System.out.println(output);
            System.out.println("tokenused" + totalTokenCount);

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