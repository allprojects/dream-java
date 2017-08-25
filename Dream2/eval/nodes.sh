#!/bin/bash

broker1=ip-172-31-43-217.eu-central-1.compute.internal
broker2=ip-172-31-47-82.eu-central-1.compute.internal
broker3=ip-172-31-41-23.eu-central-1.compute.internal
broker4=ip-172-31-32-245.eu-central-1.compute.internal
broker5=ip-172-31-34-178.eu-central-1.compute.internal
broker6=ip-172-31-39-44.eu-central-1.compute.internal
broker7=ip-172-31-44-11.eu-central-1.compute.internal
broker8=ip-172-31-47-133.eu-central-1.compute.internal
broker9=ip-172-31-44-19.eu-central-1.compute.internal
broker10=ip-172-31-42-224.eu-central-1.compute.internal

lock_manager=ip-172-31-39-95.eu-central-1.compute.internal

client=ip-172-31-43-235.eu-central-1.compute.internal

nodes=($broker1 $broker2 $broker3 $broker4 $broker5 $broker6 $broker7 $broker8 $broker9 $broker10 $lock_manager $client)
