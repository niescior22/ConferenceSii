package com.example.demo.services;


import com.example.demo.entity.Conference;
import com.example.demo.entity.User;
import com.example.demo.exceptions.EmailMissmatchException;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserRepository userRepository;


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
            createUser(login, email);
        }
        return Optional.empty();
    }

    @Override

    public void addUserToConference(User user, Conference clickedConference) {
        user.getConferences().add(clickedConference);
        updateUser(user);
    }

    @Override
    public void removeUserToConference(User user, Conference clickedConference) {
        user.getConferences().remove(clickedConference);
        updateUser(user);

    }

    private Optional<User> checkUserEmail(String email, Optional<User> optionalUser) {
        User user = optionalUser.get();
        if (!user.getEmail().equalsIgnoreCase(email)) {
            throw new EmailMissmatchException();
        } else {
            return Optional.of(user);
        }
    }

    private void createUser(String login, String email) {
        User user = new User(login, email);
        userRepository.save(user);
    }


}

