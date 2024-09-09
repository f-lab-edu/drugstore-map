package org.healthmap.openapi.pattern;

public interface PatternMatcherInterface {
    boolean matches(String input);
    String process(String input);
}
