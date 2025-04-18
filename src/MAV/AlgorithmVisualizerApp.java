package MAV;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

// Main application using CardLayout for different views
public class AlgorithmVisualizerApp {
	
	// Cards
    public static final String HOME_CARD = "home";
    public static final String PATHFINDING_CARD = "pathfinding";
    public static final String SORTING_CARD = "sorting";

    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public AlgorithmVisualizerApp() {
        // Main Frame
        frame = new JFrame("Algorithm Visualizer App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);

        // CardLayout container
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create and add the Home panel
        HomePanel homePanel = new HomePanel(this);
        mainPanel.add(homePanel, HOME_CARD);

        // Create and add the Pathfinding Visualizer panel
        AlgorithmVizualizerPanel pathfindingPanel = new AlgorithmVizualizerPanel(this);
        mainPanel.add(pathfindingPanel, PATHFINDING_CARD);

        // Create and add the Sorting Visualizer panel.
        SortingPanel sortingPanel = new SortingPanel(this);
        mainPanel.add(sortingPanel, SORTING_CARD);

        // Add the main panel to the frame and display it
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    // Method to switch views
    public void showCard(String cardName) {
        cardLayout.show(mainPanel, cardName);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AlgorithmVisualizerApp());
    }
}

// Home page
class HomePanel extends JPanel {
    private AlgorithmVisualizerApp app;

    public HomePanel(AlgorithmVisualizerApp app) {
        this.app = app;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Welcome to Algorithm Visualizer", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton pathfindingButton = new JButton("Pathfinding Visualizer");
        JButton sortingButton = new JButton("Sorting Visualizer");

        pathfindingButton.addActionListener(e -> app.showCard(AlgorithmVisualizerApp.PATHFINDING_CARD));
        sortingButton.addActionListener(e -> app.showCard(AlgorithmVisualizerApp.SORTING_CARD));

        buttonPanel.add(pathfindingButton);
        buttonPanel.add(sortingButton);
        add(buttonPanel, BorderLayout.CENTER);
    }
}

// Sorting Page
class SortingPanel extends JPanel {
    private AlgorithmVisualizerApp app;

