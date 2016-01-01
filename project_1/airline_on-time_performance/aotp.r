# By: Alexander Simeonov

# Data acquired from: 
# www.transtats.bts.gov/DL_SelectFields.asp?Table_ID=236&DB_Short_Name=On-Time

# We shall focus on invastingating flight delays using the data set for time
# performance of flights within the United States for 2013.

# Set up the environment

# Makes sure package is not already installed and loaded before it loads it
prepare.package <- function(package) {
    if(package %in% rownames(installed.packages()) == FALSE) {
        install.packages(package)
    }
    require(package, character.only = TRUE)
}

setwd(dirname(parent.frame(2)$ofile))
prepare.package("doBy")
prepare.package("ggplot2")
prepare.package("quantreg")
prepare.package("plyr")
prepare.package("rworldmap")
prepare.package("ggmap")
prepare.package("mapproj")

################################################################################
# Load in and clean up the data for a single month, remove outliers, missing,
# and unnecessary values, and make sure our data is formatted correctly.
################################################################################

cat(c("Initializing month...\n"), sep="")

if(!file.exists("clean_data_set")) {
    dir.create("clean_data_set")
}
if(!file.exists("geocodes")) {
    dir.create("geocodes")
}
if(!file.exists("year")) {
    dir.create("year")
}

COMPILE_YEAR <- FALSE
GEO_CITY <- "./geocodes/city.csv"
GEO_MONTH <- "./geocodes/1.csv"
CLEAN_MONTH <- "./clean_data_set/1.csv"
MONTH <- "./data_set/On_Time_On_Time_Performance_2013_1.csv"

if(file.exists(GEO_MONTH)) {
    month <- read.csv(file=GEO_MONTH)
} else if(file.exists(CLEAN_MONTH)) {
    month <- read.csv(file=CLEAN_MONTH)
} else if(file.exists(MONTH)) {
    month <- read.csv(file=MONTH)
} else {
    cat(c("Can't find data file!\n"), sep="")
}

# Sample data to get familiar with its layout
head(month)

# Make sure certain columns have expected values
if(!file.exists(CLEAN_MONTH)) {
    levels(factor(month$Cancelled))
    levels(factor(month$Diverted))
    levels(factor(month$Flights))
    levels(factor(month$DivAirportLandings))
    levels(factor(month$OriginState))
    levels(factor(month$DestState))

    summaryBy(DivAirportLandings~DivAirportLandings, data=month, FUN=length)
    summaryBy(Cancelled~Cancelled, data=month, FUN=length)
}

# It seems like a bunch of columns may be unnecessary because of missing values
# Let's do something about that first

# Checks if a cell in a column is empty
is.empty <- function(column) {
    column==""
}

# For a specific column, remove the column if it exceeds na.max.frequency
remove.na.column <- function(frame, col.num, na.max.frequency) {
    count <- length(frame[,col.num])
    
    if((is.na(count(is.na(frame[,col.num]))[1,1]) == FALSE) &&
       (count(is.na(frame[,col.num]))[1,1] == TRUE)) {
        na.count <- count(is.na(frame[,col.num]))[1,2]
    } else if((is.na(count(is.na(frame[,col.num]))[2,1]) == FALSE) &&
              (count(is.na(frame[,col.num]))[2,1] == TRUE)) {
        na.count <- count(is.na(frame[,col.num]))[2,2]
    } else {
        na.count <- 0
    }
    
    if((is.na(count(is.empty(frame[,col.num]))[1,1]) == FALSE) &&
           (count(is.empty(frame[,col.num]))[1,1] == TRUE)) {
        empty.count <- count(is.empty(frame[,col.num]))[1,2]
    } else if((is.na(count(is.empty(frame[,col.num]))[2,1]) == FALSE) &&
                  (count(is.empty(frame[,col.num]))[2,1] == TRUE)) {
        empty.count <- count(is.empty(frame[,col.num]))[2,2]
    } else {
        empty.count <- 0
    }
    
    if(((na.count/count) > na.max.frequency) ||
       ((empty.count/count) > na.max.frequency)) {
        frame[,col.num] <- NULL
    }
    
    return(frame)
}

