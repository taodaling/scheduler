import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Project {
    public Map<String, Project> dep = new HashMap<>();
    public File absolutePath;
    public String name;
}
