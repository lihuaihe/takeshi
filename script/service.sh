#!/bin/bash

#启动jar包的脚本，springboot的jar开启了优雅关闭，且设置了ApplicationPidFileWriter的监听器，使用该脚本可以优雅关闭jar
userLanguage='en'
userRegion='US'
userTimezone='UTC'

#jar包模块名：例如：takeshi-app
APP_NAME="$2"
#完整的jar包名称
JAR_FILE="prod-${APP_NAME}-0.0.1-SNAPSHOT.jar"
#ApplicationPidFileWriter监听器生成的pid文件
PID_FILE_PATH="./pid/${APP_NAME}.pid"

#启动方法
function start() {
  if [ -f "$PID_FILE_PATH" ] && ps -p "$(< "$PID_FILE_PATH")" >/dev/null; then
    echo_with_timestamp "${JAR_FILE} is already running with PID $(< "$PID_FILE_PATH")"
  else
    #设置jar包启动参数
    JAVA_OPTS="-Xms256m -Xmx1024m "

    if [[ ${userLanguage} ]]; then
      #设置JVM默认语言
      JAVA_OPTS+="-Duser.language=${userLanguage} "
    fi

    if [[ ${userRegion} ]]; then
      #设置JVM默认区域
      JAVA_OPTS+="-Duser.region=${userRegion} "
    fi

    if [[ ${userTimezone} ]]; then
      #设置JVM默认时区
      JAVA_OPTS+="-Duser.timezone=${userTimezone} "
    fi

    #组合启动命令
    command="nohup java ${JAVA_OPTS}-jar ${JAR_FILE} >/dev/null 2>&1 &"
    echo_with_timestamp "$command"
    eval "$command"
    echo_with_timestamp "${JAR_FILE} start success with PID $!"
  fi
}

#停止方法
function stop() {
  if [ -f "$PID_FILE_PATH" ] && ps -p "$(< "$PID_FILE_PATH")" >/dev/null; then
    PID=$(< "$PID_FILE_PATH")
    echo_with_timestamp "Waiting ${JAR_FILE} (pid $PID) to die..."
    xargs kill <"$PID_FILE_PATH"
    while ps -p "$PID" >/dev/null; do
      sleep 1
    done
    echo_with_timestamp "${JAR_FILE} kill success with PID $PID"
  else
    echo_with_timestamp "${JAR_FILE} is not running"
  fi
}

#输出运行状态
function status() {
  if [ -f "$PID_FILE_PATH" ] && ps -p "$(< "$PID_FILE_PATH")" >/dev/null; then
    echo_with_timestamp "${JAR_FILE} is running with PID $(< "$PID_FILE_PATH")"
  else
    echo_with_timestamp "${JAR_FILE} is not running"
  fi
}

#重启
function restart() {
  stop
  start
}

# 带时间戳的输出
function echo_with_timestamp() {
  echo "[$(date '+%Y-%m-%d %H:%M:%S %Z')] $1"
}


#根据输入参数，选择执行对应方法，不输入则执行使用说明
case "$1" in
start)
  start
  ;;
stop)
  stop
  ;;
status)
  status
  ;;
restart)
  restart
  ;;
*)
  echo "Usage: $0 {start|stop|status|restart}"
  exit 1
  ;;
esac

exit 0
