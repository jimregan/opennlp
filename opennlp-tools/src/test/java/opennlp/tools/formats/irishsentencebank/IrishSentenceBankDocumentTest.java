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

package opennlp.tools.formats.irishsentencebank;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class IrishSentenceBankDocumentTest {

  @Test
  public void testParsingSimpleDoc() throws IOException {
    try (InputStream irishSBXmlIn = 
          IrishSentenceBankDocumentTest.class.getResourceAsStream("irishsentencebank-sample.xml");) {

      IrishSentenceBankDocument doc = IrishSentenceBankDocument.parse(irishSBXmlIn);

      List<IrishSentenceBankDocument.IrishSentenceBankSentence> sents = doc.getSentences();

      Assert.assertEquals(1, sents.size());

      IrishSentenceBankDocument.IrishSentenceBankSentence sent1 = sents.get(0);

      Assert.assertEquals("A Dhia, tá mé ag iompar clainne!", sent1.getOriginal());

    }
  }
}
