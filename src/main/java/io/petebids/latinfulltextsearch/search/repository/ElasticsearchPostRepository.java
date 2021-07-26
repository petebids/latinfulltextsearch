package io.petebids.latinfulltextsearch.search.repository;

import io.petebids.latinfulltextsearch.search.model.SearchablePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticsearchPostRepository extends ElasticsearchRepository<SearchablePost, String> {

    Page<SearchablePost> findByContentContaining(String content, Pageable pageable);
}
