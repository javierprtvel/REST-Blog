package me.lnsprt.projects.restblog.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@ApiModel(value = "App subscription", description = "A subscription made by an user on a tag or author")
@Document(collection = "subscriptions")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Subscription {

    @Id @JsonProperty("_id") @EqualsAndHashCode.Include
    private String id;

    @ApiModelProperty(name = "user", value = "The subscription user id", required = true)
    private String user;

    @ApiModelProperty(name = "type", value = "The subscription type", allowableValues = "tag,author", example = "tag", required = true)
    private String type;

    @ApiModelProperty(name = "reference", value = "The subscription target reference", example = "exampleTag", required = true)
    private String reference;
}
