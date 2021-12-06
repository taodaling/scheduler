import com.google.common.base.Preconditions;
import utils.FileUtils;

import java.io.File;
import java.util.*;

//TODO
//- delete comments -> maybe done
//- projctName::xx full qualified name -> wriedly done
public class RustInline {
    public static void main(String[] args) {
        Preconditions.checkArgument(args.length >= 2, "RustInline {path-to-project} {inline-path}");
        System.out.println("Inline [" + args[0] + "] to [" + args[1] + "]");

        RustInline rustInline = new RustInline();
        rustInline.projFile = new File(args[0]);
        rustInline.projName = rustInline.projFile.getName();
        rustInline.srcFile = new File(rustInline.projFile, "src");

        String res = rustInline.include(Arrays.asList(rustInline.projName), new HashSet<>(), "main");
        //remove projctName::xx full qualified name
        String uuid = UUID.randomUUID().toString();
        res = res.replaceAll("::" + rustInline.projName + "::", "::" + uuid + "::");
        res = res.replaceAll(rustInline.projName + "::", "crate::");
        res = res.replaceAll("::" + uuid + "::", "::" + rustInline.projName + "::");

        FileUtils.write(args[1], res);
    }

    String projName;
    File projFile;
    File srcFile;

    private File getModFile(String modName) {
        File ans = new File(srcFile, modName + ".rs");
        if (!ans.exists()) {
            ans = new File(new File(srcFile, modName), "mod.rs");
        }
        if (!ans.exists()) {
            throw new IllegalArgumentException("Mod [" + modName + "] not exists");
        }
        return ans;
    }


    public String include(Collection<String> selfRepr, Set<String> included, String modName) {
        if (included.contains(modName)) {
            return "";
        }
        included.add(modName);
        File file = getModFile(modName);
        String content = FileUtils.readFile(file);
        UseExtractor extractor = new UseExtractor(content);
        List<UseStatement> all = extractor.getAllUse();

        StringBuilder ans = new StringBuilder();
        for (UseStatement s : all) {
            String[] pieces = s.getMods();
            if (pieces.length <= 1) {
                continue;
            }
            if (!selfRepr.contains(pieces[0])) {
                continue;
            }
            ans.append(include(Arrays.asList("super", "crate"), included, pieces[1]));
        }

        boolean isMod = !modName.equals("main");
        if (isMod) {
            ans.append("pub mod ").append(modName).append("{\n");
        }
        for (UseStatement s : extractor.getUseHeader()) {
            String[] pieces = s.getMods();
            if (pieces.length <= 1) {
                continue;
            }
            if (pieces[0].equals(projName)) {
                pieces[0] = "crate";
            }
            ans.append("use ").append(s.concatMods());
            if (s.alias != null) {
                ans.append(" as ").append(s.alias);
            }
            ans.append(";\n");
        }
        ans.append(extractor.getBodyWithoutUse());
        //publish
        for(String s : extractor.getExport()) {
            ans.append(s).append('\n');
        }
        if (isMod) {
            ans.append("\n}\n");
        }
        return ans.toString();
    }
}
