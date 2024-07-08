package main.java.com.example;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

import java.util.List;
import java.util.Map;

/**
 * @class   GraphVisualizer
 * @brief   Visualizes the regions and their connections in a graph.
 */
public class GraphVisualizer {
    private final Graph graph;
    private final Viewer viewer;

    /**
     * @brief   Constructor of the GraphVisualizer class.
     *
     * @param   regions Map of all regions in the simulation.
     *
     * @pre     regions is not null.
     * @post    Initializes the graph and adds nodes and edges based on regions and their neighbors.
     */
    public GraphVisualizer(Map<String, Region> regions) {
        graph = new SingleGraph("Virus Simulation");

        System.setProperty("org.graphstream.ui", "swing");

        // Add regions as nodes with initial styling
        for (String regionName : regions.keySet()) {
            graph.addNode(regionName).setAttribute("ui.label", regionName);
            graph.getNode(regionName).setAttribute("ui.style", "fill-color: red;");
        }

        // Add edges based on neighboring regions with initial styling
        for (Region region : regions.values()) {
            for (Region neighbor : region.getNeighboringRegions().keySet()) {
                addEdge(region.getName(), neighbor.getName());
            }
        }

        // Initialize viewer
        viewer = graph.display();
        viewer.enableAutoLayout();
    }

    /**
     * @brief   Displays the graph.
     *
     * @pre     The viewer is initialized.
     * @post    The graph is displayed.
     */
    public void display() {
        viewer.getDefaultView();
    }

    /**
     * @brief   Adds an edge between two regions in the graph.
     *
     * @param   region1 Name of the first region.
     * @param   region2 Name of the second region.
     *
     * @pre     region1 and region2 are valid region names.
     * @post    Adds an edge between the two regions if it does not already exist.
     */
    public void addEdge(String region1, String region2) {
        String edgeId = region1 + "-" + region2;
        String reverseEdgeId = region2 + "-" + region1;
        if (graph.getEdge(edgeId) == null && graph.getEdge(reverseEdgeId) == null &&
                graph.getNode(region1) != null && graph.getNode(region2) != null) {
            graph.addEdge(edgeId, region1, region2).setAttribute("ui.style", "fill-color: lightpink;");
        }
    }

    /**
     * @brief   Removes an edge between two regions in the graph.
     *
     * @param   region1 Name of the first region.
     * @param   region2 Name of the second region.
     *
     * @pre     region1 and region2 are valid region names.
     * @post    Removes the edge between the two regions if it exists.
     */
    public void removeEdge(String region1, String region2) {
        String edgeId = region1 + "-" + region2;
        String reverseEdgeId = region2 + "-" + region1;
        if (graph.getEdge(edgeId) != null) {
            graph.removeEdge(edgeId);
        } else if (graph.getEdge(reverseEdgeId) != null) {
            graph.removeEdge(reverseEdgeId);
        }
    }

    /**
     * @brief   Updates the graph based on the current state of the simulation.
     *
     * @param   s   Simulator containing the current state of the simulation.
     *
     * @pre     s is not null.
     * @post    Updates the size and color of the nodes based on the number of
     *          infected inhabitants.
     */
    public void updateGraph(Simulator s) {
        Map<Region, List<Virus>> virusRegions = s.getRegionViruses();
        for (Region region : virusRegions.keySet()) {
            List<Virus> viruses = virusRegions.get(region);
            if (viruses != null) {
                int totalInfected = 0;
                for (Virus virus : viruses) {
                    totalInfected += region.getNumInfected(virus);
                }
                org.graphstream.graph.Node node = graph.getNode(region.getName());
                if (node != null) {
                    double size = Math.min(20.0, 5.0 + totalInfected / 100.0); // Example logic for node size
                    node.setAttribute("ui.style", "fill-color: red; size: " + size + "px;");
                }
            }
        }
    }
}
