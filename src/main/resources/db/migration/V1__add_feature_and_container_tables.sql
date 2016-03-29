CREATE TABLE FEATURE (
  ID            SERIAL        PRIMARY KEY,
  FEATURE_NAME  VARCHAR(256)  NOT NULL,
  DOCKER_DATA   VARCHAR(4096) NOT NULL
);

CREATE TABLE CONTAINER (
  ID              SERIAL      PRIMARY KEY,
  CONTAINER_NAME  VARCHAR(32) NOT NULL
);

CREATE TABLE CONTAINER_FEATURE (
  ID            SERIAL  PRIMARY KEY,
  CONTAINER_ID  INT     REFERENCES CONTAINER (ID),
  FEATURE_ID    INT     REFERENCES FEATURE (ID)
);

CREATE TABLE FEATURE_DEPENDENCY (
  ID                    SERIAL  PRIMARY KEY,
  FEATURE_ID            INT     REFERENCES FEATURE (ID),
  DEPENDENCY_FEATURE_ID INT     REFERENCES FEATURE (ID)
);