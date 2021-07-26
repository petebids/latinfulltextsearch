package io.petebids.latinfulltextsearch.domain.model;

import lombok.Data;

import java.time.Instant;


@Data
public class Post {

    String id;

    String title;

    String content;

    Instant created;

    Instant lastUpdated;

}
