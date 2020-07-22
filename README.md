# Project
This is MapLab, an incomplete side project of mine.



## Compilation:
#### Terminal
```
$ rm src/*.class
$ javac -d bin src/*.java
```
#### Run the bash
```
$ bash compile.sh
```

## Running:
#### Terminal
```
$ cd bin
$ java Run
```
#### Run the bash
```
$ bash run.sh
```

# Description
I planned for it to be a software where Game Developers could design maps for strategy games.
The extend of what it can do is to procedurally generate maps using a function that the user can write.
You can generate new maps by pressing the 'New Map' button (at the top, center to right).
![new-map](https://user-images.githubusercontent.com/48674623/88171180-2d0b7700-cc1f-11ea-9e6b-98a83f796f21.gif)

Perhaps the coolest part is the 3d rendering that I built myself. Under the '3D' tab, you can see the 3D version of the map displayed.
![3d-gif](https://user-images.githubusercontent.com/48674623/88170329-cb96d880-cc1d-11ea-8432-63c29a7ede1d.gif)


# Instructions
To edit the settings, you can go to settings/settings.txt. The program has an interpreter that reads this file and interprets what the user wrote. I also uses the same syntax that Java does for commenting ('//')



## MapType
MapType describes the type of map the software must generate.
Here are the amount of map types currently existing:
1. SqrMap - Grid map made up of sqrs
2. HexMap - Hexagonal map, like the Civilizations franchise
3. GlobeMap - 3Dimensional map of a planet
4. SiteMap - Most abstract type, a list of coordinates in certain dimension plane.

Examples:
```
MapType: SqrMap
MapType: HexMap
```
Note that only SqrMap is available right now


## Dimensions
Since only SqrMap is available right now, we are limited to a 2d dimension restriction. Below shows an example where width=20 and height=10

```
Dimensions: 20x10
```
This sets global variables `w=20` and `h=10` respectively for the rest of the following settings.

## Generating the map
In SqrMap, generation works by choosing a starting coordinate (via `ProcStart`) and an ending coordinate (via `ProcEnd`). We generate the first cell at the first coordinate and then use an algorithm (via `ProcDirection`) to choose which next coordinate's cell we generate. We determine what the value of that cell will be via `ProcAlgorithm`. Note that `ProcDirection` will give us the next coordinate using the previous coorinate and is used repeatedly until the next coordinate is the ending coordinate. Also note that even though `ProcDirection` determines the coordinate of the next cell, we use `ProcAlgorithm` to determine the value of that cell.


## ProcStart
Describes the first position/coordinate that the map will generate at. 

This examples below show a case where it starts at `x=0`, `y=0`
```
ProcStart: [0;0]
```
While this examples starts at a random coordinate within the dimensions set by `w` and `h`.
```
ProcStart: [random*w;random*h]
```

## ProcEnd
Describes when the map should stop producing.

This example is the classic case where we end at the very last coordinate of the map, opposite of `[0;0]`.
```
ProcEnd: [w-1;h-1]
```
This example will make the program check whether all cells have been generated. This is useful if the `ProcDirection` uses randomness to determine the coordinate of the next cell.
```
ProcEnd: untildone
```

## ProcDirection
This describes the next coordinate the map generates

This example mimics a standard nested forloop. It checks whether x has reached the end, `w`(width)`-1`. If it has not, it returns the new coordinate where x has incremented, but y remained the same. If x has reached the end, the x resets to 0 and y increases.
```
ProcDirection: if(x<w-1?[x+1;y]:[0;y+1])
```
This example is where the next cell's coordinate is completely random
```
ProcDirection: [random*w;random*h]
```


## FirstState
This defines the state of the first cell. A state is a description for a cell. Usually it would mean the cell's height (as in terrain height), but can also sometimes be used to define terrain type (grassy, forest, stone etc).

This examples sets the first state to always be 0, indicating a terrain height of zero (sea level).
```
FirstState: 0
```
This example sets the first state to be an integer being 0 or 1
```
FirstState: round(random*2)
```

## ProcAlgorithm
This describes the state for the next cell. This is one of the more interesting settings since you can really describe the landform of the map you are generating.
You can also mention variables in this setting:

`random`: generates a random floating point number between 0(inclusive) and 1(enclusive).
`adj`: fetches the average value of any already defined surrounding cells. Rounds average to closest Integer.
`last`: the value of the last generated cell.
`hasAdj`: true if the cell has any defined cell adjacent to it



This example will give a cell a state of 0 or 1 randomly
```
ProcAlgorithm: if(random<0.5?1:0)
```

This example is a very classic case. It simulates a dice roll: if the previous cell was 0, you need to roll a 6 for the next cell to become 1, else it will stay 0. If the previous cell was 1 however, then we need only roll 5 or a 6 for the next cell to become 0 again. Thus we have large chunks of 0s together and slightly smaller chunks of 1 together.
```
ProcAlgorithm: if(adj==0?(if(random>0.83?1:0)):(if(random>0.66?0:1)))
```

This example is an adaption of the previous example, but extending the height to 2.
```
ProcAlgorithm: if(adj==0?(if(random>0.83?1:0)):(if(adj==1?(if(random<0.5?1:(if(random<0.5?2:0)))):(if(random<0.5?2:1)))))
```

This example shows that you can have a fairly complicated(mathematically) method to create an algorithm . I'll leave you to figure out exactly how it works
```
ProcAlgorithm: if(random<0.52^(0.5*adj+1)?adj:if(random<(0.52^adj)/(adj+2)?adj+1:adj-1))
```

This example is another complicated algorithm.
```
ProcAlgorithm: if(hadAdj==1?(if(random<0.52^(0.5*adj+1)?adj:if(random<(0.52^adj)/(adj+2)?adj+1:adj-1))):1)
```

This example will create a checkerboard
```
ProcAlgorithm: if(last==0?1:0)
```

## Symmetry

There are two types of symmetry: Rotation and Reflection.

rotate() and reflect() describes their respective commands, in the braces:
- Before comma: [0;90] describes the less than 180 degree angle pie of the map (in this case, all between the 0 degree line and the 90 degree line drawn from the middle, thus the top right corner). 
- After comma: 90 describes the angle of the axis at which we would like to apply the symmetry. In 90 case, it is over the `x=0` axis.


This is classic 4-way symmetry
```
Symmetry: rotate([0;90],90);rotate([0;180],180);
```

I call this 'Butterfly' symmetry
```
Symmetry: reflect([270;90],90);
```

I call this 'Fish' symmetry
```
Symmetry: reflect([315;135],135)
```


## Replace
Describes which states we would like to replace with what based on conditions in order to patchfix the map.

Inside braces:
- Before comma: This is the state (may be double or integer) that we will be looking out for and attempt to replace.
- After comma: This function returns the state we would like to replace the state with.
We also have access to a new variable here:
`area`: gets the size of bodies in the map.

This example shows that it will replace all bodies of size 3 that are all 1 with 0. It ill also reaplce all bodies that are of size 3 and with value 0 with 1.
```
Replace: replace(1,if(area<3?0:1));replace(0,if(area<3?1:0));
```


## ProcSet
Describes all states that the map may contain. If any cell states go beyond these, they are rounded to the closest legal state.

Examples:
```
ProcSet: {0;1}
ProcSet: [0;1]
ProcSet: [-1;6)
```
{} braces mean that all states must be exactly the values inside them
    {0;1} means the map may only have points with state 0 or 1.
[ brackets on either ends means including the state closest to that bracket
    [0;1] means the map may include all numbers from 0 to 1 inclusive
( brackets on either ends means discluding the state closest to that bracket
    [-1;6) means the map may include -1 onwards but is capped just before 6


## Connect
Describes which states must be connected at all times. Useful for when we want to make sure that all plains are accessible by each other.
Connect is used to 'fix' the map if there are any open bodies that are not accessable by each other.

This example shows that all cells with a state 0 must be connected to each other.
```
Connect: 0
```



