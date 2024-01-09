#!/bin/bash

# 备份mysql数据库和jar包运行日志到aws的s3中的备份脚本
# 将该脚本放在jar包同级目录下运行
# 例如：test.jar 在/www/wwwroot/java目录下，那么就将该脚本放在/www/wwwroot/java目录下运行

# 检查当前用户是否是 root
if [[ $EUID -eq 0 ]]; then
    echo "请不要使用root用户执行此脚本"
    exit 1
fi

CRT_DIR=$(pwd)
echo "当前目录：$CRT_DIR"

# 检查是否不存在 *.jar 文件
if ! [ -e "$CRT_DIR"/*.jar ]; then
    echo "请在jar包目录下执行此脚本"
    exit 1
fi

# 检查是否不存在 logs 目录
if [ ! -d "$CRT_DIR/logs" ]; then
    echo "当前执行脚本的目录没有logs目录，请先启动jar包生成logs目录"
    exit 1
fi

cd ../
sudo mkdir -p backup
sudo chmod 777 backup
BACKUP_DIR="$(pwd)/backup/mysql"
mkdir -p $BACKUP_DIR

echo "备份的mysql数据目录是：$BACKUP_DIR"
echo "备份的jar日志目录是： $CRT_DIR/logs"


# 获取用户输入的字符串，直到输入为非空值
function get_non_empty_input() {
  local prompt="$1"
  local input
  while true; do
    read -p "$prompt" input
    if [[ -n "$input" ]]; then
      echo "$input"
      break
    else
      echo "输入无效，$prompt"
    fi
  done
}

# 存储桶名称
BUCKET_NAME=$(get_non_empty_input "请输入存储桶名称: ")

# mysql的root用户密码
DB_PASSWORD=$(get_non_empty_input "请输入mysql的root用户密码: ")

# 保留备份文件的数量
read -p "请输入保留备份文件的数量（直接回车可使用默认值，默认是 3）: " MAX_BACKUP_FILES
MAX_BACKUP_FILES=${MAX_BACKUP_FILES:-3}
# 使用正则表达式检查输入是否为数字
if [[ ! "$MAX_BACKUP_FILES" =~ ^[0-9]+$ ]]; then
  echo "输入无效，使用默认值 3。"
  MAX_BACKUP_FILES="3"
fi

# 检测是否已安装 aws-cli
if ! command -v aws &> /dev/null; then
  # aws-cli 未安装，执行安装命令
  sudo yum remove -y awscli
  curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
  unzip -u awscliv2.zip
  sudo ./aws/install
  aws --verison
  # 根据提示添加密钥和区域
  aws configure
fi

# 定义cron脚本内容
MYSQL_SCRIPT_FILE="$(pwd)/backup/backup_mysql.sh"
cat <<EOL > $MYSQL_SCRIPT_FILE
#!/bin/bash
DATE=\$(date +"%Y%m%d%H%M%S")
RANDOM_STRING=\$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 6 | head -n 1)
BACKUP_FILE="$BACKUP_DIR/backup_all_databases_\${DATE}_\${RANDOM_STRING}.sql"
mysqldump -u root -p$DB_PASSWORD -h localhost --all-databases > \$BACKUP_FILE
ls -t $BACKUP_DIR | tail -n +\$(($MAX_BACKUP_FILES + 1)) | xargs -I {} rm -- "$BACKUP_DIR/{}"
aws s3 sync $BACKUP_DIR s3://$BUCKET_NAME/backup/mysql
EOL

LOG_SCRIPT_FILE="$(pwd)/backup/backup_log.sh"
cat <<EOL > $LOG_SCRIPT_FILE
#!/bin/bash
aws s3 sync $CRT_DIR/logs s3://$BUCKET_NAME/backup/logs
EOL

sudo chmod 777 $MYSQL_SCRIPT_FILE
sudo chmod 777 $LOG_SCRIPT_FILE

# 将任务添加到当前用户的 crontab
(crontab -l 2>/dev/null; echo "0 0 * * 1 $MYSQL_SCRIPT_FILE") | crontab -
(crontab -l 2>/dev/null; echo "15 0 * * 1 $LOG_SCRIPT_FILE") | crontab -

echo "定时备份脚本配置完成。"
crontab -l
