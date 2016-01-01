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
prepare.package("gdata")
prepare.package("plyr")

################################################################################
# First challenge: load in and clean up the data
################################################################################

cat(c("Initializing data...\n"), sep="")

if(file.exists("./data_set/rollingsales_manhattan.xls")) {
    mh <- read.xls("./data_set/rollingsales_manhattan.xls",pattern="BOROUGH")
} else {
    cat(c("Can't find data file!\n"), sep="")
}

# Get some information about what we have to work with
head(mh)
summary(mh)

# Set column names to lower case
names(mh) <-tolower(names(mh))

# Creates a numerical version of columns containing commas and/or dollar signs
mh$sale.price.n <- as.numeric(gsub("[^[:digit:]]", "", mh$sale.price))
mh$gross.sqft <- as.numeric(gsub("[^[:digit:]]", "", mh$gross.square.feet))
mh$land.sqft <- as.numeric(gsub("[^[:digit:]]", "", mh$land.square.feet))

# See if we have any invalid values within the newly created columns
count(is.na(mh$sale.price.n))
count(is.na(mh$gross.sqft))
count(is.na(mh$land.sqft))

################################################################################
# Next, conduct exploratory data analysis in order to find out where there are 
# outlier or missing values, decide how you will treat them, make sure the 
# dates are formatted correctly, make sure values you think are numerical are 
# being treated as such, etc.
################################################################################

# Making sure dates are formatted correctly
mh$sale.date <- as.Date(mh$sale.date)
# Making sure values I think are numerical are being treated as such
mh$year.built <- as.numeric(as.character(mh$year.built))

# Some exploration to make sure there's nothing weird going on with sale prices

# Total sales on record
ggplot(mh, aes(x=sale.price.n)) + 
    geom_histogram(binwidth = diff(range(mh$sale.price.n)))
# Total sales on record with a price more than 0
ggplot(subset(mh, sale.price.n>0), aes(x=sale.price.n)) + 
    geom_histogram(binwidth = diff(range(mh$sale.price.n)))
# Total sales on record with a price of 0
ggplot(subset(mh, sale.price.n==0), aes(x=gross.sqft)) + 
    geom_histogram(binwidth = diff(range(mh$gross.sqft)))

# We shall treat sales with price of 0 as missing values and omit them

# Keep only the actual sales
mh.sale <- mh[mh$sale.price.n!=0,]

# Let us look for some outliers
ggplot(mh.sale, aes(gross.sqft, sale.price.n)) + geom_point()
ggplot(mh.sale, aes(log(gross.sqft), log(sale.price.n))) + geom_point()

# Seems like we found some let's investigate further and see what we can do

# Factorize building.class.category
mh.sale$building.class.category <- factor(mh.sale$building.class.category)

# Let us look at building types and see what is most reasonable to work with
levels(mh.sale$building.class.category)

# We do want to work with building types that give us gross.sqft and lad.sqft
summaryBy(gross.sqft+land.sqft~building.class.category, 
          data=mh.sale, FUN=c(min, max))

# It seems most reasonable to work with 1, 2, and 3 family homes
mh.homes <- mh.sale[which(grepl("FAMILY",mh.sale$building.class.category)),]

# Let us look and see if we still have outliers
ggplot(mh.homes, aes(gross.sqft, sale.price.n)) + geom_point()
ggplot(mh.homes, aes(log(gross.sqft), log(sale.price.n))) + geom_point()

# We seem to have some, let's take a closer look
summaryBy(sale.price.n~address, data=subset(mh.homes, sale.price.n<10000), 
          FUN=c(length, min, max))

# The summarized values above are definitely outliers and so we shall omit them
mh.homes$outliers <- (log(mh.homes$sale.price.n) <=5) + 0
mh.homes <- mh.homes[which(mh.homes$outliers==0),]

# Let us look and see if we still have outliers
ggplot(mh.homes, aes(gross.sqft, sale.price.n)) + geom_point()
ggplot(mh.homes, aes(log(gross.sqft), log(sale.price.n))) + geom_point()

# This looks beautiful, we can now continue with our work!

################################################################################
# Once the data is in good shape, conduct exploratory data analysis to visualize
# and make compariosons (i) across neighborhoods,
################################################################################

# Factorize neighborhood
mh.homes$neighborhood <- factor(mh.homes$neighborhood)

# Let's first see which neighborhoods are included in our data
levels(mh.homes$neighborhood)

# Let's explore the data quantitatively first:

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

