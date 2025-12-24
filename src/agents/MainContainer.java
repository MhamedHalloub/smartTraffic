package agents;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class MainContainer {

    public static TrafficView view; 

    public static void main(String[] args) {
        // 1. Launch GUI
        view = new TrafficView();
        
        // 2. Launch JADE
        setupJADE();
    }

    public static void setupJADE() {
        try {
            Runtime rt = Runtime.instance();
            Profile p = new ProfileImpl();
            p.setParameter(Profile.MAIN_HOST, "localhost");
            p.setParameter(Profile.GUI, "true");
            AgentContainer mainContainer = rt.createMainContainer(p);

            // 3. Start Agents
            mainContainer.createNewAgent("Light", "agents.TrafficLightAgent", null).start();
            
            // Spawn 3 Cars
            mainContainer.createNewAgent("Car1", "agents.CarAgent", null).start();
            mainContainer.createNewAgent("Car2", "agents.CarAgent", null).start();
            mainContainer.createNewAgent("Car3", "agents.CarAgent", null).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}