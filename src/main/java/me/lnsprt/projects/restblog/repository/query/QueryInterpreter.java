package me.lnsprt.projects.restblog.repository.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface QueryInterpreter<T extends Object> {
    Page<T> executeQuery(String query, Pageable pageRequest) throws Exception;
    Collection<T> executeQuery(String query) throws Exception;
}
