set terminal postscript enhance color dashed dl 4 font 16
set pointsize 3
# set size ratio 0.55

set style line 1 lw 4 lt rgb 'red' pt 1
set style line 2 lw 4 lt rgb 'black' pt 6
set style line 3 lw 4 lt rgb 'orange' pt 8
set style line 4 lw 4 lt rgb 'brown' pt 10 

set key above
set log y

############
# LOCALITY #
############

set xlabel "Degree of locality" offset 0,0.5

set output "../graphs/localityDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0

plot "../resultsAvg/locality_causal_DelayAvg" u 1:2:3 t "Causal" w yerrorbars ls 1, \
"../resultsAvg/locality_single_glitch_free_DelayAvg" u 1:2:3 t "Single" w yerrorbars ls 2, \
"../resultsAvg/locality_complete_glitch_free_DelayAvg" u 1:2:3 t "Complete" w yerrorbars ls 3, \
"../resultsAvg/locality_atomic_DelayAvg" u 1:2:3 t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/locality_causal_DelayAvg" u 1:2 t "Causal" w lines ls 1, \
"../resultsAvg/locality_single_glitch_free_DelayAvg" u 1:2 t "Single" w lines ls 2, \
"../resultsAvg/locality_complete_glitch_free_DelayAvg" u 1:2 t "Complete" w lines ls 3, \
"../resultsAvg/locality_atomic_DelayAvg" u 1:2 t "Atomic" w lines ls 4

set output "../graphs/localityTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/locality_causal_TrafficByte" u ($1):($8/1000):($15/1000) t "Causal" w yerrorbars ls 1, \
"../resultsAvg/locality_single_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Single" w yerrorbars ls 2, \
"../resultsAvg/locality_complete_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Complete" w yerrorbars ls 3, \
"../resultsAvg/locality_atomic_TrafficByte" u ($1):($8/1000):($15/1000) t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/locality_causal_TrafficByte" u ($1):($8/1000) t "Causal" w lines ls 1, \
"../resultsAvg/locality_single_glitch_free_TrafficByte" u ($1):($8/1000) t "Single" w lines ls 2, \
"../resultsAvg/locality_complete_glitch_free_TrafficByte" u ($1):($8/1000) t "Complete" w lines ls 3, \
"../resultsAvg/locality_atomic_TrafficByte" u ($1):($8/1000) t "Atomic" w lines ls 4

#####################
# NUMBER OF BROKERS #
#####################

set xlabel "Number of brokers" offset 0,0.5

set output "../graphs/numBrokersDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0

plot "../resultsAvg/numBrokers_causal_DelayAvg" u 1:2:3 t "Causal" w yerrorbars ls 1, \
"../resultsAvg/numBrokers_single_glitch_free_DelayAvg" u 1:2:3 t "Single" w yerrorbars ls 2, \
"../resultsAvg/numBrokers_complete_glitch_free_DelayAvg" u 1:2:3 t "Complete" w yerrorbars ls 3, \
"../resultsAvg/numBrokers_atomic_DelayAvg" u 1:2:3 t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/numBrokers_causal_DelayAvg" u 1:2 t "Causal" w lines ls 1, \
"../resultsAvg/numBrokers_single_glitch_free_DelayAvg" u 1:2 t "Single" w lines ls 2, \
"../resultsAvg/numBrokers_complete_glitch_free_DelayAvg" u 1:2 t "Complete" w lines ls 3, \
"../resultsAvg/numBrokers_atomic_DelayAvg" u 1:2 t "Atomic" w lines ls 4

