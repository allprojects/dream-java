#!/bin/bash

source nodes.sh

for i in "${nodes[@]}"
do
    echo 'Gathering files from '$i
    scp $i:*.txt ./results/
done
