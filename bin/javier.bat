@echo off
set PATH=%ProgramFiles%\Java\jre6\bin;..\lib\x86
set CLASSPATH=..\src;..\lib\commons-codec-1.3.jar;..\lib\commons-httpclient-3.0.1.jar;..\lib\commons-logging-1.1.jar;..\lib\jdom.jar;..\lib\orderlycalls.jar;..\lib\swing-worker.jar;..\lib\swingx-bean.jar;..\lib\swingx-ws.jar;..\lib\jacob.jar
java org.javier.browser.Javier %1 %2