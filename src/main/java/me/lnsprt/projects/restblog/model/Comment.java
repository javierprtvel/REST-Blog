package me.lnsprt.projects.restblog.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@ApiModel(value = "App comment", description = "A comment published by an user in a post")
@Document(collection = "comments")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Comment {

    @Id @JsonProperty("_id") @EqualsAndHashCode.Include
    private String id;

    @ApiModelProperty(name = "post", value = "The comment post id", required = true)
    private String post;

    @ApiModelProperty(name = "user", value = "The comment user id", required = true)
    private String user;

    @ApiModelProperty(name = "text", value = "The comment text content", example = "This is an example comment.", required = true)
    private String text;

    @ApiModelProperty(name = "date", value = "The comment publication date", example = "2018-11-11")
    private Date date;
}
