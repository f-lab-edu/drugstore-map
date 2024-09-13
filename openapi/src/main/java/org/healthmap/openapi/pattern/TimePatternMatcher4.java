package org.healthmap.openapi.pattern;

import lombok.extern.slf4j.Slf4j;
import org.healthmap.openapi.utility.PatternUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class TimePatternMatcher4 implements PatternMatcherInterface {
    private final Pattern timePattern = Pattern.compile("(오전|오후|아침)?(\\d{1,2})시(\\d{2}분)?부터(오후|저녁)?(\\d{1,2})시(\\d{2}분)?(까지)?");         //1시부터 2시(30분)까지

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
        String endHour = matcher.group(5);
        String endMinute = matcher.group(6);

        // start hour
        if (startTimeAmPm != null) {
            resultStr.append(PatternUtils.getStartHourWithAmPm(startHour, startTimeAmPm));
        } else {                                            // (오전/오후)가 없을시
            resultStr.append(PatternUtils.getStartHourWithoutAmPm(startHour));
        }
        // start minute
        resultStr.append(PatternUtils.getStartMinute(startMinute));
        //end 시간
        resultStr.append(PatternUtils.getEndHour(endHour));
        //end 분
        resultStr.append(PatternUtils.getEndMinute(endMinute));

        return resultStr.toString();

    }
}


