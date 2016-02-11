from math import sqrt
import os
import scipy.stats

numSeeds = 10
protocols = ["causal", "single_glitch_free", "complete_glitch_free", "complete_glitch_free_optimized", "atomic", "sid_up"]

def summarizeAll(filename, values):
    global protocols
    global numSeeds
    for seed in range(0, numSeeds):
        for protocol in protocols:
            summarizeTraffic(filename + "_" + str(seed), protocol + "_Traffic", values)
            summarizeTraffic(filename + "_" + str(seed), protocol + "_TrafficByte", values)
            summarizeDelay(filename + "_" + str(seed), protocol + "_DelayAvg", values)

def summarizeTraffic(filename, suffix, values):
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

def summarizeDelay(filename, suffix, values):
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

def avgTraffic(filename, suffix, numRepetitions):
    output = open("../resultsAvg/" + filename + "_" + suffix, "w")
    temp = open("../resultsAvg/" + filename + "_0_" + suffix, "r")
    numLines = len(temp.readlines())
    temp.close()

    for line in range(0, numLines):
        count = 0

        sumEvents = 0
        sumSubscriptions = 0
        sumAdvertisements = 0
        sumLockRelease = 0
        sumLockRequest = 0
        sumLockGrant = 0
        sumTotal = 0

        sumEventsSquare = 0
        sumSubscriptionsSquare = 0
        sumAdvertisementsSquare = 0
        sumLockReleaseSquare = 0
        sumLockRequestSquare = 0
        sumLockGrantSquare = 0
        sumTotalSquare = 0
        
        label = ""
        for i in range(0, numRepetitions):    
            count = count+1
            f = open("../resultsAvg/" + filename + "_" + str(i) + "_" + suffix, "r")
            lines = f.readlines()

            tokens = lines[line].split("\t")
            label = tokens[0]

            sumEvents = sumEvents + float(tokens[1])
            sumSubscriptions = sumSubscriptions + float(tokens[2])
            sumAdvertisements = sumAdvertisements + float(tokens[3])
            sumLockRelease = sumLockRelease + float(tokens[4])
            sumLockRequest = sumLockRequest + float(tokens[5])
            sumLockGrant = sumLockGrant + float(tokens[6])
            sumTotal = sumTotal + float(tokens[7])

            sumEventsSquare = sumEventsSquare + (float(tokens[1]))**2
            sumSubscriptionsSquare = sumSubscriptionsSquare + (float(tokens[2]))**2
            sumAdvertisementsSquare = sumAdvertisementsSquare + (float(tokens[3]))**2
            sumLockReleaseSquare = sumLockReleaseSquare + (float(tokens[4]))**2
            sumLockRequestSquare = sumLockRequestSquare + (float(tokens[5]))**2
            sumLockGrantSquare = sumLockGrantSquare + (float(tokens[6]))**2
            sumTotalSquare = sumTotalSquare + (float(tokens[7]))**2
            
            f.close()

        meanEvents = sumEvents/count
        meanSubscriptions = sumSubscriptions/count
        meanAdvertisements = sumAdvertisements/count
        meanLockRelease = sumLockRelease/count
        meanLockRequest = sumLockRequest/count
        meanLockGrant = sumLockGrant/count
        meanTotal = sumTotal/count

        sampleStdDevEvents = sqrt(abs(sumEventsSquare/count - meanEvents**2)*count/(count-1))
        sampleStdDevSubscriptions = sqrt(abs(sumSubscriptionsSquare/count - meanSubscriptions**2)*count/(count-1))
        sampleStdDevAdvertisements = sqrt(abs(sumAdvertisementsSquare/count - meanAdvertisements**2)*count/(count-1))
        sampleStdDevLockRelease = sqrt(abs(sumLockReleaseSquare/count - meanLockRelease**2)*count/(count-1))
        sampleStdDevLockRequest = sqrt(abs(sumLockRequestSquare/count - meanLockRequest**2)*count/(count-1))
        sampleStdDevLockGrant = sqrt(abs(sumLockGrantSquare/count - meanLockGrant**2)*count/(count-1))
        sampleStdDevTotal = sqrt(abs(sumTotalSquare/count - meanTotal**2)*count/(count-1))

        deltaEvents = 0
        deltaSubscriptions = 0
        deltaAdvertisements = 0
        deltaLockRelease = 0
        deltaLockRequest = 0
        deltaLockGrant = 0
        deltaTotal = 0

        deltaEvents = (-scipy.stats.t.ppf(0.05,count-1).sum() * sampleStdDevEvents)/sqrt(count)
        deltaSubscriptions = (-scipy.stats.t.ppf(0.05,count-1).sum() * sampleStdDevSubscriptions)/sqrt(count)
        deltaAdvertisements = (-scipy.stats.t.ppf(0.05,count-1).sum() * sampleStdDevAdvertisements)/sqrt(count)
        deltaLockRelease = (-scipy.stats.t.ppf(0.05,count-1).sum() * sampleStdDevLockRelease)/sqrt(count)
        deltaLockRequest = (-scipy.stats.t.ppf(0.05,count-1).sum() * sampleStdDevLockRequest)/sqrt(count)
        deltaLockGrant = (-scipy.stats.t.ppf(0.05,count-1).sum() * sampleStdDevLockGrant)/sqrt(count)
        deltaTotal = (-scipy.stats.t.ppf(0.05,count-1).sum() * sampleStdDevTotal)/sqrt(count)

        output.write(label + \
                     "\t" + str(meanEvents) + \
                     "\t" + str(meanSubscriptions) + \
                     "\t" + str(meanAdvertisements) + \
                     "\t" + str(meanLockRelease) + \
                     "\t" + str(meanLockRequest) + \
                     "\t" + str(meanLockGrant) + \
                     "\t" + str(meanTotal) + \
                     "\t" + str(deltaEvents) + \
                     "\t" + str(deltaSubscriptions) + \
                     "\t" + str(deltaAdvertisements) + \
                     "\t" + str(deltaLockRelease) + \
                     "\t" + str(deltaLockRequest) + \
                     "\t" + str(deltaLockGrant) + \
                     "\t" + str(deltaTotal) + \
                     "\n")

def avgDelay(filename, suffix, numRepetitions):
    output = open("../resultsAvg/" + filename + "_" + suffix, "w")
    temp = open("../resultsAvg/" + filename + "_0_" + suffix, "r")
    numLines = len(temp.readlines())
    temp.close()

    for line in range(0, numLines):
        count = 0
        sumDelay = 0
        sumDelaySquare = 0
 
        label = ""
        for i in range(0, numRepetitions):    
            count = count+1
            f = open("../resultsAvg/" + filename + "_" + str(i) + "_" + suffix, "r")
            lines = f.readlines()

            tokens = lines[line].split("\t")
            label = tokens[0]

            sumDelay = sumDelay + float(tokens[1])
            sumDelaySquare = sumDelaySquare + (float(tokens[1]))**2
            
            f.close()

        meanDelay = sumDelay/count
        sampleStdDevDelay = sqrt((sumDelaySquare/count - meanDelay**2)*count/(count-1))
        deltaDelay = (-scipy.stats.t.ppf(0.05,count-1).sum() * sampleStdDevDelay)/sqrt(count)

        output.write(label + \
                     "\t" + str(meanDelay) + \
                     "\t" + str(deltaDelay) + \
                     "\n")

def prepareDefault():
    causal = open("../resultsAvg/default_causal_TrafficByte", "r").readlines()[0].split("\t")
    single = open("../resultsAvg/default_single_glitch_free_TrafficByte", "r").readlines()[0].split("\t")
    complete = open("../resultsAvg/default_complete_glitch_free_TrafficByte", "r").readlines()[0].split("\t")
    atomic = open("../resultsAvg/default_atomic_TrafficByte", "r").readlines()[0].split("\t")
    sidup = open("../resultsAvg/default_sid_up_TrafficByte", "r").readlines()[0].split("\t")

    result = open("../resultsAvg/default", "w")

    result.write("Events\t" + causal[1] + "\t" + single[1] + "\t" + complete[1] + "\t" + atomic[1] + "\t" + sidup[1] + "\n")
    result.write("Adv.\t" + causal[3] + "\t" + single[3] + "\t" + complete[3] + "\t" + atomic[3] + "\t" + sidup[3] + "\n")
    result.write("Subs.\t" + causal[2] + "\t" + single[2] + "\t" + complete[2] + "\t" + atomic[2] + "\t" + sidup[2] + "\n")
    result.write("Req.\t" + causal[5] + "\t" + single[5] + "\t" + complete[5] + "\t" + atomic[5] + "\t" + sidup[5] + "\n")
    result.write("Grant\t" + causal[6] + "\t" + single[6] + "\t" + complete[6] + "\t" + atomic[6] + "\t" + sidup[6] + "\n")        
    result.write("Rel.\t" + causal[4] + "\t" + single[4] + "\t" + complete[4] + "\t" + atomic[4] + "\t" + sidup[4] + "\n")

def avgAll(filename, values):
    global numSeeds
    global protocols
    for protocol in protocols:
        avgTraffic(filename, protocol + "_Traffic", numSeeds)
        avgTraffic(filename, protocol + "_TrafficByte", numSeeds)
        avgDelay(filename, protocol + "_DelayAvg", numSeeds)
    
# Values
default = [0]
centralized = [0]
locality = [0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0]
numBrokers = [2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24]
numVars = [1, 4, 7, 10, 40, 70, 100]
graphDepth = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
numGraphDependencies = [1, 2, 3, 4, 5, 6, 7, 8, 9]
graphShare = [0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8]
timeBetweenEvents = [100, 400, 700, 1000, 4000, 7000, 10000]
# timeBetweenReads = [100, 400, 700, 1000, 4000, 7000, 10000]
timeBetweenReads = [400, 700, 1000, 4000, 7000, 10000]

# Invocations
summarizeAll("default", default)
summarizeAll("centralized", centralized)
summarizeAll("locality", locality)
summarizeAll("numBrokers", numBrokers)
summarizeAll("numVars", numVars)
summarizeAll("graphDepth", graphDepth)
summarizeAll("numGraphDependencies", numGraphDependencies)
summarizeAll("graphShare", graphShare)
summarizeAll("timeBetweenEvents", timeBetweenEvents)
summarizeAll("timeBetweenReads", timeBetweenReads)

avgAll("default", default)
avgAll("centralized", centralized)
avgAll("locality", locality)
avgAll("numBrokers", numBrokers)
avgAll("numVars", numVars)
avgAll("graphDepth", graphDepth)
avgAll("numGraphDependencies", numGraphDependencies)
avgAll("graphShare", graphShare)
avgAll("timeBetweenEvents", timeBetweenEvents)
avgAll("timeBetweenReads", timeBetweenReads)

prepareDefault()
