
INSERT INTO SCORE (ID,VALUE)
VALUES (NEXTVAL('SCORE_SEQ'),10);

INSERT INTO SCORE (ID,VALUE)
VALUES (NEXTVAL('SCORE_SEQ'),100);

INSERT INTO SCORE (ID,VALUE)
VALUES (NEXTVAL('SCORE_SEQ'),100);

INSERT INTO USERS (ID,AUTHORITIES,EMAIL,FIRST_NAME,IS_ACTIVE,IS_NOT_LOCKED,JOIN_DATE,LAST_LOGIN_DATE,LAST_NAME,PASSWORD,PROFILE_IMAGE_URL,ROLE,USER_ID,USERNAME,SCORE_ID)
VALUES (
        NEXTVAL('USER_SEQ'),
        decode('ACED0005757200135B4C6A6176612E6C616E672E537472696E673BADD256E7E91D7B4702000078700000000C740009757365723A7265616474000B757365723A63726561746574000B757365723A75706461746574000B757365723A64656C65746574000C776F726B6F75743A7265616474000E776F726B6F75743A63726561746574000E776F726B6F75743A75706461746574000E776F726B6F75743A64656C6574657400136D656D62657273686970547970653A726561647400156D656D62657273686970547970653A6372656174657400156D656D62657273686970547970653A7570646174657400156D656D62657273686970547970653A64656C657465','hex'),
        'admin@gym.com',
        'admin',
        true,
        true,
        NOW(),
        NOW(),
        'admin',
        '$2a$10$liUOg1YXk8bsYMWB3nDRs.ZnK.NefKzmwVyxFJM/ju9KHnZCte6cK',
        'http://localhost:8081/user/image/profile/admin',
        'ROLE_ADMIN',
        'cus_Q1bqVY72jiJYEY',
        'admin',
        1
        );
INSERT INTO USERS (ID,AUTHORITIES,EMAIL,FIRST_NAME,IS_ACTIVE,IS_NOT_LOCKED,JOIN_DATE,LAST_LOGIN_DATE,LAST_NAME,PASSWORD,PROFILE_IMAGE_URL,ROLE,USER_ID,USERNAME,SCORE_ID)
VALUES (
        NEXTVAL('USER_SEQ'),
        decode('ACED0005757200135B4C6A6176612E6C616E672E537472696E673BADD256E7E91D7B47020000787000000006740009757365723A7265616474000C776F726B6F75743A726561647400136D656D62657273686970547970653A7265616474000E776F726B6F75743A63726561746574000E776F726B6F75743A75706461746574000E776F726B6F75743A64656C657465','hex'),
        'igor_gym@gym.com',
        'Igor',
        true,
        true,
        NOW(),
        NOW(),
        'Gralak',
        '$2a$10$uRPOhCOIikRM9i6f7s4ECe.cGi33zUsB11bOqaZKSwuwDUvX8YoVu',
        'http://localhost:8081/user/image/profile/igor_gym',
        'ROLE_COACH',
        'cus_Q1bsMWy21w1CK8',
        'igor_gym',
        2
        );
INSERT INTO USERS (ID,AUTHORITIES,EMAIL,FIRST_NAME,IS_ACTIVE,IS_NOT_LOCKED,JOIN_DATE,LAST_LOGIN_DATE,LAST_NAME,PASSWORD,PROFILE_IMAGE_URL,ROLE,USER_ID,USERNAME,SCORE_ID)
VALUES (
        NEXTVAL('USER_SEQ'),
        decode('ACED0005757200135B4C6A6176612E6C616E672E537472696E673BADD256E7E91D7B47020000787000000006740009757365723A7265616474000C776F726B6F75743A726561647400136D656D62657273686970547970653A7265616474000E776F726B6F75743A63726561746574000E776F726B6F75743A75706461746574000E776F726B6F75743A64656C657465','hex'),
        'marcin_gym@gym.com',
        'Marcin',
        true,
        true,
        NOW(),
        NOW(),
        'Marcin',
        '$2a$10$uRPOhCOIikRM9i6f7s4ECe.cGi33zUsB11bOqaZKSwuwDUvX8YoVu',
        'http://localhost:8081/user/image/profile/marcin_gym',
        'ROLE_COACH',
        'cus_M1bSw5T64K1CK8',
        'marcin_gym',
        3
       );

INSERT INTO MEMBERSHIP_TYPE (ID,DESCRIPTION,MEMBERSHIP_PRODUCT_ID,NAME,PRICE,TYPE,VALIDITY_PERIOD_NUMBER,VALIDITY_UNIT_OF_TIME)
VALUES (NEXTVAL('MEMBERSHIP_TYPE_SEQ'),'Pozwala wejść urzytkownikowi do klubu w godzinach 6:00 - 15:00','prod_Q1kQog4NkOIye8','MORNING',50,'MORNING',1,1);

