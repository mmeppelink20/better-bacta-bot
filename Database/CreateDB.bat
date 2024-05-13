@echo off

REM Set path to .env file
set EnvFile=..\.env

REM Read variables from .env file
for /f "usebackq tokens=1,* delims==" %%a in ("%EnvFile%") do set "%%a=%%b"

REM Set SQL Server instance name and database name
set ServerName=DESKTOP-A9LNC45\INSTANCE1
set DatabaseName=BactaBotDB

REM Set path to sqlcmd executable
set SqlCmdPath="C:\Program Files\Microsoft SQL Server\Client SDK\ODBC\170\Tools\Binn\sqlcmd.exe"

echo  **************************
echo     Creating %DatabaseName% Tables.
echo  **************************

echo.

REM Execute SQL scripts
%SqlCmdPath% -S %ServerName% -U %SQLUSERNAME% -P %SQLPASSWORD% -i "CreateDB.sql" >> DB.log 2>&1
%SqlCmdPath% -S %ServerName% -d %DatabaseName% -U %SQLUSERNAME% -P %SQLPASSWORD% -i ".\Tables\Guilds.sql" >> DB.log 2>&1
%SqlCmdPath% -S %ServerName% -d %DatabaseName% -U %SQLUSERNAME% -P %SQLPASSWORD% -i ".\Tables\Channels.sql" >> DB.log 2>&1
%SqlCmdPath% -S %ServerName% -d %DatabaseName% -U %SQLUSERNAME% -P %SQLPASSWORD% -i ".\Tables\Users.sql" >> DB.log 2>&1
%SqlCmdPath% -S %ServerName% -d %DatabaseName% -U %SQLUSERNAME% -P %SQLPASSWORD% -i ".\Tables\UserConfiguration.sql" >> DB.log 2>&1
%SqlCmdPath% -S %ServerName% -d %DatabaseName% -U %SQLUSERNAME% -P %SQLPASSWORD% -i ".\Tables\DiscordMessages.sql" >> DB.log 2>&1
%SqlCmdPath% -S %ServerName% -d %DatabaseName% -U %SQLUSERNAME% -P %SQLPASSWORD% -i ".\Tables\Configuration.sql" >> DB.log 2>&1

echo  **************************
echo     Tables created successfully.
echo  **************************

echo.
echo.
echo.

echo  **************************
echo     Creating %DatabaseName% Stored Procedures.
echo  **************************


echo  **************************
echo     Stored Procedures created successfully.
echo  **************************

echo.
echo.
echo.

echo  **************************
echo     Database: %DatabaseName% created successfully.
echo  **************************

pause
