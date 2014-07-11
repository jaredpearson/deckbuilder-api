ALTER TABLE Users
ADD COLUMN facebookId BIGINT NULL UNIQUE;
CREATE UNIQUE INDEX users_facebookid_idx ON Users(facebookId);