CREATE TABLE ChatMessages (
    message_id NUMBER PRIMARY KEY,
    sender VARCHAR2(50) NOT NULL,
    receiver VARCHAR2(50),
    message_text VARCHAR2(1000) NOT NULL,
    message_type VARCHAR2(20) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sender FOREIGN KEY (sender) REFERENCES Users(nickname),
    CONSTRAINT fk_receiver FOREIGN KEY (receiver) REFERENCES Users(nickname)
);