set output "../graphs/numBrokersTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/numBrokers_causal_TrafficByte" u ($1):($8/1000):($15/1000) t "Causal" w yerrorbars ls 1, \
"../resultsAvg/numBrokers_single_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Single" w yerrorbars ls 2, \
"../resultsAvg/numBrokers_complete_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Complete" w yerrorbars ls 3, \
"../resultsAvg/numBrokers_atomic_TrafficByte" u ($1):($8/1000):($15/1000) t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/numBrokers_causal_TrafficByte" u ($1):($8/1000) t "Causal" w lines ls 1, \
"../resultsAvg/numBrokers_single_glitch_free_TrafficByte" u ($1):($8/1000) t "Single" w lines ls 2, \
"../resultsAvg/numBrokers_complete_glitch_free_TrafficByte" u ($1):($8/1000) t "Complete" w lines ls 3, \
"../resultsAvg/numBrokers_atomic_TrafficByte" u ($1):($8/1000) t "Atomic" w lines ls 4

##################
# NUMBER OF VARS #
##################

set xlabel "Number of sources (vars) in the graph" offset 0,0.5

set output "../graphs/numVarsDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0

plot "../resultsAvg/numVars_causal_DelayAvg" u 1:2:3 t "Causal" w yerrorbars ls 1, \
"../resultsAvg/numVars_single_glitch_free_DelayAvg" u 1:2:3 t "Single" w yerrorbars ls 2, \
"../resultsAvg/numVars_complete_glitch_free_DelayAvg" u 1:2:3 t "Complete" w yerrorbars ls 3, \
"../resultsAvg/numVars_atomic_DelayAvg" u 1:2:3 t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/numVars_causal_DelayAvg" u 1:2 t "Causal" w lines ls 1, \
"../resultsAvg/numVars_single_glitch_free_DelayAvg" u 1:2 t "Single" w lines ls 2, \
"../resultsAvg/numVars_complete_glitch_free_DelayAvg" u 1:2 t "Complete" w lines ls 3, \
"../resultsAvg/numVars_atomic_DelayAvg" u 1:2 t "Atomic" w lines ls 4

set output "../graphs/numVarsTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/numVars_causal_TrafficByte" u ($1):($8/1000):($15/1000) t "Causal" w yerrorbars ls 1, \
"../resultsAvg/numVars_single_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Single" w yerrorbars ls 2, \
"../resultsAvg/numVars_complete_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Complete" w yerrorbars ls 3, \
"../resultsAvg/numVars_atomic_TrafficByte" u ($1):($8/1000):($15/1000) t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/numVars_causal_TrafficByte" u ($1):($8/1000) t "Causal" w lines ls 1, \
"../resultsAvg/numVars_single_glitch_free_TrafficByte" u ($1):($8/1000) t "Single" w lines ls 2, \
"../resultsAvg/numVars_complete_glitch_free_TrafficByte" u ($1):($8/1000) t "Complete" w lines ls 3, \
"../resultsAvg/numVars_atomic_TrafficByte" u ($1):($8/1000) t "Atomic" w lines ls 4

#####################
# NUMBER OF SIGNALS #
#####################

set xlabel "Number of inner nodes (signals) in the graph" offset 0,0.5

set output "../graphs/numSignalsDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0

plot "../resultsAvg/numSignals_causal_DelayAvg" u 1:2:3 t "Causal" w yerrorbars ls 1, \
"../resultsAvg/numSignals_single_glitch_free_DelayAvg" u 1:2:3 t "Single" w yerrorbars ls 2, \
"../resultsAvg/numSignals_complete_glitch_free_DelayAvg" u 1:2:3 t "Complete" w yerrorbars ls 3, \
"../resultsAvg/numSignals_atomic_DelayAvg" u 1:2:3 t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/numSignals_causal_DelayAvg" u 1:2 t "Causal" w lines ls 1, \
"../resultsAvg/numSignals_single_glitch_free_DelayAvg" u 1:2 t "Single" w lines ls 2, \
"../resultsAvg/numSignals_complete_glitch_free_DelayAvg" u 1:2 t "Complete" w lines ls 3, \
"../resultsAvg/numSignals_atomic_DelayAvg" u 1:2 t "Atomic" w lines ls 4

