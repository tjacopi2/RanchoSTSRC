setlocal
REM set path=%path%;C:\Program Files\Java\jdk-25\bin
set path=%path%;"c:\Program Files\Java\jdk-13.0.2\bin"
REM java -cp commons-lang3-3.11.jar;opencsv-5.3.jar; -jar guestReport.jar ..\entryLogs\
java -cp commons-lang3-3.11.jar;opencsv-5.3.jar;guestReport.jar GuestReport ..\entryLogs\
pause