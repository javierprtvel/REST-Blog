package me.lnsprt.projects.restblog.repository;

import me.lnsprt.projects.restblog.model.Subscription;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface SubscriptionRepository extends MongoRepository<Subscription, ObjectId> {
    boolean existsById(String id);
    Subscription findById(String id);
    Page<Subscription> findByUser(String user, Pageable pageRequest);
    void deleteById(String id);
    void deleteByUser(String user);

    Page<Subscription> findByUser(Pageable pageRequest, String user);
    Page<Subscription> findByUserAndType(Pageable pageRequest, String user, String type);
    Page<Subscription> findByUserAndTypeAndReference(Pageable pageRequest, String user, String type, String reference);
    Page<Subscription> findByUserAndReference(Pageable pageRequest, String user, String reference);

    Page<Subscription> findByType(Pageable pageRequest, String type);
    Page<Subscription> findByTypeAndReference(Pageable pageRequest, String type, String reference);

    Page<Subscription> findByReference(Pageable pageRequest, String reference);
}
