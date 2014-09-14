#!/bin/sh
if [ $# != 1 ]
then
	echo "Usage: bash $0 {deploy|restart|build|download|all}"
	exit
fi

PROJECT="soccer"
SCRIPTS="${PROJECT}/scripts/"

build()
{
    mvn package assembly:single
    STATUS=$?
    if [ ${STATUS} -eq 0 ]; then
        echo "Build Successful"
    else
        echo "Build Failed, exiting"
        exit
    fi
    rsync -avr target/soccer-0.1-SNAPSHOT-jar-with-dependencies.jar linode:${PROJECT}
}

deploy()
{
    deployService
    deployWeb
}

deployService()
{
    echo "deploying files"
    #rsync -avr data/* linode:${PROJECT}/data
    rsync -avr config/prod/ linode:${PROJECT}/config/
    rsync -avr scripts/*.sh linode:${SCRIPTS}
    ssh linode chmod u+x ${SCRIPTS}*.sh
}

deployWeb()
{
    echo "deploying web files"
    rsync -avr web/ dreamhost:${PROJECT}.moulliet.com/
}

restart()
{
    echo "restarting service"
    ssh linode bash ${SCRIPTS}${PROJECT}.sh restart
}

download()
{
    echo "downloading and restarting service"
    ssh linode bash ${SCRIPTS}${PROJECT}.sh download
}

case $1 in
	deploy)
		deploy
		;;
	restart)
	    deploy
	    restart
	    ;;
	build)
		build
		deploy
		restart
		;;
    web)
        deployWeb
        ;;
    all)
		build
		deploy
		download
		restart
		;;
	download)
	    deploy
	    download
	    ;;

	*)
		echo "Usage: bash $0 {deploy|restart|build|download|all}" >&2
esac