set output "../graphs/numSignalsTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/numSignals_causal_TrafficByte" u ($1):($8/1000):($15/1000) t "Causal" w yerrorbars ls 1, \
"../resultsAvg/numSignals_single_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Single" w yerrorbars ls 2, \
"../resultsAvg/numSignals_complete_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Complete" w yerrorbars ls 3, \
"../resultsAvg/numSignals_atomic_TrafficByte" u ($1):($8/1000):($15/1000) t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/numSignals_causal_TrafficByte" u ($1):($8/1000) t "Causal" w lines ls 1, \
"../resultsAvg/numSignals_single_glitch_free_TrafficByte" u ($1):($8/1000) t "Single" w lines ls 2, \
"../resultsAvg/numSignals_complete_glitch_free_TrafficByte" u ($1):($8/1000) t "Complete" w lines ls 3, \
"../resultsAvg/numSignals_atomic_TrafficByte" u ($1):($8/1000) t "Atomic" w lines ls 4

#####################################
# NUMBER OF DEPENDENCIES PER SIGNAL #
#####################################

set xlabel "Number of dependencies per signal" offset 0,0.5

set output "../graphs/numGraphDependenciesDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0

plot "../resultsAvg/numGraphDependencies_causal_DelayAvg" u 1:2:3 t "Causal" w yerrorbars ls 1, \
"../resultsAvg/numGraphDependencies_single_glitch_free_DelayAvg" u 1:2:3 t "Single" w yerrorbars ls 2, \
"../resultsAvg/numGraphDependencies_complete_glitch_free_DelayAvg" u 1:2:3 t "Complete" w yerrorbars ls 3, \
"../resultsAvg/numGraphDependencies_atomic_DelayAvg" u 1:2:3 t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/numGraphDependencies_causal_DelayAvg" u 1:2 t "Causal" w lines ls 1, \
"../resultsAvg/numGraphDependencies_single_glitch_free_DelayAvg" u 1:2 t "Single" w lines ls 2, \
"../resultsAvg/numGraphDependencies_complete_glitch_free_DelayAvg" u 1:2 t "Complete" w lines ls 3, \
"../resultsAvg/numGraphDependencies_atomic_DelayAvg" u 1:2 t "Atomic" w lines ls 4

set output "../graphs/numGraphDependenciesTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/numGraphDependencies_causal_TrafficByte" u ($1):($8/1000):($15/1000) t "Causal" w yerrorbars ls 1, \
"../resultsAvg/numGraphDependencies_single_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Single" w yerrorbars ls 2, \
"../resultsAvg/numGraphDependencies_complete_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Complete" w yerrorbars ls 3, \
"../resultsAvg/numGraphDependencies_atomic_TrafficByte" u ($1):($8/1000):($15/1000) t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/numGraphDependencies_causal_TrafficByte" u ($1):($8/1000) t "Causal" w lines ls 1, \
"../resultsAvg/numGraphDependencies_single_glitch_free_TrafficByte" u ($1):($8/1000) t "Single" w lines ls 2, \
"../resultsAvg/numGraphDependencies_complete_glitch_free_TrafficByte" u ($1):($8/1000) t "Complete" w lines ls 3, \
"../resultsAvg/numGraphDependencies_atomic_TrafficByte" u ($1):($8/1000) t "Atomic" w lines ls 4

#######################
# TIME BETWEEN EVENTS #
#######################

set xlabel "Publication frequency (k events/s)" offset 0,0.5

set output "../graphs/pubFrequencyDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0

plot "../resultsAvg/timeBetweenEvents_causal_DelayAvg" u 1:2:3 t "Causal" w yerrorbars ls 1, \
"../resultsAvg/timeBetweenEvents_single_glitch_free_DelayAvg" u 1:2:3 t "Single" w yerrorbars ls 2, \
"../resultsAvg/timeBetweenEvents_complete_glitch_free_DelayAvg" u 1:2:3 t "Complete" w yerrorbars ls 3, \
"../resultsAvg/timeBetweenEvents_atomic_DelayAvg" u 1:2:3 t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/timeBetweenEvents_causal_DelayAvg" u 1:2 t "Causal" w lines ls 1, \
"../resultsAvg/timeBetweenEvents_single_glitch_free_DelayAvg" u 1:2 t "Single" w lines ls 2, \
"../resultsAvg/timeBetweenEvents_complete_glitch_free_DelayAvg" u 1:2 t "Complete" w lines ls 3, \
"../resultsAvg/timeBetweenEvents_atomic_DelayAvg" u 1:2 t "Atomic" w lines ls 4

