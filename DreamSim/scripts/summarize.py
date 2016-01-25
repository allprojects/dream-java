import os

def SummarizeAll(filename, values):
    protocols = ["causal", "single_glitch_free", "complete_glitch_free", "atomic"]
    seeds = [0, 1, 2, 3, 4]
    for seed in seeds:
        for protocol in protocols:
            SummarizeTraffic(filename + "_" + str(seed), protocol + "_Traffic", values)
            SummarizeTraffic(filename + "_" + str(seed), protocol + "_TrafficByte", values)
            SummarizeDelay(filename + "_" + str(seed), protocol + "_DelayAvg", values)

def SummarizeTraffic(filename, suffix, values):
    fileOut = open("../resultsAvg/" + filename + "_" + suffix, "w")

    for val in values:
        subscription_sum = 0
        event_sum = 0
        advertisement_sum = 0
        lockRelease_sum = 0
        lockRequest_sum = 0
        lockGrant_sum = 0
        total = 0

        fileIn = open("../results/" + filename + "_" + str(val) + "_" + suffix)
        lines = fileIn.readlines()
        for numLine in range(0, len(lines)):
            line = lines[numLine]
            tokens = line.split("\t")
            lineValue = float(tokens[2])
            if ("SubscriptionPacket" in line):
                subscription_sum += lineValue
            elif ("EventPacket" in line):
                event_sum += lineValue
            elif ("AdvertisementPacket" in line):
                advertisement_sum += lineValue
            elif ("LockReleasePacket" in line):
                lockRelease_sum += lineValue
            elif ("LockRequestPacket" in line):
                lockRequest_sum += lineValue
            elif ("LockGrantPacket" in line):
                lockGrant_sum += lineValue
            total += lineValue
        fileIn.close()
        
        fileOut.write(str(val) + "\t" \
            + str(event_sum) + "\t" \
            + str(subscription_sum) + "\t" \
            + str(advertisement_sum) + "\t" \
            + str(lockRelease_sum) + "\t" \
            + str(lockRequest_sum) + "\t" \
            + str(lockGrant_sum) + "\t" \
            + str(total) + "\n")
        
    fileOut.close()

def SummarizeDelay(filename, suffix, values):
    fileOut = open("../resultsAvg/" + filename + "_" + suffix, "w")

    for val in values:
        delay_Sum = 0

        fileIn = open("../results/" + filename + "_" + str(val) + "_" + suffix)
        lines = fileIn.readlines()
        for numLine in range(0, len(lines)):
            line = lines[numLine]
            tokens = line.split("\t")
            lineValue = float(tokens[0])
            delay_Sum += lineValue
        fileIn.close()
        
        fileOut.write(str(val) + "\t" \
            + str(delay_Sum) + "\n")
                    
    fileOut.close()

# TODO: compute statistics using multiple seeds
def AvgAll(filename, values):
    protocols = ["causal", "single_glitch_free", "complete_glitch_free", "atomic"]
    for protocol in protocols:
        seed = 0
        
        inTraffic = "../resultsAvg/" + filename + "_" + str(seed) + "_" + protocol + "_Traffic"
        inTrafficByte = "../resultsAvg/" + filename + "_" + str(seed) + "_" + protocol + "_TrafficByte"
        inDelay = "../resultsAvg/" + filename + "_" + str(seed) + "_" + protocol + "_DelayAvg"

        outTraffic = "../resultsAvg/" + filename + "_" + protocol + "_Traffic"
        outTrafficByte = "../resultsAvg/" + filename + "_" + protocol + "_TrafficByte"
        outDelay = "../resultsAvg/" + filename + "_" + protocol + "_DelayAvg"

        shutil.copyfile(inTraffic, outTraffic)
        shutil.copyfile(inTrafficByte, outTrafficByte)
        shutil.copyfile(inDeay, outDelay)
    
# Values
default = [0]
centralized = [0]
locality = [0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0]
numBrokers = [2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24]
numVars = [1, 4, 7, 10, 40, 70, 100]
numSignals = [10, 40, 70, 100, 400, 700, 1000]
numGraphDependencies = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
timeBetweenEvents = [1, 4, 7, 10, 40, 70, 100, 400, 700, 1000]
timeBetweenReads = [100, 400, 700, 1000, 4000, 7000, 10000]

# Invocations
SummarizeAll("default", default)
SummarizeAll("centralized", centralized)
SummarizeAll("locality", locality)
SummarizeAll("numBrokers", numBrokers)
SummarizeAll("numVars", numVars)
SummarizeAll("numSignals", numSignals)
SummarizeAll("numGraphDependencies", numGraphDependencies)
SummarizeAll("timeBetweenEvents", timeBetweenEvents)
SummarizeAll("timeBetweenReads", timeBetweenReads)

AvgAll("default", default)
AvgAll("centralized", centralized)
AvgAll("locality", locality)
AvgAll("numBrokers", numBrokers)
AvgAll("numVars", numVars)
AvgAll("numSignals", numSignals)
AvgAll("numGraphDependencies", numGraphDependencies)
AvgAll("timeBetweenEvents", timeBetweenEvents)
AvgAll("timeBetweenReads", timeBetweenReads)
