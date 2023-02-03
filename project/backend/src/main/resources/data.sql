-- DELETE commands below have a strict order
DELETE FROM ticket WHERE id > 0;
DELETE FROM ticket_acquisition WHERE id > 0;

DELETE FROM act WHERE id > 0;
DELETE FROM event WHERE id > 0;
DELETE FROM location WHERE id > 0;
DELETE FROM address WHERE id > 0;

-- Address
INSERT INTO address (id, street, city, country, postal_code)
VALUES
    (1, 'Favoritenstrasse 8', 'Vienna', 'Austria', 123),
    (2, 'Juan Tabo 6', 'Albuquerque', 'USA', 6353),
    (3, 'Palisady 5', 'Bratislava', 'Slovakia', 85012),
    (4, 'Zone 4', 'Doha', 'Qatar', 3932)
;

-- Location
INSERT INTO location (id, venue_name, address_id)
VALUES
    (1, 'Allianz Arena', 1),
    (2, 'White Hart Lane', 2),
    (3, 'Red Bull Stadium', 3),
    (4, 'Lusail Stadium', 4)
;

-- Events
INSERT INTO event (id, name, type, duration, location_id)
VALUES
    (1, 'ABBA Concert',  'CONCERT', 30, 1),
    (2, 'Linkin Park',  'CONCERT', 45, 2),
    (3, 'NIN',  'CONCERT', 60, 3),
    (4, 'World Cup Final',  'SPORT', 60, 3)
;

-- Act
-- Existing stage_id's: 1, 2, 3
INSERT INTO act (id, start, nr_tickets_reserved, nr_tickets_sold, stage_id, event_id)
VALUES
    (1, '2024-01-01', 0, 0, 1, 1),
    (2, '2024-01-03', 0, 0, 2, 2),
    (3, '2024-01-05', 0, 0, 3, 3),
    (4, '2024-01-07', 0, 0, 3, 4)
;

INSERT INTO ticket_acquisition (id, buyer, cancelled)
VALUES
    (1, 1, false),
    (2, 1, true),
    (3, 1, false)
;


INSERT INTO ticket (id, cancelled, creation_date, reservation, seat_no, ticket_first_name, ticket_last_name, act_id, sector_map_id, ticket_order, buyer)
VALUES
    (1, false, '2024-01-01', 'PURCHASED', 1, 'Alfa', 'Alfonson', 1, 1, null, 1),
    (2, false, '2024-01-01', 'PURCHASED', 3, 'Roll', 'Safe', 2, 1, null, 1),
    (3, false, '2024-01-01', 'RESERVED', 5, 'Spongey', 'Bubble', 4, 1, null, 1),
    (4, false, '2024-01-01', 'RESERVED', 4, 'Sneed', 'Feed', 4, 1, null, 1),
    (5, true, '2024-01-01', 'RESERVED', 2, 'Bravo', 'Bravonson', 2, 1, 1, 1),
    (6, true, '2024-01-01', 'RESERVED', 2, 'Charlie', 'Charlson', 2, 1, 2, 1),
    (7, true, '2024-01-01', 'PURCHASED', 2, 'Delta', 'Deltason', 3, 1, 3, 1),
    (8, false, '2024-01-01', 'INITIALISED', 2, 'Echo', 'Echonson', 4, 1, 3, 1)
;
