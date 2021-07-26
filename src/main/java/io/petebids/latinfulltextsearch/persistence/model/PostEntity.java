package io.petebids.latinfulltextsearch.persistence.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.time.Instant;

@EntityListeners(AuditingEntityListener.class)
@Data
@Entity
public class PostEntity {


    @Id
    String id;


    @Column(name = "last_updated", nullable = false)
    @LastModifiedDate
    Instant lastUpdated;

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    Instant created;


    String title;

    String content;
}
