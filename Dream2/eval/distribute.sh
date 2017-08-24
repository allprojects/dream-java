#!/bin/bash

source nodes.sh

for i in "${nodes[@]}"
do
    echo 'Sending files to '$i
    scp -r $1 $i:
done
