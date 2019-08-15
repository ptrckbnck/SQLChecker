package de.unifrankfurt.dbis;


import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestResources {


    private static Path resourcePath(String path){
        try {
            URI p = TestResources.class.getResource(path).toURI();
            return Paths.get(p);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Path getSampleConfigPath() {
        return resourcePath("/test/config/sampleConfig.txt");
    }

    public static String getSampleConfigString() {
        return "#SQLChecker Configuration File (version: 1.0)\n" +
                "\n" +
                "##### MySQL database Settings #####\n" +
                "#Server name or ip-address (default: localhost)\n" +
                "db.host=192.168.1.1\n" +
                "#Server port (default: 3306)\n" +
                "db.port=1234\n" +
                "#username for authentication (default: root)\n" +
                "db.username=username\n" +
                "#password for authentication\n" +
                "db.password=password\n" +
                "#save password (default: false)\n" +
                "db.savepassword=true\n" +
                "#database name\n" +
                "db.databasename=database\n" +
                "#path to mysql executables\n" +
                "db.executable=exe\n" +
                "\n" +
                "#### Survey Settings #####\n" +
                "#path of the reset script that should be used\n" +
                "db.resetscript=test_uebung_reset.sql\n" +
                "\n" +
                "##### DBFitSolution generator settings #####\n" +
                "#The raw solution file, from which the dbfit solution file should be created\n" +
                "test.infile=raw.sql\n" +
                "#The path at which the output solution file should reside (default: /solution.txt)\n" +
                "test.outfile=solution.txt\n" +
                "#The path at which the sample submission file should reside\n" +
                "# This file shows, how a correct submission might look (default: /submissions/sample.sql)\n" +
                "test.samplefile=sample.sql\n" +
                "\n" +
                "##### Inner executor settings #####\n" +
                "#Folder of the submissions which should be checked (default: /submissions/)\n" +
                "chk.submissions=/subs/\n" +
                "#The path of the dbfit solution file, which should be checked (default: /solution.txt)\n" +
                "chk.solution=solution.txt\n" +
                "#Output path of the log- and result-files (default: /out/)\n" +
                "chk.outpath=/result/\n" +
                "#True, if static queries should be allowed in Student submissions\n" +
                "#These queries do not belong to a specific placeholder in the solution file. They get\n" +
                "executed independently (default: false)\n" +
                "chk.allowstatic=true\n";
    }




}