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

@ApiModel(value = "App post", description = "A post published by an user")
@Document(collection = "posts")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Post {

    @Id @JsonProperty("_id") @EqualsAndHashCode.Include
    private String id;

    @ApiModelProperty(name = "author", value = "The author id", required = true)
    private String author;

    @ApiModelProperty(name = "title", value = "The post title", example = "Example Post Title", required = true)
    private String title;

    @ApiModelProperty(name = "body", value = "The post body", example = "Lorem ipsum dolor...", required = true)
    private String body;

    @ApiModelProperty(name = "summary", value = "The post summary", example = "This is an example post", required = true)
    private String summary;

    @ApiModelProperty(name = "date", value = "The post publication date", example = "2018-07-25")
    private Date date;

    @ApiModelProperty(name = "tags", value = "Post tags", example = "example,tag1,tag2", required = true)
    private List<String> tags;
}
