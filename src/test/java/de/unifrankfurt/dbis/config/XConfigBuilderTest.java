package de.unifrankfurt.dbis.config;

import org.junit.Test;

import static org.junit.Assert.*;

public class XConfigBuilderTest {

    @Test
    public void setDatabase() {
        XConfigBuilder cb = new XConfigBuilder();
        XConfig c = cb.createConfig();
        //checks default value
        assertTrue(c.getDatabase()==null);

        //checks setter
        cb.setDatabase("database");
        c = cb.createConfig();
        assertEquals("database",c.getDatabase());
    }

    @Test
    public void setUsername() {
        XConfigBuilder cb = new XConfigBuilder();
        XConfig c = cb.createConfig();
        //checks default value
        assertEquals("root",c.getUsername());

        //checks setter
        cb.setUsername("user");
        c = cb.createConfig();
        assertEquals("user",c.getUsername());
    }

    @Test
    public void setPassword() {
        XConfigBuilder cb = new XConfigBuilder();
        XConfig c = cb.createConfig();
        //checks default value
        assertNull(c.getPassword());

        //checks setter
        cb.setPassword("password");
        c = cb.createConfig();
        assertEquals("password",c.getPassword());
    }

    @Test
    public void setSavePassword() {
        XConfigBuilder cb = new XConfigBuilder();
        XConfig c = cb.createConfig();
        //checks default value
        assertFalse(c.getSavePassword());

        //checks setter
        cb.setSavePassword(true);
        c = cb.createConfig();
        assertTrue(c.getSavePassword());
    }

    @Test
    public void setHostname() {
        XConfigBuilder cb = new XConfigBuilder();
        XConfig c = cb.createConfig();
        //checks default value
        assertEquals("localhost",c.getHostname());

        //checks setter
        cb.setHostname("hostname");
        c = cb.createConfig();
        assertEquals("hostname",c.getHostname());
    }

    @Test
    public void setPort() {
        XConfigBuilder cb = new XConfigBuilder();
        XConfig c = cb.createConfig();
        //checks default value
        assertEquals("3306",c.getPort());

        //checks setter
        cb.setPort("1234");
        c = cb.createConfig();
        assertEquals("1234",c.getPort());

    }

    @Test
    public void setExecutable() {
        XConfigBuilder cb = new XConfigBuilder();
        XConfig c = cb.createConfig();
        //checks default value
        assertNull(c.getExecutable());

        //checks setter
        cb.setExecutable("/exe");
        c = cb.createConfig();
        assertEquals("/exe",c.getExecutable());

    }

    @Test
    public void setResetPath() {
        XConfigBuilder cb = new XConfigBuilder();
        XConfig c = cb.createConfig();
        //checks default value
        assertNull(c.getResetPath());

        //checks setter
        cb.setResetPath("/reset.sql");
        c = cb.createConfig();
        assertEquals("/reset.sql",c.getResetPath());
    }

    @Test
    public void setSolutionInPath() {
        XConfigBuilder cb = new XConfigBuilder();
        XConfig c = cb.createConfig();
        //checks default value
        assertNull(c.getSolutionInPath());

        //checks setter
        cb.setSolutionInPath("/reset.sql");
        c = cb.createConfig();
        assertEquals("/reset.sql",c.getSolutionInPath());
    }

    @Test
    public void setSolutionOutPath() {
        XConfigBuilder cb = new XConfigBuilder();
        XConfig c = cb.createConfig();
        //checks default value
        assertEquals("/solution.txt",c.getSolutionOutPath());

        //checks setter
        cb.setSolutionOutPath("/sol.txt");
        c = cb.createConfig();
        assertEquals("/sol.txt",c.getSolutionOutPath());
    }

    @Test
    public void setSolutionSamplePath() {
        XConfigBuilder cb = new XConfigBuilder();
        XConfig c = cb.createConfig();
        //checks default value
        assertEquals("/submissions/sample.sql",c.getSolutionSamplePath());

        //checks setter
        cb.setSolutionSamplePath("/sample.txt");
        c = cb.createConfig();
        assertEquals("/sample.txt",c.getSolutionSamplePath());
    }

    @Test
    public void setExecutorSubmissionPath() {
        XConfigBuilder cb = new XConfigBuilder();
        XConfig c = cb.createConfig();
        //checks default value
        assertEquals("/submissions/",c.getExecutorSubmissionPath());

        //checks setter
        cb.setExecutorSubmissionPath("/sub/");
        c = cb.createConfig();
        assertEquals("/sub/",c.getExecutorSubmissionPath());
    }

    @Test
    public void setExecutorSolutionPath() {
        XConfigBuilder cb = new XConfigBuilder();
        XConfig c = cb.createConfig();
        //checks default value
        assertEquals("/solution.txt",c.getExecutorSolutionPath());

        //checks setter
        cb.setExecutorSolutionPath("/sol.txt");
        c = cb.createConfig();
        assertEquals("/sol.txt",c.getExecutorSolutionPath());
    }

    @Test
    public void setExecutorResultPath() {
        XConfigBuilder cb = new XConfigBuilder();
        XConfig c = cb.createConfig();
        //checks default value
        assertEquals("/out/",c.getExecutorResultPath());

        //checks setter
        cb.setExecutorResultPath("/o/");
        c = cb.createConfig();
        assertEquals("/o/",c.getExecutorResultPath());

    }

    @Test
    public void setExecutorStaticEnabled() {
        XConfigBuilder cb = new XConfigBuilder();
        XConfig c = cb.createConfig();
        //checks default value
        assertFalse(c.getExecutorStaticEnabled());

        //checks setter
        cb.setExecutorStaticEnabled(true);
        c = cb.createConfig();
        assertTrue(c.getExecutorStaticEnabled());
    }

    @Test
    public void createConfig() {
        XConfigBuilder cb = new XConfigBuilder()
                .setDatabase("data")
                .setUsername("user")
                .setPassword("password")
                .setSavePassword(true)
                .setHostname("hostname")
                .setPort("1234")
                .setExecutable("exe")
                .setResetPath("reset")
                .setSolutionInPath("in")
                .setSolutionOutPath("out")
                .setSolutionSamplePath("sample")
                .setExecutorSubmissionPath("in")
                .setExecutorSolutionPath("sol")
                .setExecutorResultPath("out")
                .setExecutorStaticEnabled(true);
        XConfig c = cb.createConfig();
        assertEquals("data",c.getDatabase());
        assertEquals("user",c.getUsername());
        assertEquals("password",c.getPassword());
        assertTrue(c.getSavePassword());
        assertEquals("hostname",c.getHostname());
        assertEquals("1234",c.getPort());
        assertEquals("exe",c.getExecutable());
        assertEquals("reset",c.getResetPath());
        assertEquals("in",c.getSolutionInPath());
        assertEquals("out",c.getSolutionOutPath());
        assertEquals("sample",c.getSolutionSamplePath());
        assertEquals("in",c.getExecutorSubmissionPath());
        assertEquals("sol",c.getExecutorSolutionPath());
        assertEquals("out",c.getExecutorResultPath());
        assertTrue(c.getExecutorStaticEnabled());
    }
}