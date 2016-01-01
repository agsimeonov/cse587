# By: Alexander Simeonov

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

# Load the data for a specific day
cat(c("Initializing data...\n"), sep="")

if(file.exists("./data_set/nyt1.csv")) {
    day <- read.csv(file="./data_set/nyt1.csv")
} else {
    cat(c("Can't find data file!\n"), sep="")
    #day <- read.csv(url("http://stat.columbia.edu/~rachel/datasets/nyt1.csv"))
}

# Once you have the data loaded, it's time for some EDA:

# Sample data to get familiar with its layout
head(day)

# Only signed in users have ages and genders
summaryBy(Gender+Signed_In+Impressions+Clicks~age_group, data = day)

# QUESTION:
# 1. Create a new variable, age_group, that categorizes users as
#    "<18", "18-24", "25-34", "35-44", "45-54", "55-64", and "65+".
intervals <- c(-Inf,0,18,24,34,44,54,64,Inf)
categories <- c("N/A","<18","18-24","25-34","35-44","45-54","55-64","65+")
age_group <- cut(day$Age, intervals, categories)
day$age_group <- age_group

# QUESTION:
# 2. For a single day:

#      BULLET 1:
#    - Plot the distributions of number impressions and click-
#      through-rate (CTR=# click/# impressions) for these eight age
#      categories.

# We can't have no impressions for the click-through-rate
subset(day, (Clicks>0)&(Impressions==0))

# It looks like there are no clicks where there are no impressions which is
# good and to be expected.

# Number of impressions distribution plots:
# Histogram
ggplot(day, aes(x=Impressions, fill=age_group)) + geom_histogram(binwidth=1)
# Box and whisker plot
ggplot(day, aes(x=age_group, y=Impressions, fill=age_group)) + geom_boxplot()
# Density plot
ggplot(day, aes(x=Impressions,color=age_group)) + geom_density()
# Violin plot
ggplot(day, aes(x=age_group, y=Impressions, fill=age_group)) + geom_violin()
# Jitter
ggplot(day, aes(x=age_group, y=Impressions, colour=age_group)) + geom_jitter()
# Frequency polygon plot
ggplot(day, aes(x=Impressions, color=age_group)) + geom_freqpoly(binwidth=1)
# Kernel density estimate
ggplot(day, aes(x=Impressions, fill=age_group)) + stat_density()

# Click-through-rate plots:
# Density plot
# Subset with Clicks>0 provides more understandable plots than Impressions>0
# This can be seen by plotting the following density plots:
ggplot(subset(day, Impressions>0), 
       aes(x=Clicks/Impressions, colour=age_group)) + geom_density()
ggplot(subset(day, Clicks>0), 
       aes(x=Clicks/Impressions, colour=age_group)) + geom_density()
# Box and whisker plot
ggplot(subset(day, Clicks>0), 
       aes(x=age_group, y=Clicks/Impressions, fill=age_group)) + geom_boxplot()
# Viloin plot
ggplot(subset(day, Clicks>0), 
       aes(x=age_group, y=Clicks/Impressions, fill=age_group)) + geom_violin()
# Jitter
ggplot(subset(day, Clicks>0), 
       aes(x=age_group, y=Clicks/Impressions, color=age_group)) + geom_jitter()
# Frequency polygon plot
ggplot(subset(day, Clicks>0),
       aes(x=Clicks/Impressions, color=age_group)) + geom_freqpoly(binwidth=.05)
# Histogram
ggplot(subset(day, Clicks>0), 
       aes(x=Clicks/Impressions, fill=age_group)) + 
    geom_histogram(binwidth=.025)
# Kernel density estimate
ggplot(subset(day, Clicks>0), 
       aes(x=Clicks/Impressions, fill=age_group)) + stat_density()

#      BULLET 2:
#    - Define a new variable to segment or categorize users based on
#      their click behavior.

# Investigate click behavior
ggplot(subset(day, Clicks>0), 
       aes(x=age_group, y=Clicks, fill=age_group)) + geom_boxplot()
ggplot(subset(day, Clicks>0), aes(x=Clicks, colour=age_group)) + geom_density()
ggplot(subset(day, Clicks>0), aes(x=Clicks, colour=age_group)) + stat_density()

