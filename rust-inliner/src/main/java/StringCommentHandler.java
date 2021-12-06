import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StringCommentHandler {
    public int get(char[] s, int i) {
        if (i < s.length && i >= 0) {
            return s[i];
        }
        return -1;
    }

    int last = 0;
    char[] s;
    Handler handler;
    String body;
    List<String> fragments;

    private void processOtherUtil(int l) {
        l = Math.min(l, s.length);
        if (l > last) {
            fragments.add(handler.replaceOther(body.substring(last, l)));
        }
        last = l;
    }

    private void processString(int l, int r) {
        r = Math.min(r, s.length - 1);
        processOtherUtil(l);
        fragments.add(handler.replaceString(body.substring(l, r + 1)));
        last = r + 1;
    }

    private void processComment(int l, int r) {
        r = Math.min(r, s.length - 1);
        processOtherUtil(l);
        fragments.add(handler.replaceComment(body.substring(l, r + 1)));
        last = r + 1;
    }

    public String replace(String body, Handler handler) {
        s = body.toCharArray();
        fragments = new ArrayList<>();
        this.body = body;
        this.handler = handler;
        this.last = 0;
        for (int i = 0; i < s.length; i++) {
            if (get(s, i) == 'r' && (i == 0 || !Character.isLetterOrDigit(get(s, i - 1)))) {
                int l = i;
                int sharpNum = 0;
                while (get(s, i + 1) == '#') {
                    sharpNum++;
                    i++;
                }

                if (get(s, i + 1) == '"') {
                    i++;
                    String searchFor = "\"" + "#".repeat(sharpNum);
                    int nextOccur = body.indexOf(searchFor, i + 1);
                    i = nextOccur + searchFor.length() - 1;
                    processString(l, i);
                    continue;
                }
            }
            if (get(s, i) == '"' && get(s, i - 1) != '\'') {
                //cool
                int l = i;
                i++;
                while (i < s.length) {
                    if (get(s, i) == '\\') {
                        i++;
                    } else if (get(s, i) == '"') {
                        break;
                    }
                    i++;
                }
                processString(l, i);
                continue;
            }
            if (get(s, i) == '/' && get(s, i + 1) == '/') {
                int l = i;
                i++;
                while (i < s.length && s[i] != '\n') {
                    i++;
                }
                processComment(l, i);
                continue;
            }
            if (get(s, i) == '/' && get(s, i + 1) == '*') {
                int l = i;
                int nextOccur = body.indexOf("*/", i + 2);
                i = nextOccur + 1;
                processComment(l, i);
                continue;
            }

        }
        processOtherUtil(s.length);

        return fragments.stream().collect(Collectors.joining());
    }

    public static interface Handler {
        String replaceString(String s);

        String replaceComment(String s);

        String replaceOther(String s);
    }
}
