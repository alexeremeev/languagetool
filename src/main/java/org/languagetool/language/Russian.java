/* LanguageTool, a natural language style checker 
 * Copyright (C) 2007 Daniel Naber (http://www.danielnaber.de)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.language;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.languagetool.*;
import org.languagetool.chunking.Chunker;
import org.languagetool.chunking.RussianChunker;
import org.languagetool.languagemodel.LanguageModel;
import org.languagetool.rules.*;
import org.languagetool.rules.ru.*;
import org.languagetool.rules.spelling.SpellingCheckRule;
import org.languagetool.synthesis.Synthesizer;
import org.languagetool.synthesis.ru.RussianSynthesizer;
import org.languagetool.tagging.Tagger;
import org.languagetool.tagging.disambiguation.Disambiguator;
import org.languagetool.tagging.disambiguation.ru.RussianHybridDisambiguator;
import org.languagetool.tagging.ru.RussianTagger;
import org.languagetool.tokenizers.SRXSentenceTokenizer;
import org.languagetool.tokenizers.SentenceTokenizer;
import org.languagetool.tokenizers.Tokenizer;
import org.languagetool.tokenizers.ru.RussianWordTokenizer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class Russian extends LanguageWithModel {

  private static final String LANGUAGE_SHORT_CODE = "ru";

  private static volatile Throwable instantiationTrace;

  public Russian() {
    Throwable trace = instantiationTrace;
    if (trace != null) {
//      throw new RuntimeException("Language was already instantiated, see the cause stacktrace below.", trace);
    }
    instantiationTrace = new Throwable();
  }

  /**
   * This is a fake constructor overload for the subclasses. Public constructors can only be used by the LT itself.
   */
  public Russian(boolean fakeValue) {
  }
  @Override
  public Pattern getIgnoredCharactersRegex() {
    return Pattern.compile("[\u00AD\u0301\u0300]");
  }

  @Override
  public String getName() {
    return "Russian";
  }

  @Override
  public String getShortCode() {
    return LANGUAGE_SHORT_CODE;
  }

  @Override
  public String[] getCountries() {
    return new String[] {"RU"};
  }

  @NotNull
  @Override
  public Tagger createDefaultTagger() {
    return RussianTagger.INSTANCE;
  }

  @Override
  public Disambiguator createDefaultDisambiguator() {
    return RussianHybridDisambiguator.getInstance();
  }

  @Nullable
  @Override
  public Chunker createDefaultPostDisambiguationChunker() {
    return new RussianChunker();
  }


  @Nullable
  @Override
  public Synthesizer createDefaultSynthesizer() {
    return RussianSynthesizer.INSTANCE;
  }

  @Override
  public SentenceTokenizer createDefaultSentenceTokenizer() {
    return new SRXSentenceTokenizer(this);
  }

  @Override
  public Tokenizer createDefaultWordTokenizer() {
    return new RussianWordTokenizer();
  }

  @Override
  public Contributor[] getMaintainers() {
    return new Contributor[] {
            new Contributor("Yakov Reztsov", "http://myooo.ru/content/view/83/43/")
    };
  }
  
  /** @since 5.1 */
  @Override
  public String getOpeningDoubleQuote() {
    return "«";
  }

  /** @since 5.1 */
  @Override
  public String getClosingDoubleQuote() {
    return "»";
  }
  
  /** @since 5.1 */
  @Override
  public String getOpeningSingleQuote() {
    return "‘";
  }

  /** @since 5.1 */
  @Override
  public String getClosingSingleQuote() {
    return "’";
  }
  
  /** @since 5.1 */
  @Override
  public boolean isAdvancedTypographyEnabled() {
    return true;
  }

  @Override
  public List<Rule> getRelevantRules(ResourceBundle messages, UserConfig userConfig, Language motherTongue, List<Language> altLanguages) throws IOException {
    return Arrays.asList(
            new CommaWhitespaceRule(messages,
                    Example.wrong("Не род<marker> ,</marker> а ум поставлю в воеводы."),
                    Example.fixed("Не род<marker>,</marker> а ум поставлю в воеводы.")),
        //  new DoublePunctuationRule(messages),  // replace to xml rule
            new UppercaseSentenceStartRule(messages, this,
                    Example.wrong("Закончилось лето. <marker>дети</marker> снова сели за школьные парты."),
                    Example.fixed("Закончилось лето. <marker>Дети</marker> снова сели за школьные парты.")),
            new MorfologikRussianSpellerRule(messages, this, userConfig, altLanguages),
        //  new WordRepeatRule(messages, this), // move to RussianSimpleWordRepeatRule
            new MultipleWhitespaceRule(messages, this),
	    new SentenceWhitespaceRule(messages),
            new WhiteSpaceBeforeParagraphEnd(messages, this),  
            new WhiteSpaceAtBeginOfParagraph(messages),
        //  new EmptyLineRule(messages, this),  // too picky rule 
            new LongSentenceRule(messages, userConfig, 50),
            new LongParagraphRule(messages, this, userConfig),
            new ParagraphRepeatBeginningRule(messages, this),   //re-activate rule, issue #3509
            new RussianFillerWordsRule(messages, this, userConfig),
        //  new PunctuationMarkAtParagraphEnd(messages, this),
            new PunctuationMarkAtParagraphEnd2(messages, this),  //
        //  new ReadabilityRule(messages, this, userConfig, false), // need use localise rule
        //  new ReadabilityRule(messages, this, userConfig, true),  // need use localise rule
      
            
                // specific to Russian :
            new MorfologikRussianYOSpellerRule(messages, this, userConfig, altLanguages), // This rule must set off by default!!!
            new RussianUnpairedBracketsRule(messages, this),
            new RussianCompoundRule(messages, this, userConfig),
            new RussianSimpleReplaceRule(messages),
            new RussianSimpleWordRepeatRule(messages, this),
            new RussianWordCoherencyRule(messages),
            new RussianWordRepeatRule(messages),
            new RussianWordRootRepeatRule(messages),
            new RussianVerbConjugationRule(messages),
            new RussianDashRule(messages),
            new RussianSpecificCaseRule(messages)
    );
  }

  /** @since 3.1 */
  @Override
  public List<Rule> getRelevantLanguageModelRules(ResourceBundle messages, LanguageModel languageModel, UserConfig userConfig) throws IOException {
    return List.of(
      new RussianConfusionProbabilityRule(messages, languageModel, this)
    );
  }

  /** @since 3.3 */
  @Override
  public LanguageMaintainedState getMaintainedState() {
    return LanguageMaintainedState.ActivelyMaintained;
  }

  @Override
  protected int getPriorityForId(String id) {
    switch (id) {
      case "RU_DASH_RULE":                  return 12;  // higher prio than RU_COMPOUNDS
      case "RU_COMPOUNDS":                  return 11;
      case "RUSSIAN_SIMPLE_REPLACE_RULE":   return 10;  // higher prio than spell checker
      case "RUSSIAN_SPECIFIC_CASE":         return 9;   // higher prio than spell checker
      case "MORFOLOGIC_RULE_RU_RU_YO":      return 2;   // spell checker yo
      case "MORFOLOGIC_RULE_RU_RU":         return 1;   // standard spell checker yo+ie


      case "Word_root_repeat":              return -1;
      case "PUNCT_DPT_2":                   return -2;
      case "TOO_LONG_PARAGRAPH":            return -15;
    }
    return super.getPriorityForId(id);
  }

  @Nullable
  @Override
  protected SpellingCheckRule createDefaultSpellingRule(ResourceBundle messages) throws IOException {
    return new MorfologikRussianSpellerRule(messages, this, null, null);
  }

  public static @NotNull Russian getInstance() {
    Language language = Objects.requireNonNull(Languages.getLanguageForShortCode(LANGUAGE_SHORT_CODE));
    if (language instanceof Russian russian) {
      return russian;
    }
    throw new RuntimeException("Russian language expected, got " + language);
  }
}
