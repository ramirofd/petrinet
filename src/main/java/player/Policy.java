package player;

import java.util.ArrayList;

public interface Policy {
    public Transition nextTransition(ArrayList<Transition> t_list);
}