# For a specific frame, remove all columns that exceed na.max.frequency
remove.na.columns <- function(frame, na.max.frequency) {
    y <- 0
    n <- ncol(frame)
    
    for(x in 1:n) {
        frame <- remove.na.column(frame, x-y, na.max.frequency)
        
        if(ncol(frame) < n) {
            y <- y + 1
            n = ncol(frame)
        }
    }
    
    return(frame)
}

# There are a lot of other colums we won't need, let's compress the data even
# more by removing ones we won't need due to redundancy, and irrelevat data
remove.unnecessary <- function(frame) {
    frame$Year <- NULL                 # Can be determined from FlightDate
    frame$Quarter <- NULL              # Can be determined from FlightDate
    frame$Month <- NULL                # Can be determined from FlightDate
    frame$DayofMonth <- NULL           # Can be determined from FlightDate
    frame$DayOfWeek <- NULL            # Can be determined from FlightDate
    frame$UniqueCarrier <- NULL        # Can be determined from Carrier
    frame$AirlineID <- NULL            # Can be determined from Carrier
    frame$OriginAirportID <- NULL      # Can be determined from Origin
    frame$OriginAirportSeqID <- NULL   # Can be determined from Origin
    frame$OriginCityMarketID <- NULL   # Can be determined from Origin
    frame$OriginStateFips <- NULL      # Can be determined from OriginState
    frame$OriginStateName <- NULL      # Can be determined from OriginState
    frame$OriginWac <- NULL            # Can be determined from OriginState
    frame$DestAirportID <- NULL        # Can be determined from Dest
    frame$DestAirportSeqID <- NULL     # Can be determined from Dest
    frame$DestCityMarketID <- NULL     # Can be determined from Dest
    frame$DestStateFips <- NULL        # Can be determined from DestState
    frame$DestStateName <- NULL        # Can be determined from DestState
    frame$DestWac <- NULL              # Can be determined from DestState
    frame$DepDelayMinutes <- NULL      # Can be determined from DepDelay
    frame$DepDel15 <- NULL             # Can be determined from DepDelay
    frame$DepartureDelayGroups <- NULL # Can be determined from DepDelay
    frame$DepTimeBlk <- NULL           # Can be determined from DepDelay
    frame$ArrDelayMinutes <- NULL      # Can be determined from ArrDelay
    frame$ArrDel15 <- NULL             # Can be determined from ArrDelay
    frame$ArrivalDelayGroups <- NULL   # Can be determined from ArrDelay
    frame$ArrTimeBlk <- NULL           # Can be determined from ArrDelay
    frame$DistanceGroup <- NULL        # Can be determined from Distance
    frame$Flights <- NULL              # This value is always 1
    frame$DivAirportLandings <- NULL   # Outlier amounts
    frame$Diverted <- NULL             # Outlier amounts
    frame$Cancelled <- NULL            # Outlier amounts
    frame$FlightNum <- NULL            # Irrelevant
    
    return(frame)
}

cleanup.month <- function(frame) {
    # Remove columns consisting of more than 5% NA/empty values
    frame <- remove.na.columns(frame, .05)
    frame <- remove.unnecessary(frame)

    # Let's also remove rows with missing values, make sure to remove NA, 
    # empty, and unnecessary colums first, so that we will remove the fewest 
    # amount of rows
    frame <- frame[complete.cases(frame),]

    # Remove US Territories we aren't interested in those
    frame <- subset(frame, (OriginState!="PR"))
    frame <- subset(frame, (DestState!="PR"))
    frame <- subset(frame, (OriginState!="TT"))
    frame <- subset(frame, (DestState!="TT"))
    
    return(frame)
}

if(!file.exists(CLEAN_MONTH)) {
    month <- cleanup.month(month)
    write.csv(month, file=CLEAN_MONTH, row.names=FALSE)
}

# Now to check for additional outliers we only really need to look at delays
ggplot(month, aes(DepDelay, ArrDelay)) + geom_point()
ggplot(month, aes(log(abs(DepDelay)), log(abs(ArrDelay)))) + geom_point()

# This looks beautiful, we can now continue with our work!

################################################################################
# Categorize the data, leave only what we are going to be working with
################################################################################

