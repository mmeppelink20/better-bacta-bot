CREATE TABLE DiscordMessages (
    message_id BIGINT PRIMARY KEY,
    channel_id BIGINT NOT NULL,
    guild_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    content NVARCHAR(MAX) NOT NULL,
    message_datetime DATETIME NOT NULL,
    message_edited_datetime DATETIME,
    is_bot BIT NOT NULL,
    attachment_url NVARCHAR(MAX),
    CONSTRAINT FK_DiscordMessage_Channel FOREIGN KEY (channel_id) REFERENCES Channels(channel_id),
    CONSTRAINT FK_DiscordMessage_Guild FOREIGN KEY (guild_id) REFERENCES Guilds(guild_id),
    CONSTRAINT FK_DiscordMessage_Author FOREIGN KEY (author_id) REFERENCES Users(user_id)
);