set output "../graphs/pubFrequencyTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/timeBetweenEvents_causal_TrafficByte" u ($1):($8/1000):($15/1000) t "Causal" w yerrorbars ls 1, \
"../resultsAvg/timeBetweenEvents_single_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Single" w yerrorbars ls 2, \
"../resultsAvg/timeBetweenEvents_complete_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Complete" w yerrorbars ls 3, \
"../resultsAvg/timeBetweenEvents_atomic_TrafficByte" u ($1):($8/1000):($15/1000) t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/timeBetweenEvents_causal_TrafficByte" u ($1):($8/1000) t "Causal" w lines ls 1, \
"../resultsAvg/timeBetweenEvents_single_glitch_free_TrafficByte" u ($1):($8/1000) t "Single" w lines ls 2, \
"../resultsAvg/timeBetweenEvents_complete_glitch_free_TrafficByte" u ($1):($8/1000) t "Complete" w lines ls 3, \
"../resultsAvg/timeBetweenEvents_atomic_TrafficByte" u ($1):($8/1000) t "Atomic" w lines ls 4

######################
# TIME BETWEEN READS #
######################

set xlabel "Signal access frequency (k reads/s)" offset 0,0.5

set output "../graphs/readFrequencyDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0

plot "../resultsAvg/timeBetweenReads_causal_DelayAvg" u 1:2:3 t "Causal" w yerrorbars ls 1, \
"../resultsAvg/timeBetweenReads_single_glitch_free_DelayAvg" u 1:2:3 t "Single" w yerrorbars ls 2, \
"../resultsAvg/timeBetweenReads_complete_glitch_free_DelayAvg" u 1:2:3 t "Complete" w yerrorbars ls 3, \
"../resultsAvg/timeBetweenReads_atomic_DelayAvg" u 1:2:3 t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/timeBetweenReads_causal_DelayAvg" u 1:2 t "Causal" w lines ls 1, \
"../resultsAvg/timeBetweenReads_single_glitch_free_DelayAvg" u 1:2 t "Single" w lines ls 2, \
"../resultsAvg/timeBetweenReads_complete_glitch_free_DelayAvg" u 1:2 t "Complete" w lines ls 3, \
"../resultsAvg/timeBetweenReads_atomic_DelayAvg" u 1:2 t "Atomic" w lines ls 4

set output "../graphs/readFrequencyTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/timeBetweenReads_causal_TrafficByte" u ($1):($8/1000):($15/1000) t "Causal" w yerrorbars ls 1, \
"../resultsAvg/timeBetweenReads_single_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Single" w yerrorbars ls 2, \
"../resultsAvg/timeBetweenReads_complete_glitch_free_TrafficByte" u ($1):($8/1000):($15/1000) t "Complete" w yerrorbars ls 3, \
"../resultsAvg/timeBetweenReads_atomic_TrafficByte" u ($1):($8/1000):($15/1000) t "Atomic" w yerrorbars ls 4, \
"../resultsAvg/timeBetweenReads_causal_TrafficByte" u ($1):($8/1000) t "Causal" w lines ls 1, \
"../resultsAvg/timeBetweenReads_single_glitch_free_TrafficByte" u ($1):($8/1000) t "Single" w lines ls 2, \
"../resultsAvg/timeBetweenReads_complete_glitch_free_TrafficByte" u ($1):($8/1000) t "Complete" w lines ls 3, \
"../resultsAvg/timeBetweenReads_atomic_TrafficByte" u ($1):($8/1000) t "Atomic" w lines ls 4