# Sale price across neighborhoods
summaryBy(sale.price.n~neighborhood, data=mh.homes, FUN=metrics,
          fun.names=c("Count","Mean","Median","Mode","SD","Min","Max","Range"))
# Gross square feet across neighborhoods
summaryBy(gross.sqft~neighborhood, data=mh.homes, FUN=metrics,
          fun.names=c("Count","Mean","Median","Mode","SD","Min","Max","Range"))
# Land square feet across neighborhoods
summaryBy(land.sqft~neighborhood, data=mh.homes, FUN=metrics,
          fun.names=c("Count","Mean","Median","Mode","SD","Min","Max","Range"))
# Year built  across neighborhoods
summaryBy(year.built~neighborhood, data=mh.homes, FUN=metrics,
          fun.names=c("Count","Mean","Median","Mode","SD","Min","Max","Range"))

# Let's now explore the data by visualizing it:

# Number of sales of homes across neighborhoods
ggplot(mh.homes, aes(x=neighborhood, fill=neighborhood, ymax=max(..count..))) + 
    geom_histogram(binwidth = diff(range(mh.homes$sale.price.n)/28)) +
    stat_bin(binwidth=1,geom="text",drop=TRUE,aes(label=..count.., vjust=-1))

# Sale price across neighborhoods
# Histogram
ggplot(mh.homes, aes(x=sale.price.n, fill=neighborhood)) + 
    geom_histogram(binwidth = diff(range(mh.homes$sale.price.n)/60))
# Box and whisker plot
ggplot(mh.homes, aes(x=neighborhood, y=sale.price.n, fill=neighborhood)) + 
    geom_boxplot()
# Density plot
ggplot(mh.homes, aes(x=sale.price.n, color=neighborhood)) + geom_density()
# Jitter Plot
ggplot(mh.homes, aes(x=neighborhood, y=sale.price.n, color=neighborhood)) + 
    geom_jitter()
# Scatter Plot
ggplot(mh.homes, aes(x=neighborhood, sale.price.n, color=neighborhood)) + 
    geom_point()
# Kernel density estimate
ggplot(mh.homes, aes(x=sale.price.n, fill=neighborhood)) + stat_density()

# Gross square feet across neighborhoods
# Histogram
ggplot(mh.homes, aes(x=gross.sqft, fill=neighborhood)) + 
    geom_histogram(binwidth = diff(range(mh.homes$gross.sqft)/60))
# Box and whisker plot
ggplot(mh.homes, aes(x=neighborhood, y=gross.sqft, fill=neighborhood)) + 
    geom_boxplot()
# Density plot
ggplot(mh.homes, aes(x=gross.sqft, color=neighborhood)) + geom_density()
# Jitter Plot
ggplot(mh.homes, aes(x=neighborhood, y=gross.sqft, color=neighborhood)) + 
    geom_jitter()
# Scatter Plot
ggplot(mh.homes, aes(x=neighborhood, gross.sqft, color=neighborhood)) + 
    geom_point()
# Kernel density estimate
ggplot(mh.homes, aes(x=gross.sqft, fill=neighborhood)) + stat_density()

# Land square feet across neighborhoods
# Histogram
ggplot(mh.homes, aes(x=land.sqft, fill=neighborhood)) + 
    geom_histogram(binwidth = diff(range(mh.homes$land.sqft)/30))
# Box and whisker plot
ggplot(mh.homes, aes(x=neighborhood, y=land.sqft, fill=neighborhood)) + 
    geom_boxplot()
# Density plot
ggplot(mh.homes, aes(x=land.sqft, color=neighborhood)) + geom_density()
# Jitter Plot
ggplot(mh.homes, aes(x=neighborhood, y=land.sqft, color=neighborhood)) + 
    geom_jitter()
# Scatter Plot
ggplot(mh.homes, aes(x=neighborhood, land.sqft, color=neighborhood)) + 
    geom_point()
# Kernel density estimate
ggplot(mh.homes, aes(x=land.sqft, fill=neighborhood)) + stat_density()

# Year built across neighborhoods
# Histogram
ggplot(mh.homes, aes(x=year.built, fill=neighborhood)) + 
    geom_histogram(binwidth = diff(range(mh.homes$year.built)/60))
# Box and whisker plot
ggplot(mh.homes, aes(x=neighborhood, y=year.built, fill=neighborhood)) + 
    geom_boxplot()
# Density plot
ggplot(mh.homes, aes(x=year.built, color=neighborhood)) + geom_density()
# Jitter Plot
ggplot(mh.homes, aes(x=neighborhood, y=year.built, color=neighborhood)) + 
    geom_jitter()
