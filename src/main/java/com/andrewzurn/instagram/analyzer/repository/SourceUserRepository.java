package com.andrewzurn.instagram.analyzer.repository;

import com.andrewzurn.instagram.analyzer.model.SourceUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;

/**
 * Created by andrew on 8/10/15.
 */
public interface SourceUserRepository extends CrudRepository<SourceUser, Integer> {

  @Query("select * from source_user_entity where has_been_follows_traversed = ?0")
  List<SourceUser> findByHasBeenFollowsTraversed(boolean hasBeenTraversed);

  @Query("select * from source_user_entity where has_been_follows_traversed = false limit 1")
  SourceUser findSingleUntraversedFollowsUser();
}
