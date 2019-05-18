package com.example.demo.services;


import com.example.demo.entity.Conference;
import com.example.demo.entity.User;
import com.example.demo.exceptions.EmailMissmatchException;
import com.example.demo.repositories.ConferenceRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConferenceRepository conferenceRepository;

    @Override
    public User saveUser(User user) {
        User save = userRepository.save(user);
        return save;
    }

    @Override
    public User getUser(Long id) {
        return userRepository.getOne(id);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);

    }

    @Override
    public User updateUser(User user) {
        return userRepository.saveAndFlush(user);
    }

    @Override
    public long countUser() {
        return userRepository.count();
    }

    @Override
    public Optional<User> tryToSaveUser(String login, String email) {
        Optional<User> optionalUser = userRepository.findByLogin(login);
        if (optionalUser.isPresent()) {
            return checkUserEmail(email, optionalUser);
        } else {
            return Optional.of(createUser(login, email));
        }
    }

    @Transactional
    @Override
    public void addUserToConference(Long userId, Long conferenceId) {
        User user = userRepository.getOne(userId);
        Conference conference = conferenceRepository.getOne(conferenceId);

        user.getConferences().add(conference);
        conference.getUsers().add(user);

        conferenceRepository.save(conference);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void removeUserToConference(Long userId, Long conferenceId) {
        User user = userRepository.getOne(userId);
        Conference conference = conferenceRepository.getOne(conferenceId);

        user.getConferences().remove(conference);
        conference.getUsers().remove(user);

        conferenceRepository.save(conference);
        userRepository.save(user);
    }

    private Optional<User> checkUserEmail(String email, Optional<User> optionalUser) {
        User user = optionalUser.get();
        if (!user.getEmail().equalsIgnoreCase(email)) {
            throw new EmailMissmatchException();
        } else {
            return Optional.of(user);
        }
    }

    private User createUser(String login, String email) {
        User user = new User(login, email, new HashSet<>());
        return userRepository.save(user);
    }


}

