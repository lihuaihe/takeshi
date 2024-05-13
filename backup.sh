#!/bin/bash

# 备份mysql数据库和jar包运行日志到aws的s3中的备份脚本
# 将该脚本放在jar包同级目录下运行
# 例如：test.jar 在/www/wwwroot/java目录下，那么就将该脚本放在/www/wwwroot/java目录下运行
# 使用命令【bash -c "$(curl https://raw.githubusercontent.com/lihuaihe/takeshi/master/backup.sh)"】然后按照提示输入即可

# ANSI颜色和格式定义
RED='\033[0;31m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color
# 警告消息
warning_message() {
    echo -e "${YELLOW}警告: $1${NC}"
}
# 提示消息
tip_message() {
    echo -e "${CYAN}提示: $1${NC}"
}
# 错误消息
error_message() {
    echo -e "${RED}错误: $1${NC}"
}

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
      warning_message "输入无效，$prompt"
    fi
  done
}

# 检查当前用户是否是 root
if [[ $EUID -eq 0 ]]; then
    error_message "请不要使用root用户执行此脚本"
    exit 1
fi

CRT_DIR=$(pwd)
tip_message "当前执行脚本的目录是：$CRT_DIR"

# 检查是否不存在 *.jar 文件
if [ ! "$(find . -maxdepth 1 -name '*.jar' -print -quit)" ]; then
    error_message "请在jar包目录下执行此脚本"
    exit 1
fi

# 检查是否不存在 logs 目录
if [ ! -d "logs" ]; then
    error_message "当前执行脚本的目录没有logs目录，请先启动jar包生成logs目录"
    exit 1
fi

cd ..
sudo mkdir -p backup
sudo chmod 777 backup
cd backup
mkdir -p mysql
BACKUP_DIR=$(pwd)

tip_message "备份的mysql数据目录是：$BACKUP_DIR/mysql"
tip_message "备份的jar日志目录是： $CRT_DIR/logs"

# 检测是否已安装 aws-cli
if ! command -v aws &> /dev/null; then
  # aws-cli 未安装，执行安装命令
  sudo yum remove -y awscli
  curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
  unzip -u awscliv2.zip
  sudo ./aws/install
  # 删除下载的文件
  rm -rf awscliv2.zip aws
  # 根据提示添加密钥和区域
  aws configure
  aws --verison
fi
AWS_PATH=$(which aws)
warning_message "aws命令路径是：$AWS_PATH"
# 列出存储桶列表
tip_message "这是是你的存储桶列表："
aws s3 ls

# 存储桶名称
BUCKET_NAME=$(get_non_empty_input "请输入存储桶名称: ")

# mysql的root用户密码
DB_PASSWORD=$(get_non_empty_input "请输入mysql的root用户密码: ")

# 保留备份文件的数量
read -p "请输入保留备份文件的数量（直接回车可使用默认值，默认是 3）: " MAX_BACKUP_FILES
MAX_BACKUP_FILES=${MAX_BACKUP_FILES:-3}
# 使用正则表达式检查输入是否为数字
if [[ ! "$MAX_BACKUP_FILES" =~ ^[0-9]+$ ]]; then
  warning_message "输入无效，使用默认值 3。"
  MAX_BACKUP_FILES="3"
fi

# 定义cron脚本内容
MYSQL_SCRIPT_FILE="$BACKUP_DIR/backup_mysql.sh"
cat <<EOL > $MYSQL_SCRIPT_FILE
#!/bin/bash
echo "mysql备份脚本执行开始：\$(date)"
DATE=\$(date +"%Y%m%d%H%M%S")
RANDOM_STRING=\$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 6 | head -n 1)
BACKUP_FILE="$BACKUP_DIR/mysql/backup_all_databases_\${DATE}_\${RANDOM_STRING}.sql"
mysqldump -u root -p$DB_PASSWORD -h localhost --all-databases > \$BACKUP_FILE
ls -t $BACKUP_DIR/mysql | tail -n +\$(($MAX_BACKUP_FILES + 1)) | xargs -I {} rm -- "$BACKUP_DIR/mysql/{}"
$AWS_PATH s3 sync $BACKUP_DIR/mysql s3://$BUCKET_NAME/backup/mysql
echo -e "mysql备份脚本执行完毕：\$(date)\n\n"
EOL

LOG_SCRIPT_FILE="$BACKUP_DIR/backup_log.sh"
cat <<EOL > $LOG_SCRIPT_FILE
#!/bin/bash
echo "log备份脚本执行开始：\$(date)"
$AWS_PATH s3 sync $CRT_DIR/logs s3://$BUCKET_NAME/backup/logs
echo -e "log备份脚本执行完毕：\$(date)\n\n"
EOL

sudo chmod 777 $MYSQL_SCRIPT_FILE
sudo chmod 777 $LOG_SCRIPT_FILE

# 将任务添加到当前用户的 crontab
(crontab -l 2>/dev/null; echo "0 0 * * 1 $MYSQL_SCRIPT_FILE >> $BACKUP_DIR/backup-mysql-info.log 2>&1") | crontab -
(crontab -l 2>/dev/null; echo "15 0 * * 1 $LOG_SCRIPT_FILE >> $BACKUP_DIR/backup-log-info.log 2>&1") | crontab -

tip_message "定时备份脚本配置完成。"
crontab -l
