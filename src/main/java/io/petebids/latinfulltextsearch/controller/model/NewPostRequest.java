package io.petebids.latinfulltextsearch.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NewPostRequest {

    @JsonProperty("content")
    String content;

    String title;

}
