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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import opennlp.tools.util.Span;

public class NKJPSegmentationDocument {

  public static class Pointer {
    String doc;
    String id;
    int offset;
    int length;
    boolean space_after;

    public Pointer(String doc, String id, int offset, int length, boolean space_after) {
      this.doc = doc;
      this.id = id;
      this.offset = offset;
      this.length = length;
      this.space_after = space_after;
    }

    public Span toSpan() {
      return new Span(this.offset, this.offset + this.length);
    }
  }

  public Map<String, Map<String, Pointer>> getSegments() {
    return segments;
  }

  Map<String, Map<String, Pointer>> segments;

  NKJPSegmentationDocument() {
    this.segments = new HashMap<>();
  }

  NKJPSegmentationDocument(Map<String, Map<String, Pointer>> segments) {
    this();
    this.segments = segments;
  }

  public static NKJPSegmentationDocument parse(InputStream is) throws IOException {

    Map<String, Map<String, Pointer>> sentences = new HashMap<>();

    try {
      DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
      Document doc = docBuilder.parse(is);

      XPathFactory xPathfactory = XPathFactory.newInstance();
      XPath xpath = xPathfactory.newXPath();

      final XPathExpression SENT_NODES = xpath.compile("/teiCorpus/TEI/text/body/p/s");
      final XPathExpression SEG_NODES = xpath.compile("./seg");

      NodeList nl = (NodeList) SENT_NODES.evaluate(doc, XPathConstants.NODESET);

      for (int i = 0; i < nl.getLength(); i++) {
        Node sentnode = nl.item(i);

        String sentid = null;
        if (sentnode.getAttributes().getNamedItem("xml:id") != null) {
          sentid = sentnode.getAttributes().getNamedItem("xml:id").getTextContent();
        }

        Map<String, Pointer> segments = new HashMap<>();
        NodeList segnl = (NodeList) SEG_NODES.evaluate(sentnode, XPathConstants.NODESET);

        for(int j = 0; j < segnl.getLength(); j++) {
          Node n = segnl.item(j);

          if (n.getAttributes() == null || n.getAttributes().getLength() < 2) {
            throw new IOException("Missing required attributes");
          }

          String id = null;
          if (n.getAttributes().getNamedItem("xml:id") != null) {
            id = n.getAttributes().getNamedItem("xml:id").getTextContent();
          }
          String ptr = null;
          if (n.getAttributes().getNamedItem("corresp") != null) {
            ptr = n.getAttributes().getNamedItem("corresp").getTextContent();
          }
          String spacing = "";
          if (n.getAttributes().getNamedItem("nkjp:nps") != null) {
            spacing = n.getAttributes().getNamedItem("nkjp:nps").getTextContent();
          }

          if (id == null || ptr == null) {
            throw new IOException("Missing required attribute");
          }

          boolean space_after = (ptr != null && ptr.equals("yes"));

          if (!ptr.contains("#") || !ptr.contains("(") || ptr.charAt(ptr.length() - 1) != ')') {
            throw new IOException("String " + ptr + " does not appear to be a valid NKJP corresp attribute");
          }

          int docend = ptr.indexOf('#');
          String document = ptr.substring(0, docend);

          int pointer_start = ptr.indexOf('(') + 1;
          String[] pieces = ptr.substring(pointer_start, ptr.length() - 1).split(",");

          if (pieces.length != 3) {
            throw new IOException("String " + ptr + " does not appear to be a valid NKJP corresp attribute");
          }

          String docid = pieces[1];
          int offset = Integer.parseInt(pieces[1]);
          int length = Integer.parseInt(pieces[2]);

          Pointer pointer = new Pointer(document, docid, offset, length, space_after);
          segments.put(id, pointer);
        }

        sentences.put(sentid, segments);
      }

    } catch (ParserConfigurationException e) {
      throw new IllegalStateException(e);
    } catch (SAXException | XPathExpressionException | IOException e) {
      throw new IOException("Failed to parse NKJP document", e);
    }

    return new NKJPSegmentationDocument(sentences);
  }

  static NKJPSegmentationDocument parse(File file) throws IOException {
    try (InputStream in = new FileInputStream(file)) {
      return parse(in);
    }
  }
}
