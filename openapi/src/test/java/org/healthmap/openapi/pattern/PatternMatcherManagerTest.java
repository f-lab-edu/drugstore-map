package org.healthmap.openapi.pattern;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PatternMatcherManagerTest {
    @Autowired
    private PatternMatcherManager patternMatcherManager;

    @Test
    @DisplayName("patternMatcher가 잘 되는지 확인")
    void testTimePatternMatchManager() {
        String check1 = "오전11시-03시";
        String check2 = "오후12:00 - 13:30";
        String check3 = "11시-3";
        String check4 = "오전11시30분부터 02시까지";
        String check5 = "오후01시부터";
        String check6 = "09:08부터접수";


        String result1 = patternMatcherManager.matchAndFormat(check1);
        String result2 = patternMatcherManager.matchAndFormat(check2);
        String result3 = patternMatcherManager.matchAndFormat(check3);
        String result4 = patternMatcherManager.matchAndFormat(check4);
        String result5 = patternMatcherManager.matchAndFormat(check5);
        String result6 = patternMatcherManager.matchAndFormat(check6);

        Assertions.assertThat(result1).isEqualTo("11:00 ~ 15:00");
        Assertions.assertThat(result2).isEqualTo("12:00 ~ 13:30");
        Assertions.assertThat(result3).isEqualTo("11:00 ~ 15:00");
        Assertions.assertThat(result4).isEqualTo("11:30 ~ 14:00");
        Assertions.assertThat(result5).isEqualTo("13:00 ~ ");
        Assertions.assertThat(result6).isEqualTo("09:08 ~ ");
    }
}
