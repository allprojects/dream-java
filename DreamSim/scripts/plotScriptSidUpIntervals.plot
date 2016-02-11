set terminal postscript enhance color dashed dl 4 font 16
set pointsize 3
# set size ratio 0.55

set style line 1 lw 4 lt rgb 'orange' pt 1
set style line 2 lw 4 lt rgb 'black' pt 6
set style line 3 lw 4 lt rgb 'green' pt 8
set style line 4 lw 4 lt rgb 'blue' pt 3
set style line 5 lw 4 lt rgb 'red' pt 2 

set key above
set log y

############
# LOCALITY #
############

set xlabel "Degree of locality" offset 0,0.2

set output "../graphs/localityDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0

plot "../resultsAvg/locality_causal_DelayAvg" u 1:2:3 t "Causal" w yerrorbars ls 1, \
"../resultsAvg/locality_single_glitch_free_DelayAvg" u 1:2:3 t "Single" w yerrorbars ls 2, \
"../resultsAvg/locality_complete_glitch_free_DelayAvg" u 1:2:3 t "Complete" w yerrorbars ls 3, \
"../resultsAvg/locality_atomic_DelayAvg" u 1:2:3 t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/locality_sid_up_DelayAvg" u 1:2:3 t "Sid Up" w yerrorbars ls 5, \
"../resultsAvg/locality_causal_DelayAvg" u 1:2 notitle w lines ls 1, \
"../resultsAvg/locality_single_glitch_free_DelayAvg" u 1:2 notitle w lines ls 2, \
"../resultsAvg/locality_complete_glitch_free_DelayAvg" u 1:2 notitle w lines ls 3, \
"../resultsAvg/locality_atomic_DelayAvg" u 1:2 notitle w lines ls 4, \
"../resultsAvg/locality_sid_up_DelayAvg" u 1:2 notitle w lines ls 5

set output "../graphs/localityTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/locality_causal_TrafficByte" u ($1):($8/1000):($15/1000) t "Causal" w yerrorbars ls 1, \
"../resultsAvg/locality_single_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Single" w yerrorbars ls 2, \
"../resultsAvg/locality_complete_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Complete" w yerrorbars ls 3, \
"../resultsAvg/locality_atomic_TrafficByte" u ($1):($8/1000):($15/1000) t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/locality_sid_up_TrafficByte" u ($1):($8/1000):($15/1000) t "Sid Up" w yerrorbars ls 5, \
"../resultsAvg/locality_causal_TrafficByte" u ($1):($8/1000) notitle w lines ls 1, \
"../resultsAvg/locality_single_glitch_free_TrafficByte" u ($1):($8/1000) notitle w lines ls 2, \
"../resultsAvg/locality_complete_glitch_free_TrafficByte" u ($1):($8/1000) notitle w lines ls 3, \
"../resultsAvg/locality_atomic_TrafficByte" u ($1):($8/1000) notitle w lines ls 4, \
"../resultsAvg/locality_sid_up_TrafficByte" u ($1):($8/1000) notitle w lines ls 5

#####################
# NUMBER OF BROKERS #
#####################

set xlabel "Number of brokers" offset 0,0.2

set output "../graphs/numBrokersDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0

plot "../resultsAvg/numBrokers_causal_DelayAvg" u 1:2:3 t "Causal" w yerrorbars ls 1, \
"../resultsAvg/numBrokers_single_glitch_free_DelayAvg" u 1:2:3 t "Single" w yerrorbars ls 2, \
"../resultsAvg/numBrokers_complete_glitch_free_DelayAvg" u 1:2:3 t "Complete" w yerrorbars ls 3, \
"../resultsAvg/numBrokers_atomic_DelayAvg" u 1:2:3 t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/numBrokers_sid_up_DelayAvg" u 1:2:3 t "Sid Up" w yerrorbars ls 5, \
"../resultsAvg/numBrokers_causal_DelayAvg" u 1:2 notitle w lines ls 1, \
"../resultsAvg/numBrokers_single_glitch_free_DelayAvg" u 1:2 notitle w lines ls 2, \
"../resultsAvg/numBrokers_complete_glitch_free_DelayAvg" u 1:2 notitle w lines ls 3, \
"../resultsAvg/numBrokers_atomic_DelayAvg" u 1:2 notitle w lines ls 4, \
"../resultsAvg/numBrokers_sid_up_DelayAvg" u 1:2 notitle w lines ls 5

