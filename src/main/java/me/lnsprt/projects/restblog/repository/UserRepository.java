package me.lnsprt.projects.restblog.repository;

import me.lnsprt.projects.restblog.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    boolean existsById(String id);
    boolean existsByNickname(String nickname);
    void deleteById(String id);
    void deleteByNickname(String nickname);
    User findById(String id);
    User findByNickname(String nickname);
    List<User> findByRoles(String role);
    Page<User> findByRolesContains(String role, Pageable pageRequest);
    List<User> findAll();
    Page<User> findAll(Pageable pageable);

    Page<User> findByName(Pageable pageable, String name);
    Page<User> findByNameAndRolesContains(Pageable pageable, String name, String roles);
    Page<User> findByNameAndRolesContainsAndSignupDate(Pageable pageRequest, String name, String roles, Date date);
    Page<User> findByNameAndRolesContainsAndSignupDateBefore(Pageable pageRequest, String name, String roles, Date date);
    Page<User> findByNameAndRolesContainsAndSignupDateAfter(Pageable pageRequest, String name, String roles, Date date);
    Page<User> findByNameAndRolesContainsAndSignupDateBetween(Pageable pageRequest, String name, String roles, Date startDate, Date endDate);
    Page<User> findByNameAndSuspended(Pageable pageable, String name, Boolean suspended);
    Page<User> findByNameContains(Pageable pageable, String name);
    Page<User> findByNameContainsAndRolesContains(Pageable pageable, String name, String roles);
    Page<User> findByNameContainsAndRolesContainsAndSignupDate(Pageable pageRequest, String name, String roles, Date date);
    Page<User> findByNameContainsAndRolesContainsAndSignupDateBefore(Pageable pageRequest, String name, String roles, Date date);
    Page<User> findByNameContainsAndRolesContainsAndSignupDateAfter(Pageable pageRequest, String name, String roles, Date date);
    Page<User> findByNameContainsAndRolesContainsAndSignupDateBetween(Pageable pageRequest, String name, String roles, Date startDate, Date endDate);
    Page<User> findByNameContainsAndRolesContainsAndSignupDateAndSuspended(Pageable pageRequest, String name, String roles, Date date, Boolean suspended);
    Page<User> findByNameContainsAndRolesContainsAndSignupDateBeforeAndSuspended(Pageable pageRequest, String name, String roles, Date date, Boolean suspended);
    Page<User> findByNameContainsAndRolesContainsAndSignupDateAfterAndSuspended(Pageable pageRequest, String name, String roles, Date date, Boolean suspended);
    Page<User> findByNameContainsAndRolesContainsAndSignupDateBetweenAndSuspended(Pageable pageRequest, String name, String roles, Date startDate, Date endDate, Boolean suspended);
    Page<User> findByNameContainsAndSignupDate(Pageable pageRequest, String name, Date date);
    Page<User> findByNameContainsAndSignupDateBefore(Pageable pageRequest, String name, Date date);
    Page<User> findByNameContainsAndSignupDateAfter(Pageable pageRequest, String name, Date date);
    Page<User> findByNameContainsAndSignupDateBetween(Pageable pageRequest, String name, Date startDate, Date endDate);
    Page<User> findByNameContainsAndSignupDateAndSuspended(Pageable pageRequest, String name, Date date, Boolean suspended);
    Page<User> findByNameContainsAndSignupDateBeforeAndSuspended(Pageable pageRequest, String name, Date date, Boolean suspended);
    Page<User> findByNameContainsAndSignupDateAfterAndSuspended(Pageable pageRequest, String name, Date date, Boolean suspended);
    Page<User> findByNameContainsAndSignupDateBetweenAndSuspended(Pageable pageRequest, String name, Date startDate, Date endDate, Boolean suspended);
    Page<User> findByNameContainsAndSuspended(Pageable pageable, String name, Boolean suspended);

    Page<User> findByEmail(Pageable pageRequest, String email);
    Page<User> findByEmailAndSignupDate(Pageable pageRequest, String email, Date date);
    Page<User> findByEmailAndSignupDateBefore(Pageable pageRequest, String email, Date date);
    Page<User> findByEmailAndSignupDateAfter(Pageable pageRequest, String email, Date date);
    Page<User> findByEmailAndSignupDateBetween(Pageable pageRequest, String email, Date startDate, Date endDate);
    Page<User> findByEmailAndSignupDateAndSuspended(Pageable pageRequest, String email, Date date, Boolean suspended);
    Page<User> findByEmailAndSignupDateBeforeAndSuspended(Pageable pageRequest, String email, Date date, Boolean suspended);
    Page<User> findByEmailAndSignupDateAfterAndSuspended(Pageable pageRequest, String email, Date date, Boolean suspended);
    Page<User> findByEmailAndSignupDateBetweenAndSuspended(Pageable pageRequest, String email, Date startDate, Date endDate, Boolean suspended);
    Page<User> findByEmailContains(Pageable pageRequest, String email);
    Page<User> findByEmailContainsAndSignupDate(Pageable pageRequest, String email, Date date);
    Page<User> findByEmailContainsAndSignupDateBefore(Pageable pageRequest, String email, Date date);
    Page<User> findByEmailContainsAndSignupDateAfter(Pageable pageRequest, String email, Date date);
    Page<User> findByEmailContainsAndSignupDateBetween(Pageable pageRequest, String email, Date startDate, Date endDate);
    Page<User> findByEmailContainsAndSignupDateAndSuspended(Pageable pageRequest, String email, Date date, Boolean suspended);
    Page<User> findByEmailContainsAndSignupDateBeforeAndSuspended(Pageable pageRequest, String email, Date date, Boolean suspended);
    Page<User> findByEmailContainsAndSignupDateAfterAndSuspended(Pageable pageRequest, String email, Date date, Boolean suspended);
    Page<User> findByEmailContainsAndSignupDateBetweenAndSuspended(Pageable pageRequest, String email, Date startDate, Date endDate, Boolean suspended);
    Page<User> findByEmailAndSuspended(Pageable pageRequest, String email, Boolean suspended);
    Page<User> findByEmailContainsAndSuspended(Pageable pageRequest, String email, Boolean suspended);

    Page<User> findBySignupDate(Pageable pageRequest, Date date);
    Page<User> findBySignupDateBefore(Pageable pageRequest, Date date);
    Page<User> findBySignupDateAfter(Pageable pageRequest, Date date);
    Page<User> findBySignupDateBetween(Pageable pageRequest, Date startDate, Date endDate);
    Page<User> findBySignupDateAndSuspended(Pageable pageRequest, Date date, Boolean suspended);
    Page<User> findBySignupDateBeforeAndSuspended(Pageable pageRequest, Date date, Boolean suspended);
    Page<User> findBySignupDateAfterAndSuspended(Pageable pageRequest, Date date, Boolean suspended);
    Page<User> findBySignupDateBetweenAndSuspended(Pageable pageRequest, Date startDate, Date endDate, Boolean suspended);

    Page<User> findByRolesContains(Pageable pageRequest, String role);
    Page<User> findByRolesContainsAndSignupDate(Pageable pageRequest, String role, Date date);
    Page<User> findByRolesContainsAndSignupDateBefore(Pageable pageRequest, String role, Date date);
    Page<User> findByRolesContainsAndSignupDateAfter(Pageable pageRequest, String role, Date date);
    Page<User> findByRolesContainsAndSignupDateBetween(Pageable pageRequest, String role, Date startDate, Date endDate);
    Page<User> findByRolesContainsAndSignupDateAndSuspended(Pageable pageRequest, String role, Date date, Boolean suspended);
    Page<User> findByRolesContainsAndSignupDateBeforeAndSuspended(Pageable pageRequest, String role, Date date, Boolean suspended);
    Page<User> findByRolesContainsAndSignupDateAfterAndSuspended(Pageable pageRequest, String role, Date date, Boolean suspended);
    Page<User> findByRolesContainsAndSignupDateBetweenAndSuspended(Pageable pageRequest, String role, Date startDate, Date endDate, Boolean suspended);
    Page<User> findByRolesContainsAndSuspended(Pageable pageRequest, String role, Boolean suspended);

    Page<User> findBySuspended(Pageable pageRequest, Boolean suspended);
}
