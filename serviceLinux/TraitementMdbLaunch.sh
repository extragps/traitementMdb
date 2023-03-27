#!/bin/sh
#BEGIN INIT INFO
# Provides:          startapp
# Required-Start:    $remote_fs $syslog
# Required-Stop:     $remote_fs $syslog
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Example script demarrage de service.
# Description:       This file should be used to construct scripts to be
#                    placed in /etc/init.d.  This example start a
#                    single forking daemon capable of writing a pid
#                    file.  To get other behavoirs, implemend
#                    do_start(), do_stop() or other functions to
#                    override the defaults in /lib/init/init-d-script.
### END INIT INFO

DESC="Demarrage de l'application RDT"
DAEMON=cpuRdtMain
DAEMON2=c4su3
DAEMON3=c4shttpd

case "$1" in
start)  log_daemon_msg "Demarrage de l'application RDT"
        (cd /home/root/ ; ./startappcpuRdtMain.sh )

        ;;
stop)   log_daemon_msg "Arret de l'application RDT"
        pkill $DAEMON
        pkill $DAEMON2
        pkill $DAEMON3
        RETVAL=0

        log_end_msg $RETVAL
        ;;
restart) log_daemon_msg "Redemarrage de l'application RDT"
        $0 stop
        $0 start
        ;;
*)

esac
exit 0
