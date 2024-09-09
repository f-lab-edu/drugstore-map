package org.healthmap.openapi.pattern;

import lombok.extern.slf4j.Slf4j;
import org.healthmap.openapi.utility.PatternUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class TimePatternMatcher6 implements PatternMatcherInterface {
    private final Pattern timePattern = Pattern.compile("(오전|오후|아침)?(\\d{1,2})[:;](\\d{2})부터(접수)?");         //09:30부터

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
        String startTimeAmPm = matcher.group(1);
        String startHour = matcher.group(2);
        String startMinute = matcher.group(3);

        if (startTimeAmPm != null) {                                    //start hour
            resultStr.append(PatternUtils.getStartHourWithAmPm(startHour, startTimeAmPm));
        } else {
            resultStr.append(PatternUtils.getStartHourWithoutAmPm(startHour));
        }

        resultStr.append(PatternUtils.getStartMinute(startMinute));     // start minute

        return resultStr.toString();

    }
}