set output "../graphs/numBrokersTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/numBrokers_causal_TrafficByte" u ($1):($8/1000):($15/1000) t "Causal" w yerrorbars ls 1, \
"../resultsAvg/numBrokers_single_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Single" w yerrorbars ls 2, \
"../resultsAvg/numBrokers_complete_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Complete" w yerrorbars ls 3, \
"../resultsAvg/numBrokers_atomic_TrafficByte" u ($1):($8/1000):($15/1000) t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/numBrokers_sid_up_TrafficByte" u ($1):($8/1000):($15/1000) t "Sid Up" w yerrorbars ls 5, \
"../resultsAvg/numBrokers_causal_TrafficByte" u ($1):($8/1000) notitle w lines ls 1, \
"../resultsAvg/numBrokers_single_glitch_free_TrafficByte" u ($1):($8/1000) notitle w lines ls 2, \
"../resultsAvg/numBrokers_complete_glitch_free_TrafficByte" u ($1):($8/1000) notitle w lines ls 3, \
"../resultsAvg/numBrokers_atomic_TrafficByte" u ($1):($8/1000) notitle w lines ls 4, \
"../resultsAvg/numBrokers_sid_up_TrafficByte" u ($1):($8/1000) notitle w lines ls 5

##################
# NUMBER OF VARS #
##################

set xlabel "Number of sources (vars) in the graph" offset 0,0.2

set output "../graphs/numVarsDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0

plot "../resultsAvg/numVars_causal_DelayAvg" u 1:2:3 t "Causal" w yerrorbars ls 1, \
"../resultsAvg/numVars_single_glitch_free_DelayAvg" u 1:2:3 t "Single" w yerrorbars ls 2, \
"../resultsAvg/numVars_complete_glitch_free_DelayAvg" u 1:2:3 t "Complete" w yerrorbars ls 3, \
"../resultsAvg/numVars_atomic_DelayAvg" u 1:2:3 t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/numVars_sid_up_DelayAvg" u 1:2:3 t "Sid Up" w yerrorbars ls 5, \
"../resultsAvg/numVars_causal_DelayAvg" u 1:2 notitle w lines ls 1, \
"../resultsAvg/numVars_single_glitch_free_DelayAvg" u 1:2 notitle w lines ls 2, \
"../resultsAvg/numVars_complete_glitch_free_DelayAvg" u 1:2 notitle w lines ls 3, \
"../resultsAvg/numVars_atomic_DelayAvg" u 1:2 notitle w lines ls 4, \
"../resultsAvg/numVars_sid_up_DelayAvg" u 1:2 notitle w lines ls 5

set output "../graphs/numVarsTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/numVars_causal_TrafficByte" u ($1):($8/1000):($15/1000) t "Causal" w yerrorbars ls 1, \
"../resultsAvg/numVars_single_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Single" w yerrorbars ls 2, \
"../resultsAvg/numVars_complete_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Complete" w yerrorbars ls 3, \
"../resultsAvg/numVars_atomic_TrafficByte" u ($1):($8/1000):($15/1000) t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/numVars_sid_up_TrafficByte" u ($1):($8/1000):($15/1000) t "Sid Up" w yerrorbars ls 5, \
"../resultsAvg/numVars_causal_TrafficByte" u ($1):($8/1000) notitle w lines ls 1, \
"../resultsAvg/numVars_single_glitch_free_TrafficByte" u ($1):($8/1000) notitle w lines ls 2, \
"../resultsAvg/numVars_complete_glitch_free_TrafficByte" u ($1):($8/1000) notitle w lines ls 3, \
"../resultsAvg/numVars_atomic_TrafficByte" u ($1):($8/1000) notitle w lines ls 4, \
"../resultsAvg/numVars_sid_up_TrafficByte" u ($1):($8/1000) notitle w lines ls 5

