# CNF-Generator
This Code generates CNF-files using [Input data](#input-data).

Example for a CNF:
> c  
> c Input Variance: 1000000000000  
> c Actual Variance: 1028411394552  
> c Input number of vars : 100  
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

# Run
To run this Code you need to download the Sat4J/c2d Solver and integrate it into your Project.

#### [Download Sat4J](#download-sat4j-1)
*  In IntelliJ go to File -> Project Structure -> Libaries -> + -> Java and select the org.sat4j-core.jar
#### [Download c2d](#download-c2d)
*  Just put the c2d.exe into the main folder(CNF-Generator)
*  If the name is not 'c2d.exe' e.g. 'c2d_windows.exe'/ 'c2d_linux.exe' you have to rename the file

# Download Sat4J

Releases are available at [OW2 Releases](https://gitlab.ow2.org/sat4j/sat4j/-/releases)

Select [Precompiled binaries](https://release.ow2.org/sat4j/) to download a .jar directly. I used the core-version for this Project.

# Download c2d

Release are available at [reasoning.cs.ucla.edu](http://reasoning.cs.ucla.edu/c2d/)

Under the Download section you will find the download link

# Input data
TODO