    public SortingPanel(AlgorithmVisualizerApp app) {
        this.app = app;
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Sorting Visualizer (Coming Soon)", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 32));
        add(label, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Home");
        backButton.addActionListener(e -> app.showCard(AlgorithmVisualizerApp.HOME_CARD));
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}

// Pathfinding Page
class AlgorithmVizualizerPanel extends JPanel {
    private AlgorithmVisualizerApp app;
    // Primitive Variables
    private int delay = 8;
    private int WIDTH = 1200;
    private int endX = -1;
    private int endY = -1;
    private int startX = -1;
    private int startY = -1;
    private int cells = 30;
    private int cellSize;
    private int currTool = 0;
    private int currAlg = 0;
    private int mapHeight = 600;
    private String[] tools = {"Start", "End", "Wall", "Eraser"};
    private String[] algorithm = {"BFS", "DFS", "A*"};
    private int dRow[] = { 0, 1, 0, -1 };
    private int dCol[] = { -1, 0, 1, 0 };

    // Panels and Controls
    private JPanel controlPanel;
    private JPanel titlePanel;
    private JPanel mapPanel;
    private JLabel densityValueLabel = new JLabel(cells + "x" + cells);
    private Node[][] map;
    private Algorithms alg = new Algorithms();

    // Colors
    private Color darkPurple = new Color(44, 32, 66); 
    private Color turq = new Color(124, 170, 170);
    private Color green = new Color(39, 110, 0);
    private Color red = new Color(200, 0, 0);

    // GUI Components
    private JComboBox<String> toolDropDownMenu = new JComboBox<>(tools);
    private JComboBox<String> algorithmDropDownMenu = new JComboBox<>(algorithm);
    private JButton startAlgButton = new JButton("Start Search");
    private JButton resetButton = new JButton("Reset Map");
    private JButton homeButton = new JButton("Home");
    private JSlider mapDensitySlider = new JSlider(1, 7, 4);

    // Flags
    private boolean solving = false;

    public AlgorithmVizualizerPanel(AlgorithmVisualizerApp app) {
        this.app = app;
        setLayout(null);
        // Initialize the map grid
        clearMap();

        // Create Title Area
        titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBounds(0, 0, WIDTH, 50);
        titlePanel.add(Box.createRigidArea(new Dimension(80, 0)), BorderLayout.LINE_END);
        JLabel title = new JLabel("Algorithm Visualizer");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24)); 
        
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        buttonWrapper.setOpaque(false); 
        homeButton.setPreferredSize(new Dimension(80, 30));
        buttonWrapper.add(homeButton);

        titlePanel.add(buttonWrapper, BorderLayout.LINE_START);
        titlePanel.add(title, BorderLayout.CENTER);
        add(titlePanel);
        

        // Create Control Area
        controlPanel = new JPanel();
        controlPanel.setBounds(0, 50, 1185, 90);
        controlPanel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), ""));
        controlPanel.add(startAlgButton);
        controlPanel.add(resetButton);
        controlPanel.add(toolDropDownMenu);
        controlPanel.add(algorithmDropDownMenu);
    

        // Map Density Slider Setup (optional)
        mapDensitySlider.setMajorTickSpacing(10);
        JPanel densityPanel = new JPanel();
        densityPanel.setPreferredSize(new Dimension(100, 50));
        densityPanel.setLayout(new BoxLayout(densityPanel, BoxLayout.Y_AXIS));
        JLabel mapDensityTitle = new JLabel("Map Density");
        mapDensityTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mapDensitySlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        densityValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        densityPanel.add(mapDensityTitle);
        densityPanel.add(mapDensitySlider);
        densityPanel.add(densityValueLabel);
        controlPanel.add(densityPanel);

        add(controlPanel);
        
       
        // Create Map Panel
        mapPanel = new Map();
        mapPanel.setBounds(290, 150, WIDTH, mapHeight + 5);
        add(mapPanel);

        // Calculate cell size based on map dimensions
        cellSize = Math.min(mapPanel.getWidth() / cells, mapPanel.getHeight() / cells);

        // Add Listeners for the controls
        toolDropDownMenu.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                currTool = toolDropDownMenu.getSelectedIndex();
            }
        });

        algorithmDropDownMenu.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                currAlg = algorithmDropDownMenu.getSelectedIndex();
            }
        });

        startAlgButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
                if ((startX > -1 && startY > -1) && (endX > -1 && endY > -1))
                    solving = true;
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetMap();
                update();
            }
        });

        mapDensitySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                cells = mapDensitySlider.getValue() * 10;
                clearMap();
                resetMap();
                cellSize = Math.min(mapPanel.getWidth() / cells, mapPanel.getHeight() / cells);
                update();
            }
        });

        //Return to home page
        homeButton.addActionListener(e -> app.showCard(AlgorithmVisualizerApp.HOME_CARD));

        new Thread(() -> startSearch()).start();
    }

    // Clears the entire map grid
    public void clearMap() {
        endX = -1;
        endY = -1;
        startX = -1;
        startY = -1;
        map = new Node[cells][cells];
        for (int x = 0; x < cells; x++) {
            for (int y = 0; y < cells; y++) {
                map[x][y] = new Node(3, x, y);
            }
        }
        reset();
    }

    // Resets visited nodes (but not walls, start, or end)
    public void clearVisited() {
        for (int x = 0; x < cells; x++) {
            for (int y = 0; y < cells; y++) {
                if (map[x][y].getType() == 4 || map[x][y].getType() == 5) {
                    map[x][y] = new Node(3, x, y);
                }
            }
        }
    }

    // Reset flag
    public void reset() {
        solving = false;
    }

    // Delay method
    public void delay() {
        try {
            Thread.sleep(delay);
        } catch (Exception e) { }
    }

    // Starts the search algorithm based on the current selection
    public void startSearch() {
        while (true) {
            if (solving) {
                if (currAlg == 0) {
                    alg.BFS();
                } else if (currAlg == 1) {
                    alg.DFS();
                } // else if (currAlg == 2) { TD: A* implementation  }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) { }
        }
    }

    // Resets the map’s visited/path nodes but preserves start/end and walls.
    public void resetMap() {
        for (int x = 0; x < cells; x++) {
            for (int y = 0; y < cells; y++) {
                int type = map[x][y].getType();
                if (type == 4 || type == 5 || type == 2)
                    map[x][y] = new Node(3, x, y);
            }
        }
        if (startX > -1 && startY > -1) {
            map[startX][startY] = new Node(0, startX, startY);
            map[startX][startY].setHops(0);
        }
        if (endX > -1 && endY > -1) {
            map[endX][endY] = new Node(1, endX, endY);
        }
        reset();
    }

    // Calls repaint on the panel
    public void update() {
        repaint();
    }

    // Node Class
    class Node {
        private int cellType;
        private int hops, x, y, lastX, lastY;
        private double distToEnd;

        public Node(int type, int x, int y) {
            this.cellType = type;
            this.x = x;
            this.y = y;
            this.hops = -1;
        }

        public double getEuclidDist() {
            int xdif = Math.abs(x - endX);
            int ydif = Math.abs(y - endY);
            distToEnd = Math.sqrt((xdif * xdif) + (ydif * ydif));
            return distToEnd;
        }

        public int getX() { return x; }
        public int getY() { return y; }
        public int getLastX() { return lastX; }
        public int getLastY() { return lastY; }
        public int getType() { return cellType; }
        public int getHops() { return hops; }

        public void setType(int type) {
            cellType = type;
        }

        public void setLastNode(int x, int y) {
            lastX = x;
            lastY = y;
        }

        public void setHops(int hops) {
            this.hops = hops;
        }
    }

    // Map class handles actions on the Grid and Painting components
    class Map extends JPanel implements MouseListener, MouseMotionListener {
        public Map() {
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (int x = 0; x < cells; x++) {
                for (int y = 0; y < cells; y++) {
                    Color fillColor;
                    switch (map[x][y].getType()) {
                        case 0: fillColor = green; break;
                        case 1: fillColor = red; break;
                        case 2: fillColor = Color.BLACK; break;
                        case 3: fillColor = Color.WHITE; break;
                        case 4: fillColor = turq; break;
                        case 5: fillColor = darkPurple; break;
                        default: fillColor = Color.GRAY; break;
                    }
                    g.setColor(fillColor);
                    g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                    g.setColor(Color.BLACK);
                    g.drawRect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }
        }

        @Override public void mouseClicked(MouseEvent e) { }
        @Override public void mouseEntered(MouseEvent e) { }
        @Override public void mouseExited(MouseEvent e) { }

        @Override
        public void mousePressed(MouseEvent e) {
            if (solving) return;
            try {
                int x = e.getX() / cellSize;
                int y = e.getY() / cellSize;
                Node curr = map[x][y];
                switch (currTool) {
                    // Start Node
                    case 0:
                        if (curr.getType() != 2) {
                            if (startX > -1 && startY > -1) {
                                map[startX][startY].setType(3);
                            }
                            startX = x;
                            startY = y;
                            curr.setType(0);
                        }
                        break;
                    // End Node
                    case 1:
                        clearVisited();
                        if (endX > -1 && endY > -1) {
                            map[endX][endY].setType(3);
                        }
                        endX = x;
                        endY = y;
                        curr.setType(1);
                        solving = false;
                        break;
                    // Wall Node
                    case 2:
                        if (curr.getType() != 0 && curr.getType() != 1) {
                            curr.setType(2);
                        }
                        solving = false;
                        break;
                    // Eraser
                    case 3:
                        if (curr.getType() != 0 && curr.getType() != 1) {
                            curr.setType(3);
                        }
                        break;
                    default:
                }
                AlgorithmVizualizerPanel.this.update();
            } catch (Exception ex) { }
        }

        @Override
        public void mouseReleased(MouseEvent e) { }

        @Override
        public void mouseDragged(MouseEvent e) {
            try {
                int x = e.getX() / cellSize;
                int y = e.getY() / cellSize;
                Node curr = map[x][y];
                if (currTool == 2 && curr.getType() != 0 && curr.getType() != 1) { 
                    curr.setType(2);
                }
                if (currTool == 3 && curr.getType() != 0 && curr.getType() != 1) { 
                    curr.setType(3);
                }
                AlgorithmVizualizerPanel.this.update();
            } catch(Exception ex) { }
        }

        @Override public void mouseMoved(MouseEvent e) { }
    }

    // Alogirthm Implementaions
    public class Algorithms {
    	
        public void DFS() {
            if (startX < 0 || startY < 0 || endX < 0 || endY < 0) {
                return;
            }
            Stack<Node> stack = new Stack<>();
            Node startNode = map[startX][startY];
            startNode.setHops(0);
            stack.add(startNode);
            boolean found = false;
            while (!stack.isEmpty() && !found && solving) {
                Node current = stack.pop();
                int x = current.getX();
                int y = current.getY();
                for (int i = 0; i < 4; i++) {
                    if (!solving) break;
                    int newX = x + dRow[i];
                    int newY = y + dCol[i];
                    if (newX < 0 || newX >= cells || newY < 0 || newY >= cells) continue;
                    Node neighbor = map[newX][newY];
                    if (neighbor.getType() == 2 || neighbor.getType() == 4 || neighbor.getType() == 5) continue;
                    neighbor.setLastNode(x, y);
                    neighbor.setHops(current.getHops() + 1);
                    if (newX == endX && newY == endY) {
                        found = true;
                        break;
                    }
                    if (neighbor.getType() != 0 && neighbor.getType() != 1) {
                        neighbor.setType(4);
                        update();
                        delay();
                    }
                    stack.add(neighbor);
                }
            }
            if (!solving || !found) {
                solving = false;
                return;
            }
            int x = endX, y = endY;
            while (!(x == startX && y == startY) && solving) {
                Node node = map[x][y];
                x = node.getLastX();
                y = node.getLastY();
                if (!(x == startX && y == startY) && !(x == endX && y == endY)) {
                    map[x][y].setType(5);
                }
                update();
                delay();
            }
            solving = false;
        }

        public void BFS() {
            if (startX < 0 || startY < 0 || endX < 0 || endY < 0) {
                return;
            }
            Queue<Node> queue = new LinkedList<>();
            Node startNode = map[startX][startY];
            startNode.setHops(0);
            queue.add(startNode);
            boolean found = false;
            while (!queue.isEmpty() && !found && solving) {
                Node current = queue.poll();
                int x = current.getX();
                int y = current.getY();
                for (int i = 0; i < 4; i++) {
                    if (!solving) break;
                    int newX = x + dRow[i];
                    int newY = y + dCol[i];
                    if (newX < 0 || newX >= cells || newY < 0 || newY >= cells) continue;
                    Node neighbor = map[newX][newY];
                    if (neighbor.getType() == 2 || neighbor.getType() == 4 || neighbor.getType() == 5) continue;
                    neighbor.setLastNode(x, y);
                    neighbor.setHops(current.getHops() + 1);
                    if (newX == endX && newY == endY) {
                        found = true;
                        break;
                    }
                    if (neighbor.getType() != 0 && neighbor.getType() != 1) {
                        neighbor.setType(4);
                        update();
                        delay();
                    }
                    queue.add(neighbor);
                }
            }
            if (!solving || !found) {
                solving = false;
                return;
            }
            int x = endX, y = endY;
            while (!(x == startX && y == startY) && solving) {
                Node node = map[x][y];
                x = node.getLastX();
                y = node.getLastY();
                if (!(x == startX && y == startY) && !(x == endX && y == endY)) {
                    map[x][y].setType(5);
                }
                update();
                delay();
            }
            solving = false;
        }
    }
}
