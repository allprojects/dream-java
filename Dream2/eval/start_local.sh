java -cp DREAM.jar dream.eval.StartLockManager lock-manager 9999 &
sleep 1
java -cp DREAM.jar dream.eval.StartServer server1 9001 &
sleep 1
java -cp DREAM.jar dream.eval.StartServer server2 9002 reds-tcp:localhost:9001 &
sleep 1
java -cp DREAM.jar dream.eval.EvalVarClient reds-tcp:localhost:9001 reds-tcp:localhost:9999 client1 v1 100 &
sleep 1
java -cp DREAM.jar dream.eval.EvalVarClient reds-tcp:localhost:9002 reds-tcp:localhost:9999 client2 v2 100 &
sleep 1
java -cp DREAM.jar dream.eval.EvalSignalClient reds-tcp:localhost:9001 reds-tcp:localhost:9999 client3 v3 v1@client1:v2@client2 &
sleep 1
java -cp DREAM.jar dream.eval.EvalSignalClient reds-tcp:localhost:9002 reds-tcp:localhost:9999 client4 v4 v3@client3 &
