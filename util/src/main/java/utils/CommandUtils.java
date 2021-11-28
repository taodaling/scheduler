package utils;

import java.util.ArrayList;
import java.util.List;

public class CommandUtils {
    public static List<String> splitCommands(String command) {
        List<String> ans = new ArrayList<>();
        char[] s = command.toCharArray();
        for (int r = 0; r < s.length; r++) {
            if (Character.isWhitespace(s[r])) {
                continue;
            }
            int l = r;
            if (s[r] == '"') {
                r++;
                while (s[r] != '"') {
                    r++;
                }
            } else {
                r++;
                while (r < s.length && !Character.isWhitespace(s[r])) {
                    r++;
                }
            }
            ans.add(command.substring(l, r));
        }
        return ans;
    }
}
