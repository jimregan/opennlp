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
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import opennlp.tools.util.Span;

/**
 * http://www.lexiconista.com/datasets/sentencebank-ga/
 */
public class IrishSentenceBankDocument {
  public static class IrishSentenceBankFlex {
    String surface;
    String[] flex;
    public String getSurface() {
      return surface;
    }
    public String[] getFlex() {
      return flex;
    }
    public IrishSentenceBankFlex(String sf, String[] fl) {
      this.surface = sf;
      this.flex = fl;
    }
  }
  public static class IrishSentenceBankSentence {
    private String source;
    private String translation;
    private String original;
    private Span[] tokens;
    private IrishSentenceBankFlex[] flex;
    public String getSource() {
      return source;
    }
    public String getTranslation() {
      return translation;
    }
    public String getOriginal() {
      return original;
    }
    public Span[] getTokens() {
      return tokens;
    }
    public IrishSentenceBankFlex[] getFlex() {
      return flex;
    }
  }

  private List<IrishSentenceBankSentence> sentences = new ArrayList<IrishSentenceBankSentence>();

  /**
   * Helper to adjust the span of punctuation tokens: ignores spaces to the left of the string
   * @param s the string to check
   * @param start the offset of the start of the string
   * @return the offset adjusted to ignore spaces to the left
   */
  private static int advanceLeft(String s, int start) {
    int ret = start;
    for (char c : s.toCharArray()) {
      if (c == ' ') {
        ret++;
      } else {
        return ret;
      }
    }
    return ret;
  }

  /**
   * Helper to adjust the span of punctuation tokens: ignores spaces to the right of the string
   * @param s the string to check
   * @param start the offset of the start of the string
   * @return the offset of the end of the string, adjusted to ignore spaces to the right
   */
  private static int advanceRight(String s, int start) {
    int end = s.length() - 1;
    int ret = start + end + 1;
    for (int i = end; i > 0; i--) {
      if (s.charAt(i) == ' ') {
        ret--;
      } else {
        return ret;
      }
    }
    return ret;
  }

  public void loadXML(InputStream is) throws IOException {
    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
    Document doc = docBuilder.parse(is);
    String root = doc.getDocumentElement().getNodeName();
    if (root != "sentences") {
      throw new IOException("Expected root node " + root);
    }
    NodeList nl = doc.getDocumentElement().getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node sentnode = nl.item(i);
      if (!sentnode.getNodeName().equals("sentence")) {
        throw new IOException("Unexpected node: " + sentnode.getNodeName());
      }
      String src = sentnode.getAttributes().getNamedItem("source").getNodeValue();
      String trans;
      Map<Integer, String> toks = new HashMap<Integer, String>();
      Map<Integer, List<String>> flx = new HashMap<Integer, List<String>>();
      List<Span> spans = new ArrayList<Span>();
      NodeList sentnl = sentnode.getChildNodes();
      int flexes = 1;
      for (int j = 0; j < sentnl.getLength(); j++) {
        String name = sentnl.item(j).getNodeName();
        if (name.equals("original")) {
          StringBuilder orig;
          int last = 0;
          List<Span> spans = new ArrayList<Span>();
          NodeList orignl = sentnl.item(j).getChildNodes();
          for (int k = 0; k < orignl.getLength(); k++) {
            if (orignl.item(k).getNodeName().equals("token")) {
              String tmp = orignl.item(k).getFirstChild().getTextContent();
              spans.put(new Span(last, last + tmp.length()));
              String slottmp = orignl.item(k).getAttributes().getNamedItem("slot").getNodeValue();
              Integer slot = Integer.parseInt(slottmp);
              if (slot > flexes) {
                flexes = slot;
              }
              toks.put(slot, tmp);
              orig += tmp;
              last += tmp.length();              
            } else if (orignl.item(k).getNodeName().equals("#text")) {
              String tmp = orignl.item(k).getFirstChild().getTextContent();
              orig += tmp;
              spans.put(new Span(adjustLeft(tmp, last), adjustRight(tmp, last)));
              last += tmp.length();
            } else {
              throw new IOException("Unexpected node: " + orignl.item(k).getNodeName());
            }
          }
        } else if (name.equals("translation")) {
          trans = sentnl.item(j).getFirstChild().getTextContent();
        } else if (name.equals("flex")) {
          String slottmp = sentnl.item(j).getAttributes().getNamedItem("slot").getNodeValue();
          Integer slot = Integer.parseInt(slottmp);
          if (slot > flexes) {
            flexes = slot;
          }
          if (flx.get(slot) == null) {
            flx.put(slot, new ArrayList<String>());
          }
          String tkn = sentnl.item(j).getFirstChild().getTextContent();
          flx.get(slot).add(tkn);
        } else {
          throw new IOException("Unexpected node: " + name);
        }
        IrishSentenceBankFlex[] flexa = new IrishSentenceBankFlex[flexes - 1];
        for (int flexidx = 1; flexidx <= flexes; flexidx++) {
          String left = toks.get(flexidx);
          String[] right = new String[flx.get(flexidx).size()];
          right = flx.get(flexidx).toArray(right);
          flexa[flexidx - 1] = new IrishSentenceBankFlex(left, right);
        }
      }
    }
  }
}
