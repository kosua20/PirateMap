# PirateMap
This is my entry for the PCG Monthly Challenge #1 (Dec, 2015) - Procedural Pirate Map, on [r/proceduralgeneration](https://www.reddit.com/r/proceduralgeneration/comments/3vcbb3/monthly_challenge_1_dec_2015_procedural_pirate_map/).  
This was a really fun and interesting experiment, where I finally had the opportunity to put into practice some ideas and notions (more below). Sadly the second half of the month was a bit busy for me, so I couldn't spend as much time on this as I would have wished. I'm awaiting the next challenge impatiently!  

The imgur album where I tracked my progress: [Album](http://imgur.com/a/7Qjk1)

##Results

![First sample](http://i.imgur.com/oYggaOT.png)  

*Go northwest for 36 steps, across the sea.  
Go west for 51 steps, along the coast.  
Go northwest for 58 steps, in the valley.  
Go northwest for 40 steps, across the mountains.  
Go northeast for 39 steps, through the forest.  
Go northeast for 35 steps, across the sea.  
Go northeast for 57 steps, through the forest.  
Don't disturb the monkeys in the trees.   
Go east for 35 steps, through the forest.  
Start digging where the cross is.*  


![Second sample](http://i.imgur.com/YJltq3t.png)   

*Go southeast for 42 steps, through the forest.  
Go east for 68 steps, across the sea.  
This is where my ship sank.  
Go east for 33 steps, across the sea.  
Here be sharks.  
Go east for 55 steps, across the sea.  
You'll be above an old shipwreck.  
Go north for 47 steps, across the sea.  
Beware of the mermaids in this area!  
Go northwest for 26 steps, across the sea.  
Go north for 62 steps, through the forest.  
Start digging where the cross is.*

##Details
###The islands
I'm generating an height map using some Perlin noise for the general shape of the island and fractal noise for the details. This map is thresholded and filled with plain colors. Then I detect the countours with a simple neighbours-checking algorithm, and draw those isolines. By repeating the drawing pass multiple times with some noise added, I can generate a thicker, pencil-drawn line for the coast.

###Decorations 
Moutains are added above a certain height, with some padding to avoid overlays.
For the waves, many points are drawn from a poisson-disc distribution, and filtered based on their proximity to the coast.
The background is composed of a picture selected randomly among a set of old papers scans, and of a general tint generated at random.

###The path 
For the path, the start and finish points are first selected randomly. The algorithm tries to find points far enough from each other and both on land. Then, a few other points are selected. By joining all the points, we get a first version of the path. Between each pair of points, a few other points are added and randomly disturbed : this brings more variation and the hand-drawn look. Then, a dotted red line with an irregular pattern is drawn.

###The directions
While building the path, the description is also created. For each line segment we sample the type of terrain crossed (sea, coast, forest, plain, valley or mountain) based on the height. The length of the segment is converted in a number of steps. When two consecutive segments are on the same terrain type, a line with a comment taken at random from a predefined list is added.
