import javax.swing.*;
import java.awt.*;

/**
 * Main Application Launching point for the GUI MVP.
 */
public class RentalSystemGUIApp {
    public static void main(String[] args) {
        // Thread security handler for running desktop GUI instances
        SwingUtilities.invokeLater(() -> {
            new RentalMainFrame().setVisible(true);
        });
    }
}

/**
 * Primary Windows Frame constructing interface components and event handling.
 */
class RentalMainFrame extends JFrame { // Restored to original structure
    // Polymorphic Array Container to hold all our vehicles
    private Vehicle[] fleet;
    
    // GUI Window Components
    private JTextArea displayArea;
    private JTextField daysInput;
    private JCheckBox peakCheckBox;
    private JComboBox<String> vehicleSelectDropdown; // Dropdown to select a vehicle

    public RentalMainFrame() {
        // Instantiating Subclass Objects into Superclass Array Slots
        fleet = new Vehicle[3];
        fleet[0] = new Car("C001", "Tesla Model 3", 120.0, true);   // Luxury Car
        fleet[1] = new Car("C002", "Toyota Corolla", 50.0);          // Standard Car
        fleet[2] = new Motorcycle("M001", "Harley Davidson", 70.0, 1200); // Heavy Motorcycle

        // Window configuration settings
        setTitle("BITS College - Vehicle Rental System MVP");
        setSize(750, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centers window on screen
        setLayout(new BorderLayout(10, 10));

        // Typography Setup 
        Font interfaceFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font outputFont = new Font("Monospaced", Font.PLAIN, 13);

        // --- Layout Region 1: Top Input / Interactive Control Panel ---
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Simulation Control Panel"));

        JLabel daysLabel = new JLabel("Days:");
        daysLabel.setFont(interfaceFont);
        daysInput = new JTextField("5", 3);
        daysInput.setFont(interfaceFont);

        peakCheckBox = new JCheckBox("Peak Season");
        peakCheckBox.setFont(interfaceFont);

        JButton processButton = new JButton("Calculate Costs");
        processButton.setFont(interfaceFont);
        
        JButton inventoryButton = new JButton("Check Active Inventory");
        inventoryButton.setFont(interfaceFont);

        // Append to top panel
        inputPanel.add(daysLabel);
        inputPanel.add(daysInput);
        inputPanel.add(peakCheckBox);
        inputPanel.add(processButton);
        inputPanel.add(inventoryButton);

        // --- NEW FEATURE: Layout Region 2: Renting Operations Panel ---
        JPanel rentalActionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        rentalActionPanel.setBorder(BorderFactory.createTitledBorder("Rental Operations"));

        JLabel selectLabel = new JLabel("Select Vehicle:");
        selectLabel.setFont(interfaceFont);

        // Create dropdown items using vehicle models
        String[] dropdownItems = {
            fleet[0].getModel() + " (C001)",
            fleet[1].getModel() + " (C002)",
            fleet[2].getModel() + " (M001)"
        };
        vehicleSelectDropdown = new JComboBox<>(dropdownItems);
        vehicleSelectDropdown.setFont(interfaceFont);

        JButton rentButton = new JButton("Mark as Rented");
        rentButton.setFont(interfaceFont);
        rentButton.setBackground(new Color(220, 53, 69)); // Reddish color for action
        rentButton.setForeground(Color.WHITE);

        JButton returnButton = new JButton("Mark as Available/Returned");
        returnButton.setFont(interfaceFont);
        returnButton.setBackground(new Color(40, 167, 69)); // Greenish color for action
        returnButton.setForeground(Color.WHITE);

        rentalActionPanel.add(selectLabel);
        rentalActionPanel.add(vehicleSelectDropdown);
        rentalActionPanel.add(rentButton);
        rentalActionPanel.add(returnButton);

        // Combine Top Control Panel and Rental Action Panel into a single upper area
        JPanel combinedTopPanel = new JPanel(new GridLayout(2, 1));
        combinedTopPanel.add(inputPanel);
        combinedTopPanel.add(rentalActionPanel);

        // --- Layout Region 3: Central Text Presentation Window ---
        displayArea = new JTextArea();
        displayArea.setEditable(false); // Protect output from user modifications
        displayArea.setFont(outputFont);
        displayArea.setBackground(new Color(248, 249, 250));
        displayArea.setMargin(new Insets(12, 12, 12, 12));
        JScrollPane scrollPane = new JScrollPane(displayArea);

        // Mount main regions to base Frame container
        add(combinedTopPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- Event Listeners Mapping Actions to Graphical Triggers ---
        processButton.addActionListener(e -> runFleetSimulation());
        inventoryButton.addActionListener(e -> displayStaticInventoryState());
        
        // Connect the new interactive button actions
        rentButton.addActionListener(e -> toggleRentalStatus(true));
        returnButton.addActionListener(e -> toggleRentalStatus(false));

        // Print initial welcome text
        displayOverview();
    }

    private void displayOverview() {
        displayArea.setText("====================================================================\n");
        displayArea.append("       WELCOME TO THE BITS COLLEGE VEHICLE RENTAL SYSTEM GUI        \n");
        displayArea.append("====================================================================\n");
        displayArea.append("Current Fleet Status:\n\n");
        for (Vehicle v : fleet) {
            displayArea.append(" • " + v.getDetails() + "\n");
        }
    }

    private void displayStaticInventoryState() {
        displayArea.setText("=== SYSTEM MEMORY RECORD ===\n\n");
        displayArea.append("Total allocated active Vehicle instances inside system: " 
                + Vehicle.getTotalVehiclesCount() + " records currently open.\n");
    }

    /**
     * Handles updating the vehicle's rented status based on dropdown selection.
     */
    private void toggleRentalStatus(boolean rentMe) {
        int selectedIndex = vehicleSelectDropdown.getSelectedIndex();
        Vehicle selectedVehicle = fleet[selectedIndex];

        if (rentMe) {
            if (selectedVehicle.isRented()) {
                JOptionPane.showMessageDialog(this, 
                        selectedVehicle.getModel() + " is already marked as RENTED!", 
                        "Status Warning", JOptionPane.WARNING_MESSAGE);
            } else {
                selectedVehicle.setRented(true); // ENCAPSULATION: setting the private state
                JOptionPane.showMessageDialog(this, 
                        selectedVehicle.getModel() + " has been successfully rented out!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            if (!selectedVehicle.isRented()) {
                JOptionPane.showMessageDialog(this, 
                        selectedVehicle.getModel() + " is already marked as AVAILABLE!", 
                        "Status Warning", JOptionPane.WARNING_MESSAGE);
            } else {
                selectedVehicle.setRented(false); // ENCAPSULATION: setting the private state
                JOptionPane.showMessageDialog(this, 
                        selectedVehicle.getModel() + " has been returned and is now available.", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        // Automatically refresh view to show the new status update immediately
        runFleetSimulation(); 
    }

    /**
     * Loops through tracking registry via upcast layers applying calculation rules.
     */
    private void runFleetSimulation() {
        try {
            int inputDays = Integer.parseInt(daysInput.getText().trim());
            boolean isPeakActive = peakCheckBox.isSelected();

            displayArea.setText(""); // Clear area
            displayArea.append("====================================================================\n");
            displayArea.append("            POLYMORPHIC FLEET ENGINE EXECUTION RESULTS             \n");
            displayArea.append("====================================================================\n");

            // Dynamic Method Dispatch (Runtime Polymorphism)
            for (Vehicle currentVehicle : fleet) {
                displayArea.append(currentVehicle.getDetails() + "\n");

                // Compile-time Polymorphism (Method Overloading)
                double baselineCost = currentVehicle.calculateRentalCost(inputDays);
                double totalCost = currentVehicle.calculateRentalCost(inputDays, isPeakActive);

                displayArea.append(String.format("  > Standard Price Schedule (%d Days): $%.2f\n", inputDays, baselineCost));
                if (isPeakActive) {
                    displayArea.append(String.format("  > Active Season Surge Total Rate  : $%.2f\n", totalCost));
                }

                // Safe Structural Verifications & Downcasting
                if (currentVehicle instanceof Car) {
                    Car carInstance = (Car) currentVehicle;
                    displayArea.append("  [Unique Car Attribute] Luxury Tier: " 
                            + (carInstance.isLuxury() ? "Premium Class Asset" : "Standard Fleet Item") + "\n");
                } else if (currentVehicle instanceof Motorcycle) {
                    Motorcycle motorcycleInstance = (Motorcycle) currentVehicle;
                    displayArea.append("  [Unique Moto Attribute] Motor Displacement: " 
                            + motorcycleInstance.getEngineCc() + "cc displacement\n");
                }
                displayArea.append("--------------------------------------------------------------------\n");
            }
        } catch (NumberFormatException errorObj) {
            JOptionPane.showMessageDialog(this, 
                    "Invalid Day value! Please type in a positive integer number.", 
                    "Input Form Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

// =========================================================================
// CORE OBJECT-ORIENTED STRUCTURES ARCHITECTURE (V1.0 - V4.0)
// =========================================================================

class Vehicle { // Restored to original structure
    private String vehicleId;
    private String model;
    private double baseRatePerDay;
    private boolean isRented = false; // NEW STATE FIELD: Tracks rental condition
    
    private static int totalVehiclesCreated = 0;

    public Vehicle(String vehicleId, String model, double baseRatePerDay) {
        setVehicleId(vehicleId);
        setModel(model);
        setBaseRatePerDay(baseRatePerDay);
        totalVehiclesCreated++;
    }

    public static int getTotalVehiclesCount() {
        return totalVehiclesCreated;
    }

    // --- ENCAPSULATION: Getters & Setters for the new isRented state ---
    public boolean isRented() { return isRented; }
    public void setRented(boolean rented) { this.isRented = rented; }

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) {
        this.vehicleId = (vehicleId == null || vehicleId.trim().isEmpty()) ? "ERR-ID" : vehicleId;
    }

    public String getModel() { return model; }
    public void setModel(String model) {
        this.model = (model == null || model.trim().isEmpty()) ? "Default Model Blueprint" : model;
    }

    public double getBaseRatePerDay() { return baseRatePerDay; }
    public void setBaseRatePerDay(double baseRatePerDay) {
        this.baseRatePerDay = (baseRatePerDay > 0) ? baseRatePerDay : 35.0;
    }

    /**
     * Updated details method to clearly append the runtime availability state tags.
     */
    public String getDetails() {
        String availabilityStatus = isRented ? "[STATUS: RENTED]" : "[STATUS: AVAILABLE]";
        return availabilityStatus + " ID: " + vehicleId + " | Model: " + model + " | Rate: $" + baseRatePerDay + "/day";
    }

    // Method Overloading - Version Alpha
    public double calculateRentalCost(int days) {
        return this.baseRatePerDay * days;
    }

    // Method Overloading - Version Beta
    public double calculateRentalCost(int days, boolean isPeakSeason) {
        double calculatedTotal = calculateRentalCost(days);
        if (isPeakSeason) {
            calculatedTotal += (20.0 * days);
        }
        return calculatedTotal;
    }
}

class Car extends Vehicle { // Restored to original structure
    private boolean isLuxury;

    public Car(String vehicleId, String model, double baseRatePerDay, boolean isLuxury) {
        super(vehicleId, model, baseRatePerDay);
        this.isLuxury = isLuxury;
    }

    public Car(String vehicleId, String model, double baseRatePerDay) {
        this(vehicleId, model, baseRatePerDay, false);
    }

    public boolean isLuxury() { return isLuxury; }
    public void setLuxury(boolean luxury) { this.isLuxury = luxury; }

    @Override
    public String getDetails() {
        return super.getDetails() + " [Car]";
    }

    @Override
    public double calculateRentalCost(int days) {
        double derivedCost = super.calculateRentalCost(days);
        if (isLuxury) {
            derivedCost += (45.0 * days);
        }
        return derivedCost;
    }
}

class Motorcycle extends Vehicle { // Restored to original structure
    private int engineCc;

    public Motorcycle(String vehicleId, String model, double baseRatePerDay, int engineCc) {
        super(vehicleId, model, baseRatePerDay);
        setEngineCc(engineCc);
    }

    public int getEngineCc() { return engineCc; }
    public void setEngineCc(int engineCc) {
        this.engineCc = (engineCc > 0) ? engineCc : 150;
    }

    @Override
    public String getDetails() {
        return super.getDetails() + " [Motorcycle]";
    }

    @Override
    public double calculateRentalCost(int days) {
        double derivedCost = super.calculateRentalCost(days);
        if (days > 4) {
            derivedCost *= 0.85;
        }
        return derivedCost;
    }
}
