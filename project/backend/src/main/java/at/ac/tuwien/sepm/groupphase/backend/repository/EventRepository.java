package at.ac.tuwien.sepm.groupphase.backend.repository;


import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.type.EventType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface EventRepository extends JpaRepository<Event, Long> {


    /**
     * Find events matching search parameters that do not include a category.
     *
     * @param search the search input which can match the name of artist, venue or description
     * @param dateFrom the earliest date
     * @param dateTo the latest date
     * @param location the search input which can match postalcode, city or country
     * @param pageRequest the size and id of the current page
     *
     * @return list of events matching search parameters that do not include a category
     */
    @Query(value = "SELECT DISTINCT e FROM Event e"
        + " JOIN Location l ON e.location.id = l.id"
        + " JOIN act a ON e.id = a.event.id"
        + " LEFT OUTER JOIN e.featuredArtists AS f"
        + " WHERE (UPPER(e.description) LIKE %?1%"
        + " OR UPPER(e.name) like %?1%"
        + " OR UPPER(l.venueName) like %?1%"
        + " OR UPPER(f.firstName) like %?1%"
        + " OR UPPER(f.lastName) like %?1%"
        + " OR UPPER(f.stageName) like %?1%"
        + " OR UPPER(f.bandName) like %?1%)"
        + " AND a.start BETWEEN ?2 AND ?3"
        + " AND (UPPER(l.address.city) like %?4%"
        + " OR UPPER(l.address.country) like %?4%"
        + " OR UPPER(l.address.postalCode) like %?4%)")
    List<Event> findBySearchWithoutCategory(String search, LocalDateTime dateFrom, LocalDateTime dateTo, String location, PageRequest pageRequest);

    /**
     * Find events matching search parameters that do include a category.
     *
     * @param search the search input which can match the name of artist, venue or description
     * @param category the type of event
     * @param dateFrom the earliest date
     * @param dateTo the latest date
     * @param location the search input which can match postalcode, city or country
     * @param pageRequest the size and id of the current page
     *
     * @return list of events matching search parameters that do include a category
     */
    @Query(value = "SELECT DISTINCT e FROM Event e"
        + " JOIN Location l ON e.location.id = l.id"
        + " JOIN act a ON e.id = a.event.id"
        + " LEFT OUTER JOIN e.featuredArtists AS f"
        + " WHERE (UPPER(e.description) LIKE %?1%"
        + " OR UPPER(e.name) like %?1%"
        + " OR UPPER(l.venueName) like %?1%"
        + " OR UPPER(f.firstName) like %?1%"
        + " OR UPPER(f.lastName) like %?1%"
        + " OR UPPER(f.stageName) like %?1%"
        + " OR UPPER(f.bandName) like %?1%)"
        + " AND e.type = ?2"
        + " AND a.start BETWEEN ?3 AND ?4"
        + " AND (UPPER(l.address.city) like %?5%"
        + " OR UPPER(l.address.country) like %?5%"
        + " OR UPPER(l.address.postalCode) like %?5%)")
    List<Event> findBySearchWithCategory(String search, EventType category, LocalDateTime dateFrom, LocalDateTime dateTo, String location, PageRequest pageRequest);

    /**
     * Find top 10 events of a category in one month.
     *
     * @param category the type of event
     * @param month the selected month
     * @param year the selected year
     * @param limit the limit of results
     *
     * @return list of top 10 events
     */
    @Query(value = "SELECT DISTINCT e.ID, e.DESCRIPTION, e.DURATION, e.NAME, e.TYPE, e.LOCATION_ID, a.TOTAL_TICKETS_SOLD"
        + " FROM event e"
        + " JOIN ("
        + " SELECT EVENT_ID, SUM(NR_TICKETS_SOLD) AS TOTAL_TICKETS_SOLD, START"
        + " FROM ACT"
        + " GROUP BY EVENT_ID, START)"
        + " a ON e.ID = a.EVENT_ID"
        + " WHERE e.TYPE = ?1"
        + " AND EXTRACT(MONTH FROM a.START) = ?2"
        + " AND EXTRACT(YEAR FROM a.START) = ?3"
        + " ORDER BY TOTAL_TICKETS_SOLD DESC"
        + " LIMIT ?4", nativeQuery = true)
    List<Event> findTopByNrTicketsSoldAndByCategoryAndByMonthAndYear(String category, int month, int year, Long limit);
    /* Category must be a String instead of EventType, else it does not work with native query*/

    /**
     * Find amount of events matching search parameters that do not include a category.
     *
     * @param search the search input which can match the name of artist, venue or description
     * @param dateFrom the earliest date
     * @param dateTo the latest date
     * @param location the search input which can match postalcode, city or country
     *
     * @return amount of events matching search parameters that do not include a category
     */
    @Query(value = "SELECT COUNT (DISTINCT e) FROM Event e"
        + " JOIN Location l ON e.location.id = l.id"
        + " JOIN act a ON e.id = a.event.id"
        + " LEFT OUTER JOIN e.featuredArtists AS f"
        + " WHERE (UPPER(e.description) LIKE %?1%"
        + " OR UPPER(e.name) like %?1%"
        + " OR UPPER(l.venueName) like %?1%"
        + " OR UPPER(f.firstName) like %?1%"
        + " OR UPPER(f.lastName) like %?1%"
        + " OR UPPER(f.stageName) like %?1%"
        + " OR UPPER(f.bandName) like %?1%)"
        + " AND a.start BETWEEN ?2 AND ?3"
        + " AND (UPPER(l.address.city) like %?4%"
        + " OR UPPER(l.address.country) like %?4%"
        + " OR UPPER(l.address.postalCode) like %?4%)")
    int getSizeOfSearchResultsWithoutCategory(String search, LocalDateTime dateFrom, LocalDateTime dateTo, String location);

    /**
     * Find amount of events matching search parameters that do include a category.
     *
     * @param search the search input which can match the name of artist, venue or description
     * @param category the type of event
     * @param dateFrom the earliest date
     * @param dateTo the latest date
     * @param location the search input which can match postalcode, city or country
     *
     * @return amount of events matching search parameters that do include a category
     */
    @Query(value = "SELECT COUNT (DISTINCT e) FROM Event e"
        + " JOIN Location l ON e.location.id = l.id"
        + " JOIN act a ON e.id = a.event.id"
        + " LEFT OUTER JOIN e.featuredArtists AS f"
        + " WHERE (UPPER(e.description) LIKE %?1%"
        + " OR UPPER(e.name) like %?1%"
        + " OR UPPER(l.venueName) like %?1%"
        + " OR UPPER(f.firstName) like %?1%"
        + " OR UPPER(f.lastName) like %?1%"
        + " OR UPPER(f.stageName) like %?1%"
        + " OR UPPER(f.bandName) like %?1%)"
        + " AND e.type = ?2"
        + " AND a.start BETWEEN ?3 AND ?4"
        + " AND (UPPER(l.address.city) like %?5%"
        + " OR UPPER(l.address.country) like %?5%"
        + " OR UPPER(l.address.postalCode) like %?5%)")
    int getSizeOfSearchResultsWithCategory(String search, EventType category, LocalDateTime dateFrom, LocalDateTime dateTo, String location);

}
