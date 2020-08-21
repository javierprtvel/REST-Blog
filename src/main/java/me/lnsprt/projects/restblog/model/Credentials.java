package me.lnsprt.projects.restblog.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "Credentials", description = "The user credentials (only for login purpose)")
@Data
public class Credentials {

    @ApiModelProperty(name = "username", value = "The user name", example = "example_user", required = true)
    private String username;

    @ApiModelProperty(name = "password", value = "The user password", example = "pwd_1234", required = true)
    private String password;
}
