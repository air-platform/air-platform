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

kill `cat ${AIR_PLATFORM_HOME}/@pidfile@`
echo "AIR Platform v${AIR_PLATFORM_VERSION} stopped."