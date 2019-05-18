package com.example.demo.services;


import com.example.demo.entity.Conference;
import com.example.demo.entity.User;
import com.example.demo.repositories.ConferenceRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class ConferenceServiceImp implements ConferenceService {
    @Autowired
    ConferenceRepository conferenceRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public Conference saveConference(Conference conference) {
        conference = conferenceRepository.save(conference);
        return conference;

    }

    @Override
    public Conference getConference(Long id) {
        return conferenceRepository.getOne(id);
    }

    @Override
    public List<Conference> getallConferences() {
        return conferenceRepository.findAll();
    }

    @Override
    public void deleteConference(Conference conference) {
       conferenceRepository.delete(conference);
    }

    @Override
    public Conference updateConference(Conference conference) {
        return conferenceRepository.saveAndFlush(conference);
    }

    @Override
    public long countConference() {
        return conferenceRepository.count();
    }

    @Transactional
    public boolean isConferenceinSameTime(Long userId, Long conferenceId) {
        User user = userRepository.getOne(userId);
        Conference clickedConference = conferenceRepository.getOne(conferenceId);

        // todo: zastąp to zapytaniem o przynależność - niech baza danych zwraca wynik.
        for (Conference conference1 : userRepository.getOne(userId).getConferences()) {
            if ((conference1.getDate().isEqual(clickedConference.getDate())) && (conference1.getStartTime().equalsIgnoreCase(clickedConference.getStartTime()))) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    @Override
    public boolean conferenceContainsUser(Long conferenceId, Long userId) {
        User user = userRepository.getOne(userId);
        Conference conference = conferenceRepository.getOne(conferenceId);

        return conference.getUsers().contains(user);
    }
}
