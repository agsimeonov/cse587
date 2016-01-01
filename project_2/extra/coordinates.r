# Makes sure package is not already installed and loaded before it loads it
prepare.package <- function(package) {
    if(package %in% rownames(installed.packages()) == FALSE) {
        install.packages(package)
    }
    require(package, character.only = TRUE)
}

prepare.package("rworldmap")

coordinates <- read.csv("./coordinates.csv", col.names=c("lon","lat"))
map <- getMap("low")
plot(map)
points(coordinates$lon, coordinates$lat, col="red", cex=3, pch=46)