min(month$CRSDepTime)
max(month$CRSDepTime)
min(month$CRSArrTime)
max(month$CRSArrTime)
min(month$Distance)
max(month$Distance)

categorize.month <- function(frame) {
    # Time of day - Departure
    intervals <- c(0,100,200,300,400,500,600,700,800,900,1000,1100,1200,
                   1300,1400,1500,1600,1700,1800,1900,2000,2100,2200,2300,2400)
    categories <- c("00-01","01-02","02-03","03-04","04-05","05-06", 
                    "06-07","07-08","08-09","09-10","10-11","11-12",
                    "12-13","13-14","14-15","15-16","16-17","17-18", 
                    "18-19","19-20","20-21","21-22","22-23","23-24")
    frame$DTime <- cut(frame$CRSDepTime, intervals, categories)
    # Time of day - Arrival
    frame$ATime <- cut(frame$CRSArrTime, intervals, categories)
    # Day of the week
    frame$FlightDate <- as.Date(frame$FlightDate)
    frame$WeekDay <- format(frame$FlightDate, "%A")
    frame$WeekDay <- factor(frame$WeekDay, 
                            levels=c("Monday","Tuesday","Wednesday", 
                                     "Thursday","Friday","Saturday","Sunday"))
    # Day of the month
    frame$Day <- format(frame$FlightDate, "%d")
    # Carrier
    frame$CarrierName[frame$Carrier=="9E"] <- "Pinnacle Airlines"
    frame$CarrierName[frame$Carrier=="AA"] <- "American Airlines"
    frame$CarrierName[frame$Carrier=="AS"] <- "Alaska Airlines"
    frame$CarrierName[frame$Carrier=="B6"] <- "JetBlue Airways"
    frame$CarrierName[frame$Carrier=="DL"] <- "Delta Air Lines"
    frame$CarrierName[frame$Carrier=="EV"] <- "Atlantic Southeast Airlines"
    frame$CarrierName[frame$Carrier=="F9"] <- "Frontier Airlines"
    frame$CarrierName[frame$Carrier=="FL"] <- "AirTran Airways"
    frame$CarrierName[frame$Carrier=="HA"] <- "Hawaiian Airlines"
    frame$CarrierName[frame$Carrier=="MQ"] <- "American Eagle Airlines"
    frame$CarrierName[frame$Carrier=="OO"] <- "SkyWest Airlines"
    frame$CarrierName[frame$Carrier=="UA"] <- "United Airlines"
    frame$CarrierName[frame$Carrier=="US"] <- "US Airways"
    frame$CarrierName[frame$Carrier=="VX"] <- "Virgin America"
    frame$CarrierName[frame$Carrier=="WN"] <- "Southwest Airlines"
    frame$CarrierName[frame$Carrier=="YV"] <- "Mesa Airlines"
    # Distance
    intervals <- c(-Inf,500,1000,1500,2000,2500,3000,3500,4000,45000,Inf)
    categories <- c("<500","500-1000","1000-1500","1500-2000","2000-2500",
                    "2500-3000","3000-3500","3500-4000","4000-4500",">4500")
    frame$DistanceGroup <- cut(frame$Distance, intervals, categories)
    
    return(frame)
}

if(!file.exists(GEO_MONTH)) {
    cat(c("Categorizing month...\n"), sep="")
    month <- categorize.month(month)
}

# Our categories for now will be Origin State, Destination State, as well 
# as those categorized above in the the function categorize.month

################################################################################
# Conduct exploratory data analysis for January 2013!
################################################################################

metrics <- function(x) {
    c(length(x),                 # count
      round(mean(x)),            # mean
      round(median(x)),          # median
      names(sort(-table(x)))[1], # mode
      round(sd(x)),              # standard deviation
      min(x),                    # minimum
      max(x),                    # maximum
      max(x)-min(x))             # range
}