###############
# GRAPH DEPTH #
###############

set xlabel "Depth of the dependency graph" offset 0,0.2

set output "../graphs/graphDepthDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0

plot "../resultsAvg/graphDepth_causal_DelayAvg" u 1:2:3 t "Causal" w yerrorbars ls 1, \
"../resultsAvg/graphDepth_single_glitch_free_DelayAvg" u 1:2:3 t "Single" w yerrorbars ls 2, \
"../resultsAvg/graphDepth_complete_glitch_free_DelayAvg" u 1:2:3 t "Complete" w yerrorbars ls 3, \
"../resultsAvg/graphDepth_atomic_DelayAvg" u 1:2:3 t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/graphDepth_sid_up_DelayAvg" u 1:2:3 t "Sid Up" w yerrorbars ls 5, \
"../resultsAvg/graphDepth_causal_DelayAvg" u 1:2 notitle w lines ls 1, \
"../resultsAvg/graphDepth_single_glitch_free_DelayAvg" u 1:2 notitle w lines ls 2, \
"../resultsAvg/graphDepth_complete_glitch_free_DelayAvg" u 1:2 notitle w lines ls 3, \
"../resultsAvg/graphDepth_atomic_DelayAvg" u 1:2 notitle w lines ls 4, \
"../resultsAvg/graphDepth_sid_up_DelayAvg" u 1:2 notitle w lines ls 5

set output "../graphs/graphDepthTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/graphDepth_causal_TrafficByte" u ($1):($8/1000):($15/1000) t "Causal" w yerrorbars ls 1, \
"../resultsAvg/graphDepth_single_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Single" w yerrorbars ls 2, \
"../resultsAvg/graphDepth_complete_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Complete" w yerrorbars ls 3, \
"../resultsAvg/graphDepth_atomic_TrafficByte" u ($1):($8/1000):($15/1000) t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/graphDepth_sid_up_TrafficByte" u ($1):($8/1000):($15/1000) t "Sid Up" w yerrorbars ls 5, \
"../resultsAvg/graphDepth_causal_TrafficByte" u ($1):($8/1000) notitle w lines ls 1, \
"../resultsAvg/graphDepth_single_glitch_free_TrafficByte" u ($1):($8/1000) notitle w lines ls 2, \
"../resultsAvg/graphDepth_complete_glitch_free_TrafficByte" u ($1):($8/1000) notitle w lines ls 3, \
"../resultsAvg/graphDepth_atomic_TrafficByte" u ($1):($8/1000) notitle w lines ls 4, \
"../resultsAvg/graphDepth_sid_up_TrafficByte" u ($1):($8/1000) notitle w lines ls 5

#####################################
# NUMBER OF DEPENDENCIES PER SIGNAL #
#####################################

set xlabel "Number of dependencies per signal" offset 0,0.2

set output "../graphs/numGraphDependenciesDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0

plot "../resultsAvg/numGraphDependencies_causal_DelayAvg" u 1:2:3 t "Causal" w yerrorbars ls 1, \
"../resultsAvg/numGraphDependencies_single_glitch_free_DelayAvg" u 1:2:3 t "Single" w yerrorbars ls 2, \
"../resultsAvg/numGraphDependencies_complete_glitch_free_DelayAvg" u 1:2:3 t "Complete" w yerrorbars ls 3, \
"../resultsAvg/numGraphDependencies_atomic_DelayAvg" u 1:2:3 t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/numGraphDependencies_sid_up_DelayAvg" u 1:2:3 t "Sid Up" w yerrorbars ls 5, \
"../resultsAvg/numGraphDependencies_causal_DelayAvg" u 1:2 notitle w lines ls 1, \
"../resultsAvg/numGraphDependencies_single_glitch_free_DelayAvg" u 1:2 notitle w lines ls 2, \
"../resultsAvg/numGraphDependencies_complete_glitch_free_DelayAvg" u 1:2 notitle w lines ls 3, \
"../resultsAvg/numGraphDependencies_atomic_DelayAvg" u 1:2 notitle w lines ls 4, \
"../resultsAvg/numGraphDependencies_sid_up_DelayAvg" u 1:2 notitle w lines ls 5

