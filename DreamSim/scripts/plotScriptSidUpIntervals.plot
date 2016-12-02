set terminal pdf fsize 15 size 4.2,3.6

set pointsize 3
# set size ratio 0.4

set style line 1 lw 4 lt 1 lc rgb 'orange' pt 1
set style line 2 lw 4 lt 2 lc rgb 'black' pt 6
set style line 3 lw 4 lt 3 lc rgb 'green' pt 8
set style line 4 lw 4 lt 4 lc rgb 'blue' pt 3
set style line 5 lw 4 lt 5 lc rgb 'red' pt 2

# set style line 1  linecolor rgb "black" lw 3 dashtype 1 pt 1
# set style line 2  linecolor rgb "black" lw 3 dashtype 2 pt 6
# set style line 3  linecolor rgb "black" lw 3 dashtype 3 pt 8
# set style line 4  linecolor rgb "black" lw 3 dashtype 4 pt 3
# set style line 5  linecolor rgb "black" lw 3 dashtype 5 pt 2

set log y
set format y "10^%T"

############
# LOCALITY #
############

set key bottom left

set xlabel "Degree of locality" offset 0,0.2

set output "../graphs/localityDelay.pdf"
set ylabel "Average Delay (ms)" offset 0.5,0
unset yr

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

set output "../graphs/localityTraffic.pdf"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0
unset yr

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

set key top right

set xlabel "Number of brokers" offset 0,0.2

set output "../graphs/numBrokersDelay.pdf"
set ylabel "Average Delay (ms)" offset 0.5,0
unset yr
set xtics 5

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

set output "../graphs/numBrokersTraffic.pdf"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0
unset yr

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

set xtics auto

##################
# NUMBER OF VARS #
##################

set key center right

set xlabel "Number of sources (vars) in the graph" offset 0,0.2

set output "../graphs/numVarsDelay.pdf"
set ylabel "Average Delay (ms)" offset 0.5,0
unset yr
set xtics 20

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

set key bottom right

set output "../graphs/numVarsTraffic.pdf"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0
unset yr

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

set xtics auto

###############
# GRAPH DEPTH #
###############

set key bottom right

set xlabel "Depth of the dependency graph" offset 0,0.2

set output "../graphs/graphDepthDelay.pdf"
set ylabel "Average Delay (ms)" offset 0.5,0
unset yr

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

set output "../graphs/graphDepthTraffic.pdf"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0
unset yr

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

set key bottom right

set xlabel "Number of dependencies per signal" offset 0,0.2

set output "../graphs/numGraphDependenciesDelay.pdf"
set ylabel "Average Delay (ms)" offset 0.5,0
unset yr
set yr[1:]

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

set output "../graphs/numGraphDependenciesTraffic.pdf"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0
unset yr
set yr[1:]

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

set key top left

set xlabel "Degree of nodes sharing" offset 0,0.2

set output "../graphs/graphShareDelay.pdf"
set ylabel "Average Delay (ms)" offset 0.5,0
unset yr

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

set output "../graphs/graphShareTraffic.pdf"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0
unset yr
set yr[:10000]

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

set key top left

set xlabel "Publication frequency (k events/s)" offset 0,0.2
set log x

set output "../graphs/pubFrequencyDelay.pdf"
set ylabel "Average Delay (ms)" offset 0.5,0
unset yr
set yr[:10000000]

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

set key bottom right

set output "../graphs/pubFrequencyTraffic.pdf"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0
unset yr

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

set key top left

set xlabel "Signal access frequency (k reads/s)" offset 0,0.2
set log x
set xr [0.1:2.5]

set output "../graphs/readFrequencyDelay.pdf"
set ylabel "Average Delay (ms)" offset 0.5,0
unset yr

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

set output "../graphs/readFrequencyTraffic.pdf"
set ylabel "Overall Traffic (KB/s)" offset 0.5,0
unset yr

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
set terminal pdf fsize 12 size 4.2,3.0

set style line 1 lw 2 lt 1 lc rgb 'black' pt 1

set style data histogram
set style histogram cluster gap 1
# set style fill solid border 0

set key above
set log y
set format y "10^%T"

set log y
set xtics rotate by 35 offset -1.2,-1.6

set output "../graphs/defaultTraffic.pdf"

set ylabel "Overall Traffic (KB/s)" offset 0.5,0

plot "../resultsAvg/defaultTraffic" u ($2/1000):xticlabel(1) ls 1 fs pattern 1 title "Causal", \
"../resultsAvg/defaultTraffic" u ($3/1000):xticlabel(1) ls 1 fs pattern 2 title "Single", \
"../resultsAvg/defaultTraffic" u ($4/1000):xticlabel(1) ls 1 fs pattern 3 title "Complete", \
"../resultsAvg/defaultTraffic" u ($5/1000):xticlabel(1) ls 1 fs pattern 4 title "Atomic", \
"../resultsAvg/defaultTraffic" u ($6/1000):xticlabel(1) ls 1 fs pattern 5 title "Sid Up"