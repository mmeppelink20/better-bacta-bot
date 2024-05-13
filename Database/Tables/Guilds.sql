CREATE TABLE Guilds (
    guild_id BIGINT PRIMARY KEY,
    guild_name NVARCHAR(255) NOT NULL,
    CONSTRAINT UC_guild_name UNIQUE (guild_name)
);