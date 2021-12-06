package utils;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static final Pattern StringPattern = Pattern.compile("\"(\\\\\"|\\\\n|\\\\\\\\|[^\"])*\"");

    public static String replace(String text, Pattern pattern, Function<Matcher, String> function) {
        return replace(text, pattern, function, Function.identity());
    }

    public static String replace(String text, Pattern pattern, Function<Matcher, String> function, Function<String, String> nonMatchTextFunction) {
        StringBuilder builder = new StringBuilder();
        Matcher matcher = pattern.matcher(text);
        int last = 0;
        while (matcher.find()) {
            builder.append(nonMatchTextFunction.apply(text.substring(last, matcher.start())));
            builder.append(function.apply(matcher));
            last = matcher.end();
        }
        builder.append(nonMatchTextFunction.apply(text.substring(last, text.length())));
        return builder.toString();
    }

}
