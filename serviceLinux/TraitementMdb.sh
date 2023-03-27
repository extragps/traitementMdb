#!/bin/sh
SERVICE_NAME=TraitementMdb
PATH_TO_JAV=/opt/java/jre/bin/java
PATH_TO_JAR=/home/pi/TraitementMdb/traitementMdb.jar
PID_PATH_NAME=/tmp/TerminalServer-pid
case $1 in
    start)
        echo "Starting $SERVICE_NAME ..."
        if [ ! -f $PID_PATH_NAME ]; then
            nohup $PATH_TO_JAVA -jar $PATH_TO_JAR -noconsole -portSerie /dev/ttyACM0 -rep . >> /tmp/TerminalServer.log 2>&1 &
                        echo $! > $PID_PATH_NAME
            echo "$SERVICE_NAME started ..."
        else
            echo "$SERVICE_NAME is already running ..."
        fi
    ;;
    stop)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stoping ..."
            kill $PID;
            echo "$SERVICE_NAME stopped ..."
            rm $PID_PATH_NAME
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
    restart)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stopping ...";
            kill $PID;
            echo "$SERVICE_NAME stopped ...";
            rm $PID_PATH_NAME
            echo "$SERVICE_NAME starting ..."
            nohup $PATH_TO_JAVA -jar $PATH_TO_JAR -noconsole -portSerie /dev/ttyACM0 -rep . >> /tmp/TerminalServer.log 2>&1 &
            echo $! > $PID_PATH_NAME
            echo "$SERVICE_NAME started ..."
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
esac

