package com.example.demo.bootstrap;


import com.example.demo.entity.Conference;
import com.example.demo.entity.User;

import com.example.demo.services.ConferenceService;
import com.example.demo.services.UserService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * class used to initialize data to database.
 * Saves hard coded Conferences using Conference Service
 */

@Component
public class DevBootstrap implements ApplicationListener<ContextRefreshedEvent> {

     private UserService userService;

     private ConferenceService conferenceService;


    public DevBootstrap(UserService userService, ConferenceService conferenceService) {
        this.userService = userService;
        this.conferenceService = conferenceService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        initData();

    }

    private void initData() {

        Conference dockerConference= new Conference("Docker", LocalDate.of(2019,06,01),"10:00","11:45");
        Conference cloudConference = new Conference("Cloud",LocalDate.of(2019,06,01),"10:00","11:45");
        Conference amazonWebServicesConference = new Conference("amazonWebServices",LocalDate.of(2019,06,01),"10:00","11:45");
        Conference hrITConference= new Conference("HrIT", LocalDate.of(2019,06,01),"12:00","13:45");
        Conference vaadinConference = new Conference("vaadin",LocalDate.of(2019,06,01),"12:00","13:45");
        Conference cleanCodeConference = new Conference("CleanCode",LocalDate.of(2019,06,01),"12:00","13:45");

        Conference java11Conference= new Conference("java11", LocalDate.of(2019,06,02),"10:00","11:45");
        Conference swaggerConference = new Conference("Swagger",LocalDate.of(2019,06,02),"10:00","11:45");
        Conference javaEEConference= new Conference("javaEE", LocalDate.of(2019,06,02),"10:00","11:45");
        Conference webFluxConference = new Conference("webFlux",LocalDate.of(2019,06,02),"12:00","13:45");
        Conference thymeleafConference = new Conference("thymeleaf",LocalDate.of(2019,06,02),"12:00","13:45");
        Conference howToNotGetAJobConference = new Conference("howToNotGetAJob",LocalDate.of(2019,06,02),"12:00","13:45");


      //  User user = new User("kamil","kamil@gmail.com");

        //user.getConferences().add(java11Conference);
        //java11Conference.getUsers().add(user);
       // user.getConferences().add(java11Conference);
       // userService.saveUser(user);
        conferenceService.saveConference(dockerConference);
        conferenceService.saveConference(cloudConference);
        conferenceService.saveConference(amazonWebServicesConference);
        conferenceService.saveConference(hrITConference);
        conferenceService.saveConference(vaadinConference);
        conferenceService.saveConference(cleanCodeConference);
        conferenceService.saveConference(java11Conference);
        conferenceService.saveConference(swaggerConference);
        conferenceService.saveConference(javaEEConference);
        conferenceService.saveConference(webFluxConference);
        conferenceService.saveConference(thymeleafConference);
        conferenceService.saveConference(howToNotGetAJobConference);

    }
}
