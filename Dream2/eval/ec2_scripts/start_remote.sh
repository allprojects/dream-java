#!/bin/bash

source nodes.sh

#j=java
j=./jdk1.8.0_144/jre/bin/java

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

ssh $lock_manager $j -cp DREAM.jar dream.eval.StartLockManager lock-manager 9999 &
sleep 1

ssh $broker1 $j -cp DREAM.jar dream.eval.StartServer broker1 9000 &
sleep 1
ssh $broker2 $j -cp DREAM.jar dream.eval.StartServer broker2 9000 $reds_broker1 &
sleep 1
ssh $broker3 $j -cp DREAM.jar dream.eval.StartServer broker3 9000 $reds_broker1 &
sleep 1
ssh $broker4 $j -cp DREAM.jar dream.eval.StartServer broker4 9000 $reds_broker1 &
sleep 1
ssh $broker5 $j -cp DREAM.jar dream.eval.StartServer broker5 9000 $reds_broker1 &
sleep 1
ssh $broker6 $j -cp DREAM.jar dream.eval.StartServer broker6 9000 $reds_broker1 &
sleep 1
ssh $broker7 $j -cp DREAM.jar dream.eval.StartServer broker7 9000 $reds_broker3 &
sleep 1
ssh $broker8 $j -cp DREAM.jar dream.eval.StartServer broker8 9000 $reds_broker3 &
sleep 1
ssh $broker9 $j -cp DREAM.jar dream.eval.StartServer broker9 9000 $reds_broker2 &
sleep 1
ssh $broker10 $j -cp DREAM.jar dream.eval.StartServer broker10 9000 $reds_broker2 &
sleep 1

###

ssh $client $j -cp DREAM.jar dream.eval.EvalVarClient $reds_broker4 $reds_lock_manager v1 v1 80000 2000 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalVarClient $reds_broker5 $reds_lock_manager v2 v2 80000 2000 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalVarClient $reds_broker6 $reds_lock_manager v3 v3 80000 2000 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalVarClient $reds_broker7 $reds_lock_manager v4 v4 80000 2000 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalVarClient $reds_broker7 $reds_lock_manager v5 v5 80000 2000 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalVarClient $reds_broker4 $reds_lock_manager v6 v6 80000 2000 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalVarClient $reds_broker5 $reds_lock_manager v7 v7 80000 2000 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalVarClient $reds_broker6 $reds_lock_manager v8 v8 80000 2000 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalVarClient $reds_broker7 $reds_lock_manager v9 v9 80000 2000 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalVarClient $reds_broker7 $reds_lock_manager v10 v10 80000 2000 &
sleep 1

###

ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker6 $reds_lock_manager a11 a11 v1@v1:v6@v6 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker7 $reds_lock_manager a12 a12 v1@v1 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker8 $reds_lock_manager a21 a21 a11@a11:a12@a12 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker9 $reds_lock_manager a22 a22 a12@a12 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker9 $reds_lock_manager a31 a31 a21@a21:a22@a22 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker8 $reds_lock_manager a32 a32 a22@a22 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker10 $reds_lock_manager a41 a41 a31@a31:a32@a32 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker7 $reds_lock_manager a42 a42 a32@a32 &
sleep 1

###

ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker6 $reds_lock_manager b11 b11 v2@v2:v7@v7 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker5 $reds_lock_manager b12 b12 v2@v2 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker4 $reds_lock_manager b21 b21 a12@a12:b11@b11:b12@b12 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker4 $reds_lock_manager b22 b22 b12@b12 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker4 $reds_lock_manager b31 b31 b21@b21:b22@b22 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker5 $reds_lock_manager b32 b32 b22@b22 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker6 $reds_lock_manager b41 b41 b31@b31:b32@b32 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker7 $reds_lock_manager b42 b42 b32@b32 &

###

ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker5 $reds_lock_manager c11 c11 v3@v3:v8@v8 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker7 $reds_lock_manager c12 c12 v3@v3 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker7 $reds_lock_manager c21 c21 b12@b12:c11@c11:c12@c12 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker4 $reds_lock_manager c22 c22 c12@c12 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker4 $reds_lock_manager c31 c31 c21@c21:c22@c22 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker6 $reds_lock_manager c32 c32 c22@c22 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker10 $reds_lock_manager c41 c41 c31@c31:c32@c32 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker9 $reds_lock_manager c42 c42 c32@c32 &

###

ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker8 $reds_lock_manager d11 d11 v4@v4:v9@v9 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker8 $reds_lock_manager d12 d12 v4@v4 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker9 $reds_lock_manager d21 d21 c12@c12:d11@d11:d12@d12 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker9 $reds_lock_manager d22 d22 d12@d12 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker10 $reds_lock_manager d31 d31 d21@d21:d22@d22 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker6 $reds_lock_manager d32 d32 d22@d22 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker8 $reds_lock_manager d41 d41 d31@d31:d32@d32 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker9 $reds_lock_manager d42 d42 d32@d32 &

###

ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker8 $reds_lock_manager e11 e11 v5@v5:v10@v10 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker8 $reds_lock_manager e12 e12 v5@v5 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker9 $reds_lock_manager e21 e21 d12@d12:e11@e11:e12@e12 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker9 $reds_lock_manager e22 e22 e12@e12 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker10 $reds_lock_manager e31 e31 e21@e21:e22@e22 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker6 $reds_lock_manager e32 e32 e22@e22 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker8 $reds_lock_manager e41 e41 e31@e31:e32@e32 &
sleep 1
ssh $client $j -cp DREAM.jar dream.eval.EvalSignalClient $reds_broker9 $reds_lock_manager e42 e42 e32@e32 &
