#!/bin/bash

export LC_ALL="zh_CN.UTF-8"
export LANG="zh_CN.UTF-8"

script_dir()
{
    SOURCE="${BASH_SOURCE[0]}"
    while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
	DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
	SOURCE="$(readlink "$SOURCE")"
	[[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE"
    done
    DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
    echo ${DIR}
}


export AIR_PLATFORM_VERSION=@projectVersion@
export AIR_PLATFORM_COMMIT=@gitCommit@
export AIR_PLATFORM_COMMIT_TIME=@gitCommitTime@
export AIR_PLATFORM_BUILD_TIME=@gitBuildTime@

export AIR_PLATFORM_HOME="$( script_dir )"
export AIR_PLATFORM_JAR=${AIR_PLATFORM_HOME}/air-platform-${AIR_PLATFORM_VERSION}.jar

echo ""
echo "AIR_PLATFORM_HOME: $AIR_PLATFORM_HOME"
echo "AIR_PLATFORM_LOG: $AIR_PLATFORM_HOME/logs"
echo "AIR_PLATFORM_BUILD_INFO:"
echo "------------------------------------------------------"
echo "| Version: $AIR_PLATFORM_VERSION"
echo "| Build Time: $AIR_PLATFORM_BUILD_TIME"
echo "| Commit Time: $AIR_PLATFORM_COMMIT_TIME"
echo "| Git Commit: $AIR_PLATFORM_COMMIT"
echo "------------------------------------------------------"

# mode: fg - foreground, otherwise in background
mode=$1
JAVA_OPTIONS="-Dloader.path=lib/,config/ -Dspring.profiles.active=@activeProfile@"
JAVA_OPTIONS="$JAVA_OPTIONS -Dfile.encoding=UTF8"
JAVA_OPTIONS="$JAVA_OPTIONS -Xms4G -Xmx4G -XX:+UseG1GC"
JAVA_OPTIONS="$JAVA_OPTIONS -XX:+UseCompressedOops -XX:+OptimizeStringConcat"

PIDFILE=${AIR_PLATFORM_HOME}/@pidfile@

if [[ -f ${PIDFILE} ]]
then
   echo "AirCommunity Platform is already running with PID: `cat ${PIDFILE}`"
   echo ""
   exit 1
fi

if [[ "${mode}" = "fg" ]]
then
   echo  "Starting AIR Platform in foreground in [@activeProfile@] mode..."
   java ${JAVA_OPTIONS} -jar ${AIR_PLATFORM_JAR}
else
   nohup java ${JAVA_OPTIONS}  -jar ${AIR_PLATFORM_JAR} > /dev/null 2>&1 &
   echo  "AirCommunity Platform will be started in background in [@activeProfile@] mode."
   echo  "Check logs at ${AIR_PLATFORM_LOG} for details."
fi
echo ""