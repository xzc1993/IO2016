#!/usr/bin/env bash

. /etc/bashrc
cd ~/IntObl2016

module load plgrid/tools/sbt/0.13.9
module load plgrid/tools/java8/oracle/1.8.0

sbt "run -i $1 -h $2 -c $3 -x $4 -d $5 -b $6 -e $7 -s $8 -m data/MazeRoboLabFullMap.roson"
