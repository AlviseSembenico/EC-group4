javac -cp contest.jar %1.java %2.java     
jar uf .\contest.jar %2.class  
jar uf .\contest.jar configuration.txt          
jar cmf MainClass.txt submission.jar %1.class %2.class                           
java -jar testrun.jar -submission=%1 -evaluation=BentCigarFunction -seed=1