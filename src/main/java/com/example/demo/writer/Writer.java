package com.example.demo.writer;

import com.example.demo.entity.Conference;
import com.example.demo.entity.User;
import com.example.demo.repositories.ConferenceRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;

import java.util.Date;

/**
 * Writer class creates file "Powiadomienia" if it dont exists or append it
 * its used when User singed up to conference
 * its imitating sending an email
 */

@Component
public class Writer {
    @Autowired
    ConferenceRepository conferenceRepository;

    @Autowired
    UserRepository userRepository;


    public void writeEmail(Long userId,Long conferenceId ) throws Exception {
        FileWriter fw = new FileWriter("powiadomienia.txt",true);
        BufferedWriter bufferedWriter = new BufferedWriter(fw);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String format = formatter.format(date);


        User user = userRepository.getOne(userId);
        Conference conference = conferenceRepository.getOne(conferenceId);
        bufferedWriter.write("<==========================================================>");
        bufferedWriter.newLine();
        bufferedWriter.write(format);
        bufferedWriter.newLine();
        bufferedWriter.write(user.getEmail());
        bufferedWriter.newLine();
        bufferedWriter.write("Dziękuemy "+user.getLogin()+" za zapisanie się na konferencje "+ conference.getName());
        bufferedWriter.newLine();
        bufferedWriter.close();

    }
}
