package me.lnsprt.projects.restblog.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@ApiModel(value = "App comment", description = "A comment published by an user in a post")
@Document(collection = "comments")
public class Comment {
    @Id @JsonProperty("_id") private String id;
    @ApiModelProperty(name = "post", value = "The comment post id", required = true)
    private String post;
    @ApiModelProperty(name = "user", value = "The comment user id", required = true)
    private String user;
    @ApiModelProperty(name = "text", value = "The comment text content", example = "This is an example comment.", required = true)
    private String text;
    @ApiModelProperty(name = "date", value = "The comment publication date", example = "2018-11-11")
    private Date date;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        return id != null ? id.equals(comment.id) : comment.id == null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPost() { return post; }

    public void setPost(String post) { this.post = post; }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
