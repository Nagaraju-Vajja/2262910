package com.macys.uop.order.ordercollectorchestrator.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class MaskingPatternLayout extends PatternLayout {

    private String patternsProperty;
    private Optional<Pattern> pattern;

    public String getPatternsProperty() {
        return patternsProperty;
    }

    public void setPatternsProperty(String patternsProperty) {
        this.patternsProperty = patternsProperty;
        if (this.patternsProperty != null) {
            this.pattern = Optional.of(Pattern.compile(patternsProperty, Pattern.MULTILINE));
        } else {
            this.pattern = Optional.empty();
        }
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        final StringBuilder message = new StringBuilder(super.doLayout(event));

        if (pattern.isPresent()) {
            Matcher matcher = pattern.get().matcher(message);
            while (matcher.find()) {

                int group = 1;
                while (group <= matcher.groupCount()) {
                    if (matcher.group(group) != null) {

                        final int startGroupIndexVal = matcher.start(group);
                        final int endGroupIndexVal = matcher.end(group);
                        final int indexDifference = endGroupIndexVal - startGroupIndexVal + 1;
                        int startIndexVal = startGroupIndexVal + indexDifference;

                        // finding the index positions
                        final int endIndexVal1 = message.indexOf(",", startIndexVal);
                        final int endIndexVal2 = message.indexOf(" ", startIndexVal);
                        final int endIndexVal3 = message.indexOf(")", startIndexVal);
                        final int endIndexVal4 = message.indexOf("\n", startIndexVal);

                        // To get the smallest end index value
                        final Integer endIndexVal = getSmallestInt(
                                Arrays.asList(Integer.valueOf(endIndexVal1), Integer.valueOf(endIndexVal2),
                                        Integer.valueOf(endIndexVal3), Integer.valueOf(endIndexVal4)));

                        if (endIndexVal == null || endIndexVal <= 0) {
                            continue;
                        }

                        // traversing the log and masking the data field value based on defined regex
                        for (int i = startIndexVal; i < endIndexVal; i++) {
                            if (Character.toString(message.charAt(i)).matches("([.A-Za-z0-9+=@])*")) {
                                message.setCharAt(i, '*');
                            }
                        }
                    }
                    group++;
                }
            }
        }
        return message.toString();
    }

    private Integer getSmallestInt(List<Integer> integerList) {

        return integerList.stream().filter(integer -> integer > 0).reduce((x, y) -> x < y ? x : y).get();
    }

}