set output "../graphs/numGraphDependenciesTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/numGraphDependencies_causal_TrafficByte" u ($1):($8/1000):($15/1000) t "Causal" w yerrorbars ls 1, \
"../resultsAvg/numGraphDependencies_single_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Single" w yerrorbars ls 2, \
"../resultsAvg/numGraphDependencies_complete_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Complete" w yerrorbars ls 3, \
"../resultsAvg/numGraphDependencies_atomic_TrafficByte" u ($1):($8/1000):($15/1000) t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/numGraphDependencies_sid_up_TrafficByte" u ($1):($8/1000):($15/1000) t "Sid Up" w yerrorbars ls 5, \
"../resultsAvg/numGraphDependencies_causal_TrafficByte" u ($1):($8/1000) notitle w lines ls 1, \
"../resultsAvg/numGraphDependencies_single_glitch_free_TrafficByte" u ($1):($8/1000) notitle w lines ls 2, \
"../resultsAvg/numGraphDependencies_complete_glitch_free_TrafficByte" u ($1):($8/1000) notitle w lines ls 3, \
"../resultsAvg/numGraphDependencies_atomic_TrafficByte" u ($1):($8/1000) notitle w lines ls 4, \
"../resultsAvg/numGraphDependencies_sid_up_TrafficByte" u ($1):($8/1000) notitle w lines ls 5

###########################
# GRAPH SHARE PROBABILITY #
###########################

set xlabel "Probability for a signal to depend on multiple sources" offset 0,0.2

set output "../graphs/graphShareDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0

plot "../resultsAvg/graphShare_causal_DelayAvg" u 1:2:3 t "Causal" w yerrorbars ls 1, \
"../resultsAvg/graphShare_single_glitch_free_DelayAvg" u 1:2:3 t "Single" w yerrorbars ls 2, \
"../resultsAvg/graphShare_complete_glitch_free_DelayAvg" u 1:2:3 t "Complete" w yerrorbars ls 3, \
"../resultsAvg/graphShare_atomic_DelayAvg" u 1:2:3 t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/graphShare_sid_up_DelayAvg" u 1:2:3 t "Sid Up" w yerrorbars ls 5, \
"../resultsAvg/graphShare_causal_DelayAvg" u 1:2 notitle w lines ls 1, \
"../resultsAvg/graphShare_single_glitch_free_DelayAvg" u 1:2 notitle w lines ls 2, \
"../resultsAvg/graphShare_complete_glitch_free_DelayAvg" u 1:2 notitle w lines ls 3, \
"../resultsAvg/graphShare_atomic_DelayAvg" u 1:2 notitle w lines ls 4, \
"../resultsAvg/graphShare_sid_up_DelayAvg" u 1:2 notitle w lines ls 5

set output "../graphs/graphShareTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/graphShare_causal_TrafficByte" u ($1):($8/1000):($15/1000) t "Causal" w yerrorbars ls 1, \
"../resultsAvg/graphShare_single_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Single" w yerrorbars ls 2, \
"../resultsAvg/graphShare_complete_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Complete" w yerrorbars ls 3, \
"../resultsAvg/graphShare_atomic_TrafficByte" u ($1):($8/1000):($15/1000) t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/graphShare_sid_up_TrafficByte" u ($1):($8/1000):($15/1000) t "Sid Up" w yerrorbars ls 5, \
"../resultsAvg/graphShare_causal_TrafficByte" u ($1):($8/1000) notitle w lines ls 1, \
"../resultsAvg/graphShare_single_glitch_free_TrafficByte" u ($1):($8/1000) notitle w lines ls 2, \
"../resultsAvg/graphShare_complete_glitch_free_TrafficByte" u ($1):($8/1000) notitle w lines ls 3, \
"../resultsAvg/graphShare_atomic_TrafficByte" u ($1):($8/1000) notitle w lines ls 4, \
"../resultsAvg/graphShare_sid_up_TrafficByte" u ($1):($8/1000) notitle w lines ls 5

