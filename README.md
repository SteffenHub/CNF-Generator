# CNF-Generator
This code generates CNF files based on input data that is entered in a dialog.  
An example of a dialog can be found here [Example Dialog](#example-dialog). 

# Example CNF:
> c  
> c Input Variance: 1000000000000  
> c Actual Variance: 1028411394552  
> c Input number of vars : 100  
> c Input use Fam rules: true  
> c Input Fam size: 1-4  
> c Input Rule size: 2-5  
> c Input False Variables: 3  
> c Actual False Variables: 3 Vars  
> c Input True Variables: 13  
> c Actual True Variables: 12 Vars  
> c  
> p cnf 100 231  
> 26 0  
> -60 -43 -48 -71 0  
> -93 -71 -74 -93 0  
> -87 -89 -84 -29 -62 0  
> -100 -33 -81 -6 -94 0  
> -10 -45 -29 -6 -94 0  
> -72 -92 -73 -49 -51 0  
> -1 -74 -87 0  
> -17 -74 -48 -72 0  
> -79 -3 -80 -79 0  
> ...

# Run
To run this Code you need to download the Sat4J/c2d Solver and integrate it into your Project.

#### [Download Sat4J](#download-sat4j-1)
*  In IntelliJ go to File -> Project Structure -> Libraries -> + -> Java and select the org.sat4j-core.jar
#### [Download c2d](#download-c2d)
*  Just put the c2d.exe into the main folder(CNF-Generator)
*  If the name is not 'c2d.exe' e.g. 'c2d_windows.exe'/ 'c2d_linux.exe' you have to rename the file

# Download Sat4J

Releases are available at [OW2 Releases](https://gitlab.ow2.org/sat4j/sat4j/-/releases)

Select [Precompiled binaries](https://release.ow2.org/sat4j/) to download a .jar directly. I used the core-version for this Project.

# Download c2d

Release are available at [reasoning.cs.ucla.edu](http://reasoning.cs.ucla.edu/c2d/)

Under the Download section you will find the download link

# Example Dialog
With starting the program you will get a dialog like this:

> How many Variables should exist?  

> 50

> Do you want to use family rules? type: 'true' or 'false'  

> false

> Only the (family) rules result in a variance of: 1125899906842624  
> respectively  
> 1.125.899.906.842.624
> What variance should be maintained, with the other rules added independently of the families?  
> Write WITHOUT points. The result can deviate up to 5% from the variance entered.  

> 1000000000

> Maximum deviation in the result: 50000000  
> what lengths should the other family independent rules have. e.g. (2-6)  
> You should start with min Rule size = 2. If rule size is 1 the variable in this rule will be always false  

> 2-5

> What percentage of the variables should be False. Slice as decimal number. e.g. 0.11 or 0.04  
> Now there are 0 False Vars in rule set

> 0.04

> This results in 2 variables that should always be False  
> What percentage of the variables should be True. Slice as decimal number. e.g. 0.09  
> Now there are 0 True Vars in rule set  

> 0.08

> This results in 4 variables that should always be True  
 
After answering all questions, the program will start to pick random variables, create rules with them and verify them with satisfiability and variance.
