/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.swarm.tokenization.common;

import org.apache.beam.sdk.io.FileIO.ReadableFile;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSourceDoFn
    extends DoFn<KV<String, Iterable<ReadableFile>>, KV<String, ReadableFile>> {
  public static final Logger LOG = LoggerFactory.getLogger(FileSourceDoFn.class);
  private static final String FILE_PATTERN = "([^\\s]+(\\.(?i)(csv))$)";

  @ProcessElement
  public void processElement(ProcessContext c) {
    c.element()
        .getValue()
        .forEach(
            file -> {
              String fileName = file.getMetadata().resourceId().getFilename().toString();
              String bucketName = file.getMetadata().resourceId().getCurrentDirectory().toString();
              String key = String.format("%s%s", bucketName, fileName);
              LOG.info("File Name {}, BucketName {}", fileName, bucketName);
              LOG.info("Key {}", key);
              if (fileName.matches(FILE_PATTERN)) {
                c.output(KV.of(key, file));
              } else {
                LOG.error("Only csv extension is currently supported");
              }
            });
  }
}
