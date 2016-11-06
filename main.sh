#!/usr/bin/env bash

HOSTS=(`cat $PBS_NODEFILE | uniq`)

J=0
for host in ${HOSTS[*]}; do
    pbsdsh -o -h ${host} ~/IntObl2016/main_node.sh ${J} > node${J}.log 2>&1 &
    J=$((J+1))
done
