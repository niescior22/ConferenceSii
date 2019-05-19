package com.example.demo;


import com.example.demo.validator.EmailValidator;
import com.example.demo.validator.LoginValidator;
import com.example.demo.services.ConferenceService;
import com.example.demo.services.CurrentSessionComponent;
import com.example.demo.services.UserService;
import com.example.demo.entity.Conference;
import com.example.demo.entity.User;
import com.example.demo.exceptions.EmailMissmatchException;
import com.example.demo.writer.Writer;
import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.TransactionSystemException;

import javax.validation.ConstraintViolationException;

import java.util.Optional;


@SpringUI
public class ConferenceUI extends UI {

    @Autowired
    UserService userService;

    @Autowired
    ConferenceService conferenceService;

    @Autowired
    private CurrentSessionComponent currentSessionComponent;

    Component currentSidePanel;

    @Autowired
    LoginValidator loginValidator = new LoginValidator();

    @Autowired
    EmailValidator emailValidator = new EmailValidator();

    @Autowired
    Writer writer = new Writer();






    public final static Logger log = Logger.getLogger(ConferenceUI.class);


    @Override
    protected void init(VaadinRequest vaadinRequest) {
        VerticalLayout mainLayout = new VerticalLayout();
        GridLayout gridLayout = new GridLayout(2, 2);
        setContent(mainLayout);
        mainLayout.addComponentsAndExpand(gridLayout);
        gridLayout.setRowExpandRatio(0, 0.1f);
        gridLayout.setRowExpandRatio(1, 0.9f);
        gridLayout.setColumnExpandRatio(0, 1.0f);
        gridLayout.setColumnExpandRatio(1, 1.0f);
        gridLayout.setWidth("100%");
        gridLayout.setHeight("100%");



        Label conference = new Label("Conference");
        VerticalLayout verticalLayoutTopLeft = new VerticalLayout();
        verticalLayoutTopLeft.addComponent(conference);
        gridLayout.addComponent(verticalLayoutTopLeft, 0, 0);

        FormLayout formLayout = new FormLayout();
        formLayout.setHeight(160f, Unit.PIXELS);
        verticalLayoutTopLeft.setHeight(160f, Unit.PIXELS);

        TextField textFieldLogin = new TextField("Login");
        textFieldLogin.setRequiredIndicatorVisible(true);
        textFieldLogin.setIcon(VaadinIcons.USER);
        textFieldLogin.setMaxLength(20);


        TextField textFieldEmail = new TextField("Email");
        textFieldEmail.setRequiredIndicatorVisible(true);
        textFieldEmail.setIcon(VaadinIcons.MAILBOX);
        textFieldEmail.setMaxLength(50);


        Button btnSubmit = new Button("Zaloguj");
        formLayout.setWidth(null);
        formLayout.addComponent(textFieldLogin);
        formLayout.addComponent(textFieldEmail);
        formLayout.addComponent(btnSubmit);


        gridLayout.addComponent(formLayout, 1, 0);

        FormLayout formLayoutToChangeEmail = new FormLayout();
        formLayoutToChangeEmail.setWidth(null);
        formLayoutToChangeEmail.setHeight(160f, Unit.PIXELS);
        verticalLayoutTopLeft.setHeight(160f, Unit.PIXELS);


        gridLayout.setComponentAlignment(formLayout, Alignment.TOP_RIGHT);
        Button btntochangeEmail = new Button("Zmien Email");

        TextField changeEmail = new TextField("Email");
        changeEmail.setRequiredIndicatorVisible(true);
        changeEmail.setIcon(VaadinIcons.MAILBOX);
        changeEmail.setMaxLength(50);
        formLayoutToChangeEmail.addComponent(changeEmail);
        formLayoutToChangeEmail.addComponent(btntochangeEmail);

       Binder<User> binder1 = new Binder<>();

      binder1.forField(textFieldLogin).withValidator(loginValidator).bind(User::getLogin, User::setEmail);

     binder1.forField(textFieldEmail).withValidator(emailValidator).bind(User::getEmail, User::setEmail);

        binder1.forField(changeEmail).withValidator(emailValidator).bind(User::getEmail, User::setEmail);


        Grid<Conference> conferencesGrid = new Grid<>();
        Grid<User> registeredUsersGrid = new Grid<>();
        registeredUsersGrid.addColumn(User::getLogin)
                .setCaption("Login");
        registeredUsersGrid.addColumn(User::getEmail)
                .setCaption("Email");

        btnSubmit.addClickListener(click -> {
            try {
                Optional<User> optionalUser = userService.tryToSaveUser(textFieldLogin.getValue(), textFieldEmail.getValue());
                optionalUser.ifPresent(user -> {

                    Notification.show("Uzytkownik zapisany z :" + user.getId());
                    log.info("User saved with ID:" + user.getId());

                    currentSessionComponent.setUserId(user.getId());

                    verticalLayoutTopLeft.addComponent(new Label("Witaj,  " + user.getLogin() ));
                    gridLayout.removeComponent(formLayout);
                    gridLayout.addComponent(formLayoutToChangeEmail, 1, 0);
                    gridLayout.setComponentAlignment(formLayoutToChangeEmail, Alignment.TOP_RIGHT);


                });
            } catch (EmailMissmatchException eme) {
                Notification.show("Podany login jest juz zajęty");
                log.info(" user tried to login with same login and diffrent name");

            } catch (TransactionSystemException exc) {
                Notification.show("zły format loginu lub maila");
                log.info("validation stop user from loging in");
            }
            conferencesGrid.setItems(conferenceService.getallConferences());
        });


        btntochangeEmail.addClickListener(clickEvent -> {
            User user1 = userService.getUser(currentSessionComponent.getUserId());
            String oldEmail;
            oldEmail = user1.getEmail();
            try {

                if (!changeEmail.getValue().equalsIgnoreCase(user1.getEmail())) {
                    log.info("user email changed to " + user1.getEmail());
                    user1.setEmail(changeEmail.getValue());
                    userService.updateUser(user1);
                    conferencesGrid.setItems(conferenceService.getallConferences());

                    Notification.show("Email zmieniony na : " + user1.getEmail());

                    changeEmail.clear();
                } else {
                    Notification.show("Wpisałeś aktualny email");
                }

            } catch (ConstraintViolationException exce) {
                Notification.show("zły format loginu lub maila");
                log.info("user id:"+ currentSessionComponent.getUserId()+ "triend to change email with wrong format");
                conferencesGrid.setItems(conferenceService.getallConferences());

            }
            conferencesGrid.setItems(conferenceService.getallConferences());
        });


        conferencesGrid.addColumn(Conference::getName)
                .setCaption("Konfenrencja");
        conferencesGrid.addColumn(Conference::getDate)
                .setCaption("Data");
        conferencesGrid.addColumn(Conference::getStartTime)
                .setCaption("Poczatek");
        conferencesGrid.addColumn(Conference::getEndTime)
                .setCaption("Koniec");
        conferencesGrid.addColumn(Conference::getUsers)
                .setCaption("Zapisani uczestnicy");

        conferencesGrid.addItemClickListener(itemClick -> {
            Long clickedConference = itemClick.getItem().getId();
            if (currentSidePanel != null) {
                gridLayout.removeComponent(currentSidePanel);
            }

            GridLayout conferenceGrid = new GridLayout(1, 4);
            conferenceGrid.setWidth("100%");
            conferenceGrid.setHeight("100%");
            conferenceGrid.addComponent(new Label("Konferencja: " + itemClick.getItem().getName()), 0, 0);

            Button buttonControl;
            try {
                if (conferenceService.conferenceContainsUser(clickedConference, currentSessionComponent.getUserId())) {
                    buttonControl = new Button("Anuluj rezerwacje");
                    buttonControl.addClickListener(listener -> {
                        userService.removeUserToConference(currentSessionComponent.getUserId(), clickedConference);
                        Notification.show("anulowałes rezerwacje na konference " + clickedConference);
                        log.info("user with id:"+currentSessionComponent.getUserId() + "  canceled reservation with conference id "+ clickedConference);
                        conferencesGrid.setItems(conferenceService.getallConferences());
                        registeredUsersGrid.setItems(conferenceService.getConference(clickedConference).getUsers());
                    });
                } else {
                    buttonControl = new Button("Dodaj do tej konferencji");
                    buttonControl.setEnabled(itemClick.getItem().getUsers().size() < 5 && !conferenceService.isConferenceinSameTime(currentSessionComponent.getUserId(), clickedConference));
                    buttonControl.addClickListener(listener -> {
                        userService.addUserToConference(currentSessionComponent.getUserId(), clickedConference);
                        Notification.show("Zarezerwowałes mniejsce na konferencji :" + clickedConference);
                        log.info( "user with id"+currentSessionComponent.getUserId()+" singed up to conference with Id " + clickedConference);
                        try {
                            writer.writeEmail(currentSessionComponent.getUserId(),clickedConference);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        conferencesGrid.setItems(conferenceService.getallConferences());
                        registeredUsersGrid.setItems(conferenceService.getConference(clickedConference).getUsers());

                    });
                }

                conferenceGrid.addComponent(new Label("Miejsca na konferencji: (" + itemClick.getItem().getUsers().size() + "/5)"), 0, 1);
                conferenceGrid.addComponent(buttonControl, 0, 2);

            } catch (InvalidDataAccessApiUsageException e) {
                Notification.show("zaloguj się by zapisac na prelekcje");
            }

            registeredUsersGrid.setItems(conferenceService.getConference(clickedConference).getUsers());

            conferenceGrid.addComponent(registeredUsersGrid, 0, 3);
            conferenceGrid.setWidth("100%");
            conferenceGrid.setHeight("100%");

            gridLayout.addComponent(conferenceGrid, 1, 1);

            currentSidePanel = conferenceGrid;
        });

        conferencesGrid.setItems(conferenceService.getallConferences());
        conferencesGrid.setWidth("100%");
        gridLayout.addComponent(conferencesGrid, 0, 1);
    }
}












