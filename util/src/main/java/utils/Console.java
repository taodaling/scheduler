package utils;

import java.io.PrintStream;

public class Console {
    public static void main(String[] args) throws InterruptedException {
        Console c = new Console(System.out);
        c.newLine();
        c.red(" Hello, world! ");
        c.green(" I'm daltao! ");
        c.storeCursor();

        for (int i = 0; i < 10; i++) {
            c.restoreCursor();
            c.clearContentAfterCursor();
            c.white("" + i);
            Thread.sleep(1000);
        }
    }

    public Console newLine() {
        ps.println();
        return this;
    }

    public Console white(String content) {
        output("\033[40;37m", content);
        return this;
    }

    public Console red(String content) {
        output("\033[40;31m", content);
        return this;
    }

    public Console green(String content) {
        output("\033[40;32m", content);
        return this;
    }

    public Console blue(String content) {
        output("\033[40;34m", content);
        return this;
    }
    private Console output(String color, String content) {
        ps.append(color + content + "\033[0m");
        return this;
    }

    public Console(PrintStream ps) {
        this.ps = ps;
    }

    public Console clear() {
        ps.append("\033[2J");
        return this;
    }

    public Console setCursor(int x, int y) {
        ps.append("\033[" + y + ";" + x + "H");
        return this;
    }

    public Console storeCursor() {
        ps.append("\033[s");
        return this;
    }

    public Console restoreCursor() {
        ps.append("\033[u");
        return this;
    }

    public Console clearContentAfterCursor() {
        ps.append("\033[K");
        return this;
    }

    PrintStream ps;
}