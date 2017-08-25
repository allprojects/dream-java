#!/bin/bash

source nodes.sh

reds_lock_manager='reds-tcp:'$lock_manager':9999'
reds_broker1='reds-tcp:'$broker1':9000'
reds_broker2='reds-tcp:'$broker2':9000'
reds_broker3='reds-tcp:'$broker3':9000'
reds_broker4='reds-tcp:'$broker4':9000'
reds_broker5='reds-tcp:'$broker5':9000'
reds_broker6='reds-tcp:'$broker6':9000'
reds_broker7='reds-tcp:'$broker7':9000'
reds_broker8='reds-tcp:'$broker8':9000'
reds_broker9='reds-tcp:'$broker9':9000'
reds_broker10='reds-tcp:'$broker10':9000'

ssh $lock_manager java -cp DREAM.jar dream.eval.StartLockManager lock-manager 9999 &
sleep 1

ssh $broker1 java -cp DREAM.jar dream.eval.StartServer broker1 9000 &
sleep 1
ssh $broker2 java -cp DREAM.jar dream.eval.StartServer broker2 9000 $reds_broker1 &
sleep 1
ssh $broker3 java -cp DREAM.jar dream.eval.StartServer broker3 9000 $reds_broker1 &
sleep 1
ssh $broker4 java -cp DREAM.jar dream.eval.StartServer broker4 9000 $reds_broker1 &
sleep 1
ssh $broker5 java -cp DREAM.jar dream.eval.StartServer broker5 9000 $reds_broker1 &
sleep 1
ssh $broker6 java -cp DREAM.jar dream.eval.StartServer broker6 9000 $reds_broker1 &
sleep 1
ssh $broker7 java -cp DREAM.jar dream.eval.StartServer broker7 9000 $reds_broker3 &
sleep 1
ssh $broker8 java -cp DREAM.jar dream.eval.StartServer broker8 9000 $reds_broker3 &
sleep 1
ssh $broker9 java -cp DREAM.jar dream.eval.StartServer broker9 9000 $reds_broker2 &
sleep 1
ssh $broker10 java -cp DREAM.jar dream.eval.StartServer broker10 9000 $reds_broker2 &
sleep 1

ssh $client java -cp DREAM.jar dream.eval.EvalVarClient $reds_broker4 $reds_lock_manager v1 v1 100 &
sleep 1
ssh $client java -cp DREAM.jar dream.eval.EvalVarClient $reds_broker5 $reds_lock_manager v2 v2 100 &
sleep 1

ssh $client java -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker6 $reds_lock_manager a11 a11 v1@v1 &
sleep 1
ssh $client java -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker7 $reds_lock_manager a12 a12 v1@v1 &
sleep 1
ssh $client java -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker8 $reds_lock_manager a21 a21 a11@a11:a12@a12 &
sleep 1
ssh $client java -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker9 $reds_lock_manager a22 a22 a12@a12 &
sleep 1
ssh $client java -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker9 $reds_lock_manager a31 a31 a21@a21:a22@a22 &
sleep 1
ssh $client java -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker8 $reds_lock_manager a32 a32 a22@a22 &
sleep 1
ssh $client java -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker10 $reds_lock_manager a41 a41 a31@a31:a32@a32 &
sleep 1
ssh $client java -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker7 $reds_lock_manager a42 a42 a32@a32 &
sleep 1

ssh $client java -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker6 $reds_lock_manager b11 b11 v2@v2 &
sleep 1
ssh $client java -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker5 $reds_lock_manager b12 b12 v2@v2 &
sleep 1
ssh $client java -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker4 $reds_lock_manager b21 b21 b11@b11:b12@b12 &
sleep 1
ssh $client java -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker4 $reds_lock_manager b22 b22 b12@b12 &
sleep 1
ssh $client java -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker4 $reds_lock_manager b31 b31 b21@b21:b22@b22 &
sleep 1
ssh $client java -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker5 $reds_lock_manager b32 b32 b22@b22 &
sleep 1
ssh $client java -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker6 $reds_lock_manager b41 b41 b31@b31:b32@b32 &
sleep 1
ssh $client java -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker7 $reds_lock_manager b42 b42 b32@b32 &
