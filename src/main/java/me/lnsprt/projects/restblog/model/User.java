package me.lnsprt.projects.restblog.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@ApiModel(value = "App user", description = "An user in the app")
@Document(collection = "users")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id @JsonProperty("_id") @EqualsAndHashCode.Include
    private String id;

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
    private boolean suspended;

    public User setPassword(String password) {
        this.password = password;
        return this;
    }
}
