# Assignment 2

Author: `Terry Davis Fan Club`

# TraceAbility Links

![diagram](../../../raw/renders/Model.IntelliBus_System._4___Functional_architecture._4_1_Functional_decomposition.Automated_Passenger_Counting.High_level_component_definitions_of_Automated_Passenger_Counting__APC_.svg)

## Requirments:

#### - Monitor passengers (core function):

    (Multiple objects together satisfy this requirment) The "PassengerMonitoringModule" satisfies this requirment directly.

#### - Monitor Number of passengers:

    The "PassengerCounter" satisfies this requirment.

#### - APC equipment for counting:

    The "CCTVScanner" object satisfies this requirment, since it is an equipment that will be used to count the number of passangers.

#### - Passanger counters accuaracy:

    The "CCTVScanner" object satisfies this requirment, because of the CCTVScanner's description: "High accuracy in counting passengers, not reliable for facial recognition."

#### - Monitor identity of passengers:

    The "PassengerIdentifier" satisfies this requirment.

#### - APC equipment for identifying:

    The "RFIDScanner" object satisfies this requirment, since it is an equipment that will be used to identify passangers.

#### - Passenger identifiers accuaracy:

    The "RFIDScanner" object satisfies this requirment, because of the RFIDScanner's description: "Preliminary experiments showed no misreading, but may not recognize the RFID chip if shielded or too far away from reader."

#### - Report passenger status:

    The "PassengerMonitoringModule" object satisfies this requirment, because it is responsible for sending passenger status reports.

#### - Keep track of passangers:

    The "BoardingDoor" object satisfies this requirment, because the vehicle can summarize boarding and alighting detections from each their doors. (And the BoardingDoor represents a door on the vehicle.)

# Intelligent Intersection Control

## Part Catalogue

The Intelligent Intesection Control will be assembled using off-the-self elements described in the part catalogue.

![diagram](../../../raw/renders/Model.IntelliBus_System._4___Functional_architecture._4_1_Functional_decomposition.Intelligent_Intersection_Control.Manufacturer_Catalog.Manufacturer_catalogue_for_intersection_control.svg)

## J42 IIC Design

![diagram](../../../raw/renders/Model.IntelliBus_System._4___Functional_architecture._4_1_Functional_decomposition.Intelligent_Intersection_Control.Junction_42.Junction_42.svg)

As it is clearly visible on the diagram above, the J42 Intersection is designed by reusing components from the Manufacturer Part Catalogue.

![diagram](../../../raw/renders/Model.IntelliBus_System._4___Functional_architecture._4_1_Functional_decomposition.Intelligent_Intersection_Control.Junction_42.Junction_42.Junction_42.svg)

The `Junction 42` Internal Block Diagramn illustrates the internals of the junction's `intersection` element. The diagram provides a detailed blueprint of the interactions between the Intersection Control and the other system components.

The IBD consists of the Intersection Control block's internals and it contains references to other instances of components from the part catalogue, but these are not part of the Intersection block.

The Intersection Control block communicates with vehicle detectors and provides an interface for handling pedestrian crossing requests.

## Architectural Decisions

### Communication Between Components

Given that there is 3-way traffic at the junction, we must interact with sensors and devices for recognizing **eastbound**, **northbound** and **southbound** traffic, as well as handling pedestrian crossing requests from all three crossings. Hence, the elements we have to interact with are:

1. Vehicle Detectors

   1.1 Eastbound Vehicle Detector

   1.2 Northbound Vehicle Detector

   1.3 Southbound Vehicle Detector

2. Crossing Intent Buttons (`Pedestrian Call Button`s)

   1.1 Eastbound Pedestrian Call Button

   1.2 Northbound Pedestrian Call Button

   1.3 Southbound Pedestrian Call Button

As these components should not be part of the IBD (since the IBD shows the internals of Junction 42 - the Intersection's descendant), these are shown as references.

### Telemetry Egress Port

Sends operational data and metrics to a central monitoring system for analysis, reporting, and further optimization. It is intentionally connected to the system's outer bound with a proxy port, since there is no direct user of the provided interface.

### RemoteControl Egress Port

Allows remote management and configuration of the intersection control parameters, supporting real-time adjustments and monitoring. It is intentionally connected to the system's outer bound with a proxy port, since there is no direct communication with any component in the part catalogue.

---

# **Top-down design of the traffic and route planning (TRP) functionality**

_As one of the design teams, we have designed the the top-down design of the traffic and route planning (TRP) functionality, according to requirements provided in package IntelliBus System/2 - Requirements/Traffic and Route Planning._

First of all, we have completed the pre-created BDD of the component, with several subcomponents:

<!-- ![Overview of the TRP component](figures/trp.png) -->

![diagram](../../../raw/renders/Model.IntelliBus_System._4___Functional_architecture._4_1_Functional_decomposition.Traffic_Route_Planning.Traffic_Route_Planning.svg)

**There were several high-level requirements which we have had to considered during the design:**

- Designate destination
  - Take into account vehicle locations
  - Take into account travel requests_imported
    - The service route planner must take into account the layout/map (roads, bus stop, etc.) of its area of opreation)
    - Request transportation from personal devices
    - Report passenger identities
  - Take into account planning suggestions
    - Detect current congestions
      - Take into account locations of autonomous vehicles
      - Take into account locations of other vehicles
    - Predict upcoming congestions
      - Take into account historical congestion data
      - Take into account travel requests
    - Predict upcoming transportation demands
      - Take into account aggregated travel requests
      - Take into account special events
  - Take into account the layout
    - Specify the layout
- Coordinate with traffic lights
- Optimize for reducing delay
- Undergo maintenance and recharging
- Take into account current status

**Several subcomponents of the TRP has been taken into account as the procreation of the requirements above:**

- <ins>Traffic management</ins>
- <ins>Route management</ins>
- <ins>Database management</ins>

_In a nutshell these three components are able to include all of the core functionalities which were stated by the customer and the system architect, but in fact the most important of them is our database component._

**Traffic management**

To be consistent, the team resued the blocks and interfaces from the APC and IIC component's bdd, because numerous functionalities like passenger counting, vehicle detection etc. could reused for the reason that traffic management system must be able to do that regarding to the requirements.

The components mentioned above could be connected to the missing traffic manegement capabilites with ease.

As it can be seen on the diagram, core functionalities such as opreating area, request monitoring, traffic prediction and the intersection component which contains the extremely essential traffic detection are directly binded to the central traffic management block and hence the Bus database as well.

**Route management**

Although the traffic management component is mostly reused from previously designed products, the route management is a bare procreation from our team.

The idea behind the realization is simple, we have collected all of the requested and other useful functions in order to achieve the maximalization of our system.

This component stands for the implementation of the autonomous representation while optimize their daily labor.

**Database management**

As the most important component, the database controls all the data between the subcomponents and their ports.

Each components and their subcomponents have their adequate ports for the specific communication with the system, hence in our perspective the database is not only store representative data, instead it sends data to the subcomponents as well, and works like the systems brain.

---
