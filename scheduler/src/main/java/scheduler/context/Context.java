package scheduler.context;

import utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Context implements Cloneable {
    Map<String, String> map = new HashMap<>();
    Context inner = null;

    public static Context inherit(Context ctx, Map<String, String> map) {
        if (map == null || map.size() == 0) {
            return ctx;
        }
        return new Context(map, ctx);
    }

    private Context(Map<String, String> map, Context inner) {
        this.map = map;
        this.inner = inner;
    }

    public Context(Map<String, String> map) {
        this(map, null);
    }


    public String get(String path){
        return translate(get0(path));
    }

    private String get0(String path) {
        if (map.containsKey(path)) {
            return map.get(path);
        }
        if (inner == null) {
            return null;
        }
        return inner.get0(path);
    }


    static Pattern pattern = Pattern.compile("\\$\\{(.*?)}");

    public String translate(String s) {
        if(s == null || s.length() == 0){
            return s;
        }

        boolean[] find = new boolean[1];
        s = StringUtils.replace(s, pattern, m -> {
           String text = m.group(1);
           find[0] = true;
           return get(text);
        });

        if(find[0]){
            return translate(s);
        }
        return s;
    }
}