#######################
# TIME BETWEEN EVENTS #
#######################

set xlabel "Publication frequency (k events/s)" offset 0,0.2
set log x

set output "../graphs/pubFrequencyDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0

plot "../resultsAvg/timeBetweenEvents_causal_DelayAvg" u (1000/$1):($2):($3) t "Causal" w yerrorbars ls 1, \
"../resultsAvg/timeBetweenEvents_single_glitch_free_DelayAvg" u (1000/$1):($2):($3) t "Single" w yerrorbars ls 2, \
"../resultsAvg/timeBetweenEvents_complete_glitch_free_DelayAvg" u (1000/$1):($2):($3) t "Complete" w yerrorbars ls 3, \
"../resultsAvg/timeBetweenEvents_atomic_DelayAvg" u (1000/$1):($2):($3) t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/timeBetweenEvents_sid_up_DelayAvg" u (1000/$1):($2):($3) t "Sid Up" w yerrorbars ls 5, \
"../resultsAvg/timeBetweenEvents_causal_DelayAvg" u (1000/$1):($2) notitle w lines ls 1, \
"../resultsAvg/timeBetweenEvents_single_glitch_free_DelayAvg" u (1000/$1):($2) notitle w lines ls 2, \
"../resultsAvg/timeBetweenEvents_complete_glitch_free_DelayAvg" u (1000/$1):($2) notitle w lines ls 3, \
"../resultsAvg/timeBetweenEvents_atomic_DelayAvg" u (1000/$1):($2) notitle w lines ls 4, \
"../resultsAvg/timeBetweenEvents_sid_up_DelayAvg" u (1000/$1):($2) notitle w lines ls 5

set output "../graphs/pubFrequencyTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/timeBetweenEvents_causal_TrafficByte" u (1000/$1):($8/1000):($15/1000) t "Causal" w yerrorbars ls 1, \
"../resultsAvg/timeBetweenEvents_single_glitch_free_TrafficByte" u (1000/$1):($8/1000):($15/1000) t "Single" w yerrorbars ls 2, \
"../resultsAvg/timeBetweenEvents_complete_glitch_free_TrafficByte" u (1000/$1):($8/1000):($15/1000) t "Complete" w yerrorbars ls 3, \
"../resultsAvg/timeBetweenEvents_atomic_TrafficByte" u (1000/$1):($8/1000):($15/1000) t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/timeBetweenEvents_sid_up_TrafficByte" u (1000/$1):($8/1000):($15/1000) t "Sid Up" w yerrorbars ls 5, \
"../resultsAvg/timeBetweenEvents_causal_TrafficByte" u (1000/$1):($8/1000) notitle w lines ls 1, \
"../resultsAvg/timeBetweenEvents_single_glitch_free_TrafficByte" u (1000/$1):($8/1000) notitle w lines ls 2, \
"../resultsAvg/timeBetweenEvents_complete_glitch_free_TrafficByte" u (1000/$1):($8/1000) notitle w lines ls 3, \
"../resultsAvg/timeBetweenEvents_atomic_TrafficByte" u (1000/$1):($8/1000) notitle w lines ls 4, \
"../resultsAvg/timeBetweenEvents_sid_up_TrafficByte" u (1000/$1):($8/1000) notitle w lines ls 5

######################
# TIME BETWEEN READS #
######################

set xlabel "Signal access frequency (k reads/s)" offset 0,0.2
set log x
set xr [0.1:2.5]

set output "../graphs/readFrequencyDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0

