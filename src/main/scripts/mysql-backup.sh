#!/bin/bash

# crontab -e
# At 03:00 AM each day
# 0 3 * * * sh /opt/air-prd/mysql-backup.sh >> /dev/null 2>&1

AIR_HOME=/opt/air-prd

host="10.70.80.91"
username="airadmin"
password="air@p0o9i8u7"
database="air_platform_prd_v2"

charset="UTF8"
date=$(date +%Y%m%d)
time=$(date +%H%M%S)
filename="$database.$date$time.sql"
backup_dir="${AIR_HOME}/backup/mysql"

mkdir -p $backup_dir
cd $backup_dir

# perform backup
mysqldump -h$host -u$username -p$password --opt --default-character-set=$charset -q $database | gzip >  $backup_dir/$filename.gz

# delete old backup 7 days ago
find $backup_dir -name "*.sql.gz" -type f -mtime +7 -exec rm {} \; > /dev/null 2>&1