# It seems clicks are close to 1 in general so we can categorize them as such:
day$click_group[day$Impressions==0] <- "No_Impressions"
day$click_group[day$Impressions>0] <- "No_Clicks"
day$click_group[day$Clicks>0] <- "Clicks"

#Convert the column to a factor
day$click_group <- factor(day$click_group)
click_group <- day$click_group
head(day)

#      BULLET 3:
#    - Explore the data and make visual and quantitative comparisons
#      across user segments/demographics (<18-year-old males versus
#      <18-year-old females or logged-in versus not, for example).

# Factorize Gender and Signed_In
day$Gender <- factor(day$Gender)
day$Signed_In <- factor(day$Signed_In)

# Visual comparisons
# Logged-in versus not
ggplot(day, aes(x=Signed_In, fill=factor(Signed_In, labels=c("No","Yes")), 
                ymax=max(..count..))) + geom_histogram(binwidth=1) + 
    stat_bin(binwidth=1,geom="text",drop=TRUE,aes(label=..count.., vjust=-1)) + 
    guides(fill=guide_legend(title="Signed In"))
# By click behavior categories
ggplot(day, aes(x=click_group, fill=click_group, 
                ymax=max(..count..))) + geom_histogram(binwidth=1) + 
    stat_bin(binwidth=1,geom="text",drop=TRUE,aes(label=..count.., vjust=-1))
# By age group
ggplot(day, aes(x=age_group, fill=age_group, 
                ymax=max(..count..))) + geom_histogram(binwidth=1) + 
    stat_bin(binwidth=1,geom="text",drop=TRUE,aes(label=..count.., vjust=-1))
# Males versus females total
ggplot(subset(day, Signed_In==1), 
       aes(x=Gender, fill=factor(Gender, labels=c("Female","Male")), 
           ymax=max(..count..))) + geom_histogram(binwidth=1) + 
    stat_bin(binwidth=1,geom="text",drop=TRUE,aes(label=..count.., vjust=-1)) + 
    guides(fill=guide_legend(title="Gender"))
# <18-year-old males versus <18-year-old females
ggplot(subset(day, age_group=="<18"), 
       aes(x=Gender, fill=factor(Gender, labels=c("Female","Male")), 
           ymax=max(..count..))) + geom_histogram(binwidth=1) + 
    stat_bin(binwidth=1,geom="text",drop=TRUE,aes(label=..count.., vjust=-1)) + 
    guides(fill=guide_legend(title="Gender"))
# 18-24-year-old males versus 18-24-year-old females
ggplot(subset(day, age_group=="18-24"), 
       aes(x=Gender, fill=factor(Gender, labels=c("Female","Male")), 
           ymax=max(..count..))) + geom_histogram(binwidth=1) + 
    stat_bin(binwidth=1,geom="text",drop=TRUE,aes(label=..count.., vjust=-1)) + 
    guides(fill=guide_legend(title="Gender"))
# 25-34-year-old males versus 25-34-year-old females
ggplot(subset(day, age_group=="25-34"), 
       aes(x=Gender, fill=factor(Gender, labels=c("Female","Male")), 
           ymax=max(..count..))) + geom_histogram(binwidth=1) + 
    stat_bin(binwidth=1,geom="text",drop=TRUE,aes(label=..count.., vjust=-1)) + 
    guides(fill=guide_legend(title="Gender"))
# 35-44-year-old males versus 35-44-year-old females
ggplot(subset(day, age_group=="35-44"), 
       aes(x=Gender, fill=factor(Gender, labels=c("Female","Male")), 
           ymax=max(..count..))) + geom_histogram(binwidth=1) + 
    stat_bin(binwidth=1,geom="text",drop=TRUE,aes(label=..count.., vjust=-1)) + 
    guides(fill=guide_legend(title="Gender"))
# 45-54-year-old males versus 45-54-year-old females
ggplot(subset(day, age_group=="45-54"), 
       aes(x=Gender, fill=factor(Gender, labels=c("Female","Male")), 
           ymax=max(..count..))) + geom_histogram(binwidth=1) + 
    stat_bin(binwidth=1,geom="text",drop=TRUE,aes(label=..count.., vjust=-1)) + 
    guides(fill=guide_legend(title="Gender"))