# By number of flight quantitive
summaryBy(OriginState~OriginState, data=month, FUN=length)
summaryBy(DestState~DestState, data=month, FUN=length)
summaryBy(DTime~DTime, data=month, FUN=length)
summaryBy(ATime~ATime, data=month, FUN=length)
summaryBy(WeekDay~WeekDay, data=month, FUN=length)
summaryBy(Day~Day, data=month, FUN=length)
summaryBy(CarrierName~CarrierName, data=month, FUN=length)
summaryBy(DistanceGroup~DistanceGroup, data=month, FUN=length)
# By number of flight visual
ggplot(month, aes(x=OriginState, fill=OriginState, ymax=max(..count..))) + 
    geom_histogram(binwidth = diff(range(month$Distance)/50)) +
    stat_bin(binwidth=1,geom="text",drop=TRUE,aes(label=..count.., vjust=-1))
ggplot(month, aes(x=DestState, fill=DestState, ymax=max(..count..))) + 
    geom_histogram(binwidth = diff(range(month$Distance)/50)) +
    stat_bin(binwidth=1,geom="text",drop=TRUE,aes(label=..count.., vjust=-1))
ggplot(month, aes(x=DTime, fill=DTime, ymax=max(..count..))) + 
    geom_histogram(binwidth = diff(range(month$Distance)/50)) +
    stat_bin(binwidth=1,geom="text",drop=TRUE,aes(label=..count.., vjust=-1))
ggplot(month, aes(x=ATime, fill=ATime, ymax=max(..count..))) + 
    geom_histogram(binwidth = diff(range(month$Distance)/50)) +
    stat_bin(binwidth=1,geom="text",drop=TRUE,aes(label=..count.., vjust=-1))
ggplot(month, aes(x=WeekDay, fill=WeekDay, ymax=max(..count..))) + 
    geom_histogram(binwidth = diff(range(month$Distance)/50)) +
    stat_bin(binwidth=1,geom="text",drop=TRUE,aes(label=..count.., vjust=-1))
ggplot(month, aes(x=Day, fill=Day, ymax=max(..count..))) + 
    geom_histogram(binwidth = diff(range(month$Distance)/50)) +
    stat_bin(binwidth=1,geom="text",drop=TRUE,aes(label=..count.., vjust=-1))
ggplot(month, aes(x=CarrierName, fill=CarrierName, ymax=max(..count..))) + 
    geom_histogram(binwidth = diff(range(month$Distance)/50)) +
    stat_bin(binwidth=1,geom="text",drop=TRUE,aes(label=..count.., vjust=-1))
ggplot(month, aes(x=DistanceGroup, fill=DistanceGroup, ymax=max(..count..))) + 
    geom_histogram(binwidth = diff(range(month$Distance)/50)) +
    stat_bin(binwidth=1,geom="text",drop=TRUE,aes(label=..count.., vjust=-1))

# From this we have learned that Day is probably not the best category because
# it provides information similar to the one that Weekday provides so we won't
# look into those much further.
# DistanceGroup does not seem to provide interesting data, other than the fact 
# that amount of flights and distance are inversely proportional. It has nothing
# too useful in terms of trying to select a flight  so we will mostly omit it.

# Departure Delay and Arrival Delay quantitive

# By origin and destination state
summaryBy(DepDelay~OriginState, data=month, FUN=metrics,
          fun.names=c("Count","Mean","Median","Mode","SD","Min","Max","Range"))
summaryBy(ArrDelay~DestState, data=month, FUN=metrics,
          fun.names=c("Count","Mean","Median","Mode","SD","Min","Max","Range"))
# By departure and arrival time
summaryBy(DepDelay~DTime, data=month, FUN=metrics,       
          fun.names=c("Count","Mean","Median","Mode","SD","Min","Max","Range"))
summaryBy(ArrDelay~ATime, data=month, FUN=metrics,
          fun.names=c("Count","Mean","Median","Mode","SD","Min","Max","Range"))
# By day of the week
summaryBy(DepDelay~WeekDay, data=month, FUN=metrics,          
          fun.names=c("Count","Mean","Median","Mode","SD","Min","Max","Range"))
summaryBy(ArrDelay~WeekDay, data=month, FUN=metrics,
          fun.names=c("Count","Mean","Median","Mode","SD","Min","Max","Range"))
# By carrier name
summaryBy(DepDelay~CarrierName, data=month, FUN=metrics,        
          fun.names=c("Count","Mean","Median","Mode","SD","Min","Max","Range"))
