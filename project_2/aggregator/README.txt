--------------------------------------------------------------------------------
This folder contains the following files:
--------------------------------------------------------------------------------
aggregator.py - aggregator for twitter data for the the situation in Ukraine
refactor.py   - combines and refactors the data files
split.py      - splits the combined file from refactor.py into desired amount of
                chunks

--------------------------------------------------------------------------------
To run the aggregator you need to have the following installed:
--------------------------------------------------------------------------------
Python 2.7.x
Tweepy
nltk.corpus (including stopwords)

--------------------------------------------------------------------------------
To install the above you need to do the following:
--------------------------------------------------------------------------------
1. Download and install Python 2.7.x from: https://www.python.org/downloads
2. Download and install Setuptools from: http://pypi.python.org/pypi/setuptools
3. Install Pip by running: sudo easy_install pip
4. Install NLTK by running: sudo pip install -U pyyaml nltk
5. Install Tweepy by running: sudo pip install tweepy
6. Run: python -c 'import nltk; nltk.download()'
7. Select the 'Corpora' Tab browse down to stopwords and click on 'Download'

--------------------------------------------------------------------------------
To run the aggregator edit aggregator.py as follows:
--------------------------------------------------------------------------------
- Add your Twitter API key on line 13
- Add your Twitter API secret on line 14
- Add your Twitter access token on line 17
- Add your Twitter access token secret on line 18
- (Optional) If you need to change the topic edit line 28

--------------------------------------------------------------------------------
Now that you are all set up you can run the aggregator:
--------------------------------------------------------------------------------
python aggregator.py

--------------------------------------------------------------------------------
(Optional) When you are done wrangling refactor the data by running :
--------------------------------------------------------------------------------
python refactor.py

--------------------------------------------------------------------------------
(Optional) If you want to split the combined file from refactor run :
--------------------------------------------------------------------------------
python split.py number_of_splits

--------------------------------------------------------------------------------
You will now have the following output files:
--------------------------------------------------------------------------------
data/ - contains data files in chunks of 1000 tweets each
data.txt - all the tweets in data/ combined into one big data file
coordinates.csv - geocodes used for tweet origin visualizations

--------------------------------------------------------------------------------
Aggregator (aggregator.py) details:
--------------------------------------------------------------------------------
The aggregator is set up to remove the following elements from a raw tweet:
- Symbols (except for '#' in hashtags)
- Links
- Retweet characters ('rt @')
- Numbers
- Stopwords (from NTLK Corpus containing over 2400 stopwords)

The aggregator collects the following information:
- Username
- Followers count
- Mentions ('@' or 'rt @')
- Tweet
- Hashtags
- Tweet coordinates (in a separate file due to low frequency)

The aggregator collects data in the folder './data'. The data is contained in 
files using the date and time of their creation as a name.  Each of those files
contains a 1000 tweets.

--------------------------------------------------------------------------------
Refactor (refactor.py) details:
--------------------------------------------------------------------------------
Refactor.py goes through all the files in './data' and removes those which are
deemed incomplete (having less than a 1000 tweets).  It then combines all the
tweets from each file into a single large data file './data.txt'.

--------------------------------------------------------------------------------
Split (split.py) details:
--------------------------------------------------------------------------------
Once refactor.py is done running you will have a './data.txt' combined file. You
can split this file by running:

python split.py number_of_splits

--------------------------------------------------------------------------------
Data format is as follows per line:
--------------------------------------------------------------------------------
Username | Followers_count | Mentions | Hashtags | Tweet

Example:
dmytrokovalov | 13 | usosce | #crimea #ukraine #unitedforukraine | mistake |

Example when a filed is missing (Mentions / Hashtags / Tweet):
talkvietnambuzz | 538 |  |  | rejects russian annexation crimea |

--------------------------------------------------------------------------------
Coordinates format:
--------------------------------------------------------------------------------
The coordinates are collected in 'coordinates.csv' in two columns the first with
longitudes and the second with latitudes.  This data is intended to be used
separately due to being rarely provided along with the tweet.  It is collected 
as a creative venture and will not be used during the MR phase of this project.
