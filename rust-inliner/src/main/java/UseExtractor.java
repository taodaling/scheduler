import utils.StringUtils;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UseExtractor {
    List<UseStatement> useHeader = new ArrayList<>();
    List<UseStatement> qualification = new ArrayList<>();
    List<String> export = new ArrayList<>();
    String bodyWithoutUse;
    static Pattern qualificationPattern = Pattern.compile("\\w+(::\\w+)+", Pattern.MULTILINE | Pattern.DOTALL);
    static Pattern useStatementPattern = Pattern.compile("use\\s+(.*?);", Pattern.MULTILINE | Pattern.DOTALL);
    static Pattern publishPattern = Pattern.compile("pub(\\s*(\\(crate\\))?\\s*|\\s+)use\\s+[^;]+;", Pattern.MULTILINE | Pattern.DOTALL);
    public List<UseStatement> getUseHeader() {
        return useHeader;
    }

    public List<UseStatement> getQualification() {
        return qualification;
    }

    public String getBodyWithoutUse() {
        return bodyWithoutUse;
    }

    public List<String> getExport() {
        return export;
    }

    public List<UseStatement> getAllUse() {
        List<UseStatement> ans = new ArrayList<>(useHeader.size() + qualification.size());
        ans.addAll(useHeader);
        ans.addAll(qualification);
        return ans;
    }

    public UseExtractor(String content, Set<String> dep) {
        List<String> useHeaderStrs = new ArrayList<>();
        //remove comments
        bodyWithoutUse = new StringCommentHandler().replace(content, new StringCommentHandler.Handler() {
            @Override
            public String replaceString(String s) {
                return s;
            }

            @Override
            public String replaceComment(String s) {
                return "";
            }

            @Override
            public String replaceOther(String s) {
                String s1 = StringUtils.replace(s, publishPattern, m ->
                {
                    export.add(s.substring(m.start(), m.end()));
                    return "";
                });
                String s2 = StringUtils.replace(s1,
                        useStatementPattern, m -> {
                            useHeaderStrs.add(s1.substring(m.start(1), m.end(1)));
                            return "";
                        });
                String s3 = StringUtils.replace(s2,
                        qualificationPattern,
                        m -> {
                            String body = s2.substring(m.start(), m.end());
                            qualification.add(new UseStatement(body));
                            int firstIndex = body.indexOf(':');
                            String head = body.substring(0, firstIndex);
                            String tail = body.substring(firstIndex);
                            if (dep.contains(head)) {
                                return "crate" + tail;
                            }
                            return head + tail;
                        });
                return s3;
            }
        });

        useHeader = useHeaderStrs.stream()
                .flatMap(x -> process(x).stream()).collect(Collectors.toList());
    }

    private List<UseStatement> process(String useHeader) {
        List<UseStatement> res = new ArrayList<>();
        StringCharacterIterator sit = new StringCharacterIterator(useHeader);
        process0(new StringBuilder(), res, sit, CharacterIterator.DONE);
        return res;
    }

    //use a::{self, b,,}
    private void process0(StringBuilder sb, List<UseStatement> res, CharacterIterator iterator, char exitBy) {
        int len = sb.length();
        while (iterator.current() != exitBy) {
            if (iterator.current() == '{') {
                iterator.next();
                process0(sb, res, iterator, '}');
                sb.setLength(len);
//                iterator.next(); //skip }
            } else if (iterator.current() == ',') {
                if (sb.length() > len) {
                    res.add(new UseStatement(sb.toString()));
                }
                sb.setLength(len);
            } else if(!Character.isWhitespace(iterator.current())){
                sb.append(iterator.current());
            }
            iterator.next();
        }

        if (sb.length() > len) {
            res.add(new UseStatement(sb.toString()));
        }

    }
}
