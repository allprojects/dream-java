#!/bin/bash

source nodes.sh

for i in "${nodes[@]}"
do
    echo 'Killing java processes in '$i
    ssh $i killall -9 java
done
