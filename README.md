# SML4ADS

### This is ...

// TODO introduction

### Environment

OS: windows java: jdk11

- VM option --module-path "${javafx-sdk-path}\lib" --add-modules javafx.controls,javafx.fxml

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

On the left side is the palette, where lies components of the tree. On the right side is the canvas, in which you can
construct behavior of models. There are 3 components: Behavior, BranchPoint and Transition.

###### Behavior

Behavior indicates how models act. 9 behaviors are included in SML4ADS

|Behavior|Function|Parameters| |--|--|--| |Keep|Move along the lane at a constant speed|duration(optional)|
|Accelerate|Move along the lane with speed increasing|acceleration(None negative), target speed| |Decelerate|Move along
the lane with speed decreasing|acceleration(None negative), target speed| |ChangeLeft|Move to the left
lane|acceleration(optional), target speed(optional)| |ChangeRight|Move to the right Lane|acceleration(optional), target
speed(optional)| |TurnLeft|Turn left at intersections|acceleration, target speed| |TurnRight|Turn right at
intersections|acceleration, target speed| |LaneOffset|Move left or right within the lane|offset, acceleration(optional),
target speed(optional), duration(optional)| |Idle|Do nothing|duration(optional)|

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

A linkable point cooperates with Transitions, creating a one to n relation. Branch Point serves as a middleman. Suppose
that Behavior A has different possibilities to transfer to Behavior 1...n when condition p is satisfied, see figure //
TODO. the possibility of transferring from Behavior A to Behavior x can be calculated by the formula below

$P(A->x) = \frac{weight_x}{sum_{i=1}^n weight_i}

###### Transition

Transitions create bonds between Behaviors, producing a behavior tree to illustrate models' actions. There are two kinds
of Transition: Common Transition and Probability Transition. Common Transition is a solid line linking two Behaviors
with a guard(optional), conditions of behavior transferring. Probability Transition, a dashed line with a weight, worked
with Branch Point. The arrow starts at a Branch Point and ends at a Behavior.

#### After editing

preprocess

### How to do the simulation

### How to do the verification

