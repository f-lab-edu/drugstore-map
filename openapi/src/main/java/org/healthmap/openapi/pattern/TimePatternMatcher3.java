package org.healthmap.openapi.pattern;

import lombok.extern.slf4j.Slf4j;
import org.healthmap.openapi.utility.PatternUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class TimePatternMatcher3 implements PatternMatcherInterface {
    private final Pattern timePattern = Pattern.compile("(\\d{1,2})시?([-~])(\\d{1,2})시?");    //ex) 13(시)-14(시), 01시-02시

    @Override
    public boolean matches(String input) {
        return timePattern.matcher(input).matches();
    }

    @Override
    public String process(String input) {
        StringBuilder resultStr = new StringBuilder();
        Matcher matcher = timePattern.matcher(input);

        if (!matcher.matches()) {
            log.info("잘못된 process 호출입니다. input: {} ", input);
            return null;
        }

        String startTime = matcher.group(1);
        String startMinute = null;
        String endTime = matcher.group(3);
        String endMinute = null;

        resultStr.append(PatternUtils.getStartHourWithoutAmPm(startTime));
        resultStr.append(PatternUtils.getStartMinute(startMinute));
        resultStr.append(PatternUtils.getEndHour(endTime));
        resultStr.append(PatternUtils.getEndMinute(endMinute));
        return resultStr.toString();

    }
}


