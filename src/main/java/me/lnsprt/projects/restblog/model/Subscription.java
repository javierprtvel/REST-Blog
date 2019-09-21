package me.lnsprt.projects.restblog.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@ApiModel(value = "App subscription", description = "A subscription made by an user on a tag or author")
@Document(collection = "subscriptions")
public class Subscription {
    @Id @JsonProperty("_id") private String id;
    @ApiModelProperty(name = "user", value = "The subscription user id", required = true)
    private String user;
    @ApiModelProperty(name = "type", value = "The subscription type", allowableValues = "tag,author", example = "tag", required = true)
    private String type;
    @ApiModelProperty(name = "reference", value = "The subscription target reference", example = "exampleTag", required = true)
    private String reference;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subscription subscription = (Subscription) o;

        return id != null ? id.equals(subscription.id) : subscription.id == null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() { return user; }

    public void setUser(String user) { this.user = user; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getReference() { return reference; }

    public void setReference(String reference) { this.reference = reference; }
}
