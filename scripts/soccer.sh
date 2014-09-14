#!/bin/sh
if [ $# != 1 ]
then
	echo "Usage: bash $0 {start|stop|restart|download}"
	exit
fi
# Make sure root can't run our script
if [[ $EUID -eq 0 ]]; then
   echo "error - this script should not run as root" 1>&2
   exit 1
fi

PROJECT=soccer
ROOT_DIR=/home/moulliet/${PROJECT}/
PID_FILE=${ROOT_DIR}${PROJECT}.pid
STD_OUT_LOG=${ROOT_DIR}logs/${PROJECT}.$(/bin/date '+%Y-%m-%d-%H-%M-%S')


startService ()
{
	echo  "Starting ${PROJECT} service ... "

	if [ -e "${PID_FILE}" ]
	then
		echo "pid file exists ${PID_FILE}, exiting"
		exit 1
	fi

	OPTIONS="-Xms256m -Xmx256m -cp ${ROOT_DIR}${PROJECT}-0.1-SNAPSHOT-jar-with-dependencies.jar:${ROOT_DIR}config
        -Dbase.dir=${ROOT_DIR}
        -Dcom.sun.management.jmxremote
        -Dcom.sun.management.jmxremote.port=9011
        -Dcom.sun.management.jmxremote.local.only=false
        -Dcom.sun.management.jmxremote.authenticate=false
        -Dcom.sun.management.jmxremote.ssl=false
	"

	#java ${OPTIONS} com.moulliet.soccer.SoccerServiceMain
	nohup java ${OPTIONS} com.moulliet.soccer.SoccerServiceMain  1>> ${STD_OUT_LOG} 2>&1 &

	echo -n $! > "${PID_FILE}"
	echo STARTED
}
stopService ()
{
	echo "Stopping ${PROJECT} service ... "
	if [ ! -e "${PID_FILE}" ]
	then
		echo "error: could not find file ${PID_FILE}"
	else
		PID=$(cat "${PID_FILE}")
		kill ${PID}
		echo "WAITING for PID ${PID}"
		while ps -p ${PID} > /dev/null; do sleep 1; echo slept; done
		rm "${PID_FILE}"
		echo STOPPED
	fi
}

download ()
{
	echo  "Downloading ${PROJECT} data ... "

	OPTIONS="-Xms128m -Xmx128m -cp ${ROOT_DIR}${PROJECT}-0.1-SNAPSHOT-jar-with-dependencies.jar:${ROOT_DIR}config
        -Dbase.dir=${ROOT_DIR}
	"

	java ${OPTIONS} com.moulliet.soccer.ncsoccer.NCSoccerDownload

}

case $1 in
	start)
		startService
		;;
	stop)
		stopService
		;;
	restart)
		stopService
		startService
		;;
    download)
        download
		stopService
		startService
		;;
	*)
		echo "Usage: bash $0 {start|stop|restart|download}" >&2
esac


