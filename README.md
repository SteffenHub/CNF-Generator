# CNF-Generator
This code generates CNF files based on input data that is entered in a dialog.  
An example of a dialog can be found here [Example Dialog](#example-dialog). 
After answering all questions, the program will start to pick random variables, 
create rules with them and verify them with satisfiability and variance.
The rules are set up so that all variables are negated in a clause.
This is fixed by us, if you do not want this,
it must currently be changed in the code.  

Family rules can be used if you answer with true in the dialog.  
A family means variables that are in an XOR relationship.  
For example a Family of 1, 2 and 3.  
means:  
> (1 XOR 2 XOR 3)  

In the conjunctive normal form, this is written as:
> ((1 ∨ 2 ∨ 3) ∧ (!1 ∨ !2) ∧ (!1 ∨ !3) ∧ (!2 ∨ !3))

# Example CNF:
The following CNF-file can be found in output CNF Folder.  
The input from [Example Dialog](#example-dialog) was used to generate this CNF-file.
>c  
>c Input Variance: 1000000000  
>c Actual Variance: 957916787  
>c Input number of vars: 100  
>c Input use Fam rules: true  
>c Input Fam size: 1-6  
>c Input Rule size: 2-6  
>c Input False Variables: 4  
>c Actual False Variables: 4 Vars  
>c Input True Variables: 7  
>c Actual True Variables: 7 Vars  
>c Calculation time: 1279 seconds  
>c Used Seed: 8267530071919704081  
>c  
>p cnf 100 621  
>1 2 3 4 5 0  
>-1 -2 0  
>-1 -3 0  
>-1 -4 0  
> ...  
>-69 -4 0  
>-60 -78 -9 -32 0  
>-6 -25 -1 -30 0  
>-11 -54 -37 -94 -66 0  
>-81 -96 -66 -57 0  
>-27 -45 -75 -63 0  
>-86 -47 0  
> ...

# Run
To run this Code you need to download the Sat4J SAT-solver and integrate it into your Project.
Also, you need to choose between the counting solver c2d and sharpSAT.
In our runs we used the c2d solver on Windows and the sharpSAT solver on Linux.
A version of c2d for linux is available, but has not been tested with the CNF-Generator.
After setting up the solver you have to run the Main file in the src folder.

#### [Download Sat4J](#download-sat4j-1)
*  In IntelliJ go to File -> Project Structure -> Libraries -> + -> Java and select the org.sat4j-core.jar
#### [Download c2d](#download-c2d)
*  Just put the c2d.exe into the main folder(CNF-Generator)
*  If the name is not 'c2d.exe' e.g. 'c2d_windows.exe'/ 'c2d_linux.exe' you have to rename the file
### [Download sharpSAT](#download-sharpSAT)
* Put the sharpSAT executable file into the main folder(CNF-Generator)

# Download Sat4J

Releases are available at [OW2 Releases](https://gitlab.ow2.org/sat4j/sat4j/-/releases)

Select [Precompiled binaries](https://release.ow2.org/sat4j/) to download a .jar directly. I used the core-version for this Project.

# Download c2d

Release are available at [reasoning.cs.ucla.edu](http://reasoning.cs.ucla.edu/c2d/)

Under the Download section you will find the download link

# Download sharpSAT

Releases are available at [github.com/marcthurley/sharpSAT](https://github.com/marcthurley/sharpSAT)  
Follow the installation guide in the GitHub readme file.  
To use sharpSAT you need to install the gmp bignum library using 'sudo apt-get install libgmp-dev'

# Example Dialog
With starting the program you will get a dialog like this:

> Do you want to use a specific seed for the random generator?  
Example: '698234689'. If you don't want to use a seed type 'None'  
If 'None' is chosen a random seed on the basis of current time and date will be used

> None

> Which counting solver do you want to use. Choose between 'c2d' or 'sharpSAT'.

> c2d

> How many Variables should exist?  

> 100

> Do you want to use family rules? type: 'true' or 'false'  

> true

> How large may families be min-max. Example: 1-4

>  1-6

> Only the (family) rules result in a variance of: 1125899906842624  
> respectively  
> 1.125.899.906.842.624
> What variance should be maintained, with the other rules added independently of the families?  
> Write WITHOUT points. The result can deviate up to 5% from the variance entered.  

> 1000000000

> Maximum deviation in the result: 50000000  
> what lengths should the other family independent rules have. e.g. (2-6)  
> You should start with min Rule size = 2. If rule size is 1 the variable in this rule will be always false  

> 2-6

> What percentage of the variables should be False. Slice as decimal number. e.g. 0.11 or 0.04  
> Now there are 0 False Vars in rule set

> 0.04

> This results in 4 variables that should always be False  
> What percentage of the variables should be True. Slice as decimal number. e.g. 0.09  
> Now there are 6 True Vars in rule set  

> 0.09

> This results in 9 variables that should always be True  
