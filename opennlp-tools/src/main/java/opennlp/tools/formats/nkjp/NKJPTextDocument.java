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

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NKJPTextDocument {
  Map<String, String> divtypes;

  NKJPTextDocument() {
    divtypes = new HashMap<>();
  }
  public static NKJPTextDocument parse(InputStream is) throws IOException {
    NKJPTextDocument document = new NKJPTextDocument();
    Map<String, String> divtypes = new HashMap<>();

    try {
      DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
      Document doc = docBuilder.parse(is);

      XPathFactory xPathfactory = XPathFactory.newInstance();
      XPath xpath = xPathfactory.newXPath();

      doc.getDocumentElement().normalize();
      String root = doc.getDocumentElement().getNodeName();
      if (!root.equalsIgnoreCase("teiCorpus")) {
          throw new IOException("Expected root node " + root);
      }

      XPathExpression expr = xpath.compile("/teiCorpus/TEI/text/group/text");


    } catch (ParserConfigurationException e) {
        throw new IllegalStateException(e);
    } catch (SAXException | XPathExpressionException | IOException e) {
        throw new IOException("Failed to parse NKJP document", e);
    }
    return document;
  }
}
