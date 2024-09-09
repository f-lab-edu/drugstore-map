package org.healthmap.openapi.pattern;

import lombok.extern.slf4j.Slf4j;
import org.healthmap.openapi.utility.PatternUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class TimePatternMatcher2 implements PatternMatcherInterface {
    private final Pattern timePattern = Pattern.compile("(오전|오후|아침)?(\\d{1,2})([:;])(\\d{2})([-~])(오후|저녁)?(\\d{1,2})([:;])(\\d{2})");     //13(:|;)00(-|~)14(:|;)00, 1:00~2:00

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
        String startMinute = matcher.group(4);
        String endHour = matcher.group(7);
        String endMinute = matcher.group(9);

        //start hour
        if (startTimeAmPm != null) {
            resultStr.append(PatternUtils.getStartHourWithAmPm(startHour,startTimeAmPm));
        } else {
            resultStr.append(PatternUtils.getStartHourWithoutAmPm(startHour));
        }
        // start minute
        resultStr.append(PatternUtils.getStartMinute(startMinute));
        // end hour
        resultStr.append(PatternUtils.getEndHour(endHour));
        // end minute
        resultStr.append(PatternUtils.getEndMinute(endMinute));

        return resultStr.toString();

    }
}


