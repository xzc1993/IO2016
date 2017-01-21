#!/usr/bin/env bash
#PBS -l walltime=01:00:00
#PBS -l pmem=1gb
#PBS -l nodes=1:ppn=12
#PBS -q plgrid
#PBS -N plggintob16
#PBS -A intob2016

. /etc/bashrc
cd ~/IntObl2016

module load plgrid/tools/sbt/0.13.9
module load plgrid/tools/java8/oracle/1.8.0

HOSTS_FILE_NAME=hosts_${PBS_JOBID}.txt
cat ${PBS_NODEFILE} | uniq > ${HOSTS_FILE_NAME}

COORDINATOR_HOST=`hostname`

HOSTS_WITHOUT_COORDINATOR_FILE_NAME=hosts_no_coord_${PBS_JOBID}.txt
grep -v ${COORDINATOR_HOST} ${HOSTS_FILE_NAME} > ${HOSTS_WITHOUT_COORDINATOR_FILE_NAME}

DRAWING_FILE_NAME=map_${PBS_JOBID}.png

J=1
while read HOST; do
    pbsdsh -o -h ${HOST} ~/IntObl2016/main_node.sh ${J} ${HOST} ${COORDINATOR_HOST} ${HOSTS_FILE_NAME} ${DRAWING_FILE_NAME} $1 $2 2>&1 &
    J=$((J+1))
done < ${HOSTS_WITHOUT_COORDINATOR_FILE_NAME}

~/IntObl2016/main_node.sh 0 ${COORDINATOR_HOST} ${COORDINATOR_HOST} ${HOSTS_FILE_NAME} ${DRAWING_FILE_NAME} $1 $2 2>&1
