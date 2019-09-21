package me.lnsprt.projects.restblog.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

@ApiModel(value = "Credentials", description = "The user credentials (only for login purpose)")
public class Credentials {
    @ApiModelProperty(name = "username", value = "The user name", example = "example_user", required = true)
    private String username;
    @ApiModelProperty(name = "password", value = "The user password", example = "pwd_1234", required = true)
    private String password;

    public Credentials() {

    }

    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Credentials that = (Credentials) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }
}
