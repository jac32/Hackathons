package HashCode.Event;

import HashCode.*;

public class Wait extends Event {
    Drone drone;
    int num_turns;
}

public Wait (Drone drone, Int num_turns) {
    this.drone = drone;
    this.num_turns = turns;
}
