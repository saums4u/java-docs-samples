/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.datalabeling;

// [START datalabeling_label_video_beta]
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.datalabeling.v1beta1.AnnotatedDataset;
import com.google.cloud.datalabeling.v1beta1.DataLabelingServiceClient;
import com.google.cloud.datalabeling.v1beta1.DataLabelingServiceSettings;
import com.google.cloud.datalabeling.v1beta1.HumanAnnotationConfig;
import com.google.cloud.datalabeling.v1beta1.LabelOperationMetadata;
import com.google.cloud.datalabeling.v1beta1.LabelVideoRequest;
import com.google.cloud.datalabeling.v1beta1.LabelVideoRequest.Feature;
import com.google.cloud.datalabeling.v1beta1.VideoClassificationConfig;
import com.google.cloud.datalabeling.v1beta1.VideoClassificationConfig.AnnotationSpecSetConfig;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

class LabelVideo {

  // Start a Video Labeling Task
  static void labelVideo(String formattedInstructionName, String formattedAnnotationSpecSetName,
      String formattedDatasetName) throws IOException {
    // String formattedInstructionName = DataLabelingServiceClient.formatInstructionName(
    //      "YOUR_PROJECT_ID", "YOUR_INSTRUCTION_UUID");
    // String formattedAnnotationSpecSetName =
    //     DataLabelingServiceClient.formatAnnotationSpecSetName(
    //         "YOUR_PROJECT_ID", "YOUR_ANNOTATION_SPEC_SET_UUID");
    // String formattedDatasetName = DataLabelingServiceClient.formatDatasetName(
    //      "YOUR_PROJECT_ID", "YOUR_DATASET_UUID");

    // [END datalabeling_label_video_beta]
    String endpoint = System.getenv("DATALABELING_ENDPOINT");
    if (endpoint == null) {
      endpoint = DataLabelingServiceSettings.getDefaultEndpoint();
    }
    // [START datalabeling_label_video_beta]

    DataLabelingServiceSettings settings = DataLabelingServiceSettings
        .newBuilder()
        // [END datalabeling_label_video_beta]
        .setEndpoint(endpoint)
        // [START datalabeling_label_video_beta]
        .build();
    try (DataLabelingServiceClient dataLabelingServiceClient =
             DataLabelingServiceClient.create(settings)) {
      HumanAnnotationConfig humanAnnotationConfig = HumanAnnotationConfig.newBuilder()
          .setAnnotatedDatasetDisplayName("annotated_displayname")
          .setAnnotatedDatasetDescription("annotated_description")
          .setInstruction(formattedInstructionName)
          .build();

      AnnotationSpecSetConfig annotationSpecSetConfig = AnnotationSpecSetConfig.newBuilder()
          .setAnnotationSpecSet(formattedAnnotationSpecSetName)
          .setAllowMultiLabel(true)
          .build();

      VideoClassificationConfig videoClassificationConfig = VideoClassificationConfig.newBuilder()
          .setApplyShotDetection(true)
          .addAnnotationSpecSetConfigs(annotationSpecSetConfig)
          .build();

      LabelVideoRequest labelVideoRequest = LabelVideoRequest.newBuilder()
          .setParent(formattedDatasetName)
          .setBasicConfig(humanAnnotationConfig)
          .setVideoClassificationConfig(videoClassificationConfig)
          .setFeature(Feature.CLASSIFICATION)
          .build();

      OperationFuture<AnnotatedDataset, LabelOperationMetadata> operation =
          dataLabelingServiceClient.labelVideoAsync(labelVideoRequest);

      // You'll want to save this for later to retrieve your completed operation.
      System.out.format("Operation Name: %s\n", operation.getName());

      // Cancel the operation to avoid charges when testing.
      dataLabelingServiceClient.getOperationsClient().cancelOperation(operation.getName());
    } catch (IOException | InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
  }
}
// [END datalabeling_label_video_beta]
