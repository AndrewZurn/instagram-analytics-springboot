package com.andrewzurn.instagram.analyzer.service;

import com.andrewzurn.instagram.analyzer.model.RawUserMedia;
import com.andrewzurn.instagram.analyzer.model.SourceUser;
import com.andrewzurn.instagram.analyzer.repository.RawUserMediaRepository;
import com.andrewzurn.instagram.analyzer.repository.SourceUserRepository;
import org.springframework.data.cassandra.repository.support.BasicMapId;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

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
    return sourceUserRepository.findAll();
  }

  public SourceUser getUserById(int userId) {
    return sourceUserRepository.findOne(BasicMapId.id("user_id", userId));
  }

  public SourceUser insertUser(SourceUser user) {
    return this.sourceUserRepository.save(user);
  }

  public RawUserMedia insertRawUserMedia(RawUserMedia rawUserMedia) {
    return this.rawUserMediaRepository.save(rawUserMedia);
  }

}
