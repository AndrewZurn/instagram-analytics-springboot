package com.andrewzurn.instagram.analyzer.repository;

import com.andrewzurn.instagram.analyzer.model.SourceUser;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by andrew on 8/10/15.
 */
public interface SourceUserRepository extends CrudRepository<SourceUser, Integer> { }
