package io.petebids.latinfulltextsearch.search.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "blog")
public class SearchablePost {


    @Id
    private String id;
    private String title;
    private String content;
}