INSERT INTO MEMBERSHIP_TYPE (ID,DESCRIPTION,MEMBERSHIP_PRODUCT_ID,NAME,PRICE,TYPE,VALIDITY_PERIOD_NUMBER,VALIDITY_UNIT_OF_TIME)
VALUES (NEXTVAL('MEMBERSHIP_TYPE_SEQ'),'Pozwala wejść urzytkownikowi do klubu  w godzinach 15:00 - 22:00','prod_Q1kQog4NkOIye8','AFTERNOON',100,'AFTERNOON',1,1);

INSERT INTO MEMBERSHIP_TYPE (ID,DESCRIPTION,MEMBERSHIP_PRODUCT_ID,NAME,PRICE,TYPE,VALIDITY_PERIOD_NUMBER,VALIDITY_UNIT_OF_TIME)
VALUES (NEXTVAL('MEMBERSHIP_TYPE_SEQ'),'Dożywotni wstęp do klubów ','prod_Q1kQog4NkOIye8','LIFETIME',1000,'LIFETIME',9999,2);


INSERT INTO USER_MEMBERSHIP (ID,END_DATE,START_DATE,MEMBERSHIP_TYPE_ID,USER_ID)
VALUES (NEXTVAL('USER_MEMBERSHIP_SEQ'),'2500-05-01 21:37:00.000',NOW(),(SELECT ID FROM MEMBERSHIP_TYPE where NAME = 'LIFETIME'),(SELECT ID FROM USERS  where USERNAME = 'admin'));

INSERT INTO USER_MEMBERSHIP (ID,END_DATE,START_DATE,MEMBERSHIP_TYPE_ID,USER_ID)
VALUES (NEXTVAL('USER_MEMBERSHIP_SEQ'),'2500-05-01 21:37:00.000',NOW(),(SELECT ID FROM MEMBERSHIP_TYPE where NAME = 'LIFETIME'),(SELECT ID FROM USERS  where USERNAME = 'igor_gym'));

INSERT INTO USER_MEMBERSHIP (ID,END_DATE,START_DATE,MEMBERSHIP_TYPE_ID,USER_ID)
VALUES (NEXTVAL('USER_MEMBERSHIP_SEQ'),'2500-05-01 21:37:00.000',NOW(),(SELECT ID FROM MEMBERSHIP_TYPE where NAME = 'LIFETIME'),(SELECT ID FROM USERS  where USERNAME = 'marcin_gym'));
----------------

-- Day 1: Workouts on today’s date, starting at specific hours
INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 101, 30, 0, 'NOGI', NOW()::date + INTERVAL '8 hour', NOW()::date + INTERVAL '8 hour' + INTERVAL '45 minutes', 'igor_gym', 1);

INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 102, 30, 0, 'PLECY', NOW()::date + INTERVAL '9 hour', NOW()::date + INTERVAL '9 hour' + INTERVAL '45 minutes', 'igor_gym', 1);

INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 103, 30, 0, 'KLATKA', NOW()::date + INTERVAL '10 hour', NOW()::date + INTERVAL '10 hour' + INTERVAL '45 minutes', 'igor_gym', 1);

INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 104, 30, 0, 'BARKI', NOW()::date + INTERVAL '11 hour', NOW()::date + INTERVAL '11 hour' + INTERVAL '45 minutes', 'igor_gym', 1);

INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 105, 30, 0, 'ZUMBA', NOW()::date + INTERVAL '12 hour', NOW()::date + INTERVAL '12 hour' + INTERVAL '45 minutes', 'igor_gym', 1);

INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 105, 30, 0, 'BRZUCH', NOW()::date + INTERVAL '13 hour', NOW()::date + INTERVAL '13 hour' + INTERVAL '45 minutes', 'igor_gym', 1);
----------------
-- Day 2: Workouts on today’s date, starting at specific hours
INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 101, 30, 0, 'NOGI', NOW()::date + INTERVAL '1 day' + INTERVAL '8 hour', NOW()::date + INTERVAL '1 day' + INTERVAL '8 hour' + INTERVAL '45 minutes', 'igor_gym', 1);

INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 102, 30, 0, 'PLECY', NOW()::date + INTERVAL '1 day' + INTERVAL '9 hour', NOW()::date + INTERVAL '1 day' + INTERVAL '9 hour' + INTERVAL '45 minutes', 'igor_gym', 1);

INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 103, 30, 0, 'KLATKA', NOW()::date + INTERVAL '1 day' + INTERVAL '15 hour', NOW()::date + INTERVAL '1 day' + INTERVAL '16 hour' + INTERVAL '45 minutes', 'igor_gym', 1);

INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 104, 30, 0, 'BARKI', NOW()::date + INTERVAL '1 day' + INTERVAL '11 hour', NOW()::date + INTERVAL '1 day' + INTERVAL '11 hour' + INTERVAL '45 minutes', 'igor_gym', 1);

INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 105, 30, 0, 'ZUMBA', NOW()::date + INTERVAL '1 day' + INTERVAL '12 hour', NOW()::date + INTERVAL '1 day' + INTERVAL '12 hour' + INTERVAL '45 minutes', 'igor_gym', 1);

INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 105, 30, 0, 'BRZUCH', NOW()::date + INTERVAL '1 day' + INTERVAL '13 hour', NOW()::date + INTERVAL '1 day' + INTERVAL '13 hour' + INTERVAL '45 minutes', 'igor_gym', 1);
----------------
-- Day 3: Workouts on today’s date, starting at specific hours
INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 101, 30, 0, 'NOGI', NOW()::date + INTERVAL '2 day' + INTERVAL '8 hour', NOW()::date + INTERVAL '2 day' + INTERVAL '8 hour' + INTERVAL '45 minutes', 'igor_gym', 1);

INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 102, 30, 0, 'PLECY', NOW()::date + INTERVAL '2 day' + INTERVAL '9 hour', NOW()::date + INTERVAL '2 day' + INTERVAL '9 hour' + INTERVAL '45 minutes', 'igor_gym', 1);

INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 103, 30, 0, 'KLATKA', NOW()::date + INTERVAL '2 day' + INTERVAL '10 hour', NOW()::date + INTERVAL '2 day' + INTERVAL '10 hour' + INTERVAL '45 minutes', 'igor_gym', 1);

INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 104, 30, 0, 'BARKI', NOW()::date + INTERVAL '2 day' + INTERVAL '11 hour', NOW()::date + INTERVAL '2 day' + INTERVAL '11 hour' + INTERVAL '45 minutes', 'igor_gym', 1);

INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 105, 30, 0, 'ZUMBA', NOW()::date + INTERVAL '2 day' + INTERVAL '12 hour', NOW()::date + INTERVAL '2 day' + INTERVAL '12 hour' + INTERVAL '45 minutes', 'igor_gym', 1);

INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 105, 30, 0, 'BRZUCH', NOW()::date + INTERVAL '2 day' + INTERVAL '16 hour', NOW()::date + INTERVAL '2 day' + INTERVAL '17 hour' + INTERVAL '45 minutes', 'igor_gym', 1);
----------------
-- Day 4: Workouts on today’s date, starting at specific hours
INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 101, 30, 0, 'NOGI', NOW()::date + INTERVAL '3 day' + INTERVAL '8 hour', NOW()::date + INTERVAL '3 day' + INTERVAL '8 hour' + INTERVAL '45 minutes', 'igor_gym', 1);

INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 102, 30, 0, 'PLECY', NOW()::date + INTERVAL '3 day' + INTERVAL '20 hour', NOW()::date + INTERVAL '3 day' + INTERVAL '21 hour' + INTERVAL '45 minutes', 'igor_gym', 1);

INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 103, 30, 0, 'KLATKA', NOW()::date + INTERVAL '3 day' + INTERVAL '10 hour', NOW()::date + INTERVAL '3 day' + INTERVAL '10 hour' + INTERVAL '45 minutes', 'igor_gym', 1);

INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 104, 30, 0, 'BARKI', NOW()::date + INTERVAL '3 day' + INTERVAL '11 hour', NOW()::date + INTERVAL '3 day' + INTERVAL '11 hour' + INTERVAL '45 minutes', 'igor_gym', 1);

INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 105, 30, 0, 'ZUMBA', NOW()::date + INTERVAL '3 day' + INTERVAL '12 hour', NOW()::date + INTERVAL '3 day' + INTERVAL '12 hour' + INTERVAL '45 minutes', 'igor_gym', 1);

INSERT INTO WORKOUT (ID, ROOM_NUMBER, CAPACITY, PARTICIPANTS_NUMBER, WORKOUT_NAME, WORKOUT_START_DATE, WORKOUT_END_DATE, TRAINER_USERNAME, WORKOUT_DIFFICULTY)
VALUES (NEXTVAL('WORKOUT_SEQ'), 105, 30, 0, 'BRZUCH', NOW()::date + INTERVAL '3 day' + INTERVAL '13 hour', NOW()::date + INTERVAL '3 day' + INTERVAL '13 hour' + INTERVAL '45 minutes', 'igor_gym', 1);
----------------