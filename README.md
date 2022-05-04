# SML4ADS
### This is ...
// TODO introduction

### Environment
OS: windows
java: jdk11
- VM option
  --module-path "${javafx-sdk-path}\lib" --add-modules javafx.controls,javafx.fxml

// TODO python requirements

### Instructions
#### File types
- *.model
- *.tree
- *.adsml

#### How to build a model

#### How to build a tree
##### Prepare
1. File -> New -> Tree
2. Enter tree name
##### Editing
On the left side is the palette, where lies components of the tree.
On the right side is the canvas, in which you can construct behavior of models.
There are 3 components: Behavior, BranchPoint and Transition.
###### Behavior
Behavior indicates how models act. 9 behaviors are included in SML4ADS

|Behavior|Function|
|--|--|
|Keep|Move along the lane at a constant speed|
|Accelerate|Move along the lane at an increasing speed, with a constance acceleration(None negative)|
|Decelerate|Move along the lane at a decreasing speed, with a constance acceleration(None negative)|
|ChangeLeft||
|ChangeRight||
|TurnLeft||
|TurnRight||
|LaneOffset||
|Idle||

Parameters
Each Behavior has at least one parameter. 
In SML4ADS, required params are marked with an asterisk(*).
On the other hand, those without * are optional params.
- duration: usually optional, means the maximum time this behavior will last for.
Notice that Accelerate and Decelerate will not stop when models reach the target speed(explained below),
they will move at the target speed until the duration runs out.
Additionally, if Guards on the transitions linked to this behavior is satisfied, 
its behavior will be changed IMMEDIATELY according to the corresponding transition.
- acceleration: 
- // TODO

###### Transition


###### BranchPoint


#### After editing
preprocess


### How to do the simulation


### How to do the verification