# 55-64-year-old males versus 55-64-year-old females
ggplot(subset(day, age_group=="55-64"), 
       aes(x=Gender, fill=factor(Gender, labels=c("Female","Male")), 
           ymax=max(..count..))) + geom_histogram(binwidth=1) + 
    stat_bin(binwidth=1,geom="text",drop=TRUE,aes(label=..count.., vjust=-1)) + 
    guides(fill=guide_legend(title="Gender"))
# 65+-year-old males versus 65+-year-old females
ggplot(subset(day, age_group=="65+"), 
       aes(x=Gender, fill=factor(Gender, labels=c("Female","Male")), 
           ymax=max(..count..))) + geom_histogram(binwidth=1) + 
    stat_bin(binwidth=1,geom="text",drop=TRUE,aes(label=..count.., vjust=-1)) + 
    guides(fill=guide_legend(title="Gender"))

# An interesting thing to note is that the only age category with
# more females than males is 65+

# Quantitative comparisons
summaryBy(Impressions~click_group+Gender+age_group+Signed_In, 
          data = day, FUN = length, var.names="Total_Users", keep.names=TRUE)

#      BULLET 4:
#    - Create metrics/measurements/statistics that summarize the data.
#      Examples of potential metrics include CTR, quantiles, mean,
#      median, variance, and max, and these can be calculated across
#      the various user segments. Be selective. Think about what will
#      be important to track over time—what will compress the data,
#      but still capture user behavior.

# When checking CTR make sure x = Clicks and y = Impressions
# When x = Impressions z = TRUE
metrics <- function(x, y, z=FALSE) {
    if(z == TRUE) {
        metrics_out <- c(sum(x))                                  # count
    } else if(missing(y)) {
        metrics_out <- c(length(x))                               # count
    } else {
        metrics_out <- c(sum(x),                                  # count
                         signif(length(x)/length(y), digits = 4)) # CTR
    }
    metrics_out <- c(metrics_out,
                     signif(mean(x), digits = 4),                 # mean
                     signif(median(x), digits = 4),               # median
                     names(sort(-table(x)))[1],                   # mode
                     signif(var(x), digits = 4),                  # variance
                     min(x),                                      # minimum
                     max(x),                                      # maximum
                     max(x)-min(x))                               # range
    metrics_out
}

# Quartiles don't seem to be particularly useful of a metric with this data set

# View general summary
summary(day)
# Age by age group
summaryBy(Age~age_group, data=day, FUN=metrics, 
          fun.names=c("Count", "Mean", "Median", "Mode", "Variance", 
                      "Min", "Max", "Range"))
# Gender by age group
summaryBy(Gender~age_group, data=day, FUN=metrics, 
          fun.names=c("Count", "Mean", "Median", "Mode", "Variance", 
                      "Min", "Max", "Range"))
# Impressions by age group
summaryBy(Impressions~age_group, data=day, FUN=metrics, var.names="Imp", z=T,
          fun.names=c("Count", "Mean", "Median", "Mode", "Variance", 
                      "Min", "Max", "Range"))
# Impressions by gender
summaryBy(Impressions~Gender, data=subset(day, Signed_In==1), 
          FUN=metrics, var.names="Imp", z=T,
          fun.names=c("Count", "Mean", "Median", "Mode", "Variance", 
                      "Min", "Max", "Range"))
# Impressions by click group
summaryBy(Impressions~click_group, data=day, FUN=metrics, var.names="Imp", z=T,
          fun.names=c("Count", "Mean", "Median", "Mode", "Variance", 
                      "Min", "Max", "Range"))
# Impressions by login status
summaryBy(Impressions~Signed_In, data=day, FUN=metrics, var.names="Imp", z=T,
          fun.names=c("Count", "Mean", "Median", "Mode", "Variance", 
                      "Min", "Max", "Range"))
# Clicks by age group
summaryBy(Clicks~age_group, data=day, FUN=metrics, y=day$Impressions, 
          fun.names=c("Count", "CTR", "Mean", "Median", "Mode", "Variance", 
                      "Min", "Max", "Range"))
# Clicks by gender
summaryBy(Clicks~Gender, data=subset(day, Signed_In==1), 
          FUN=metrics, y=day$Impressions, 
          fun.names=c("Count", "CTR", "Mean", "Median", "Mode", "Variance", 
                      "Min", "Max", "Range"))