summaryBy(ArrDelay~CarrierName, data=month, FUN=metrics,
          fun.names=c("Count","Mean","Median","Mode","SD","Min","Max","Range"))
# By distance group
summaryBy(DepDelay~DistanceGroup, data=month, FUN=metrics,          
          fun.names=c("Count","Mean","Median","Mode","SD","Min","Max","Range"))
summaryBy(ArrDelay~DistanceGroup, data=month, FUN=metrics,
          fun.names=c("Count","Mean","Median","Mode","SD","Min","Max","Range"))

# Departure Delay and Arrival Delay visual

# By origin state
ggplot(month, aes(x=OriginState, y=DepDelay, fill=OriginState)) + geom_boxplot()
ggplot(month, aes(x=DepDelay, fill=OriginState)) + stat_density()
ggplot(month, aes(x=OriginState, y=DepDelay, color=OriginState)) + geom_point()
# By destination state
ggplot(month, aes(x=DestState, y=ArrDelay, fill=DestState)) + geom_boxplot()
ggplot(month, aes(x=ArrDelay, fill=DestState)) + stat_density()
ggplot(month, aes(x=DestState, y=ArrDelay, color=DestState)) + geom_point()
# By departure time
ggplot(month, aes(x=DTime, y=DepDelay, fill=DTime)) + geom_boxplot()
ggplot(month, aes(x=DepDelay, fill=DTime)) + stat_density()
ggplot(month, aes(x=DTime, y=DepDelay, color=DTime)) + geom_point()
# By arrival time
ggplot(month, aes(x=ATime, y=ArrDelay, fill=ATime)) + geom_boxplot()
ggplot(month, aes(x=ArrDelay, fill=ATime)) + stat_density()
ggplot(month, aes(x=ATime, y=ArrDelay, color=ATime)) + geom_point()
# Departure delay by day of the week
ggplot(month, aes(x=WeekDay, y=DepDelay, fill=WeekDay)) + geom_boxplot()
ggplot(month, aes(x=DepDelay, fill=WeekDay)) + stat_density()
ggplot(month, aes(x=WeekDay, y=DepDelay, color=WeekDay)) + geom_point()

# The scatter plots don't seem too useful, because we can understand this kind 
# of data better from the box and whisers plots, so we shall continue doing
# box plots only instead

# Arrival delay by day of the week
ggplot(month, aes(x=WeekDay, y=ArrDelay, fill=WeekDay)) + geom_boxplot()
ggplot(month, aes(x=ArrDelay, fill=WeekDay)) + stat_density()
# Departure delay by carrier name
ggplot(month, aes(x=CarrierName, y=DepDelay, fill=CarrierName)) + geom_boxplot()
ggplot(month, aes(x=DepDelay, fill=CarrierName)) + stat_density()
# Arrival delay by carrier name
ggplot(month, aes(x=CarrierName, y=ArrDelay, fill=CarrierName)) + geom_boxplot()
ggplot(month, aes(x=ArrDelay, fill=CarrierName)) + stat_density()

################################################################################
# Conduct exploratory data analysis over the whole year of 2013!
################################################################################

