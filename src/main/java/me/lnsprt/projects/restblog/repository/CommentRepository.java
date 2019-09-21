package me.lnsprt.projects.restblog.repository;

import me.lnsprt.projects.restblog.model.Comment;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, ObjectId> {
    void deleteById(String id);
    void deleteByPost(String post);
    boolean existsById(String id);
    Comment findById(String id);
    Page<Comment> findByUser(String user, Pageable pageRequest);
    Page<Comment> findByPost(String post, Pageable pageRequest);

    Page<Comment> findByUser(Pageable pageRequest, String user);
    Page<Comment> findByUserAndPost(Pageable pageRequest, String user, String post);
    Page<Comment> findByUserAndPostAndDate(Pageable pageRequest, String user, String post, Date date);
    Page<Comment> findByUserAndPostAndDateBefore(Pageable pageRequest, String user, String post, Date date);
    Page<Comment> findByUserAndPostAndDateAfter(Pageable pageRequest, String user, String post, Date date);
    Page<Comment> findByUserAndPostAndDateBetween(Pageable pageRequest, String user, String post, Date startDate, Date endDate);
    Page<Comment> findByUserAndPostAndDateAndTextContains(Pageable pageRequest, String user, String post, Date date, String text);
    Page<Comment> findByUserAndPostAndDateBeforeAndTextContains(Pageable pageRequest, String user, String post, Date date, String text);
    Page<Comment> findByUserAndPostAndDateAfterAndTextContains(Pageable pageRequest, String user, String post, Date date, String text);
    Page<Comment> findByUserAndPostAndDateBetweenAndTextContains(Pageable pageRequest, String user, String post, Date startDate, Date endDate, String text);
    Page<Comment> findByUserAndDate(Pageable pageRequest, String user, Date date);
    Page<Comment> findByUserAndDateBefore(Pageable pageRequest, String user, Date date);
    Page<Comment> findByUserAndDateAfter(Pageable pageRequest, String user, Date date);
    Page<Comment> findByUserAndDateBetween(Pageable pageRequest, String user, Date startDate, Date endDate);
    Page<Comment> findByUserAndDateAndTextContains(Pageable pageRequest, String user, Date date, String text);
    Page<Comment> findByUserAndDateBeforeAndTextContains(Pageable pageRequest, String user, Date date, String text);
    Page<Comment> findByUserAndDateAfterAndTextContains(Pageable pageRequest, String user, Date date, String text);
    Page<Comment> findByUserAndDateBetweenAndTextContains(Pageable pageRequest, String user, Date startDate, Date endDate, String text);
    Page<Comment> findByUserAndTextContains(Pageable pageRequest, String user, String text);

    Page<Comment> findByPost(Pageable pageRequest, String post);
    Page<Comment> findByPostAndTextContains(Pageable pageRequest, String post, String text);
    Page<Comment> findByPostAndDate(Pageable pageRequest, String post, Date date);
    Page<Comment> findByPostAndDateBefore(Pageable pageRequest, String post, Date date);
    Page<Comment> findByPostAndDateAfter(Pageable pageRequest, String post, Date date);
    Page<Comment> findByPostAndDateBetween(Pageable pageRequest, String post, Date startDate, Date endDate);
    Page<Comment> findByPostAndDateAndTextContains(Pageable pageRequest, String post, Date date, String text);
    Page<Comment> findByPostAndDateBeforeAndTextContains(Pageable pageRequest, String post, Date date, String text);
    Page<Comment> findByPostAndDateAfterAndTextContains(Pageable pageRequest, String post, Date date, String text);
    Page<Comment> findByPostAndDateBetweenAndTextContains(Pageable pageRequest, String post, Date startDate, Date endDate, String text);


    Page<Comment> findByDate(Pageable pageRequest, Date date);
    Page<Comment> findByDateBefore(Pageable pageRequest, Date date);
    Page<Comment> findByDateAfter(Pageable pageRequest, Date date);
    Page<Comment> findByDateBetween(Pageable pageRequest, Date startDate, Date endDate);
    Page<Comment> findByDateAndTextContains(Pageable pageRequest, Date date, String text);
    Page<Comment> findByDateBeforeAndTextContains(Pageable pageRequest, Date date, String text);
    Page<Comment> findByDateAfterAndTextContains(Pageable pageRequest, Date date, String text);
    Page<Comment> findByDateBetweenAndTextContains(Pageable pageRequest, Date startDate, Date endDate, String text);

    Page<Comment> findByTextContains(Pageable pageRequest, String text);
}
