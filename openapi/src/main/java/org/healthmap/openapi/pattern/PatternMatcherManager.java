package org.healthmap.openapi.pattern;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Component
public class PatternMatcherManager {
    private final Set<PatternMatcherInterface> patternMatcherSet;

    public String matchAndFormat(String input) {
        if (input == null) {
            return null;
        }

        String spaceRemoved = input.replaceAll(" ", "");
        String result = input;
        for(PatternMatcherInterface patternMatcher : patternMatcherSet) {
            if(patternMatcher.matches(spaceRemoved)) {
                result = patternMatcher.process(spaceRemoved);
                break;
            }
        }
        return result;
    }
}