# Clicks by login status
summaryBy(Clicks~Signed_In, data=day, FUN=metrics, y=day$Impressions, 
          fun.names=c("Count", "CTR", "Mean", "Median", "Mode", "Variance", 
                      "Min", "Max", "Range"))

# QUESTION:
# 3. Now extend your analysis across days. Visualize some metrics and
#    distributions over time.

# We will do this by collecting relevant information about each day into a 
# data.frame. We can look at specific data by simply changing the day at the
# top of this file and running source on it.  To view data about the whole
# month, on the other hand, we will parse relevant totals into said data.frame.
# One can change the for loop to do only particular days as well if needed.

# NOTE: We will create a groups and a days frame.  Groups will give us data by
# specific user groups, while days will give us data by specific days

# IMPORTANT: Make sure all the csv files for the month are located in the folder 
# specified by the "folder" string variable! Also make sure totals and days are
# both loaded as defined.
folder <- "./data_set"
totals <- matrix(0, 45, 3)
days <- data.frame(Day = 1:31, Users = NA, Clicks = NA, Impressions = NA,
                   Females = NA, Males = NA, NI_Users = NA, NC_Users = NA,
                   C_Users = NA)
for(x in 1:31) {
    # Set up
    current_file <- paste(c(folder, "/nyt"), collapse = "")
    current_file <- paste(c(current_file, x), collapse = "")
    current_file <- paste(c(current_file, ".csv"), collapse = "")
    d <- read.csv(file=current_file)
    d$age_group <- cut(d$Age, intervals, categories)
    d$click_group[d$Impressions==0] <- "No_Impressions"
    d$click_group[d$Impressions>0] <- "No_Clicks"
    d$click_group[d$Clicks>0] <- "Clicks"
    d$click_group <- factor(d$click_group)
    d$Gender <- factor(d$Gender)
    d$Signed_In <- factor(d$Signed_In)
    
    cat(c("Computing data for 5/", x, "/2012\n"), sep="")
    
    # Additional per day totals
    days[days$Day==x,5] <- nrow(subset(d, (Gender==0)&(Signed_In==1)))
    days[days$Day==x,6] <- nrow(subset(d, (Gender==1)&(Signed_In==1)))
    days[days$Day==x,7] <- nrow(subset(d, click_group=="No_Impressions"))
    days[days$Day==x,8] <- nrow(subset(d, click_group=="No_Clicks"))
    days[days$Day==x,9] <- nrow(subset(d, click_group=="Clicks"))
    
    # Totals
    # Users
    days[days$Day==x,2] <- nrow(d)
    
    groups <- summaryBy(Age~age_group+Gender+click_group+Signed_In, 
                       data=d, FUN=length, keep.names=TRUE, var.names="Users")
    groups$Clicks <- 0
    groups$Impressions <- 0
    totals <- totals + as.matrix(groups[5:7])
    # Total Clicks
    days[days$Day==x,3] <- sum(d$Clicks)
    
    groups <- summaryBy(0+Clicks+0~age_group+Gender+click_group+Signed_In, 
                       data=d, FUN=sum, keep.names=TRUE,
                       var.names=c("Users", "Clicks", "Impressions"))
    totals <- totals + as.matrix(groups[5:7])
    # Total Impressions
    days[days$Day==x,4] <- sum(d$Impressions)
    
    groups <- summaryBy(0+0+Impressions~age_group+Gender+click_group+Signed_In, 
                       data=d, FUN=sum, keep.names=TRUE,
                       var.names=c("Users", "Clicks", "Impressions"))
    totals <- totals + as.matrix(groups[5:7])
}
groups <- as.data.frame(summaryBy(Age~age_group+Gender+click_group+Signed_In, 
                                 data=d, var.names=c("rm"), keep.names=TRUE))
groups["rm"] <- NULL
groups <- cbind(groups, as.data.frame(totals))

# Write the frames into csv files just in case we need to load them quickly
write.csv(groups, file="groups.csv", row.names=FALSE)
write.csv(days, file="days.csv", row.names=FALSE)
# Run this line in order to load the frames quickly from above created csv files
groups <- read.csv(file="groups.csv")
days <- read.csv(file="days.csv")

# We should now have nice data frames containing information needed to perform
# EDA analysis and create nice visualizations for metrics and distributionbs
# over time:

# Some quntitive analysis before we proceed with the visualizations:
summaryBy(Users+Clicks+Impressions+Females+Males+NI_Users+NC_Users+C_Users~Day, 
          data=days, FUN=sum)
summaryBy(Users+Clicks+Impressions~age_group, data=groups, FUN=sum)
summaryBy(Users+Clicks+Impressions~age_group, data=groups, FUN=mean)
summaryBy(Users+Clicks+Impressions~Gender, data=groups, FUN=sum)
summaryBy(Users+Clicks+Impressions~Gender, data=groups, FUN=mean)
summaryBy(Users+Clicks+Impressions~click_group, data=groups, FUN=sum)
summaryBy(Users+Clicks+Impressions~click_group, data=groups, FUN=mean)
summaryBy(Users+Clicks+Impressions~Signed_In, data=groups, FUN=sum)
summaryBy(Users+Clicks+Impressions~Signed_In, data=groups, FUN=mean)

# Metric visualizations:
# NOTE: May 1st 2012 is a Tuesday
# Users per day - very interesting to note that we see most users on Sundays
# there is a huge spike, almost double the user base.  Least amount of users
# on the other hand is on Saturdays, not by too much however.
ggplot(days, aes(x=Day,y=Users,fill=Day))+geom_bar(binwidth=1,stat="identity")
# Clicks per day - Similarly to Users per day we see most clicks on Sundays
# and least clicks on Saturdays, this is definitly due to the amount of users on
# those days
ggplot(days, aes(x=Day,y=Clicks,fill=Day))+geom_bar(binwidth=1,stat="identity")
# Impressions per day - Similarly to Users per day we see most clicks on Sundays
# and least clicks on Saturdays, this is definitly due to the amount of users on
# those days
ggplot(days, aes(x=Day,y=Impressions,fill=Day))+
    geom_bar(binwidth=1,stat="identity")
# Click-through-rate per day - We see a lot higher of a click-through-rate
# 15-30th day of the month, as compared to 31-14th, so we may conclude that
# the click through rate alternates every 15 days.
ggplot(days, aes(x=Day,y=Clicks/Impressions,fill=Day))+
    geom_bar(binwidth=1,stat="identity")
# Additional per day visualizations - similar to User observations:
ggplot(days,aes(x=Day,y=Females,fill=Day))+geom_bar(binwidth=1,stat="identity")
ggplot(days,aes(x=Day,y=Males,fill=Day))+geom_bar(binwidth=1,stat="identity")
ggplot(days,aes(x=Day,y=NI_Users,fill=Day))+geom_bar(binwidth=1,stat="identity")
ggplot(days,aes(x=Day,y=NC_Users,fill=Day))+geom_bar(binwidth=1,stat="identity")
ggplot(days,aes(x=Day,y=C_Users,fill=Day))+geom_bar(binwidth=1,stat="identity")

# Logged-in versus not totals
ggplot(groups, 
       aes(x=Signed_In, y=Users, fill=factor(Signed_In, labels=c("No","Yes"))))+
    geom_bar(binwidth=1, stat="identity")+ 
    guides(fill=guide_legend(title="Signed In"))
# By click behavior categories
ggplot(groups, aes(x=click_group, y=Users, fill=click_group))+
    geom_bar(binwidth=1, stat="identity")
# By age group
ggplot(groups, aes(x=age_group, y=Users, fill=age_group))+
    geom_bar(binwidth=1, stat="identity")
# Males versus females total
ggplot(subset(groups, Signed_In==1), aes(x=Gender, y=Users, 
                   fill=factor(Gender, labels=c("Female","Male"))))+
    geom_bar(binwidth=1, stat="identity")+ 
    guides(fill=guide_legend(title="Gender"))
# <18-year-old males versus <18-year-old females
ggplot(subset(groups, age_group=="<18"), 
       aes(x=Gender, y=Users, fill=factor(Gender, labels=c("Female","Male"))))+
    geom_bar(binwidth=1, stat="identity")+ 
    guides(fill=guide_legend(title="Gender"))
# 18-24-year-old males versus 18-24-year-old females
ggplot(subset(groups, age_group=="18-24"), 
       aes(x=Gender, y=Users, fill=factor(Gender, labels=c("Female","Male"))))+
    geom_bar(binwidth=1, stat="identity")+ 
    guides(fill=guide_legend(title="Gender"))