# Scatter Plot
ggplot(mh.homes, aes(x=neighborhood, year.built, color=neighborhood)) + 
    geom_point()
# Kernel density estimate
ggplot(mh.homes, aes(x=year.built, fill=neighborhood)) + stat_density()

################################################################################
# and (ii) across time.
################################################################################

# We only have data for August, 2012 - August 2013
# Seeing as we don't have full years or comparable months of those years
# it would be wiser to conduct EDA across months and weekdays only.

# Categorize sale dates by month and weekday
mh.homes$sale.month <- format(mh.homes$sale.date, "%B")
mh.homes$sale.day <- format(mh.homes$sale.date, "%A")

# Factorize sale dates by month and weekday
mh.homes$sale.month <- 
    factor(mh.homes$sale.month, 
           levels= c("January", "February", "March", "April", "May", "June",
                     "July", "August", "September", "October", "November",
                     "December"))
mh.homes$sale.day <- 
    factor(mh.homes$sale.day, 
           levels= c("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"))

# Let's explore the data quantitatively first:

# Sale price across months
summaryBy(sale.price.n~sale.month, data=mh.homes, FUN=metrics,
          fun.names=c("Count","Mean","Median","Mode","SD","Min","Max","Range"))
# Gross square feet across months
summaryBy(gross.sqft~sale.month, data=mh.homes, FUN=metrics,
          fun.names=c("Count","Mean","Median","Mode","SD","Min","Max","Range"))
# Land square feet across months
summaryBy(land.sqft~sale.month, data=mh.homes, FUN=metrics,
          fun.names=c("Count","Mean","Median","Mode","SD","Min","Max","Range"))
# Year built across months
summaryBy(year.built~sale.month, data=mh.homes, FUN=metrics,
          fun.names=c("Count","Mean","Median","Mode","SD","Min","Max","Range"))

# Sale price across days of the week
summaryBy(sale.price.n~sale.day, data=mh.homes, FUN=metrics,
          fun.names=c("Count","Mean","Median","Mode","SD","Min","Max","Range"))
# Gross square feet across days of the week
summaryBy(gross.sqft~sale.day, data=mh.homes, FUN=metrics,
          fun.names=c("Count","Mean","Median","Mode","SD","Min","Max","Range"))
# Land square feet across days of the week
summaryBy(land.sqft~sale.day, data=mh.homes, FUN=metrics,
          fun.names=c("Count","Mean","Median","Mode","SD","Min","Max","Range"))
# Year built across days of the week
summaryBy(year.built~sale.day, data=mh.homes, FUN=metrics,
          fun.names=c("Count","Mean","Median","Mode","SD","Min","Max","Range"))

# Let's now explore the data by visualizing it:

# Number of sales of homes
# By month
ggplot(mh.homes, aes(x=sale.month, fill=sale.month, ymax=max(..count..))) + 
    geom_histogram(binwidth = diff(range(mh.homes$sale.price.n)/12)) +
    stat_bin(binwidth=1,geom="text",drop=TRUE,aes(label=..count.., vjust=-1))
# By day of the week
ggplot(mh.homes, aes(x=sale.day, fill=sale.day, ymax=max(..count..))) + 
    geom_histogram(binwidth = diff(range(mh.homes$sale.price.n)/7)) +
    stat_bin(binwidth=1,geom="text",drop=TRUE,aes(label=..count.., vjust=-1))

# Sale price across months
# Histogram
ggplot(mh.homes, aes(x=sale.price.n, fill=sale.month)) + 
    geom_histogram(binwidth = diff(range(mh.homes$sale.price.n)/60))
# Box and whisker plot
ggplot(mh.homes, aes(x=sale.month, y=sale.price.n, fill=sale.month)) + 
    geom_boxplot()
# Density plot
ggplot(mh.homes, aes(x=sale.price.n, color=sale.month)) + geom_density()
# Scatter Plot
ggplot(mh.homes, aes(x=sale.month, sale.price.n, color=sale.month)) + 
    geom_point()
# Sale price across days of the week
# Histogram
ggplot(mh.homes, aes(x=sale.price.n, fill=sale.day)) + 
    geom_histogram(binwidth = diff(range(mh.homes$sale.price.n)/60))
# Box and whisker plot
ggplot(mh.homes, aes(x=sale.day, y=sale.price.n, fill=sale.day)) + 
    geom_boxplot()
# Density plot
ggplot(mh.homes, aes(x=sale.price.n, color=sale.day)) + geom_density()
# Scatter Plot
ggplot(mh.homes, aes(x=sale.day, sale.price.n, color=sale.day)) + geom_point()

