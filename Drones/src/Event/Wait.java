package Event;

public class Wait extends Event {
    Drone drone;
    Int num_turns;
}

public Wait (Drone drone, Int num_turns) {
    this.drone = drone;
    this.num_turns = turns;
}
