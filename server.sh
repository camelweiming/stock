#!/bin/bash

# 定义 Spring Boot JAR 文件的路径
SPRINGBOOT_JAR_PATH="target/stock-analysis-1.0-SNAPSHOT.jar"
# 定义 Spring Boot 应用的日志文件路径
SPRINGBOOT_LOG_PATH="springboot.log"
# 定义 Vue 项目的目录路径
VUE_PROJECT_PATH="front"
# 定义 Vue 应用的端口号
VUE_PORT=80

function start_apps() {
    # 启动 Spring Boot 应用
    if [ ! -f "$SPRINGBOOT_JAR_PATH" ]; then
        echo "Spring Boot JAR 文件 $SPRINGBOOT_JAR_PATH 不存在，请检查路径。"
        return 1
    fi
    nohup java -jar $SPRINGBOOT_JAR_PATH > $SPRINGBOOT_LOG_PATH 2>&1 &
    echo "Spring Boot 应用已启动，日志文件路径: $SPRINGBOOT_LOG_PATH"

    # 启动 Vue 应用
    if [ ! -d "$VUE_PROJECT_PATH" ]; then
        echo "Vue 项目目录 $VUE_PROJECT_PATH 不存在，请检查路径。"
        return 1
    fi
    cd $VUE_PROJECT_PATH
    nohup npm run serve -- --port $VUE_PORT > ../vue.log 2>&1 &
    echo "Vue 应用已启动，端口号: $VUE_PORT，日志文件路径: vue.log"
}

function stop_apps() {
    # 停止 Spring Boot 应用
    SPRINGBOOT_JAR_NAME=$(basename $SPRINGBOOT_JAR_PATH)
    PID=$(ps -ef | grep $SPRINGBOOT_JAR_NAME | grep -v grep | awk '{print $2}')
    if [ -n "$PID" ]; then
        echo "正在停止 Spring Boot 应用，进程 ID: $PID"
        kill $PID
        sleep 5
        PID=$(ps -ef | grep $SPRINGBOOT_JAR_NAME | grep -v grep | awk '{print $2}')
        if [ -n "$PID" ]; then
            echo "Spring Boot 应用未正常停止，正在强制终止，进程 ID: $PID"
            kill -9 $PID
        else
            echo "Spring Boot 应用已成功停止。"
        fi
    else
        echo "Spring Boot 应用未在运行。"
    fi

    # 停止 Vue 应用
    PID=$(lsof -t -i:$VUE_PORT)
    if [ -n "$PID" ]; then
        echo "正在停止 Vue 应用，进程 ID: $PID"
        kill $PID
        sleep 5
        PID=$(lsof -t -i:$VUE_PORT)
        if [ -n "$PID" ]; then
            echo "Vue 应用未正常停止，正在强制终止，进程 ID: $PID"
            kill -9 $PID
        else
            echo "Vue 应用已成功停止。"
        fi
    else
        echo "Vue 应用未在运行。"
    fi
}

if [ "$1" = "start" ]; then
    start_apps
elif [ "$1" = "stop" ]; then
    stop_apps
else
    echo "用法: $0 [start|stop]"
    exit 1
fi