if(!file.exists("./clean_data_set/12_compile.csv")) {   
# Let's first set up the compiled data
parital <- "./data_set/On_Time_On_Time_Performance_2013_"
yr.OriginState <- matrix(0, 12*50, 4)
colnames(yr.OriginState) <- c("Month", "OriginState", "Total", "DepDelayAvg")
yr.DestState <- matrix(0, 12*50, 4)
colnames(yr.DestState) <- c("Month", "DestState", "Total", "ArrDelayAvg")
yr.WeekDay <- matrix(0, 12*7, 5)
colnames(yr.WeekDay) <- c("Month", "WeekDay", "Total", 
                          "DepDelayAvg", "ArrDelayAvg")
yr.CarrierName <- matrix(0, 12*16, 5)
colnames(yr.CarrierName) <- c("Month", "CarrierName", "Total", 
                              "DepDelayAvg", "ArrDelayAvg")

# Compile the data
for(x in 1:12) {
    current_file <- paste(c(parital, x), collapse = "")
    current_file <- paste(c(current_file, ".csv"), collapse = "")
    
    cat(c("Computing data for ", x, "/2013...\n"), sep="")
    
    m <- read.csv(file=current_file)
    cat(c("Cleaning...\n"), sep="")
    m <- cleanup.month(m)
    cat(c("Categorizing...\n"), sep="")
    m <- categorize.month(m)
    write.csv(m, file=paste(c("./clean_data_set/", x, "_compile.csv"), 
                            collapse = ""), row.names=FALSE)
    cat(c("Compiling...\n"), sep="")
    
    yr.OriginState[(((x-1)*50)+1):(x*50),1] <- matrix(data=x, ncol=1, nrow=50)
    yr.OriginState[(((x-1)*50)+1):(x*50),2] <- levels(factor(month$OriginState))    
    yr.DestState[(((x-1)*50)+1):(x*50),1] <- matrix(data=x, ncol=1, nrow=50)
    yr.DestState[(((x-1)*50)+1):(x*50),2] <- levels(factor(month$DestState))
    for(y in (((x-1)*50)+1):(x*50)) {
        yr.OriginState[y,3] <- 
            NROW(subset(m$OriginState, m$OriginState==yr.OriginState[y,2]))
        yr.OriginState[y,4] <- 
            mean(subset(m, m$OriginState==yr.OriginState[y,2])$DepDelay)
        yr.DestState[y,3] <- 
            NROW(subset(m$DestState, m$DestState==yr.DestState[y,2]))
        yr.DestState[y,4] <- 
            mean(subset(m, m$DestState==yr.DestState[y,2])$ArrDelay)
    }
    
    yr.WeekDay[(((x-1)*7)+1):(x*7),1] <- matrix(data=x, ncol=1, nrow=7)
    yr.WeekDay[(((x-1)*7)+1):(x*7),2] <- 
        c("Monday", "Tuesday", "Wednesday", 
          "Thursday", "Friday", "Saturday", "Sunday")
    for(y in (((x-1)*7)+1):(x*7)) {
        yr.WeekDay[y,3] <- NROW(subset(m$WeekDay, m$WeekDay==yr.WeekDay[y,2]))
        yr.WeekDay[y,4] <- mean(subset(m, m$WeekDay==yr.WeekDay[y,2])$DepDelay)
        yr.WeekDay[y,5] <- mean(subset(m, m$WeekDay==yr.WeekDay[y,2])$ArrDelay)
    }
    
    yr.CarrierName[(((x-1)*16)+1):(x*16),1] <- matrix(data=x, ncol=1, nrow=16)
    yr.CarrierName[(((x-1)*16)+1):(x*16),2] <- levels(factor(month$CarrierName))
    for(y in (((x-1)*16)+1):(x*16)) {
        yr.CarrierName[y,3] <- 
            NROW(subset(m$CarrierName, m$CarrierName==yr.CarrierName[y,2]))
        yr.CarrierName[y,4] <- 
            mean(subset(m, m$CarrierName==yr.CarrierName[y,2])$DepDelay)
        yr.CarrierName[y,5] <- 
            mean(subset(m, m$CarrierName==yr.CarrierName[y,2])$ArrDelay)
    }
}

write.csv(yr.OriginState, file="./year/origin.csv", row.names=FALSE)
write.csv(yr.DestState, file="./year/destination.csv", row.names=FALSE)
write.csv(yr.WeekDay, file="./year/weekday.csv", row.names=FALSE)
write.csv(yr.CarrierName, file="./year/carrier.csv", row.names=FALSE)
} else {
    yr.OriginState <- read.csv(file="./year/origin.csv")
    yr.DestState <- read.csv(file="./year/destination.csv")
    yr.WeekDay <- read.csv(file="./year/weekday.csv")
    yr.CarrierName <- read.csv(file="./year/carrier.csv")
}

# We should now have the data we need, so on to some EDA!

ggplot(yr.WeekDay, aes(DepDelayAvg, ArrDelayAvg)) + 
    geom_point() + stat_quantile(quantiles=.5)

# Make sure WeekDay is ordered properly
yr.WeekDay$WeekDay <- 
    factor(yr.WeekDay$WeekDay, 
           levels= c("Monday", "Tuesday", "Wednesday", 
                     "Thursday", "Friday", "Saturday", "Sunday"))

