package inf.unideb.hu.backend.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;


@Entity(name = "Person")
public class PersonEntity extends AbstractPerson implements Cloneable {

    @NotNull
    @NotEmpty
    @Column(name = "USERNAME", unique = true)
    private String username = "";

    @NotNull
    @NotEmpty
    @Column(name = "EMAIL", unique = true)
    private String email;

    @NotNull
    @Column(name = "SCORE")
    private Long score;

    @NotNull
    @Column(name = "WINDATE")
    private String creationDateTime;

    @NotEmpty
    @NotNull
    @Column(name = "PASSWORD")
    private String password;

    @NotEmpty
    @NotNull
    @Column(name = "SALT")
    private String salt;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(String creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public PersonEntity(String uname, String email, Long score, String pass, String salt) {
        this.username = uname;
        this.email = email;
        this.score = score;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        this.creationDateTime = simpleDateFormat.format(date);
        this.password = pass;
        this.salt = salt;
    }


    public PersonEntity() {
    }
}
