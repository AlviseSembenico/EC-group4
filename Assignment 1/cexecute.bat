javac -cp contest.jar;jfreechart-1.0.19.jar;jcommon-1.0.23.jar %1.java %2.java %3.java
jar uf .\contest.jar %2.class %3.class     
jar uf .\contest.jar configuration.txt          
jar cmf MainClass.txt submission.jar %1.class %2.class %3.class      
java -jar testrun.jar -submission=%1 -evaluation=KatsuuraEvaluation -seed=1

