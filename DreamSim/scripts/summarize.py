import os

def AvgAll(filename, values):
    protocols = ["causal", "glitchFree", "atomic"]
    for protocol in protocols:
        AvgTraffic(filename, protocol + "_Traffic", values)
        AvgTraffic(filename, protocol + "_TrafficByte", values)
        AvgDelay(filename, protocol + "_DelayAvg", values)

def AvgTraffic(filename, suffix, values):
    fileOut = open("../resultsAvg/" + filename + "_" + suffix, "w")

    for val in values:
        tokenAckPacket_Sum = 0
        eventPacket_Sum = 0
        subscriptionPacket_Sum = 0
        advertisementPacket_Sum = 0
        total = 0

        fileIn = open("../results/" + filename + "_" + str(val) + "_" + suffix)
        lines = fileIn.readlines()
        for numLine in range(0, len(lines)):
            line = lines[numLine]
            tokens = line.split("\t")
            lineValue = float(tokens[2])
            if ("TokenAckPacket" in line):
                tokenAckPacket_Sum += lineValue
            elif ("EventPacket" in line):
                eventPacket_Sum += lineValue
            elif ("SubscriptionPacket" in line):
                subscriptionPacket_Sum += lineValue
            elif ("AdvertisementPacket" in line):
                advertisementPacket_Sum += lineValue
            total += lineValue
        fileIn.close()
        
        fileOut.write(str(val) + "\t" \
            + str(tokenAckPacket_Sum) + "\t" \
            + str(eventPacket_Sum) + "\t" \
            + str(advertisementPacket_Sum) + "\t" \
            + str(total) + "\n")
        
    fileOut.close()

def AvgDelay(filename, suffix, values):
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

# Values
default = [0]
centralized = [0]
locality = [0.0, 0.2, 0.4, 0.6, 0.8, 1.0]
numBrokers = [1, 6, 11, 16, 21, 26]
numGraphNodes = [2, 4, 6, 8, 10, 12, 14, 16]
numGraphDependencies = [1, 2, 3, 4, 5, 6, 7, 8]
timeBetweenEvents = [1, 4, 7, 10, 40, 70, 100]

# Invocations
AvgAll("default", default)
AvgAll("centralized", centralized)
AvgAll("locality", locality)
AvgAll("numBrokers", numBrokers)
AvgAll("numGraphNodes", numGraphNodes)
AvgAll("numGraphDependencies", numGraphDependencies)
AvgAll("timeBetweenEvents", timeBetweenEvents)
