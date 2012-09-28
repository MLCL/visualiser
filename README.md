# Visualiser

Visualiser is a simple Java tool for visualising undirected graphs. It takes as input a list of pairwise similarities between entries and displays a graph where entries are positioned in space according to their similarity.

## Installation

The software is built using Apache Maven. To obtain a copy of the source code and build a Java executable, type the following into your terminal:


<pre><code>
git clone https://github.com/MLCL/visualiser.git
cd Visualiser
mvn install
</code></pre>

The executable jar file will appear in the "target" directory.

## Running the software
At the moment, it is not possible to configure the software without modifying its source code. The main places of interest to a new user are:

* File Direc.java, line 79. Modify the getDirectory() method to return the directory where the input file is
* File Main.java, line 48. Change the two parameters of the run() method to be 1) the name of your input file, and 2) the name of the main entry. The main entry will be positioned in the centre of the screen.

The format of the input file is:

<pre><code>
entry-1  entry-2 similarity
entry-2  entry-3 similarity
...
</code></pre>

where entry-n are strings identifying the entries, and similarity a double between 0 and 1. A sample input file is located in "sampledata".

