CREATE TABLE Users (
    user_id BIGINT PRIMARY KEY,
    username NVARCHAR(255),
    discriminator INT,
    avatar_url NVARCHAR(MAX),
    CONSTRAINT UC_username_discriminator UNIQUE (username, discriminator)
);
