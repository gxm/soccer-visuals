soccer-visuals
==============

Simple website which displays visualizations of NCAA women's soccer seasons.  It could be modified to support other sports.

Currently, this project only works with the NCAA women's soccer data from [All White Kit](http://woso-stats.tk/college/2014/)

It is hosted at [soccer.moulliet.com](http://soccer.moulliet.com/)

The system downloads all of the data files at midnight Pacific time and then restarts.
This is run via cron:

``` 
@daily /home/moulliet/soccer/scripts/soccer.sh download
```

Deployment is done via the deploy.sh script.

The system has a web component (html, javascript and css) and a Java server component which provides a REST interface to the team data.
 
To use the system on your local machine, run the Java class com.moulliet.soccer.SoccerServiceMain
and access it via http://localhost:8081/file/index.html






