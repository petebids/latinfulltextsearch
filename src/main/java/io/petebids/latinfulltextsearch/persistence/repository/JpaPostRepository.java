package io.petebids.latinfulltextsearch.persistence.repository;

import io.petebids.latinfulltextsearch.persistence.model.PostEntity;
import org.springframework.data.repository.CrudRepository;

public interface JpaPostRepository extends CrudRepository<PostEntity, String> {
}
