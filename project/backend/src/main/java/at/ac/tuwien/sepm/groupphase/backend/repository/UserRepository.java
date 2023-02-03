package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    /**
     * Find all user entries ordered by email.
     *
     * @return ordered list of all user entries
     */
    List<ApplicationUser> findAllByEmail(String email);

    /**
     * Find user entries with the given email.
     *
     * @param email E-mail address of the user that is looked for
     * @return User with given email
     */
    ApplicationUser findApplicationUserByEmail(String email);

    /**
     * Find user entries that match the search parameters including a lockedOut value.
     *
     * @param email E-mail address of the user that is looked for
     * @param firstName first name of the user that is looked for
     * @param lastName last name of the user that is looked for
     * @param lockedOut locked status of the user
     * @return List of matching users
     */
    @Query("SELECT u FROM application_user u WHERE UPPER(u.email) LIKE %?1% and  UPPER(u.firstName) LIKE %?2% and UPPER(u.lastName) LIKE %?3% and u.lockedOut = ?4")
    List<ApplicationUser> findBySearchWithLockedValue(String email, String firstName, String lastName, Boolean lockedOut);

    /**
     * Find user entries that match the search parameters not including a lockedOut value.
     *
     * @param email E-mail address of the user that is looked for
     * @param firstName first name of the user that is looked for
     * @param lastName last name of the user that is looked for
     * @return List of matching users
     */
    @Query("SELECT u FROM application_user u WHERE UPPER(u.email) LIKE %?1% and  UPPER(u.firstName) LIKE %?2% and UPPER(u.lastName) LIKE %?3%")
    List<ApplicationUser> findBySearchWithoutLockedValue(String email, String firstName, String lastName);

    /**
     * Find user entries that match the search parameters not including a lockedOut value.
     *
     * @param email E-mail address of the user that is looked for
     * @param firstName first name of the user that is looked for
     * @param lastName last name of the user that is looked for
     * @param pageRequest the size and id of the current page
     * @return List of matching users for the current page
     */
    @Query("SELECT u FROM application_user u WHERE UPPER(u.email) LIKE %?1% and  UPPER(u.firstName) LIKE %?2% and UPPER(u.lastName) LIKE %?3%")
    List<ApplicationUser> findBySearchWithLockedValuePagination(String email, String firstName, String lastName, PageRequest pageRequest);

    /**
     * Find user entries that match the search parameters including a lockedOut value.
     *
     * @param email E-mail address of the user that is looked for
     * @param firstName first name of the user that is looked for
     * @param lastName last name of the user that is looked for
     * @param pageRequest the size and id of the current page
     * @param lockedOut locked status of the user
     * @return List of matching users for the current page
     */
    @Query("SELECT u FROM application_user u WHERE UPPER(u.email) LIKE %?1% and  UPPER(u.firstName) LIKE %?2% and UPPER(u.lastName) LIKE %?3% and u.lockedOut = ?4")
    List<ApplicationUser> findBySearchWithoutLockedValuePagination(String email, String firstName, String lastName, Boolean lockedOut, PageRequest pageRequest);

    /**
     * Find amount of user entries that match the search parameters not including a lockedOut value.
     *
     * @param email E-mail address of the user that is looked for
     * @param firstName first name of the user that is looked for
     * @param lastName last name of the user that is looked for
     * @return amount of user entries that match the search parameters
     */
    @Query("SELECT COUNT (u) FROM application_user u WHERE UPPER(u.email) LIKE %?1% and  UPPER(u.firstName) LIKE %?2% and UPPER(u.lastName) LIKE %?3%")
    int findSizeBySearchWithoutLocked(String email, String firstName, String lastName);

    /**
     * Find amount of user entries that match the search parameters including a lockedOut value.
     *
     * @param email E-mail address of the user that is looked for
     * @param firstName first name of the user that is looked for
     * @param lastName last name of the user that is looked for
     * @param lockedOut locked status of the user
     * @return amount of user entries that match the search parameters
     */
    @Query("SELECT COUNT (u) FROM application_user u WHERE UPPER(u.email) LIKE %?1% and  UPPER(u.firstName) LIKE %?2% and UPPER(u.lastName) LIKE %?3% and u.lockedOut = ?4")
    int findSizeBySearchWithLocked(String email, String firstName, String lastName, Boolean lockedOut);

    /**
     * Returns all users linked to the provided ticket ids.
     *
     * @param ticketIds the ticket ids
     * @return list of users
     * */
    @Query("""
SELECT u FROM application_user u LEFT JOIN u.tickets t
WHERE t.id IN :ids
        """)
    List<ApplicationUser> getAllUsersForTicketIds(@Param("ids") List<Long> ticketIds);
}
