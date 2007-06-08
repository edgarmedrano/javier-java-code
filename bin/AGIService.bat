@echo off
set PATH=%ProgramFiles%\Java\jre6\bin;..\lib\x86
set CLASSPATH=..\src;..\lib\jacob.jar
java org.javier.agi.AGIService org.javier.agi.AGIHandler