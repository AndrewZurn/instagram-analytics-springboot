package com.andrewzurn.instagram.analyzer.repository;

import com.andrewzurn.instagram.analyzer.model.RawUserMedia;
import org.springframework.data.cassandra.repository.CassandraRepository;

/**
 * Created by andrew on 8/12/15.
 */
public interface RawUserMediaRepository extends CassandraRepository<RawUserMedia> {
}
