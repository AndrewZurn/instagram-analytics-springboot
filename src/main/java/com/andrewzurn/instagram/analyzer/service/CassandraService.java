package com.andrewzurn.instagram.analyzer.service;

import com.andrewzurn.instagram.analyzer.model.RawUserMedia;
import com.andrewzurn.instagram.analyzer.model.SourceUser;
import com.andrewzurn.instagram.analyzer.repository.RawUserMediaRepository;
import com.andrewzurn.instagram.analyzer.repository.SourceUserRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by andrew on 8/10/15.
 */
@Service
public class CassandraService {

  @Inject
  private SourceUserRepository sourceUserRepository;

  @Inject
  private RawUserMediaRepository rawUserMediaRepository;

  public Iterable<SourceUser> getUsers() {
    return this.sourceUserRepository.findAll();
  }

  public SourceUser findByUserId(int userId) {
    return this.sourceUserRepository.findOne(userId);
  }

  public List<SourceUser> findByTraversedUserFollows(boolean hasBeenTraversed) {
    return this.sourceUserRepository.findByHasBeenFollowsTraversed(hasBeenTraversed);
  }

  public SourceUser findSingleUntraversedFollowsUser() {
    return this.sourceUserRepository.findSingleUntraversedFollowsUser();
  }

  public boolean userExists(int userId) {
    return this.sourceUserRepository.exists(userId);
  }

  public SourceUser saveUser(SourceUser user) {
    return this.sourceUserRepository.save(user);
  }

  public RawUserMedia insertRawUserMedia(RawUserMedia rawUserMedia) {
    return this.rawUserMediaRepository.save(rawUserMedia);
  }

}
