#!/usr/bin/env bash

HOSTS_FILE_NAME=hosts_${PBS_JOBID}.txt
cat ${PBS_NODEFILE} | uniq > ${HOSTS_FILE_NAME}

J=0
while read HOST; do
    pbsdsh -o -h ${HOST} ~/IntObl2016/main_node.sh ${J} ${HOST} ${HOSTS_FILE_NAME} > node${J}.log 2>&1 &
    J=$((J+1))
done < ${HOSTS_FILE_NAME}
