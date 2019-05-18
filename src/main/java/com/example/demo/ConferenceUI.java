package com.example.demo;


import com.example.demo.Validator.EmailValidator;
import com.example.demo.Validator.LoginValidator;
import com.example.demo.services.ConferenceService;
import com.example.demo.services.CurrentSessionComponent;
import com.example.demo.services.UserService;
import com.example.demo.entity.Conference;
import com.example.demo.entity.User;
import com.example.demo.exceptions.EmailMissmatchException;
import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;
import org.hibernate.internal.ExceptionMapperStandardImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionSystemException;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
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



        Button btnSubmit = new Button("Save");
        formLayout.setWidth(null);
        formLayout.addComponent(textFieldLogin);
        formLayout.addComponent(textFieldEmail);
        formLayout.addComponent(btnSubmit);


        gridLayout.addComponent(formLayout, 1, 0);

        FormLayout formLayoutToChangeEmail = new FormLayout();
        formLayoutToChangeEmail.setHeight(160f, Unit.PIXELS);
        verticalLayoutTopLeft.setHeight(160f, Unit.PIXELS);



        gridLayout.setComponentAlignment(formLayout, Alignment.TOP_RIGHT);
        Button btntochangeEmail = new Button("Change email");

        TextField changeEmail = new TextField("Email");
        changeEmail.setRequiredIndicatorVisible(true);
        changeEmail.setIcon(VaadinIcons.MAILBOX);
        changeEmail.setMaxLength(50);
        formLayoutToChangeEmail.addComponent(changeEmail);
        formLayoutToChangeEmail.addComponent(btntochangeEmail);

       Binder<User> binder1 = new Binder<>();
        binder1.setBean(currentSessionComponent.getUser());
        binder1.forField(textFieldLogin).withValidator(loginValidator).bind(User::getLogin,User::setEmail);

        binder1.forField(textFieldEmail).withValidator(emailValidator).bind(User::getEmail,User::setEmail);

        binder1.forField(changeEmail).withValidator(emailValidator).bind(User::getEmail,User::setEmail);









        btnSubmit.addClickListener(click -> {
            try {
                Optional<User> optionalUser = userService.tryToSaveUser(textFieldLogin.getValue(), textFieldEmail.getValue());
                optionalUser.ifPresent(user -> {

                            Notification.show("User saved with ID:" + user.getId());
                            log.info("User saved with ID:" + user.getId());
                            textFieldLogin.setVisible(false);
                            currentSessionComponent.setUser(user);
                            textFieldEmail.clear();
                            verticalLayoutTopLeft.addComponent(new Label("Welcome " + user.getLogin() + " fell free to sing up to as many prelessons as you want, unless they collide in time"));
                            gridLayout.removeComponent(formLayout);
                            gridLayout.addComponent(formLayoutToChangeEmail, 1, 0);
                            gridLayout.setComponentAlignment(formLayoutToChangeEmail, Alignment.TOP_RIGHT);


                });
            } catch (EmailMissmatchException eme) {
                Notification.show("Podany login jest juz zajęty");

            } catch (TransactionSystemException exc ){
                Notification.show("zły format loginu lub maila");
          }

        });


        btntochangeEmail.addClickListener(clickEvent -> {
            try {
                User user1 = currentSessionComponent.getUser();


                if(!changeEmail.getValue().equalsIgnoreCase(user1.getEmail())) {
                    log.info("user email changed to " + user1.getEmail());
                    user1.setEmail(changeEmail.getValue());
                    userService.updateUser(user1);
                    Notification.show("Email changed sucesfully " + user1.getEmail());

                    changeEmail.clear();
                }
                else {
                    Notification.show("you writed the same email");
                }

            }catch (ConstraintViolationException exce){
                Notification.show("wrong email or login format");
            }

        });

        Grid<Conference> grid = new Grid<>();

        grid.addColumn(Conference::getName)
                .setCaption("Conference");
        grid.addColumn(Conference::getDate)
                .setCaption("Date");
        grid.addColumn(Conference::getStartTime)
                .setCaption("Start");
        grid.addColumn(Conference::getEndTime)
                .setCaption("End");
        grid.addColumn(Conference::getUsers)
                .setCaption("size");

        grid.addItemClickListener(itemClick -> {
            Conference clickedConference = itemClick.getItem();
            if (currentSidePanel != null) {
                gridLayout.removeComponent(currentSidePanel);
            }

            GridLayout conferenceGrid = new GridLayout(1, 4);
            conferenceGrid.setWidth("100%");
            conferenceGrid.setHeight("100%");
            conferenceGrid.addComponent(new Label("Konferencja: " + itemClick.getItem().getName()), 0, 0);

            Button buttonControl;
            try {


                if (clickedConference.getUsers().contains(currentSessionComponent.getUser())) {
                    buttonControl = new Button("Usun mnie z konfy");
                    buttonControl.addClickListener(listener -> {
                        User user = currentSessionComponent.getUser();
                        userService.removeUserToConference(user, clickedConference);
                        conferenceService.removeUserToConference(user, clickedConference);
                        Notification.show("anulowałes rezerwacje na konference " + clickedConference.getName());
                    });
                } else {
                    User user = currentSessionComponent.getUser();
                    buttonControl = new Button("Dodaj do tej konferencji");
                    buttonControl.setEnabled(clickedConference.getUsers().size() < 5 && !isConferenceinSameTime(user, clickedConference));
                    buttonControl.addClickListener(listener -> {
                        // User user = currentSessionComponent.getUser();
                        userService.addUserToConference(user, clickedConference);
                        conferenceService.addUserToConference(user, clickedConference);
                        Notification.show("Zarezerwowałes mniejsce na konferencji :" + clickedConference.getName());
                    });
                }

            conferenceGrid.addComponent(new Label("Zajętość konferencji: (" + clickedConference.getUsers().size() + "/5)"), 0, 1);
            conferenceGrid.addComponent(buttonControl, 0, 2);

        } catch (NullPointerException e){
            Notification.show("zaloguj się by zapisac na prelekcje");
        }
            Grid<User> registeredUsersGrid = new Grid<>();

            registeredUsersGrid.addColumn(User::getLogin)
                    .setCaption("Login");
            registeredUsersGrid.addColumn(User::getEmail)
                    .setCaption("Email");
            registeredUsersGrid.setItems(clickedConference.getUsers());

            conferenceGrid.addComponent(registeredUsersGrid, 0, 3);
            conferenceGrid.setWidth("100%");
            conferenceGrid.setHeight("100%");

            gridLayout.addComponent(conferenceGrid, 1, 1);

            currentSidePanel = conferenceGrid;
        });

        grid.setItems(conferenceService.getallConferences());
        grid.setWidth("100%");
        gridLayout.addComponent(grid, 0, 1);
    }

    private boolean isConferenceinSameTime(User user, Conference clickedConference) {

        for (Conference conference1 : user.getConferences()) {
            if ((conference1.getDate().isEqual(clickedConference.getDate())) && (conference1.getStartTime().equalsIgnoreCase(clickedConference.getStartTime()))) {
                return true;
            }
        }
            return false;

        }
    }












