package com.example.demo.entity;


import javax.persistence.*;
import javax.validation.constraints.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @Size(min = 5, max = 50)
    @NotEmpty
    private String login;

    @Email
    @NotEmpty
    private String email;

    @ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
    private Set<Conference> conferences ;

    public User() {
    }

    public User(String login, String email, Set<Conference> conferences) {
        this.login = login;
        this.email = email;
        this.conferences = conferences;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Conference> getConferences() {
        return conferences;
    }

    public void setConferences(Set<Conference> conferences) {
        this.conferences = conferences;
    }

    @Override
    public String toString() {
        return login;
    }

}

