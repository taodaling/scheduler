package utils;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
    public static String replace(String text, Pattern pattern, Function<Matcher, String> function) {
        StringBuilder builder = new StringBuilder();
        Matcher matcher = pattern.matcher(text);
        int last = 0;
        while (matcher.find()) {
            builder.append(text, last, matcher.start());
            builder.append(function.apply(matcher));
            last = matcher.end();
        }
        builder.append(text, last, text.length());
        return builder.toString();
    }
}
