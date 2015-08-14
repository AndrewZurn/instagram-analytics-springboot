package com.andrewzurn.instagram.analyzer.repository;

import com.andrewzurn.instagram.analyzer.model.SourceUser;
import org.springframework.data.cassandra.repository.CassandraRepository;

/**
 * Created by andrew on 8/10/15.
 */
public interface SourceUserRepository extends CassandraRepository<SourceUser> {
}
