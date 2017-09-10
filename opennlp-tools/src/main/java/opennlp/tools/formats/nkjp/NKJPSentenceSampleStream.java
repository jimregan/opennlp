/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opennlp.tools.formats.nkjp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import opennlp.tools.sentdetect.SentenceSample;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.Span;

public class NKJPSentenceSampleStream implements ObjectStream<SentenceSample> {
  Map<String, String> paragraphs;

  private final NKJPSegmentationDocument segments;

  private final NKJPTextDocument text;

  private Iterator<Map.Entry<String, Map<String, NKJPSegmentationDocument.Pointer>>> segmentIt;

  NKJPSentenceSampleStream(NKJPSegmentationDocument segments, NKJPTextDocument text) {
    this.segments = segments;
    this.text = text;
    this.paragraphs = new HashMap<>();
    reset();
  }

  @Override
  public SentenceSample read() throws IOException {
    StringBuilder sentencesString = new StringBuilder();
    List<Span> sentenceSpans = new LinkedList<>();
    Map<String, String> paragraphs = text.getParagraphs();

    while (segmentIt.hasNext()) {
      Map.Entry<String, Map<String, NKJPSegmentationDocument.Pointer>> segment = segmentIt.next();
      for (String s : segment.getValue().keySet()) {
        NKJPSegmentationDocument.Pointer currentPointer = segment.getValue().get(s);
        String currentParagraph = paragraphs.get(currentPointer.id);
        Span currentSpan = currentPointer.toSpan();
        String currentSentence = currentParagraph.substring(currentSpan.getStart(), currentSpan.getEnd());

      }
    }
    return null;
  }

  @Override
  public void reset() {
    segmentIt = segments.getSegments().entrySet().iterator();
  }

}
