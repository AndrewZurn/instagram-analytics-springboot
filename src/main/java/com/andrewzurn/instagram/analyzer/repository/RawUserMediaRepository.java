package com.andrewzurn.instagram.analyzer.repository;

import com.andrewzurn.instagram.analyzer.model.RawUserMedia;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by andrew on 8/12/15.
 */
public interface RawUserMediaRepository extends CrudRepository<RawUserMedia, String> {
}
