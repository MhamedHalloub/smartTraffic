package agents;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import java.awt.Point;

public class CarAgent extends Agent {
    
    int currentPointIndex = 0;
    boolean isStopped = false;
    long stopTimer = 0;

    protected void setup() {
        // Wait briefly for GUI to initialize
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        if (TrafficView.trackPoints.isEmpty()) {
            System.out.println("❌ " + getLocalName() + ": No track found. Dying.");
            doDelete();
            return;
        }

        // Spawn at different locations based on name hash
        // This ensures they don't start piled on top of each other
        int startOffset = Math.abs(getLocalName().hashCode()) % 30;
        currentPointIndex = (startOffset * 5) % TrafficView.trackPoints.size();

        System.out.println("✅ " + getLocalName() + " spawned.");

        addBehaviour(new TickerBehaviour(this, 50) { // 20 FPS
            protected void onTick() {
                if (TrafficView.trackPoints.isEmpty()) return;

                // 1. UPDATE POSITION ON MAP
                Point currentPos = TrafficView.trackPoints.get(currentPointIndex);
                TrafficView.carPositions.put(getLocalName(), currentPos);

                // 2. CHECK IF BLOCKED (Collision Avoidance)
                int nextIndex = (currentPointIndex + 1) % TrafficView.trackPoints.size();
                Point nextPos = TrafficView.trackPoints.get(nextIndex);
                boolean blocked = false;

                // Look at all other cars
                for (Point otherPos : TrafficView.carPositions.values()) {
                    // If it's not me, and it's too close to my destination...
                    if (otherPos != currentPos && otherPos.distance(nextPos) < 35) {
                        blocked = true;
                        break;
                    }
                }

                // 3. TRAFFIC RULES
                // Red Light
                if (currentPointIndex == TrafficView.trafficLightIndex && !TrafficView.isGreenLight) {
                    blocked = true;
                }
                
                // Stop Sign
                if (currentPointIndex == TrafficView.stopSignIndex) {
                    if (!isStopped) {
                        isStopped = true;
                        stopTimer = System.currentTimeMillis();
                    }
                    if (System.currentTimeMillis() - stopTimer < 2000) {
                        blocked = true; // Wait here
                    }
                } else {
                    isStopped = false; // Reset flag when we leave the stop sign
                }

                // 4. MOVE (If not blocked)
                if (!blocked) {
                    currentPointIndex = nextIndex;
                }

                // 5. REFRESH SCREEN
                if (MainContainer.view != null) MainContainer.view.repaint();
            }
        });
    }
}