plot "../resultsAvg/timeBetweenReads_causal_DelayAvg" u (1000/$1):($2):($3) t "Causal" w yerrorbars ls 1, \
"../resultsAvg/timeBetweenReads_single_glitch_free_DelayAvg" u (1000/$1):($2):($3) t "Single" w yerrorbars ls 2, \
"../resultsAvg/timeBetweenReads_complete_glitch_free_DelayAvg" u (1000/$1):($2):($3) t "Complete" w yerrorbars ls 3, \
"../resultsAvg/timeBetweenReads_atomic_DelayAvg" u (1000/$1):($2):($3) t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/timeBetweenReads_sid_up_DelayAvg" u (1000/$1):($2):($3) t "Sid Up" w yerrorbars ls 5, \
"../resultsAvg/timeBetweenReads_causal_DelayAvg" u (1000/$1):($2) notitle w lines ls 1, \
"../resultsAvg/timeBetweenReads_single_glitch_free_DelayAvg" u (1000/$1):($2) notitle w lines ls 2, \
"../resultsAvg/timeBetweenReads_complete_glitch_free_DelayAvg" u (1000/$1):($2) notitle w lines ls 3, \
"../resultsAvg/timeBetweenReads_atomic_DelayAvg" u (1000/$1):($2) notitle w lines ls 4, \
"../resultsAvg/timeBetweenReads_sid_up_DelayAvg" u (1000/$1):($2) notitle w lines ls 5

set output "../graphs/readFrequencyTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/timeBetweenReads_causal_TrafficByte" u (1000/$1):($8/1000):($15/1000) t "Causal" w yerrorbars ls 1, \
"../resultsAvg/timeBetweenReads_single_glitch_free_TrafficByte" u (1000/$1):($8/1000):($15/1000) t "Single" w yerrorbars ls 2, \
"../resultsAvg/timeBetweenReads_complete_glitch_free_TrafficByte" u (1000/$1):($8/1000):($15/1000) t "Complete" w yerrorbars ls 3, \
"../resultsAvg/timeBetweenReads_atomic_TrafficByte" u (1000/$1):($8/1000):($15/1000) t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/timeBetweenReads_sid_up_TrafficByte" u (1000/$1):($8/1000):($15/1000) t "Sid Up" w yerrorbars ls 5, \
"../resultsAvg/timeBetweenReads_causal_TrafficByte" u (1000/$1):($8/1000) notitle w lines ls 1, \
"../resultsAvg/timeBetweenReads_single_glitch_free_TrafficByte" u (1000/$1):($8/1000) notitle w lines ls 2, \
"../resultsAvg/timeBetweenReads_complete_glitch_free_TrafficByte" u (1000/$1):($8/1000) notitle w lines ls 3, \
"../resultsAvg/timeBetweenReads_atomic_TrafficByte" u (1000/$1):($8/1000) notitle w lines ls 4, \
"../resultsAvg/timeBetweenReads_sid_up_TrafficByte" u (1000/$1):($8/1000) notitle w lines ls 5

###########
# DEFAULT #
###########

reset

set style line 1 lw 2 lt rgb 'orange' pt 1
set style line 2 lw 2 lt rgb 'black' pt 6
set style line 3 lw 2 lt rgb 'green' pt 8
set style line 4 lw 2 lt rgb 'blue' pt 3
set style line 5 lw 2 lt rgb 'red' pt 2

set style data histogram
set style histogram cluster gap 5
set style fill solid border 0
set log y
unset xlabel
set xtics rotate by 35 offset -0.5,-2

set output "../graphs/defaultTraffic.ps"

plot "../resultsAvg/default" u 2:xticlabel(1) ls 1 title "Causal", \
"../resultsAvg/default" u 3:xticlabel(1) ls 2 title "Single", \
"../resultsAvg/default" u 4:xticlabel(1) ls 3 title "Complete", \
"../resultsAvg/default" u 5:xticlabel(1) ls 4 title "Atomic", \
"../resultsAvg/default" u 6:xticlabel(1) ls 5 title "Sid Up"