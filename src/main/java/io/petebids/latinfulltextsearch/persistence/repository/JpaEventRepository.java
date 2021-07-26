package io.petebids.latinfulltextsearch.persistence.repository;


import io.petebids.latinfulltextsearch.persistence.model.EventEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JpaEventRepository extends CrudRepository<EventEntity, Long> {
    List<EventEntity> findByPublishedFalse();


    EventEntity findFirstByPublishedIsFalse();

}
