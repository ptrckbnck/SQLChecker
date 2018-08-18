package de.unifrankfurt.dbis.fd;

import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import temp.RowFix;

import java.io.*;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;

public class FDRunnerTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private PrintStream sout;
    /**
     * pipes stdout
     */
    @Before
    public void setUpStreams() {
        sout = System.out;
        System.setOut(new PrintStream(outContent));
    }

    /**
     * removes pipe
     */
    @After
    public void cleanUpStreams() {
        System.setOut(sout);
    }

    /**
     * tests if program ready correctly from stdin.
     */
    @Test
    public void main() {
        FileInputStream is = null;
        try {
            URL url = this.getClass().getResource("/testRelation.txt");
            is = new FileInputStream(new File(url.getFile()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (is == null) assertTrue(false);
        System.setIn(is);
        String[] args = {"-p","-r"};
        FDRunner.main(args);
        String ls = System.getProperty("line.separator");
        String expected = "A B C D E"+ls
        +"A -> B"+ls
        +"B -> C D"+ls
        +"E -> B"+ls;
        assertEquals(expected,outContent.toString());
    }

    /**
     * tests if program stops reading input correctly if empty line is found.
     */
    @Test
    public void mainNewLine() {
        FileInputStream is = null;
        try {
            URL url = this.getClass().getResource("/testRelationWithNewline.txt");
            is = new FileInputStream(new File(url.getFile()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (is == null) assertTrue(false);
        System.setIn(is);
        String[] args = {"-p","-r"};
        FDRunner.main(args);
        String ls = System.getProperty("line.separator");
        String expected = "A B C D E"+ls
                +"A -> B"+ls
                +"B -> C D"+ls;
        assertEquals(expected,outContent.toString());
    }

    /**
     * tests if program reads correctly from file.
     */
    @Test
    public void mainInputFile() {
        String path = this.getClass().getResource("/testRelation.txt").getPath();
        String[] args = {"-r","-i",path};
        assertEquals(0,FDRunner.main(args));
        String ls = System.getProperty("line.separator");
        String expected = "A B C D E"+ls
                +"A -> B"+ls
                +"B -> C D"+ls
                +"E -> B"+ls;
        assertEquals(expected,outContent.toString());
    }

    /**
     * tests if program creates output file instead of writing to stdout.
     */
    @Test
    public void mainOutputFile() {
        FileInputStream is = null;
        URL inUrl = this.getClass().getResource("/testRelation.txt");
        try {
            is = new FileInputStream(new File(inUrl.getFile()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (is == null) assertTrue(false);
        System.setIn(is);
        String outPath = inUrl.getFile().replace("/testRelation.txt", "/testOutput.txt");
        String[] args = {"-r","-p","-o",outPath};
        assertEquals(0,FDRunner.main(args));
        try {
            BufferedReader outBufReader = new BufferedReader(new FileReader(outPath));
            BufferedReader inBufReader = new BufferedReader(new FileReader(inUrl.getFile()));
            String in;
            String out;
            while ((in = inBufReader.readLine()) != null | (out = outBufReader.readLine()) != null ){
                assertTrue(in!=null);
                assertTrue(in.equals(out));
            }
            outBufReader.close();
            inBufReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * tests if program correctly returns json
     * Checks if FDRunner.Report created from stdout is okay.
     */
    @Test
    public void mainJsonForcedAttributes() {
        String path = this.getClass().getResource("/testRelation.txt").getPath();
        String[] args = {"-j","-i",path};
        assertEquals(0,FDRunner.main(args));
        List<String> input= Arrays.asList("A -> B", "B -> C D", "E -> B");
        Set<String> attributes = new HashSet<>(Arrays.asList("A","B","C","D","E"));
        Set<String> forcedAttributes = new HashSet<>(Arrays.asList("A","B","C","D","E"));
        Map<String, FDKeySet> relation = new HashMap<>();
        FDKeySet fdksA = new FDKeySet();
        fdksA.add(new FDKey("A"));
        FDKeySet fdksB = new FDKeySet();
        fdksB.add(new FDKey("A"));
        fdksB.add(new FDKey("B"));
        fdksB.add(new FDKey("E"));
        FDKeySet fdksC = new FDKeySet();
        fdksC.add(new FDKey("A"));
        fdksC.add(new FDKey("B"));
        fdksC.add(new FDKey("C"));
        fdksC.add(new FDKey("E"));
        FDKeySet fdksD = new FDKeySet();
        fdksD.add(new FDKey("A"));
        fdksD.add(new FDKey("B"));
        fdksD.add(new FDKey("D"));
        fdksD.add(new FDKey("E"));
        FDKeySet fdksE = new FDKeySet();
        fdksE.add(new FDKey("E"));
        relation.put("A",fdksA);
        relation.put("B",fdksB);
        relation.put("C",fdksC);
        relation.put("D",fdksD);
        relation.put("E",fdksE);
        Set<String> prim = new HashSet<>(Arrays.asList("A","E"));
        HashSet<String> notPrim = new HashSet<>(Arrays.asList("B","C","D"));
        FDKeySet keyCandidates = new FDKeySet();
        keyCandidates.add(new FDKey("A","E"));
        int normalForm = 1;

        FDRunner.Report expectedReport = new FDRunner.Report(
                input,
                attributes,
                forcedAttributes,
                relation,
                prim,
                notPrim,
                keyCandidates,
                normalForm);
        assertEquals(expectedReport,new Gson().fromJson(outContent.toString(), FDRunner.Report.class));
    }

    /**
     * tests if program correctly returns json.
     * Checks if FDRunner.Report created from stdout is okay.
     */
    @Test
    public void mainJsonNoForcedAttributes() {
        String path = this.getClass().getResource("/testRelationNoForcedAttributes.txt").getPath();
        String[] args = {"-j","-i",path};
        assertEquals(0,FDRunner.main(args));
        List<String> input= Arrays.asList("A -> B", "B -> C D", "E -> B");
        Set<String> attributes = new HashSet<>(Arrays.asList("A","B","C","D","E"));
        Set<String> forcedAttributes = null;
        Map<String, FDKeySet> relation = new HashMap<>();
        FDKeySet fdksA = new FDKeySet();
        fdksA.add(new FDKey("A"));
        FDKeySet fdksB = new FDKeySet();
        fdksB.add(new FDKey("A"));
        fdksB.add(new FDKey("B"));
        fdksB.add(new FDKey("E"));
        FDKeySet fdksC = new FDKeySet();
        fdksC.add(new FDKey("A"));
        fdksC.add(new FDKey("B"));
        fdksC.add(new FDKey("C"));
        fdksC.add(new FDKey("E"));
        FDKeySet fdksD = new FDKeySet();
        fdksD.add(new FDKey("A"));
        fdksD.add(new FDKey("B"));
        fdksD.add(new FDKey("D"));
        fdksD.add(new FDKey("E"));
        FDKeySet fdksE = new FDKeySet();
        fdksE.add(new FDKey("E"));
        relation.put("A",fdksA);
        relation.put("B",fdksB);
        relation.put("C",fdksC);
        relation.put("D",fdksD);
        relation.put("E",fdksE);
        Set<String> prim = new HashSet<>(Arrays.asList("A","E"));
        HashSet<String> notPrim = new HashSet<>(Arrays.asList("B","C","D"));
        FDKeySet keyCandidates = new FDKeySet();
        keyCandidates.add(new FDKey("A","E"));
        int normalForm = 1;

        FDRunner.Report expectedReport = new FDRunner.Report(
                input,
                attributes,
                forcedAttributes,
                relation,
                prim,
                notPrim,
                keyCandidates,
                normalForm);
        assertEquals(expectedReport,new Gson().fromJson(outContent.toString(), FDRunner.Report.class));
    }
}