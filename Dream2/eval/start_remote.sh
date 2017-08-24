#!/bin/bash

source nodes.sh

lock_manager='reds-tcp:'$node1':9999'
server1='reds-tcp:'$node1':9000'
server2='reds-tcp:'$node2':9000'

ssh $node1 java -cp DREAM.jar dream.eval.StartLockManager lock-manager 9999 &
sleep 2
ssh $node1 java -cp DREAM.jar dream.eval.StartServer server1 9000 &
sleep 2
ssh $node2 java -cp DREAM.jar dream.eval.StartServer server2 9000 $server1 &
sleep 2
ssh $node2 java -cp DREAM.jar dream.eval.EvalVarClient $server1 $lock_manager client1 v1 100 &
sleep 2
ssh $node2 java -cp DREAM.jar dream.eval.EvalVarClient $server2 $lock_manager client2 v2 100 &
sleep 2
ssh $node2 java -cp DREAM.jar dream.eval.EvalSignalClient $server1 $lock_manager client3 v3 v1@client1:v2@client2 &
sleep 2
ssh $node2 java -cp DREAM.jar dream.eval.EvalSignalClient $server2 $lock_manager client4 v4 v3@client3 &
