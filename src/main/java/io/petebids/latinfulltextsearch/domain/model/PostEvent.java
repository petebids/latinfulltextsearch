package io.petebids.latinfulltextsearch.domain.model;

import lombok.Data;

import java.time.Instant;

@Data
public class PostEvent {

    Instant created;

    PostEventTypes type;

    Post post;

}
