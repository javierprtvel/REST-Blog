package me.lnsprt.projects.restblog.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@ApiModel(value = "App post", description = "A post published by an user")
@Document(collection = "posts")
public class Post {
    @Id @JsonProperty("_id")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post post = (Post) o;

        return id != null ? id.equals(post.id) : post.id == null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() { return author; }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