# By number of flights quantitive
summaryBy(Total~Month, data=yr.WeekDay, FUN=sum)
summaryBy(Total~OriginState, data=yr.OriginState, FUN=sum)
summaryBy(Total~DestState, data=yr.DestState, FUN=sum)
summaryBy(Total~WeekDay, data=yr.WeekDay, FUN=sum)
summaryBy(Total~CarrierName, data=yr.CarrierName, FUN=sum)

# By number of flight visual
ggplot(yr.WeekDay, aes(x=Month, y=Total, fill=Month)) + 
    geom_histogram(binwidth=12, stat="identity")
ggplot(yr.OriginState, aes(x=OriginState, y=Total, fill=OriginState)) + 
    geom_histogram(binwidth=50, stat="identity")
ggplot(yr.DestState, aes(x=DestState, y=Total, fill=DestState)) + 
    geom_histogram(binwidth=50, stat="identity")
ggplot(yr.WeekDay, aes(x=WeekDay, y=Total, fill=WeekDay)) + 
    geom_histogram(binwidth=7, stat="identity")
ggplot(yr.CarrierName, aes(x=CarrierName, y=Total, fill=CarrierName)) + 
    geom_histogram(binwidth=16, stat="identity")

# Departure Delay Average and Arrival Delay Average quantitive

# By origin and destination state
summaryBy(DepDelayAvg~OriginState, data=yr.OriginState, FUN=mean)
summaryBy(ArrDelayAvg~DestState, data=yr.DestState, FUN=mean)
# By day of the week
summaryBy(DepDelayAvg~WeekDay, data=yr.WeekDay, FUN=mean)
summaryBy(ArrDelayAvg~WeekDay, data=yr.WeekDay, FUN=mean)
# By carrier name
summaryBy(DepDelayAvg~CarrierName, data=yr.CarrierName, FUN=mean)
summaryBy(ArrDelayAvg~CarrierName, data=yr.CarrierName, FUN=mean)

# Departure Delay Average and Arrival Delay Average quantitive

# By origin state
ggplot(yr.OriginState, aes(x=OriginState, y=DepDelayAvg, fill=OriginState)) + 
    geom_boxplot()
ggplot(yr.OriginState, aes(x=DepDelayAvg, fill=OriginState)) + stat_density()
# By destinatin state
ggplot(yr.DestState, aes(x=DestState, y=ArrDelayAvg, fill=DestState)) + 
    geom_boxplot()
ggplot(yr.DestState, aes(x=ArrDelayAvg, fill=DestState)) + stat_density()
# Departure delay day of the week
ggplot(yr.WeekDay, aes(x=WeekDay, y=DepDelayAvg, fill=WeekDay)) + geom_boxplot()
ggplot(yr.WeekDay, aes(x=DepDelayAvg, fill=WeekDay)) + stat_density()
# Arrival delay day of the week
ggplot(yr.WeekDay, aes(x=WeekDay, y=ArrDelayAvg, fill=WeekDay)) + geom_boxplot()
ggplot(yr.WeekDay, aes(x=ArrDelayAvg, fill=WeekDay)) + stat_density()
# Departure delay carrier name
ggplot(yr.CarrierName, aes(x=CarrierName, y=DepDelayAvg, fill=CarrierName)) + 
    geom_boxplot()
ggplot(yr.CarrierName, aes(x=DepDelayAvg, fill=CarrierName)) + stat_density()
# Arrival delay carrier name
ggplot(yr.CarrierName, aes(x=CarrierName, y=ArrDelayAvg, fill=CarrierName)) + 
    geom_boxplot()
ggplot(yr.CarrierName, aes(x=ArrDelayAvg, fill=CarrierName)) + stat_density()

################################################################################
# K-Means and Maps
################################################################################

# Categorize geocodes
if(!file.exists(GEO_CITY)) {
    levels(factor(month$OriginCityName)) == levels(factor(month$DestCityName))
    geocodes <- geocode(levels(factor(month$OriginCityName)))
    geocodes[,3] <- levels(factor(month$OriginCityName))
    write.csv(geocodes, file=GEO_CITY, row.names=FALSE)
} else {
    geocodes <- read.csv(file=GEO_CITY)
}

