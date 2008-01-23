@echo off
set PATH=%ProgramFiles%\Java\jre6\bin;..\lib\x86
set CLASSPATH=..\src;..\lib\jacob.jar
start javaw org.javier.browser.JavierGUI