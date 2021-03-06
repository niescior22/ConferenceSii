package com.example.demo.entity;


import javax.persistence.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 *  Entity Conference class
 *  Many to many owning site relationship with User
 */

@Entity
public class Conference {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private LocalDate date;

    private String startTime;

    private String endTime;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<User> users;


    public Conference() {
    }

    public Conference(String name, LocalDate date, String startTime, String endTime) {
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Conference(String name, LocalDate date, String startTime, String endTime, Set<User> users) {
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.users = users;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
