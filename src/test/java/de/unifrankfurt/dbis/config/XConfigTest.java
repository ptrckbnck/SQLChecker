package de.unifrankfurt.dbis.config;

import de.unifrankfurt.dbis.TestResources;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class XConfigTest {




    @Test
    public void serialize() {
        assertEquals(TestResources.getSampleConfig().serialString(),
                TestResources.getSampleConfigString());

    }

    @Test
    public void deserializeString() {
        assertEquals(TestResources.getSampleConfig(),
                XConfig.deserialize(TestResources.getSampleConfigString()));
    }

    @Test
    public void loadFromDisk() throws IOException {
        XConfig c = XConfig.fromPath(TestResources.getSampleConfigPath());
        assertEquals(TestResources.getSampleConfig(),c);
    }

    @Test
    public void saveOnDisk() throws IOException {
        XConfig c = TestResources.getSampleConfig();
        Path path = Files.createTempFile("config","");
        c.storeInPath(path);
        XConfig c2 = XConfig.fromPath(path);
        assertEquals(c,c2);
    }
}