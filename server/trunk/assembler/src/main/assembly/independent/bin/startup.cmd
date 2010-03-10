@echo off

SETLOCAL

rem *************************************************************************
rem This script is used to start Hydra Cache Server.  This script simply sets 
rem the environment variables and starts server.
rem 
rem Other variables this script takes are:
rem 
rem JAVA_OPTS    - Java command-line options for running the server. (These
rem                will be tagged on to the end of the JAVA_VM and MEM_ARGS)
rem JAVA_VM      - The java arg specifying the VM to run.  (i.e. -server,
rem                -hotspot, etc.)
rem MEM_ARGS     - The variable to override the standard memory arguments
rem                passed to java
rem 
rem *************************************************************************

rem Initialize the common environment.

set APP_HOME=..

rem Starting Server

echo .
echo CLASSPATH=%CLASSPATH%
echo .
echo PATH=%PATH%
echo .
echo ******************************************************
echo **                                                  **
echo      Hydra Distributed Cache Server ${version}        
echo **                                                  **
echo ******************************************************

rem Fine tune the following VM args for production system

rem JVM Variables
rem JAVA_VM="-server"

rem Memory and GC Tuning
rem MEM_ARGS="-Xms512m -Xmx1g -XX:NewSize=128m -XX:MaxNewSize=128m -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:CMSInitiatingOccupancyFraction=70"

%JAVA_HOME%/bin/java ^
    %JAVA_VM% ^
    %MEM_ARGS% ^
    %JAVA_OPTS% ^
    -classpath %APP_HOME%/lib/classworlds-1.1.jar ^
    -Dclassworlds.conf=%APP_HOME%/conf/classworlds.conf ^
    -Djava.net.preferIPv4Stack=true ^
    -Dapp.home=%APP_HOME% ^
    org.codehaus.classworlds.Launcher %