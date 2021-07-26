package io.petebids.latinfulltextsearch.domain.service;

import io.petebids.latinfulltextsearch.domain.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    Post createPost(String content, String title);

    Post getById(String id);

    Page<Post> getPageByPartialContent(String partialContent, Pageable pageable);

}
