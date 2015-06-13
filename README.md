# Building

Checkout from github:

    git clone https://github.com/thakkargourav/PhoneToWordConverter.git

Build using maven:

    mvn clean package

# Running

You can run the jar using following command:

    java -jar 1800-coding-challenge-1.0-jar-with-dependencies.jar [-d pathToDictionary] [pathToDatafiles..]

    -d(optional) is a parameter flag for dictionary. It should be used if user wants to override default dictionary

    [pathToDatafiles..](optional) is parameter for data files. A user can specify a list of paths for data files separated by " "(space). If no file is passed as parameter, then the program will ask for number using STDIN.

Running without a data file, will result in reading data from stdin:

    java -jar 1800-coding-challenge-1.0-jar-with-dependencies.jar -d dict.txt

Or alternatively, you can run it with a data file:

    java -jar 1800-coding-challenge-1.0-jar-with-dependencies.jar example1.txt example2.txt
