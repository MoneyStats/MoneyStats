@ECHO OFF
set "params=%*"
cd /d "%~dp0" && ( if exist "%temp%\getadmin.vbs" del "%temp%\getadmin.vbs" ) && fsutil dirty query %systemdrive% 1>nul 2>nul || (  echo Set UAC = CreateObject^("Shell.Application"^) : UAC.ShellExecute "cmd.exe", "/k cd ""%~sdp0"" && %~s0 %params%", "", "runas", 1 >> "%temp%\getadmin.vbs" && "%temp%\getadmin.vbs" && exit /B )
echo.
echo -------------------------------------------------------------
echo "    __  ___                       _____ __        __       "
echo "   /  |/  /___  ____  ___  __  __/ ___// /_____ _/ /_______"
echo "  / /|_/ / __ \/ __ \/ _ \/ / / /\__ \/ __/ __ \`/ __/ ___/"
echo " / /  / / /_/ / / / /  __/ /_/ /___/ / /_/ /_/ / /_(__  )  "
echo "/_/  /_/\____/_/ /_/\___/\__, //____/\__/\__,_/\__/____/   "
echo "                        /____/                             "
echo "                                      by Giovanni Lamarmora"
echo -------------------------------------------------------------
echo.
echo.
call :setESC
echo %ESC%[7m[%time%] [LOG] - Script to update the container maded by%ESC%[0m Lamarmora Giovanni

timeout 1 > NUL
echo.
echo %ESC%[35m[%time%] [LOG]%ESC%[0m - Loading the script to be executed... %ESC%[32mCompleted%ESC%[0m
timeout 2 > NUL
echo.
echo %ESC%[35m[%time%] [LOG]%ESC%[0m - This an info logger to trace the correct script. %ESC%[32mCompleted%ESC%[0m
timeout 1 > NUL
echo %ESC%[35m[%time%] [LOG]%ESC%[0m - Check integrity script. %ESC%[32mCompleted%ESC%[0m
timeout 1 > NUL
echo %ESC%[35m[%time%] [LOG]%ESC%[0m - Check error script. NOT FOUND. %ESC%[32mCompleted%ESC%[0m
timeout 1 > NUL
echo %ESC%[35m[%time%] [LOG]%ESC%[0m - Check System data. %ESC%[32mCompleted%ESC%[0m
timeout 1 > NUL
echo.
echo %ESC%[35m[%time%] [LOG]%ESC%[0m - Check internet Connection. Starting PING: %ESC%[32mCompleted%ESC%[0m
timeout 1 > NUL
echo.

set ip=8.8.8.8
ping -n 4 %ip% | find "TTL"
if not errorlevel 4 set message=32mConnected
if errorlevel 4 set message=31mNot Connected | exit
echo.
echo %ESC%[35m[%time%] [LOG]%ESC%[0m - Connection Status: %ESC%[%message%%ESC%[0m
echo.
echo %ESC%[35m[%time%] [LOG]%ESC%[0m - %ESC%[33mStarting Update...%ESC%[0m
echo ----------------------------------------------------------------------------------------------------------
timeout 1 > NUL
echo %ESC%[35m[%time%] [LOG]%ESC%[0m - Removing old Container
docker rm moneystats -v
echo %ESC%[35m[%time%] [LOG]%ESC%[0m - Removed. %ESC%[32mCompleted%ESC%[0m
echo ----------------------------------------------------------------------------------------------------------
timeout 1 > NUL
echo %ESC%[35m[%time%] [LOG]%ESC%[0m - Erasing image...
docker image rm giovannilamarmora/moneystats
echo %ESC%[35m[%time%] [LOG]%ESC%[0m - Erased. %ESC%[32mCompleted%ESC%[0m
echo ----------------------------------------------------------------------------------------------------------
timeout 1 > NUL
echo %ESC%[35m[%time%] [LOG]%ESC%[0m - Downloading Update...
docker pull giovannilamarmora/moneystats
echo %ESC%[35m[%time%] [LOG]%ESC%[0m - Downloaded. %ESC%[32mCompleted%ESC%[0m
echo ----------------------------------------------------------------------------------------------------------
timeout 1 > NUL
echo %ESC%[35m[%time%] [LOG]%ESC%[0m - Run Container Updated...
docker run --network moneystats-network --name moneystats -p 8080:8080 -d giovannilamarmora/moneystats
echo %ESC%[35m[%time%] [LOG]%ESC%[0m - Runned. %ESC%[32mCompleted%ESC%[0m
echo ----------------------------------------------------------------------------------------------------------
timeout 1 > NUL
echo.
echo %ESC%[32m[%time%] [LOG] - Update Completed%ESC%[0m
PAUSE
exit

:setESC
for /F "tokens=1,2 delims=#" %%a in ('"prompt #$H#$E# & echo on & for %%b in (1) do rem"') do (
  set ESC=%%b
)