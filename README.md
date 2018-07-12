# Tableau-report-scheduler
A springboot java app to schedule tableau pdf reports

The application needs to be run on the same server as the server tableau server is installed.

1. Java application using Quartz to schedule pdf exports.
2. Web UI is used to set parameters and filters and a tableau url is then generated.
3. The generated url is then run at a set schedule where Tabadmin commands saved
in scripts.txt are launched.

scripts.txt:
```
tabcmd login -s {SERVER_URL} -t {SITE_NAME} -u {USERNAME} -p {PASSWORD} --no-certcheck

tabcmd export "{URL}" --no-certcheck --pdf --pagesize unspecified --height 5000 --width 2000 -f "{FILE_NAME}.pdf"

tabcmd logout
```

{SERVER_URL} {USERNAME} {PASSWORD} is replaced by values set in application.properties
For example:
```
tableau.server.url=https://localhost
tableau.server.username=myUser
tableau.server.password=myPwd
```
{SITE_NAME} and {URL} is passed through the UI interface.

In application.properties:
set path to where config files will be saved and set path to tableu installation directory bin folder
```
dashboard.config.file.path= /path-where-config-files-will-be-saved
tableau.location.path= /path-to-tableau-bin-folder
```
Mail server settings:
```
spring.mail.default-encoding=UTF-8
spring.mail.host=myMail.server.com
spring.mail.username=myMailUser
spring.mail.password=myMailPwd
spring.mail.properties.mail.smtp.port=25
spring.mail.properties.mail.transport.protocol=smtp
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```
Option to set Start of week: SUNDAY to SATURDAY[1 to 7]
```
date.start.week = 1
```