# 25-34-year-old males versus 25-34-year-old females
ggplot(subset(groups, age_group=="25-34"), 
       aes(x=Gender, y=Users, fill=factor(Gender, labels=c("Female","Male"))))+
    geom_bar(binwidth=1, stat="identity")+ 
    guides(fill=guide_legend(title="Gender"))
# 35-44-year-old males versus 35-44-year-old females
ggplot(subset(groups, age_group=="35-44"), 
       aes(x=Gender, y=Users, fill=factor(Gender, labels=c("Female","Male"))))+
    geom_bar(binwidth=1, stat="identity")+ 
    guides(fill=guide_legend(title="Gender"))
# 45-54-year-old males versus 45-54-year-old females
ggplot(subset(groups, age_group=="45-54"), 
       aes(x=Gender, y=Users, fill=factor(Gender, labels=c("Female","Male"))))+
    geom_bar(binwidth=1, stat="identity")+ 
    guides(fill=guide_legend(title="Gender"))
# 55-64-year-old males versus 55-64-year-old females
ggplot(subset(groups, age_group=="55-64"), 
       aes(x=Gender, y=Users, fill=factor(Gender, labels=c("Female","Male"))))+
    geom_bar(binwidth=1, stat="identity")+ 
    guides(fill=guide_legend(title="Gender"))
# 65+-year-old males versus 65+-year-old females
ggplot(subset(groups, age_group=="65+"), 
       aes(x=Gender, y=Users, fill=factor(Gender, labels=c("Female","Male"))))+
    geom_bar(binwidth=1, stat="identity")+ 
    guides(fill=guide_legend(title="Gender"))

# Distribution visualizations:
# Total impressions/total users over a month by age group distribution plots:
ggplot(groups, aes(x=age_group, y=Impressions/Users, fill=age_group)) + 
    geom_boxplot()
ggplot(groups, aes(x=Impressions/Users,color=age_group)) + geom_density()
ggplot(groups, aes(x=age_group, y=Impressions/Users, fill=age_group)) + 
    geom_violin()
ggplot(groups, aes(x=Impressions/Users, fill=age_group)) + stat_density()

# Total Click-through-rate over a month by age group:
ggplot(subset(groups, Impressions>0), 
       aes(x=Clicks/Impressions, colour=age_group)) + geom_density()
ggplot(subset(groups, Impressions>0), 
       aes(x=age_group, y=Clicks/Impressions, fill=age_group)) + geom_boxplot()
ggplot(subset(groups, Impressions>0), 
       aes(x=age_group, y=Clicks/Impressions, fill=age_group)) + geom_violin()
ggplot(subset(groups, Impressions>0), 
       aes(x=Clicks/Impressions, fill=age_group)) + stat_density()

# CTR per gender
ggplot(subset(groups, (Signed_In==1)&(Impressions>0)), 
       aes(x=Gender, y=sum(Clicks)/sum(Impressions), 
           fill=factor(Gender, labels=c("Female","Male"))))+
    geom_bar(binwidth=1, stat="identity")+ 
    guides(fill=guide_legend(title="Gender"))

# QUESTION:
# 4. Describe and interpret any patterns you find.

# - Users, Clicks, and Impressions are generally much higher on Sundays, there
#   is a huge spike in usage every Sunday, near double the average!
# - Users, Clicks, and Impressions are noticably lower on Saturdays.
# - CTR seems to alternate between around .018 and .021 every 15 days at least
#   as seen in the data provided.
# - CTR over time seems to be the same for females and males
# - Clicks seem to be increasing very slightly over time, but the rate is so low
#   that we may need more data to make sure that this is indeed the case.
# - User, User Types, and Impression numbers seem pretty consistent over time.
# - On average there are around 5 impressions per user.
# - The higher the age-range the more the click-through-rate probability tends
#   towards certain values.
# - When there are clicks, they are rearely more than 1 per user.
# - The age groups seem to be normally distributed around 35-44.
# - Most of the time there are no clicks, rearly no impressions.
# - There are generaly more males than females.
# - There are significantly more males than females in the <18 age group.
# - The 65+ group is the only group with more females than males.
# - There are significantly more females than males in the 65+ group.
# - Around 2/3 of the users are singed in.