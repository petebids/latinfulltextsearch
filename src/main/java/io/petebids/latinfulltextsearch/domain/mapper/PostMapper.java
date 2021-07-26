package io.petebids.latinfulltextsearch.domain.mapper;


import io.petebids.latinfulltextsearch.domain.model.Post;
import io.petebids.latinfulltextsearch.persistence.model.PostEntity;
import io.petebids.latinfulltextsearch.search.model.SearchablePost;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    SearchablePost domainToSearchable(Post post);

    PostEntity domainToEntity(Post post);

    Post searchableToDomain(SearchablePost searchablePost);

    Post entityToDomain(PostEntity postEntity);

    List<Post> searchablesToDomains(List<SearchablePost> searchablePosts);

    List<Post> entitiesToDomain(List<PostEntity> postEntities);

}
