#!/bin/bash

# 将该脚本放在jar包同级目录下运行
# 例如：test.jar 在/www/wwwroot/java目录下，那么就将该脚本放在/www/wwwroot/java目录下运行

CRT_DIR=$(pwd)
cd ..
BACKUP_DIR="$CRT_DIR/backup/mysql"
sudo mkdir -p "$BACKUP_DIR"
sudo chown centos:centos "$BACKUP_DIR"

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
read -p "请输入保留备份文件的数量（直接回车可使用默认值，默认是 3）: " -e -i "3" MAX_BACKUP_FILES
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
  # 根据提示添加密钥和区域
  aws configure
fi

# 定义cron脚本内容
MYSQL_SCRIPT=$(cat <<EOL
DATE=\$(date +"%Y%m%d%H%M%S")
RANDOM_STRING=\$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 6 | head -n 1)
BACKUP_FILE="$BACKUP_DIR/backup_all_databases_\${DATE}_\${RANDOM_STRING}.sql"
mysqldump -u root -p$DB_PASSWORD -h localhost --all-databases > \$BACKUP_FILE
ls -t $BACKUP_DIR | tail -n +\$((MAX_BACKUP_FILES + 1)) | xargs -I {} rm -- "$BACKUP_DIR/{}"
aws s3 sync $BACKUP_DIR s3://$BUCKET_NAME/backup/mysql
EOL
)

LOG_SCRIPT=$(cat <<EOL
aws s3 sync $CRT_DIR/logs s3://$BUCKET_NAME/backup/logs
EOL
)

# 将任务添加到当前用户的 crontab
(crontab -l 2>/dev/null; echo "0 0 * * 1 /bin/bash -c \"$MYSQL_SCRIPT\"") | crontab -
(crontab -l 2>/dev/null; echo "15 0 * * 1 /bin/bash -c \"$LOG_SCRIPT\"") | crontab -

echo "脚本配置完成。"
