package io.petebids.latinfulltextsearch.domain.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.petebids.latinfulltextsearch.domain.mapper.PostMapper;
import io.petebids.latinfulltextsearch.domain.model.Post;
import io.petebids.latinfulltextsearch.domain.service.PostService;
import io.petebids.latinfulltextsearch.persistence.model.EventEntity;
import io.petebids.latinfulltextsearch.persistence.model.PostEntity;
import io.petebids.latinfulltextsearch.persistence.repository.JpaEventRepository;
import io.petebids.latinfulltextsearch.persistence.repository.JpaPostRepository;
import io.petebids.latinfulltextsearch.search.model.SearchablePost;
import io.petebids.latinfulltextsearch.search.repository.ElasticsearchPostRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.random;

@Slf4j
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    JpaPostRepository jpaPostRepository;

    @Autowired
    JpaEventRepository jpaEventRepository;

    @Autowired
    ElasticsearchPostRepository elasticsearchPostRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    ObjectMapper jackson;

    @Autowired
    PostMapper mapper;

    @SneakyThrows
    @Override
    public Post createPost(String content, String title) {
        Post post = new Post();

        post.setId(random(6, true, true));
        if (title == null) {
            post.setTitle(content.substring(0, 10));
        } else {
            post.setTitle(title);
        }

        post.setContent(content);
        EventEntity event = new EventEntity();
        event.setEventJson(jackson.writeValueAsString(post));
        event.setCreated(Instant.now());
        event.setPublished(false);
        PostEntity entity = mapper.domainToEntity(post);
        persist(entity, event);
        return post;

    }

    @SneakyThrows
    @Transactional
    void persist(PostEntity post, EventEntity event) {
        log.info("in create transaction");
        post = jpaPostRepository.save(post);
        event.setEventJson(jackson.writeValueAsString(post));
        jpaEventRepository.save(event);
        log.info("create transaction complete");
    }

    @Override
    public Post getById(String id) {
        PostEntity entity = jpaPostRepository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        return mapper.entityToDomain(entity);
    }

    @Override
    public Page<Post> getPageByPartialContent(String partialContent, Pageable pageable) {
        log.info("search invoked with {}, {}", partialContent, pageable);
        Page<SearchablePost> posts = elasticsearchPostRepository.findByContentContaining(partialContent, pageable);
        log.info("search found {} posts", posts.getSize());
        List<Post> postList = mapper.searchablesToDomains(posts.getContent());

        return new PageImpl<>(postList, posts.getPageable(), posts.getTotalElements());

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    //@Scheduled(fixedRate = 10L)
    public void publishEvents() {
        log.debug("polling for unpublished events");
        List<EventEntity> events = jpaEventRepository.findByPublishedFalse();
        if (events.isEmpty()) {
            log.debug("no events to publish");
            return;
        }
        for (EventEntity event : events) {
            log.info("publishing event {}", event);
            rabbitTemplate.convertAndSend("PostEvents", event.getEventJson());
            event.setPublished(true);
            jpaEventRepository.save(event);
            log.info("published {}", event);
        }


    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Scheduled(fixedRate = 10L)
    public void publishEvent() {
        log.debug("polling for single unpublished event");
        EventEntity event = jpaEventRepository.findFirstByPublishedIsFalse();
        if (event == null) {
            log.debug("no event");
            return;
        }
        rabbitTemplate.convertAndSend("PostEvents", event.getEventJson());
        event.setPublished(true);
        jpaEventRepository.save(event);
        log.info("published {}", event);
    }

    @SneakyThrows
    @RabbitListener(queues = "PostEvents")
    public void onMessage(String message) {
        log.info("recieved {}", message);
        Post post = jackson.readValue(message, Post.class);
        SearchablePost searchablePost = mapper.domainToSearchable(post);
        elasticsearchPostRepository.save(searchablePost);
        log.info("sent {} to elasticsearch ", searchablePost);

    }


}
