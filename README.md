# SML4ADS

### Introduction
This is a tool for autonomous driving scenario modeling, simulation and analysis. It uses a graphical modeling approach to rigorously and accurately represent autonomous driving scenario, which improves comprehensibility for varied users.
The semantic information of the scenario model is stored in JSON,
which is a lightweight data exchange format with a concise and
unambiguous hierarchical structure. By parsing the scenario model,
ADSML supports interfacing with several popular autonomous
driving simulators for scenario simulation. It also provides rigorous
verification and analysis techniques to check whether the scenario
model satisfies specific property specifications and to identify potentially hazardous scenario, which is crucial for the development
of safe Autonomous Driving Systems (ADS). Our tool
can provide a friendly and integrated environment for modeling,
simulating and analyzing scenarios with other excellent and interrelated tools. For experimental demonstration, we adopt several
typical and complex scenarios from other studies such as National
Highway Traffic Safety Administration (NHTSA) and the Chinese
car company.

### Environment

OS: windows java: jdk11

- VM option --module-path "${javafx-sdk-path}\lib" --add-modules javafx.controls,javafx.fxml

- python requirements
1. carla
2. hprose

### Instructions

#### File types

- \*.model
- \*.tree
- \*.adsml

#### How to build a model

#### How to build a tree

##### Prepare

1. File -> New -> Tree
2. Enter tree name

##### Editing

On the left side is the palette, where lies components of the tree. On the right side is the canvas, in which you can
construct behavior of models. There are 3 components: Behavior, BranchPoint and Transition.

###### Behavior

Behavior indicates how models act. 9 behaviors are included in SML4ADS

|Behavior|Function|Parameters| 
|--|--|--| 
|Keep|Move along the lane at a constant speed|duration(optional)|
|Accelerate|Move along the lane with speed increasing|acceleration(None negative), target speed| 
|Decelerate|Move along the lane with speed decreasing|acceleration(None negative), target speed| 
|ChangeLeft|Move to the left lane|acceleration(optional), target speed(optional)| 
|ChangeRight|Move to the right Lane|acceleration(optional), target speed(optional)| 
|TurnLeft|Turn left at intersections|acceleration, target speed| |TurnRight|Turn right at intersections|acceleration, target speed| |LaneOffset|Move left or right within the lane|offset, acceleration(optional), target speed(optional), duration(optional)| 
|Idle|Do nothing|duration(optional)|

Parameters Each Behavior has at least one parameter. In SML4ADS, required params are marked with an asterisk(*). On the
other hand, those without * are optional params.

- duration(second): usually optional, means the maximum time this behavior will last for. Notice that Accelerate and
  Decelerate will not stop when models reach the target speed(explained below), they will move at the target speed until
  the duration runs out. Additionally, if Guards on the transitions linked to this behavior is satisfied, its behavior
  will be changed IMMEDIATELY according to the corresponding transition.
- acceleration(m/s^2)
- target speed(m/s)
- offset(m): In laneOffset, offset means the distance between the model and the center line of the lane.

###### BranchPoint
A linkable point cooperates with Transitions, creating a one to n relation. Branch Point serves as a middleman.\
Suppose that Behavior A has different possibilities to transfer to Behavior 1...n when condition p is satisfied, see figure // TODO. the possibility of transferring from Behavior A to Behavior x can be calculated by the formula below

$$P(A\rightarrow x) = \frac{weight_x}{\sum_{i=1}^{n}weight_i}$$

###### Transition

Transitions create bonds between Behaviors, producing a behavior tree to illustrate models' actions.\
There are two kindsof Transition: Common Transition and Probability Transition.\
Common Transition is a solid line linking two Behaviors with a guard(optional), conditions of behavior transferring. 
Probability Transition, a dashed line with a weight, worked with Branch Point. The arrow starts at a Branch Point and ends at a Behavior.\
There are a lot of properties and functions which can be used in guards, shown in the table below.
|Property|Meaning|Data type|
|--|--|--|
|roadId|Id of the road|int|
|laneSectionId|Id of the lane section|int|
|laneId|Id of the lane|int|
|junctionId|Id of the junction|int|
|intersection|True if the car is in an intersection|bool|
|road_s|Offset from the starting position of the road|double|
|lane_s|Offset from the starting position of the lane|double|
|offset|Left and right offset in lane (negative number indicates left)|double|
|width|Width of the car|double|
|length|Length of the car|double|
|speed|Speed of the car|double|
|acceleration|Acceleration of the Car|double|
|model|blueprint of the car|string|
|t|The clock of the current car (in the behavior tree, t will be reset when migrating to the child node). It is used to control the time of the current behavior.|int|

Acess these properties by *CarName*.*PropertyName*, such as car1.roadId

|Function Name|Parameters|Return Value|Meaning|
|--|--|--|--|
|hasObjWithinDisInLane|1: current car name <br/> 2: distance|bool|Whether there are objects within the distance <br/> 1. objects are in the same or the succesor lane with the current car <br/> 2. objects are within the given distance <br/> 3. objects are in front of the current car. |
|hasObjWithinDisInLeftLane|1: current car name <br/> 2: distance|bool|Whether there are objects within the distance <br/> 1. objects are in the left or the left succesor lane with the current car <br/> 2. objects are within the given distance <br/> 3. objects are in front of the current car. <br/> 4. return false if the left lane doesn't exist|
|hasObjWithinDisInRightLane|1: current car name <br/> 2: distance|bool|Whether there are objects within the distance <br/> 1. objects are in the right or the right succesor lane with the current car <br/> 2. objects are within the given distance <br/> 3. objects are in front of the current car. <br/> 4. return false if the right lane doesn't exist|
|withinDisToObjsInLane|1. current car name(car1) <br/> 2. name of another car(car2) <br/> 3. distance|bool|whether car1 is within the distance of car2 <br/> 1. car2 is in the same or the succesor lane with the car1 <br/> 2. car2 is in front of car1 <br/> 3. car1 is within the distance of car2|
|withinDisToObjsInRoad|1. current car name(car1) <br/> 2. name of another car(car2) <br/> 3. distance|bool|whether car1 is within the distance of car2 <br/> 1. car2 is in front of car1 <br/> 3. car1 is within the distance of car2|
|isInSameLane|1. current car name(car1) <br/> 2. name of another car(car2)|bool|suppose that car1 is in lane1 and car2 is in lane2. <br/> whether lane1 is the same with lane2 or lane2 is the predecessor or successor lane of lane1 |

Guard Example
```
car1.t > 3             // > < <= >= == is supported
car1.speed < car2.speed  
hasObjWithinDisInLane(car1, 5)
```

#### After editing
With the \*.model file opend, click the *preprocess* button, generating a \*.adsml file.\
This is the prerequisite of simulation and verification.

### How to do the simulation
First, go to /src/main/java/com/ecnu/adsmls/simulator/adsml_carla_simulation/src/;\
Second, open Carla 0.9.13;\
Third, run main.py in a python enviroment which meets the requirements;\
Fourth, run this program, open a \*.model file and click the *simluate* button;\
Finally, choose the simulation type\
Then you'll see the simulation begins in Carla if no error occurs.

### How to do the verification
First, open a \*.model file;\
Second, click the *verify* button.\
Third, specify your *requirements*(optional) and output *filename*(optional) and click *confirm*\
Finally, a \*xml file will be generated if no error occurs.
