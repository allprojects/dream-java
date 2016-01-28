set terminal postscript enhance color dashed dl 4 font 16
set pointsize 3
# set size ratio 0.55

set style line 1 lw 4 lt rgb 'orange' pt 1
set style line 2 lw 4 lt rgb 'black' pt 6
set style line 3 lw 4 lt rgb 'green' pt 8
set style line 4 lw 4 lt rgb 'blue' pt 3 

set key above
set log y

############
# LOCALITY #
############

set xlabel "Degree of locality" offset 0,0.5

set output "../graphsOpt/localityDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0

plot "../resultsAvg/locality_complete_glitch_free_DelayAvg" u 1:2 t "Complete" w linespoint ls 1, \
"../resultsAvg/locality_complete_glitch_free_optimized_DelayAvg" u 1:2 t "Complete Opt." w linespoint ls 2

set output "../graphsOpt/localityTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/locality_complete_glitch_free_TrafficByte" u ($1):($8/1000) t "Complete" w linespoint ls 1, \
"../resultsAvg/locality_complete_glitch_free_optimized_TrafficByte" u ($1):($8/1000) t "Complete Opt." w linespoint ls 2

#####################
# NUMBER OF BROKERS #
#####################

set xlabel "Number of brokers" offset 0,0.5

set output "../graphsOpt/numBrokersDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0

plot "../resultsAvg/numBrokers_complete_glitch_free_DelayAvg" u 1:2 t "Complete" w linespoint ls 1, \
"../resultsAvg/numBrokers_complete_glitch_free_optimized_DelayAvg" u 1:2 t "Complete Opt." w linespoint ls 2

set output "../graphsOpt/numBrokersTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/numBrokers_complete_glitch_free_TrafficByte" u ($1):($8/1000) t "Complete" w linespoint ls 1, \
"../resultsAvg/numBrokers_complete_glitch_free_optimized_TrafficByte" u ($1):($8/1000) t "Complete Opt." w linespoint ls 2

##################
# NUMBER OF VARS #
##################

set xlabel "Number of sources (vars) in the graph" offset 0,0.5

set output "../graphsOpt/numVarsDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0

plot "../resultsAvg/numVars_complete_glitch_free_DelayAvg" u 1:2 t "Complete" w linespoint ls 1, \
"../resultsAvg/numVars_complete_glitch_free_optimized_DelayAvg" u 1:2 t "Complete Opt." w linespoint ls 2

set output "../graphsOpt/numVarsTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/numVars_complete_glitch_free_TrafficByte" u ($1):($8/1000) t "Complete" w linespoint ls 1, \
"../resultsAvg/numVars_complete_glitch_free_optimized_TrafficByte" u ($1):($8/1000) t "Complete Opt." w linespoint ls 2

###############
# GRAPH DEPTH #
###############

set xlabel "Depth of the dependency graph" offset 0,0.5

set output "../graphsOpt/graphDepthDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0

plot "../resultsAvg/graphDepth_complete_glitch_free_DelayAvg" u 1:2 t "Complete" w linespoint ls 1, \
"../resultsAvg/graphDepth_complete_glitch_free_optimized_DelayAvg" u 1:2 t "Complete Opt." w linespoint ls 2

set output "../graphsOpt/graphDepthTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/graphDepth_complete_glitch_free_TrafficByte" u ($1):($8/1000) t "Complete" w linespoint ls 1, \
"../resultsAvg/graphDepth_complete_glitch_free_optimized_TrafficByte" u ($1):($8/1000) t "Complete Opt." w linespoint ls 2

#####################################
# NUMBER OF DEPENDENCIES PER SIGNAL #
#####################################

set xlabel "Number of dependencies per signal" offset 0,0.5

set output "../graphsOpt/numGraphDependenciesDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0

plot "../resultsAvg/numGraphDependencies_complete_glitch_free_DelayAvg" u 1:2 t "Complete" w linespoint ls 1, \
"../resultsAvg/numGraphDependencies_complete_glitch_free_optimized_DelayAvg" u 1:2 t "Complete Opt." w linespoint ls 2

set output "../graphsOpt/numGraphDependenciesTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/numGraphDependencies_complete_glitch_free_TrafficByte" u ($1):($8/1000) t "Complete" w linespoint ls 1, \
"../resultsAvg/numGraphDependencies_complete_glitch_free_optimized_TrafficByte" u ($1):($8/1000) t "Complete Opt." w linespoint ls 2

###########################
# GRAPH SHARE PROBABILITY #
###########################

set xlabel "Probability for a signal to depend on multiple sources" offset 0,0.5

set output "../graphsOpt/graphShareDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0

plot "../resultsAvg/graphShare_complete_glitch_free_DelayAvg" u 1:2 t "Complete" w linespoint ls 1, \
"../resultsAvg/graphShare_complete_glitch_free_optimized_DelayAvg" u 1:2 t "Complete Opt." w linespoint ls 2

set output "../graphsOpt/graphShareTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/graphShare_complete_glitch_free_TrafficByte" u ($1):($8/1000) t "Complete" w linespoint ls 1, \
"../resultsAvg/graphShare_complete_glitch_free_optimized_TrafficByte" u ($1):($8/1000) t "Complete Opt." w linespoint ls 2

#######################
# TIME BETWEEN EVENTS #
#######################

set xlabel "Publication frequency (k events/s)" offset 0,0.5

set output "../graphsOpt/pubFrequencyDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0

plot "../resultsAvg/timeBetweenEvents_complete_glitch_free_DelayAvg" u 1:2 t "Complete" w linespoint ls 1, \
"../resultsAvg/timeBetweenEvents_complete_glitch_free_optimized_DelayAvg" u 1:2 t "Complete Opt." w linespoint ls 2

set output "../graphsOpt/pubFrequencyTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/timeBetweenEvents_complete_glitch_free_TrafficByte" u ($1):($8/1000) t "Complete" w linespoint ls 1, \
"../resultsAvg/timeBetweenEvents_complete_glitch_free_optimized_TrafficByte" u ($1):($8/1000) t "Complete Opt." w linespoint ls 2

######################
# TIME BETWEEN READS #
######################

set xlabel "Signal access frequency (k reads/s)" offset 0,0.5

set output "../graphsOpt/readFrequencyDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0

plot "../resultsAvg/timeBetweenReads_complete_glitch_free_DelayAvg" u 1:2 t "Complete" w linespoint ls 1, \
"../resultsAvg/timeBetweenReads_complete_glitch_free_optimized_DelayAvg" u 1:2 t "Complete Opt." w linespoint ls 2

set output "../graphsOpt/readFrequencyTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/timeBetweenReads_complete_glitch_free_TrafficByte" u ($1):($8/1000) t "Complete" w linespoint ls 1, \
"../resultsAvg/timeBetweenReads_complete_glitch_free_optimized_TrafficByte" u ($1):($8/1000) t "Complete Opt." w linespoint ls 2
