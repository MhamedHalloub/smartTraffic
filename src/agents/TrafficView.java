package agents;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TrafficView extends JFrame {

    // SHARED DATA: Agents write here, View reads here
    // ConcurrentHashMap prevents errors when multiple agents update at once
    public static Map<String, Point> carPositions = new ConcurrentHashMap<>();
    public static ArrayList<Point> trackPoints = new ArrayList<>();
    
    public static boolean isGreenLight = true;
    public static int stopSignIndex = 20;
    public static int trafficLightIndex = 90;

    // Double Buffering (Prevents Flickering)
    private Image offScreenImage;
    private Graphics offScreenGraphics;

    public TrafficView() {
        setTitle("JADE Smart Traffic Simulation");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Generate the Oval Track immediately
        createOvalTrack();
        
        setVisible(true);
    }

    private void createOvalTrack() {
        trackPoints.clear();
        int centerX = 400;
        int centerY = 300;
        int radiusX = 300;
        int radiusY = 200;
        
        // Create points every 3 degrees for a smooth curve
        for (int angle = 0; angle < 360; angle += 3) {
            double radians = Math.toRadians(angle);
            int x = (int) (centerX + radiusX * Math.cos(radians));
            int y = (int) (centerY + radiusY * Math.sin(radians));
            trackPoints.add(new Point(x, y));
        }
    }

    @Override
    public void paint(Graphics g) {
        // Init Off-screen buffer if needed
        if (offScreenImage == null) {
            offScreenImage = createImage(getWidth(), getHeight());
            if (offScreenImage == null) return;
            offScreenGraphics = offScreenImage.getGraphics();
        }

        // Draw everything to the invisible image first
        drawScene(offScreenGraphics);

        // Paste the final image to the screen
        g.drawImage(offScreenImage, 0, 0, this);
    }

    private void drawScene(Graphics g) {
        // 1. Draw Background
        g.setColor(new Color(34, 139, 34)); // Grass Green
        g.fillRect(0, 0, getWidth(), getHeight());

        // 2. Draw Road
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setStroke(new BasicStroke(40));
        g2.setColor(Color.GRAY);
        g2.drawOval(100, 100, 600, 400); // Matches the radius math
        
        // Center Line (Dashed)
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
        g2.setColor(Color.WHITE);
        g2.drawOval(100, 100, 600, 400);

        // 3. Draw Signs
        if (!trackPoints.isEmpty()) {
            // Stop Sign
            Point stop = trackPoints.get(stopSignIndex);
            g.setColor(Color.RED);
            g.fillOval(stop.x + 20, stop.y, 24, 24);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 10));
            g.drawString("STOP", stop.x + 22, stop.y + 16);

            // Traffic Light
            Point light = trackPoints.get(trafficLightIndex);
            g.setColor(Color.BLACK);
            g.fillRect(light.x + 20, light.y - 20, 20, 40);
            g.setColor(isGreenLight ? Color.GREEN : Color.RED);
            g.fillOval(light.x + 25, light.y - 15, 10, 10);
        }

        // 4. DRAW CARS (New Design)
        if (!carPositions.isEmpty()) {
            for (Map.Entry<String, Point> entry : carPositions.entrySet()) {
                String name = entry.getKey();
                Point p = entry.getValue();

                int carWidth = 34;
                int carHeight = 18;
                int x = p.x - (carWidth / 2);
                int y = p.y - (carHeight / 2);

                // Draw Wheels
                g2.setColor(Color.BLACK);
                g2.fillRoundRect(x + 4, y - 3, 6, 5, 2, 2);
                g2.fillRoundRect(x + carWidth - 10, y - 3, 6, 5, 2, 2);
                g2.fillRoundRect(x + 4, y + carHeight - 2, 6, 5, 2, 2);
                g2.fillRoundRect(x + carWidth - 10, y + carHeight - 2, 6, 5, 2, 2);

                // Pick Color based on Name
                Color bodyColor;
                int hash = Math.abs(name.hashCode()) % 3;
                if (hash == 0) bodyColor = new Color(220, 20, 60); // Red
                else if (hash == 1) bodyColor = new Color(30, 144, 255); // Blue
                else bodyColor = new Color(255, 165, 0); // Orange

                // Draw Body
                g2.setColor(bodyColor);
                g2.fillRoundRect(x, y, carWidth, carHeight, 6, 6);
                g2.setColor(bodyColor.darker());
                g2.drawRoundRect(x, y, carWidth, carHeight, 6, 6);

                // Draw Windows
                g2.setColor(new Color(200, 230, 255));
                g2.fillRect(x + 6, y + 3, 8, carHeight - 6); // Front
                g2.fillRect(x + carWidth - 10, y + 3, 5, carHeight - 6); // Back

                // Draw Name
                g2.setColor(Color.WHITE);
                g2.drawString(name, x, y - 5);
            }
        }
    }
}