# Gross square feet (for sold homes) across months
# Histogram
ggplot(mh.homes, aes(x=gross.sqft, fill=sale.month)) + 
    geom_histogram(binwidth = diff(range(mh.homes$gross.sqft)/30))
# Box and whisker plot
ggplot(mh.homes, aes(x=sale.month, y=gross.sqft, fill=sale.month)) + 
    geom_boxplot()
# Density plot
ggplot(mh.homes, aes(x=gross.sqft, color=sale.month)) + geom_density()
# Scatter Plot
ggplot(mh.homes, aes(x=sale.month, gross.sqft, color=sale.month)) + geom_point()
# Gross square feet (for sold homes) across days of the week
# Histogram
ggplot(mh.homes, aes(x=gross.sqft, fill=sale.day)) + 
    geom_histogram(binwidth = diff(range(mh.homes$gross.sqft)/30))
# Box and whisker plot
ggplot(mh.homes, aes(x=sale.day, y=gross.sqft, fill=sale.day)) + geom_boxplot()
# Density plot
ggplot(mh.homes, aes(x=gross.sqft, color=sale.day)) + geom_density()
# Scatter Plot
ggplot(mh.homes, aes(x=sale.day, gross.sqft, color=sale.day)) + geom_point()

# Land square feet (for sold homes) across months
# Histogram
ggplot(mh.homes, aes(x=land.sqft, fill=sale.month)) + 
    geom_histogram(binwidth = diff(range(mh.homes$land.sqft)/30))
# Box and whisker plot
ggplot(mh.homes, aes(x=sale.month, y=land.sqft, fill=sale.month)) + 
    geom_boxplot()
# Density plot
ggplot(mh.homes, aes(x=land.sqft, color=sale.month)) + geom_density()
# Scatter Plot
ggplot(mh.homes, aes(x=sale.month, land.sqft, color=sale.month)) + geom_point()
# Land square feet (for sold homes) across days of the week
# Histogram
ggplot(mh.homes, aes(x=land.sqft, fill=sale.day)) + 
    geom_histogram(binwidth = diff(range(mh.homes$land.sqft)/30))
# Box and whisker plot
ggplot(mh.homes, aes(x=sale.day, y=land.sqft, fill=sale.day)) + geom_boxplot()
# Density plot
ggplot(mh.homes, aes(x=land.sqft, color=sale.day)) + geom_density()
# Scatter Plot
ggplot(mh.homes, aes(x=sale.day, land.sqft, color=sale.day)) + geom_point()

# Year built (for sold homes) across months
# Histogram
ggplot(mh.homes, aes(x=year.built, fill=sale.month)) + 
    geom_histogram(binwidth = diff(range(mh.homes$year.built)/30))
# Box and whisker plot
ggplot(mh.homes, aes(x=sale.month, y=year.built, fill=sale.month)) + 
    geom_boxplot()
# Density plot
ggplot(mh.homes, aes(x=year.built, color=sale.month)) + geom_density()
# Scatter Plot
ggplot(mh.homes, aes(x=sale.month, year.built, color=sale.month)) + geom_point()
# Year built (for sold homes) across days of the week
# Histogram
ggplot(mh.homes, aes(x=year.built, fill=sale.day)) + 
    geom_histogram(binwidth = diff(range(mh.homes$year.built)/30))
# Box and whisker plot
ggplot(mh.homes, aes(x=sale.day, y=year.built, fill=sale.day)) + geom_boxplot()
# Density plot
ggplot(mh.homes, aes(x=year.built, color=sale.day)) + geom_density()
# Scatter Plot
ggplot(mh.homes, aes(x=sale.day, year.built, color=sale.day)) + geom_point()

################################################################################
# If you have time, start looking for meaningful patters in this dataset.
################################################################################

# - Most sales happen in Harlem and the Upper East Side.
# - Housing is most expensive in the Upper East Side.
# - Housing is least expensive in Harlem.
# - Houses have the most gross square feet in Gremich and the Upper East Side.
# - Houses seem about equal in terms of gross square feet elsewhere.
# - Houses seem about equal in terms of land square feet throughout.
# - The Upper West side has the newest housing.
# - Housing was built mostly around the early 1900s.
# - Harlem has the newest housing.
# - The amount of sales seem equally distributed around Wednesday.
# - December has a lot more sales than any other month.
# - The least amount of sales happen in September and October.
# - Home sales according to other metrics across time seem equally distributed.