package de.unifrankfurt.dbis.Inner;

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * This class represents a student with a name, email address and a registration number. Every attribute is optional so
 * that it can be set without any restriction.
 */
public class Student {

    /**
     * The first and last name of the student
     */
    private final String name;

    /**
     * The email address of the student
     */
    private final String emailAddress;

    /**
     * The matriculation number of the student or sometimes called the students registration number
     */
    private final String matriculationNumber;


    /**
     * Initialize the class for a specific student
     *
     * @param name                of the student
     * @param emailAddress        of the student
     * @param matriculationNumber of the student
     */
    public Student(String name, String emailAddress, String matriculationNumber) {
        this.name = name;
        this.emailAddress = emailAddress;
        this.matriculationNumber = matriculationNumber;
    }

    /**
     * Get the matriculation / registration number of the student
     *
     * @return the matriculation number of this student
     */
    public String getMatriculationNumber() {
        return matriculationNumber;
    }


    /**
     * Get the email address of the student
     *
     * @return a String
     */
    public String getEmailAddress() {
        return emailAddress;
    }


    /**
     * Get the name which was set for the student
     *
     * @return the name of the student
     */
    public String getName() {
        return name;
    }

    /**
     * This functions checks if name satisfies requirements.
     * So far the only requirement is not to be empty.
     *
     * @return true if name fulfils requirements.
     */
    public boolean checkName() {
        return !this.name.isEmpty() && !this.name.contains(";");
    }

    /**
     * This functions checks if MatriculationNumber satisfies requirements.
     * MatriculationNumber should be numeric and not empty
     *
     * @return true if MatriculationNumber fulfils requirements.
     */
    public boolean checkMatriculationNumber() {
        return (StringUtils.isNumeric(this.matriculationNumber)
                && !this.matriculationNumber.isEmpty());
    }

    /**
     * checks if given String is a valid Email String.
     *
     * @param email String to get checked.
     * @return true if valid.
     */
    private boolean validEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        return email != null && pat.matcher(email).matches();
    }


    /**
     * This functions checks if Email is vaild.
     *
     * @return true if email is valid.
     */
    public boolean checkEmail() {
        return !this.emailAddress.isEmpty() && validEmail(this.emailAddress);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(name, student.name) &&
                Objects.equals(emailAddress, student.emailAddress) &&
                Objects.equals(matriculationNumber, student.matriculationNumber);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, emailAddress, matriculationNumber);
    }

    @Override
    public String toString() {
        return name + ' '
                + emailAddress + ' '
                + matriculationNumber;
    }

    public String serialize() {
        return name + ";" + emailAddress + ";" + matriculationNumber;
    }

    public static Student parse(List<String> strings) {
        if (strings.size() < 3) return null;
        String name = strings.get(0);
        String emailAddress = strings.get(1);
        String matriculationNumber = strings.get(2);
        return new Student(name, emailAddress, matriculationNumber);
    }

    public List<String> toStringList() {
        return List.of(this.name, this.emailAddress, this.matriculationNumber);
    }
}

