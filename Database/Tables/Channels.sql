CREATE TABLE Channels (
    channel_id BIGINT PRIMARY KEY,
    channel_name NVARCHAR(255) NOT NULL,
    guild_id BIGINT NOT NULL,
    CONSTRAINT FK_Channels_Guild FOREIGN KEY (guild_id) REFERENCES Guilds(guild_id),
    CONSTRAINT UC_channel_name_guild_id UNIQUE (channel_name, guild_id)
);
