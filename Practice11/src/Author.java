public class Author {

    protected String firstName;
    protected String middleName;
    protected String lastName;
    protected String nickname;
  
    public Author() {
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public String getFullName() {
        return (firstName == null ? "" : firstName + " ")
                + (middleName == null ? "" : middleName + " ")
                + (lastName == null ? "" : lastName);
    }

}