package me.lnsprt.projects.restblog.repository;

import me.lnsprt.projects.restblog.model.Post;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;


public interface PostRepository extends MongoRepository<Post, ObjectId> {
    boolean existsById(String id);
    boolean existsByTitle(String title);
    void deleteById(String id);
    void deleteByTitle(String title);
    Post findById(String id);
    Post findByTitle(String title);

    Page<Post> findByAuthor(Pageable pageRequest, String author);
    Page<Post> findByAuthorAndTagsContains(Pageable pageRequest, String author, String ...tags);
    Page<Post> findByAuthorAndDate(Pageable pageRequest, String author, Date date);
    Page<Post> findByAuthorAndDateBefore(Pageable pageRequest, String author, Date date);
    Page<Post> findByAuthorAndDateAfter(Pageable pageRequest, String author, Date date);
    Page<Post> findByAuthorAndDateBetween(Pageable pageRequest, String author, Date startDate, Date endDate);
    Page<Post> findByAuthorAndDateAndTagsContains(Pageable pageRequest, String author, Date date, String ...tags);
    Page<Post> findByAuthorAndTitleContains(Pageable pageRequest, String author, String title);
    Page<Post> findByAuthorAndTitleContainsAndDate(Pageable pageRequest, String author, String title, Date date);
    Page<Post> findByAuthorAndTitleContainsAndDateBefore(Pageable pageRequest, String author, String title, Date date);
    Page<Post> findByAuthorAndTitleContainsAndDateAfter(Pageable pageRequest, String author, String title, Date date);
    Page<Post> findByAuthorAndTitleContainsAndDateBetween(Pageable pageRequest, String author, String title, Date startDate, Date endDate);

    Page<Post> findByTitleContains(Pageable pageRequest, String title);
    Page<Post> findByTitleContainsAndTagsContains(Pageable pageRequest, String title, String ...tags);
    Page<Post> findByTitleContainsAndDate(Pageable pageRequest, String title, Date date);
    Page<Post> findByTitleContainsAndDateBefore(Pageable pageRequest, String title, Date date);
    Page<Post> findByTitleContainsAndDateAfter(Pageable pageRequest, String title, Date date);
    Page<Post> findByTitleContainsAndDateBetween(Pageable pageRequest, String title, Date startDate, Date endDate);
    Page<Post> findByTitleContainsAndDateAndTagsContains(Pageable pageRequest, String title, Date date, String ...tags);
    Page<Post> findByTitleContainsAndDateBeforeAndTagsContains(Pageable pageRequest, String title, Date date, String ...tags);
    Page<Post> findByTitleContainsAndDateAfterAndTagsContains(Pageable pageRequest, String title, Date date, String ...tags);
    Page<Post> findByTitleContainsAndDateBetweenAndTagsContains(Pageable pageRequest, String title, Date startDate, Date endDate, String ...tags);

    Page<Post> findByDate(Pageable pageRequest, Date date);
    Page<Post> findByDateBefore(Pageable pageRequest, Date date);
    Page<Post> findByDateAfter(Pageable pageRequest, Date date);
    Page<Post> findByDateBetween(Pageable pageRequest, Date startDate, Date endDate);
    Page<Post> findByDateAndTagsContains(Pageable pageRequest, Date date, String ...tags);
    Page<Post> findByDateBeforeAndTagsContains(Pageable pageRequest, Date date, String ...tags);
    Page<Post> findByDateAfterAndTagsContains(Pageable pageRequest, Date date, String ...tags);
    Page<Post> findByDateBetweenAndTagsContains(Pageable pageRequest, Date startDate, Date endDate, String ...tags);

    Page<Post> findByTagsContains(Pageable pageRequest, String tags);
    Page<Post> findByTagsContains(Pageable pageRequest, String ...tags);

}
