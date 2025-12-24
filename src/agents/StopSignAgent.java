package agents;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import java.util.Random;

@SuppressWarnings("serial")
public class StopSignAgent extends Agent {

    protected void setup() {
        // Trigger every 8 seconds
        addBehaviour(new TickerBehaviour(this, 8000) {
            protected void onTick() {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("car-service");
                template.addServices(sd);

                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    if (result.length > 0) {
                        // Pick random car
                        int index = new Random().nextInt(result.length);
                        ACLMessage stopMsg = new ACLMessage(ACLMessage.INFORM); 
                        stopMsg.addReceiver(result[index].getName());
                        stopMsg.setContent("STOP");
                        send(stopMsg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}