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

import java.util.ArrayList;
import java.util.List;

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

  
}
