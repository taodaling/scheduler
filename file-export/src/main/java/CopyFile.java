import com.google.common.base.Preconditions;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class CopyFile {
    public static void main(String[] args) throws IOException {
        Preconditions.checkArgument(args.length >= 2, "CopyFile {src} {dst}");
        File src = new File(args[0]);
        File dst = new File(args[1]);
        if (dst.exists() && !dst.delete()) {
            throw new IOException("File [" + dst.getAbsolutePath() + "] and can't be deleted");
        }
        System.out.println("Copy [" + src.getAbsolutePath() + "] to [" + dst.getAbsolutePath() + "]");
        FileChannel sourceChannel = new RandomAccessFile(src, "r").getChannel();
        FileChannel targetChannel = new RandomAccessFile(dst, "rw").getChannel();
        sourceChannel.transferTo(0, sourceChannel.size(), targetChannel);
        sourceChannel.close();
        targetChannel.close();
    }
}
