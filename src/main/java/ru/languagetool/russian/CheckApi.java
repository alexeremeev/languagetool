package ru.languagetool.russian;

import org.languagetool.JLanguageTool;
import org.languagetool.language.Russian;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class CheckApi {
    @GetMapping("/api/check")
    public Object test(@RequestParam(defaultValue = "") String value) throws Exception {
        JLanguageTool langTool = new JLanguageTool(new Russian());
        for (Rule rule : langTool.getAllRules()) {
            if (!rule.isDictionaryBasedSpellingRule()) {
                langTool.disableRule(rule.getId());
            }
        }
        List<RuleMatch> matches = langTool.check(value);
        for (RuleMatch match : matches) {
            System.out.println("Potential typo at characters " +
                    match.getFromPos() + "-" + match.getToPos() + ": " +
                    match.getMessage());
            System.out.println("Suggested correction(s): " +
                    match.getSuggestedReplacements());
        }
        return Collections.emptyMap();
    }
}
