# Verification and Validation

##Intoduction:


##Software Testability and Reviews:


##Test Statistics and analytics:

Despite saying that new features must have Unit tests that must be passing, we did not find any of those in the repository.
In consequence of that fact, we will be analysing the codacy report of our project.

Our project certification follows a classification of B, which is not bad for an open source project.

![codacy report] (https://github.com/Fr0sk/ESOF-DuckDuckGo-Android-App/blob/master/ESOF-docs/resources/codacy_report.png)

Despite that fact, there is a very high percentage of unused code (54%), this derives from the fact that there are many unused contructors, private variables which could be replaced by local variables, as we can see in the following images.

![unused code1] (https://github.com/Fr0sk/ESOF-DuckDuckGo-Android-App/blob/master/ESOF-docs/resources/unused_code1.png)

![unused code2] (https://github.com/Fr0sk/ESOF-DuckDuckGo-Android-App/blob/master/ESOF-docs/resources/unused_code2.png)

![unused code3] (https://github.com/Fr0sk/ESOF-DuckDuckGo-Android-App/blob/master/ESOF-docs/resources/unused_code3.png)

Code style follows a high percentage (89%), which is good for an open source project, since there are many people collaborating from different backgrounds, maintaining a code pattern might be difficult and might demand some specification from developers so that this goal can be achieved.

Some of these issues have to do with long methods, package names containing upper case characters. method names starting with capital letters. As seen below:

![code style1] (https://github.com/Fr0sk/ESOF-DuckDuckGo-Android-App/blob/master/ESOF-docs/resources/code_style1.png)

![code style2] (https://github.com/Fr0sk/ESOF-DuckDuckGo-Android-App/blob/master/ESOF-docs/resources/code_style2.png)

![code style3] (https://github.com/Fr0sk/ESOF-DuckDuckGo-Android-App/blob/master/ESOF-docs/resources/code_style3.png)

![code style4] (https://github.com/Fr0sk/ESOF-DuckDuckGo-Android-App/blob/master/ESOF-docs/resources/code_style4.png)

Although, error prone shows a percentage of 0%, it makes a hole 50% of the Issues Breakdown, consisting in 259 error prone issues, as we can see in the graphic of our codacy report.

Most of this error prone issues are about avoiding unused imports, using explicit scoping instead of the default package private level. As we can see below:

![error prone1] (https://github.com/Fr0sk/ESOF-DuckDuckGo-Android-App/blob/master/ESOF-docs/resources/error_prone1.png)

![error prone2] (https://github.com/Fr0sk/ESOF-DuckDuckGo-Android-App/blob/master/ESOF-docs/resources/error_prone2.png)

Finally, we can analyse some grafics about issue severity, and project quality.

Analyzing the issue severity graphic, we can see the percentage of infos (27.2%), related with code styles and how these types of issues can influence the afect the code readability. As we have seen these fact is not very worrisome. We can also see the percentage of warnings (51,9%) and errors (20,8%). These types of errors need more attention since these issues can affect the maintainability of the project.

![issue severity graphic] (https://github.com/Fr0sk/ESOF-DuckDuckGo-Android-App/blob/master/ESOF-docs/resources/code_severity.png)

Analyzing the project graphic, see the number of files with the respective grade. As we can see there are a very low percentage of E and D files, and a very high percentage of A graded files (83,3%) which is a very good sign, showing organization and discipline.

![project quality graphic] (https://github.com/Fr0sk/ESOF-DuckDuckGo-Android-App/blob/master/ESOF-docs/resources/project_quality.png)
##Bug Report:


##Contributions:
Filipe Coelho ( @Fr0sk ) has contributed in:

Luís Cruz ( @Luis-bcruz ) has contributed in:
Test Statistics and analytics.

Shivam Agrawal ( @shivam-agr ) has contributed in:

Vinicius Ferretti ( @ViniciusFerretti ) has contributed in:
