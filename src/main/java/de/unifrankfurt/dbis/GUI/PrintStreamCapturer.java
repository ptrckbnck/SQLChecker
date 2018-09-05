package de.unifrankfurt.dbis.GUI;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.PrintStream;
import java.util.Objects;

/**
 * This class caputres a PrintStream and pipes every output to a given Textarea.
 */
public class PrintStreamCapturer extends PrintStream {

    private final TextArea text;
    private boolean atLineStart;
    private String indent;

    /**
     * @param textArea       Textarea output is piped to
     * @param capturedStream Output providing PrintStream
     * @param indent         indent in front of every line in TextArea
     */
    public PrintStreamCapturer(TextArea textArea, PrintStream capturedStream, String indent) {
        super(capturedStream);
        this.text = textArea;
        this.indent = indent;
        this.atLineStart = true;
    }

    public PrintStreamCapturer(TextArea textArea, PrintStream capturedStream) {
        this(textArea, capturedStream, "");
    }

    /**
     * tells Main process to print text to Textarea.
     *
     * @param str
     */
    private void writeToTextArea(String str) {
        Platform.runLater(() -> text.appendText(str));
    }


    /**
     * parses given String and prepares it for printing to textarea.
     *
     * @param str
     */
    private void parse(String str) {
        String[] s = str.split("\n", -1);
        if (s.length == 0)
            return;
        for (int i = 0; i < s.length - 1; i++) {
            writeWithPotentialIndent(s[i]);
            writeWithPotentialIndent("\n");
            atLineStart = true;
        }
        String last = s[s.length - 1];
        if (!last.equals("")) {
            writeWithPotentialIndent(last);
        }
    }


    /**
     * adds indent to s and prints it to TextArea.
     * @param s single line String
     */
    private void writeWithPotentialIndent(String s) {
        if (atLineStart) {
            writeToTextArea(indent + s);
            atLineStart = false;
        } else {
            writeToTextArea(s);
        }
    }

    /**
     * prints new Line to TextArea
     */
    private void newLine() {
        parse("\n");
    }

    @Override
    public void print(boolean b) {
        synchronized (this) {
            super.print(b);
            parse(String.valueOf(b));
        }
    }

    @Override
    public void print(char c) {
        synchronized (this) {
            super.print(c);
            parse(String.valueOf(c));
        }
    }

    @Override
    public void print(char[] s) {
        synchronized (this) {
            super.print(s);
            parse(String.valueOf(s));
        }
    }

    @Override
    public void print(double d) {
        synchronized (this) {
            super.print(d);
            parse(String.valueOf(d));
        }
    }

    @Override
    public void print(float f) {
        synchronized (this) {
            super.print(f);
            parse(String.valueOf(f));
        }
    }

    @Override
    public void print(int i) {
        synchronized (this) {
            super.print(i);
            parse(String.valueOf(i));
        }
    }

    @Override
    public void print(long l) {
        synchronized (this) {
            super.print(l);
            parse(String.valueOf(l));
        }
    }

    @Override
    public void print(Object o) {
        synchronized (this) {
            super.print(o);
            parse(String.valueOf(o));
        }
    }

    @Override
    public void print(String s) {
        synchronized (this) {
            super.print(s);
            parse(Objects.requireNonNullElse(s, "null"));
        }
    }

    @Override
    public void println() {
        synchronized (this) {
            newLine();
            super.println();
        }
    }

    @Override
    public void println(boolean x) {
        synchronized (this) {
            print(x);
            newLine();
            super.println();
        }
    }

    @Override
    public void println(char x) {
        synchronized (this) {
            print(x);
            newLine();
            super.println();
        }
    }

    @Override
    public void println(int x) {
        synchronized (this) {
            print(x);
            newLine();
            super.println();
        }
    }

    @Override
    public void println(long x) {
        synchronized (this) {
            print(x);
            newLine();
            super.println();
        }
    }

    @Override
    public void println(float x) {
        synchronized (this) {
            print(x);
            newLine();
            super.println();
        }
    }

    @Override
    public void println(double x) {
        synchronized (this) {
            print(x);
            newLine();
            super.println();
        }
    }

    @Override
    public void println(char x[]) {
        synchronized (this) {
            print(x);
            newLine();
            super.println();
        }
    }

    @Override
    public void println(String x) {
        synchronized (this) {
            print(x);
            newLine();
            super.println();
        }
    }

    @Override
    public void println(Object x) {
        String s = String.valueOf(x);
        synchronized (this) {
            print(s);
            newLine();
            super.println();
        }
    }
}