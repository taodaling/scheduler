import com.google.common.base.Preconditions;
import utils.FileUtils;

public class ExportArgs {
    public static void main(String[] args) {
        Preconditions.checkArgument(args.length >= 3, "ExportArgs {path} {content}");
        FileUtils.write(args[1], args[2]);
    }
}
