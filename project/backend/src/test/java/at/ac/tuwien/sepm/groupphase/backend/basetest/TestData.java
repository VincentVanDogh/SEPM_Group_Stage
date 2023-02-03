package at.ac.tuwien.sepm.groupphase.backend.basetest;

import at.ac.tuwien.sepm.groupphase.backend.type.EventType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface TestData {

    Long ID = 1L;
    String TEST_NEWS_TITLE = "Title";
    String TEST_NEWS_SUMMARY = "Summary";
    String TEST_NEWS_TEXT = "TestMessageText";
    LocalDateTime TEST_NEWS_PUBLISHED_AT =
        LocalDateTime.of(2019, 11, 13, 12, 15, 0, 0);

    String BASE_URI = "/api/v1";
    String MESSAGE_BASE_URI = BASE_URI + "/messages";
    String STAGE_PLAN_BASE_URI = BASE_URI + "/stage_plan";
    String STAGE_PLAN_GENERIC_URI = STAGE_PLAN_BASE_URI + "/generic";
    String STAGE_PLAN_TEMPLATE_URI = STAGE_PLAN_BASE_URI + "/template";

    String LOCATION_BASE_URI = BASE_URI + "/locations";
    String MERCH_ARTICLE_BASE_URI = BASE_URI + "/merch_article";
    String MERCH_PURCHASE_BASE_URI = BASE_URI + "/merch_purchase";
    String ARTIST_BASE_URI = BASE_URI + "/artists";
    String EVENT_BASE_URI = BASE_URI + "/events";
    String ACT_BASE_URI = BASE_URI + "/acts";
    String REGISTRATION_BASE_URI = BASE_URI + "/registration";
    String LOGIN_BASE_URI = BASE_URI + "/authentication";

    String USER_BASE_URI = BASE_URI + "/users";

    String PROFILE_BASE_URI = BASE_URI + "/profile";
    String TICKET_BASE_URI = BASE_URI + "/ticket";
    String TICKET_ACQUISITION_BASE_URI = BASE_URI + "/ticket_order";

    String RESET_PW_BASE_URI = BASE_URI + "/reset";

    String FIRST_NAME = "first_name";

    String LAST_NAME = "last_name";

    String EMAIL = "email@email.com";

    String PASSWORD = "password";

    String BAND_NAME = "band_name";
    String ARTIST_STAGE_NAME = "artist_stage_name";
    String STREET = "example street 123";
    String CITY = "Vienna";
    String COUNTRY = "Austria";
    int POSTAL_CODE = 1020;
    String STAGE_NAME = "stage_name";
    String VENUE_NAME = "venue name";
    String ADMIN_USER = "admin@email.com";
    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }
    };
    String DEFAULT_USER = "admin@email.com";
    List<String> USER_ROLES = new ArrayList<>() {
        {
            add("ROLE_USER");
        }
    };

    Long TEST_STANDING_SECTOR_ID = 2L;
    Integer TEST_STANDING_SECTOR_SEAT_NR = 40;

    Long TEST_SEATED_SECTOR_ID = 1L;
    Integer TEST_SEATED_SECTOR_ROWS = 10;
    Integer TEST_SEATED_SECTOR_COLUMNS = 10;

    Long FIRST_TEST_STAGE_TEMPLATE_ID = 1L;
    String FIRST_TEST_STAGE_TEMPLATE_NAME = "Basic Stage template facing South";
    Integer FIRST_TEST_STAGE_TEMPLATE_TOTAL_SEATS_NR = TEST_STANDING_SECTOR_SEAT_NR + TEST_SEATED_SECTOR_COLUMNS * TEST_SEATED_SECTOR_ROWS;

    Long SECOND_TEST_STAGE_TEMPLATE_ID = 2L;
    String SECOND_TEST_STAGE_TEMPLATE_NAME = "Test stage template";
    Integer SECOND_TEST_STAGE_TEMPLATE_TOTAL_SEATS_NR = TEST_STANDING_SECTOR_SEAT_NR * 2;

    Long FIRST_TEST_STAGE_ID = 3L;
    String FIRST_TEST_STAGE_NAME = "Test stage";

    Integer TEST_NR_TICKETS_RESERVED = 10;
    Integer TEST_NR_TICKETS_SOLD = 10;

    String TEST_VENUE_NAME = "Test venue";
    String TEST_EVENT_NAME = "Test event";
    String TEST_EVENT_DESCRIPTION = "Test description";
    EventType TEST_EVENT_TYPE = EventType.CULTURE;
    Integer TEST_EVENT_DURATION = 10;

}
