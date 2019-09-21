package me.lnsprt.projects.restblog.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@ApiModel(value = "App user", description = "An user in the app")
@Document(collection = "users")
public class User {
    @Id @JsonProperty("_id") private String id;
    @ApiModelProperty(name = "name", value = "The user name", example = "Example App User", required = true)
    private String name;
    @ApiModelProperty(name = "nickname", value = "The user nickname", example = "example_app_user-96", required = true)
    private String nickname;
    @ApiModelProperty(name = "password", value = "The user password", example = "example_p4ssw0rd", required = true)
    private String password;
    @ApiModelProperty(name = "email", value = "The user email", example = "examplemail@domain.com", required = true)
    private String email;
    @ApiModelProperty(name = "signupDate", value = "The user sign-up date", example = "2018-05-16")
    private Date signupDate;
    @ApiModelProperty(name = "roles", value = "User roles", allowableValues = "ADMIN,EDITOR,MODERATOR,READER", example = "ADMIN", required = true)
    private List<String> roles;
    @ApiModelProperty(name = "suspended", value = "Suspended flag", allowableValues = "true, false", example = "true", required = true)
    private Boolean suspended;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id != null ? id.equals(user.id) : user.id == null;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() { return password; }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getSignupDate() {
        return signupDate;
    }

    public void setSignupDate(Date signupDate) {
        this.signupDate = signupDate;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Boolean isSuspended() {
        return this.suspended;
    }

    public void setSuspended(Boolean suspended) {
        this.suspended = suspended;
    }
}
