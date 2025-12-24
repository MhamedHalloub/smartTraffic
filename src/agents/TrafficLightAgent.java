package agents;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class TrafficLightAgent extends Agent {
    protected void setup() {
        // Toggle light every 4 seconds
        addBehaviour(new TickerBehaviour(this, 4000) {
            protected void onTick() {
                TrafficView.isGreenLight = !TrafficView.isGreenLight;
                if (MainContainer.view != null) MainContainer.view.repaint();
            }
        });
    }
}