#!/usr/bin/env bash
#PBS -l walltime=24:00:00
#PBS -l pmem=2gb
#PBS -l nodes=390:ppn=12
#PBS -q plgrid
#PBS -N plggintob16
#PBS -A intob2016

. /etc/bashrc
cd ~/IntObl2016

module load plgrid/tools/sbt/0.13.9
module load plgrid/tools/java8/oracle/1.8.0

sbt clean compile

HOSTS_FILE_NAME=hosts_${PBS_JOBID}.txt
cat ${PBS_NODEFILE} | uniq > ${HOSTS_FILE_NAME}

COORDINATOR_HOST=`head -n 1 ${HOSTS_FILE_NAME}`

J=0
while read HOST; do
    pbsdsh -o -h ${HOST} ~/IntObl2016/main_node.sh ${J} ${HOST} ${COORDINATOR_HOST} ${HOSTS_FILE_NAME} 2>&1 &
    J=$((J+1))
done < ${HOSTS_FILE_NAME}
