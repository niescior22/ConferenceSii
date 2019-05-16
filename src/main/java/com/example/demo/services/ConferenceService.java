package com.example.demo.services;

import com.example.demo.entity.Conference;
import com.example.demo.entity.User;

import java.util.List;

public interface ConferenceService {
    Conference saveConference(Conference conference);

    Conference getConference(Long id);

    List<Conference>getallConferences();

    void deleteConference(Conference conference);

    Conference updateConference(Conference conference);

    long countConference();

    void addUserToConference(User user, Conference clickedConference);

    void removeUserToConference(User user, Conference clickedConference);
}
