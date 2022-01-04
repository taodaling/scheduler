import com.google.common.base.Preconditions;
import com.moandjiezana.toml.Toml;
import utils.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

//TODO
//- delete comments -> maybe done
//- projctName::xx full qualified name -> wriedly done
public class RustInline {
    public static void main(String[] args) {
        Preconditions.checkArgument(args.length >= 2, "RustInline {path-to-project} {inline-path}");
        System.out.println("Inline [" + args[0] + "] to [" + args[1] + "]");

        RustInline rustInline = new RustInline();

        Project project = rustInline.getProject(new File(args[0]));
        String res = rustInline.include("main", project);
        //remove projctName::xx full qualified name
        res = "//Timestamp: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n"
                + res;
        FileUtils.write(args[1], res);
    }

    Map<File, Project> projectMap = new HashMap<>();
    File srcFile;
    Set<File> included = new HashSet<>();

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

    private Project getProject(File projectPath) {
        Project project = projectMap.get(projectPath);
        if (project == null) {
            project = new Project();
            project.absolutePath = projectPath;
            projectMap.put(projectPath, project);
            File config = new File(projectPath, "Cargo.toml");
            Toml toml = new Toml().read(config);
            project.name = toml.getTable("package").getString("name");
            Map<String, Object> map = toml.getTable("dependencies").toMap();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey().replaceAll("-", "_");
                String value = ((Map<String, String>) entry.getValue()).get("path");
                project.dep.put(key, getProject(new File(projectPath, value)));
            }
            project.dep.put("crate", project);
            project.dep.put(project.name, project);
            project.dep.put("super", project);
        }
        return project;
    }

    public String include(String modName, Project project) {
        File file = new File(new File(project.absolutePath, "src"), modName + ".rs");
        if (included.contains(file)) {
            return "";
        }
        included.add(file);
        String content = FileUtils.readFile(file);
        UseExtractor extractor = new UseExtractor(content, project.dep.keySet());
        List<UseStatement> all = extractor.getAllUse();

        StringBuilder ans = new StringBuilder();
        for (UseStatement s : all) {
            String[] pieces = s.getMods();
            if (pieces.length <= 1) {
                continue;
            }
            if (!project.dep.containsKey(pieces[0])) {
                continue;
            }
            ans.append(include(pieces[1], project.dep.get(pieces[0])));
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
            if (project.dep.containsKey(pieces[0])) {
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
        for (String s : extractor.getExport()) {
            ans.append(s).append('\n');
        }
        if (isMod) {
            ans.append("\n}\n");
        }
        return ans.toString();
    }
}
