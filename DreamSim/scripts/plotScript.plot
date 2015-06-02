set terminal postscript enhance color dashed dl 4 font 26
set pointsize 3
set size ratio 0.55

set style line 1 lw 4 lt 1 pt 1
set style line 2 lw 4 lt rgb 'black' pt 6
set style line 3 lw 4 lt rgb 'orange' pt 8
set style line 4 lw 4 lt 3 pt 2
set style line 5 lw 4 lt 2 pt 3
set style line 6 lw 4 lt 4 pt 4
set style line 7 lw 4 lt rgb 'brown' pt 10 

set key left top

############
# LOCALITY #
############

set xlabel "Degree of Locality" offset 0,0.5

set output "../graphs/localityDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0
set yr[0:60]
plot "../resultsAvg/locality_causal_DelayAvg" u 1:2 t "Causal" w linespoint ls 1, \
"../resultsAvg/locality_glitchFree_DelayAvg" u 1:2 t "Glitch Free" w linespoint ls 2, \
"../resultsAvg/locality_atomic_DelayAvg" u 1:2 t "Atomic" w linespoint ls 3

set output "../graphs/localityTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0
set yr[0:500]
plot "../resultsAvg/locality_causal_TrafficByte" u ($1):($5/1000) t "Causal" w linespoint ls 1, \
"../resultsAvg/locality_glitchFree_TrafficByte" u ($1):($5/1000) t "Glitch Free" w linespoint ls 2, \
"../resultsAvg/locality_atomic_TrafficByte" u ($1):($5/1000) t "Atomic" w linespoint ls 3

#####################
# NUMBER OF BROKERS #
#####################

set xlabel "Number of Brokers" offset 0,0.5

set output "../graphs/numBrokersDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0
set yr[0:60]
plot "../resultsAvg/numBrokers_causal_DelayAvg" u 1:2 t "Causal" w linespoint ls 1, \
"../resultsAvg/numBrokers_glitchFree_DelayAvg" u 1:2 t "Glitch Free" w linespoint ls 2, \
"../resultsAvg/numBrokers_atomic_DelayAvg" u 1:2 t "Atomic" w linespoint ls 3

set output "../graphs/numBrokersTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0
set yr[0:500]
plot "../resultsAvg/numBrokers_causal_TrafficByte" u ($1):($5/1000) t "Causal" w linespoint ls 1, \
"../resultsAvg/numBrokers_glitchFree_TrafficByte" u ($1):($5/1000) t "Glitch Free" w linespoint ls 2, \
"../resultsAvg/numBrokers_atomic_TrafficByte" u ($1):($5/1000) t "Atomic" w linespoint ls 3

#############################
# NUMBER OF NODES PER GRAPH #
#############################

set xlabel "Number of Objects per Graph" offset 0,0.5

set output "../graphs/numGraphNodesDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0
set yr[0:60]
plot "../resultsAvg/numGraphNodes_causal_DelayAvg" u 1:2 t "Causal" w linespoint ls 1, \
"../resultsAvg/numGraphNodes_glitchFree_DelayAvg" u 1:2 t "Glitch Free" w linespoint ls 2, \
"../resultsAvg/numGraphNodes_atomic_DelayAvg" u 1:2 t "Atomic" w linespoint ls 3

set output "../graphs/numGraphNodesTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0
set yr[0:1000]
plot "../resultsAvg/numGraphNodes_causal_TrafficByte" u ($1):($5/1000) t "Causal" w linespoint ls 1, \
"../resultsAvg/numGraphNodes_glitchFree_TrafficByte" u ($1):($5/1000) t "Glitch Free" w linespoint ls 2, \
"../resultsAvg/numGraphNodes_atomic_TrafficByte" u ($1):($5/1000) t "Atomic" w linespoint ls 3

#############################
# NUMBER OF NODES PER GRAPH #
#############################

set xlabel "Number of Objects per Expression" offset 0,0.5

set output "../graphs/numGraphDependenciesDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0
set yr[0:100]
plot "../resultsAvg/numGraphDependencies_causal_DelayAvg" u 1:2 t "Causal" w linespoint ls 1, \
"../resultsAvg/numGraphDependencies_glitchFree_DelayAvg" u 1:2 t "Glitch Free" w linespoint ls 2, \
"../resultsAvg/numGraphDependencies_atomic_DelayAvg" u 1:2 t "Atomic" w linespoint ls 3

set output "../graphs/numGraphDependenciesTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0
set yr[0:1600]
plot "../resultsAvg/numGraphDependencies_causal_TrafficByte" u ($1):($5/1000) t "Causal" w linespoint ls 1, \
"../resultsAvg/numGraphDependencies_glitchFree_TrafficByte" u ($1):($5/1000) t "Glitch Free" w linespoint ls 2, \
"../resultsAvg/numGraphDependencies_atomic_TrafficByte" u ($1):($5/1000) t "Atomic" w linespoint ls 3

#######################
# TIME BETWEEN EVENTS #
#######################

set xlabel "Publication Frequency (k Events/s)" offset 0,0.5

set output "../graphs/pubFrequencyDelay.ps"
set ylabel "Average Delay (ms)" offset 0.5,0
set auto y
plot "../resultsAvg/timeBetweenEvents_causal_DelayAvg" u (1/$1):($2) t "Causal" w linespoint ls 1, \
"../resultsAvg/timeBetweenEvents_glitchFree_DelayAvg" u (1/$1):($2) t "Glitch Free" w linespoint ls 2, \
"../resultsAvg/timeBetweenEvents_atomic_DelayAvg" u (1/$1):($2) t "Atomic" w linespoint ls 3

set output "../graphs/pubFrequencyTraffic.ps"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0
set auto y
plot "../resultsAvg/timeBetweenEvents_causal_TrafficByte" u (1/$1):($5/1000) t "Causal" w linespoint ls 1, \
"../resultsAvg/timeBetweenEvents_glitchFree_TrafficByte" u (1/$1):($5/1000) t "Glitch Free" w linespoint ls 2, \
"../resultsAvg/timeBetweenEvents_atomic_TrafficByte" u (1/$1):($5/1000) t "Atomic" w linespoint ls 3