add.geocodes <- function(frame, codes) {
    for(x in 1:nrow(codes)) {
        frame$LatOrigin[frame$OriginCityName==codes[x,3]] <- codes[x,2]
        frame$LonOrigin[frame$OriginCityName==codes[x,3]] <- codes[x,1]
        frame$LatDest[frame$DestCityName==codes[x,3]] <- codes[x,2]
        frame$LonDest[frame$DestCityName==codes[x,3]] <- codes[x,1]
    }
    
    return(frame)
}

if(!file.exists(GEO_MONTH)) {
    cat(c("Geocoding month...\n"), sep="")
    month <- add.geocodes(month, geocodes)
    write.csv(month, file=GEO_MONTH, row.names=FALSE)
}

# Set up the map
left <- min(geocodes$lon)
bottom <- min(geocodes$lat)
right <- max(geocodes$lon)
top <- max(geocodes$lat)
map <- get_map(location = c(left,bottom,right,top))

# K-means (airport regions)
clusters <- data.frame(geocodes$lat, geocodes$lon)
k.clusters <- kmeans(clusters, 2)
clusters$two <- k.clusters$cluster
k.clusters <- kmeans(clusters, 3)
clusters$three <- k.clusters$cluster
k.clusters <- kmeans(clusters, 4)
clusters$four <- k.clusters$cluster
k.clusters <- kmeans(clusters, 5)
clusters$five <- k.clusters$cluster
k.clusters <- kmeans(clusters, 6)
clusters$six <- k.clusters$cluster

# K-means on the map (airport regions)
ggmap(map) + 
    geom_point(data=clusters, 
               aes(x=geocodes.lon, y=geocodes.lat, color=factor(two)))
ggmap(map) + 
    geom_point(data=clusters, 
               aes(x=geocodes.lon, y=geocodes.lat, color=factor(three)))
ggmap(map) + 
    geom_point(data=clusters, 
               aes(x=geocodes.lon, y=geocodes.lat, color=factor(four)))
ggmap(map) + 
    geom_point(data=clusters, 
               aes(x=geocodes.lon, y=geocodes.lat, color=factor(five)))
ggmap(map) + 
    geom_point(data=clusters, 
               aes(x=geocodes.lon, y=geocodes.lat, color=factor(six)))

# Four clusters seem to be the more accurate depiction of airport regions

################################################################################
# Fun creative section! Let's create some heat maps.
################################################################################

# Set up the map
usa <- map("state.carto", fill=TRUE, plot=FALSE)

# Set up the data
heat.data <- data.frame(states=usa$name)
heat.data$DepDelay <- 
    summaryBy(DepDelayAvg~OriginState, data=yr.OriginState, FUN=mean)[,2]
heat.data$ArrDelay <- 
    summaryBy(ArrDelayAvg~DestState, data=yr.DestState, FUN=mean)[,2]

# Set up the colors and legend
min(heat.data$DepDelay)
max(heat.data$DepDelay)
min(heat.data$ArrDelay)
max(heat.data$ArrDelay)
heat.data$DepBuckets <- 
    cut(heat.data$DepDelay, c(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16))
heat.data$ArrBuckets <- cut(heat.data$ArrDelay, c(1,2,3,4,5,6,7,8,9,10,11,12))
DepLegend <- c("0-1", "1-2", "2-3", "3-4", "4-5", "5-6", "6-7", "7-8", "8-9",
               "9-10", "10-11", "11-12", "12-13", "13-14", "14-15", "15-16")
ArrLegend <- c("1-2", "2-3", "3-4", "4-5", "5-6", "6-7", "7-8", "8-9",
               "9-10", "10-11", "11-12")
DepColors <- topo.colors(16)
ArrColors <- topo.colors(11)

# Departure delay average map
map("state", col=DepColors[heat.data$DepBuckets], fill=TRUE)
legend("bottomright", DepLegend, horiz = FALSE, fill=DepColors, ncol=2)
# Arrival delay average map
map("state", col=ArrColors[heat.data$ArrBuckets], fill=TRUE)
legend("bottomright", ArrLegend, horiz = FALSE, fill=ArrColors)

