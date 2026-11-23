package com.dealership;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserInterface {
    private Dealership dealership;

    public UserInterface() {
    }

    private void init() {
        this.dealership = DealershipFileManager.getDealership();
    }

    public void display()  {
      try{
          init();
          int selectedOption;
          printLineSlowly("Welcome to D & B car dealership!", 50);
          do {
              printLineSlowly("\n• loading...", 50);
              Thread.sleep(600);
              System.out.println("""
                    \n---------------------------------------
                                 ⟨ MAIN MENU ⟩
                    ---------------------------------------""");
              Thread.sleep(400);
              selectedOption = Console.promptForInt("""
                    [1]  Find vehicles within a price range
                    [2]  Find vehicles by make / model
                    [3]  Find vehicles by year range
                    [4]  Find vehicles by color
                    [5]  Find vehicles by mileage range
                    [6]  Find vehicles by type
                    [7]  View all vehicles
                    [8]  Add a vehicle
                    [9]  Remove a vehicle
                    [10] Lease a vehicle
                    [11] Buy a vehicle
                    [0]  Quit
                    
                    › Select an option [0-11]: """);

              switch (selectedOption) {
                  case 1 -> processGetByPriceRequest();
                  case 2 -> processGetByMakeModelRequest();
                  case 3 -> processGetByYearRequest();
                  case 4 -> processGetByColorRequest();
                  case 5 -> processGetByMileageRequest();
                  case 6 -> processGetByVehicleTypeRequest();
                  case 7 -> processGetAllVehiclesRequest();
                  case 8 -> processAddVehicleRequest();
                  case 9 -> processRemoveVehicleRequest();
                  case 10 -> processLeaseVehicle();
                  case 11 -> processBuyVehicle();
                  case 0 -> System.out.println("\nHave a good day!");
                  default -> System.out.println("\nInvalid command, please try again.");
              }
          } while (selectedOption != 0);


      } catch (Exception e) {
          System.out.println("There was a fatal error and we had to quit.");
          System.out.println("Details: " + e.getMessage());
      }



    }

    // Helper method – displays all vehicles from selected list
    private void displayVehicles(List<Vehicle> selectedVehicles) {

        System.out.println("""
                \n╔═══════╦══════╦═══════════════╦════════════════╦══════════╦═══════════╦════════════╦═════════════╗
                ║ vin   ║ year ║ make          ║ model          ║ type     ║ color     ║   odometer ║       price ║
                ╠═══════╬══════╬═══════════════╬════════════════╬══════════╬═══════════╬════════════╬═════════════╣""");

        for (Vehicle v : selectedVehicles) {
            System.out.println(v);
        }
    }

    public void processGetByPriceRequest() {

        // user input
        BigDecimal min = Console.promptForBigDecimal("› Enter minimum price range (press Enter↵ to skip): ");
        BigDecimal max = Console.promptForBigDecimal("› Enter maximum price range (press Enter↵ to skip): ");

        ArrayList<Vehicle> result = dealership.getVehiclesByPrice(min, max);
        displayVehicles(result);
    }

    public void processGetByMakeModelRequest() {

        // user input
        String make;
        do {
            make = Console.promptForString("› Enter make: ").trim().toLowerCase();
        } while (make.isEmpty());
        String model = Console.promptForString("› Enter model (press Enter↵ to skip): ").trim().toLowerCase();

        ArrayList<Vehicle> result = dealership.getVehiclesByMakeModel(make, model);
        displayVehicles(result);
    }

    public void processGetByYearRequest() {

        // user input
        int min;
        int max;
        min = Console.promptForInt("› Enter start of year range (press Enter↵ to skip): ");
        max = Console.promptForInt("› Enter end of year range (press Enter↵ to skip): ");

        ArrayList<Vehicle> result = dealership.getVehiclesByYear(min, max);
        displayVehicles(result);
    }

    public void processGetByColorRequest() {
        // user input
        String color = Console.promptForString("› Enter color: ").trim().toLowerCase();

        List<Vehicle> result = dealership.getVehiclesByColor(color);
        displayVehicles(result);
    }

    public void processGetByMileageRequest() {
        // user input
        int min;
        int max;
        min = Console.promptForInt("› Enter minimum mileage (press Enter↵ to skip): ");
        max = Console.promptForInt("› Enter maximum mileage (press Enter↵ to skip): ");

        ArrayList<Vehicle> result = dealership.getVehiclesByMileage(min, max);
        displayVehicles(result);
    }

    public void processGetByVehicleTypeRequest() {
        // user input
        String vehicleType = Console.promptForString("› Enter type (car/SUV/coupe/truck/sedan/van): ").trim().toLowerCase();

        ArrayList<Vehicle> result = dealership.getVehiclesByType(vehicleType);
        displayVehicles(result);
    }

    public void processGetAllVehiclesRequest() {
        ArrayList<Vehicle> allVehicles = this.dealership.getAllVehicles();
        displayVehicles(allVehicles);
    }

    public void processAddVehicleRequest() {
        // user input
        int vin = processPromptForInt("› Enter VIN (vehicle identification number): ");
        int year = processPromptForYear();
        String make = processPromptForString("› Enter vehicle make: ");
        String model = processPromptForString("› Enter vehicle model: ");
        String vehicleType = processPromptForString("› Enter vehicle type: ");
        String color = processPromptForString("› Enter vehicle color: ");
        int odometer = processPromptForOdometer();
        BigDecimal price = processPromptForPrice();
        // create new vehicle
        Vehicle v = new Vehicle(vin, year, make, model, vehicleType, color, odometer, price);
        // add it to inventory
        dealership.addVehicle(v);
        // save file
        DealershipFileManager.saveDealership(dealership);
        // success message
        System.out.println("\n✓ Vehicle is successfully added to inventory.");
    }

    public void processRemoveVehicleRequest() {
        // User input
        int vin = Console.promptForInt("\n› Enter VIN of vehicle to remove: ");
        // Remove from inventory
        boolean isFound = dealership.removeVehicle(vin);
        // Prints a message and saves file based on whether match was found
        if (isFound) {
            DealershipFileManager.saveDealership(dealership); // Save file
            System.out.println("✓ Vehicle is successfully removed from inventory.");
        } else {
            System.out.println("This vehicle doesn't exist.");
        }
    }

    private void processLeaseVehicle() {
        Vehicle foundVehicle;
        int vin;
        do { // Prompt for vin until vehicle is found
            vin = processPromptForInt("› Enter VIN (vehicle identification number): "); // Get user input
            foundVehicle = dealership.getVehicleByVin(vin); // Check inventory for vehicle with entered vin
            if (foundVehicle == null) {
                vin = processPromptForInt("""
                        \nVehicle not found.
                        › Try again: """);
            }
        } while (foundVehicle == null);
        String date = String.valueOf(LocalDate.now().getDayOfYear());
        String name = processPromptForString("› Enter your name: ");
        String email = processPromptForString("› Enter your email: ");
        // Create lease contract
        LeaseContract contract = new LeaseContract(date, name, email, foundVehicle);
        confirmContract(dealership, contract, vin);
    }

    private void processBuyVehicle() throws InterruptedException, IOException {
        Vehicle foundVehicle;
        int vin;
        do { // Prompt for vin until vehicle is found
            vin = processPromptForInt("› Enter VIN (vehicle identification number): "); // Get user input
            foundVehicle = dealership.getVehicleByVin(vin); // Check inventory for vehicle with entered vin

            if (foundVehicle == null) {
                vin = processPromptForInt("""
                        \nVehicle not found.
                        › Try again: """);
            }
        } while (foundVehicle == null);
        String date = String.valueOf(LocalDate.now().getDayOfYear());
        String name = processPromptForString("› Enter your name: ");
        String email = processPromptForString("› Enter your email: ");
        boolean isFinanced = processPromptForFinancing(); // Do you want to finance a purchase?

        // Create sales contract
        SalesContract contract = new SalesContract(date, name, email, foundVehicle, isFinanced);
        confirmContract(dealership, contract, vin);
    }

    public int processPromptForInt(String prompt) {
        int vin = Console.promptForInt(prompt);
        while (vin == 0) {
            vin = Console.promptForInt("""
                    \nField cannot be empty.
                    › Try again: """);
        }
        return vin;
    }

    public int processPromptForYear() {
        int year = 0;
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        year = Console.promptForInt("› Enter model year: ");
        while (year < 1900 || year > currentYear) {
            year = Console.promptForInt("""
                    \nModel year should be between 1900 - """ + currentYear +
                    """
                            › Try again: """);
        }
        return year;
    }

    public int processPromptForOdometer() {
        int odometer = 0;
        odometer = Console.promptForInt("› Enter mileage: ");
        while (odometer < 0) {
            odometer = Console.promptForInt("""
                    \nField cannot be empty.
                    › Enter mileage: """);
        }
        return odometer;
    }

    public BigDecimal processPromptForPrice() {
        BigDecimal price = BigDecimal.ZERO;
        price = Console.promptForBigDecimal("› Enter price: ");
        while (price.compareTo(BigDecimal.ZERO) == 0) {
            price = Console.promptForBigDecimal("""
                    \nField cannot be empty.
                    › Try again: """);
        }
        price = price.setScale(2, RoundingMode.HALF_UP);
        return price;
    }

    // Prompts user for string values
    public String processPromptForString(String prompt) {
        String input = "";
        input = Console.promptForString(prompt);
        while (input.isEmpty()) {
            input = Console.promptForString("""
                    \nField cannot be empty.
                    › Try again: """);
        }
        return input;
    }

    // Prints text letter by letter
    public static void printLineSlowly(String text, int delay) {
        for (int i = 0; i < text.length(); i++) {
            System.out.print(text.charAt(i));
            System.out.flush();
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                System.out.println("Something went wrong.");
                break;
            }
        }
        System.out.println();
    }

    // Prompts user for yes/no to get isFinanced for SalesContract
    public boolean processPromptForFinancing() {
        boolean isFinanced = false;
        int financing;
        do {
            financing = processPromptForInt("""
                    \nWe are offering financing:
                    * 4.25% interest for 48 months if the price is $10,000 or more;
                    * 5.25% interest for 24 months if the price is over $10,000)
                    
                    Do you want to finance your purchase?
                    [1] yes
                    [2] no
                    
                    › Select an option [1-2]: """);
            switch (financing) {
                case 1 -> isFinanced = true;
                case 2 -> isFinanced = false;
                default -> System.out.println("› Enter [1] or [2]: ");
            }
        } while ((financing != 1) && (financing != 2));
        return isFinanced;
    }

    // Displays contract info and prompts to confirm or cancel
    public static void confirmContract(Dealership d, Contract c, int vin) {
        try {
            // Display contract and confirm
            System.out.println("\nReview and sign your purchase contract: ");
            printLineSlowly("\n• loading...", 100);
            System.out.println(c);
            Thread.sleep(800);

            int selectedOption;
            do {
                selectedOption = Console.promptForInt("""
                        \n[1] Confirm and sign
                        [2] Cancel
                        
                        › Select an option [1-2]: """);
                switch (selectedOption) {
                    case 1 -> {
                        d.removeVehicle(vin); // Remove found vehicle from inventory
                        DealershipFileManager.saveDealership(d); // Save inventory.csv file
                        d.addContract(c); // Add contract to contracts array list
                        ContractDataManager.saveContract(c); // Save contract to contracts.csv file
                        System.out.println("\n✓ Vehicle is yours!");
                    }
                    case 2 -> {} // Return to main menu
                    default -> System.out.println("\nInvalid command, please try again.");
                }
            } while ((selectedOption != 1) && (selectedOption != 2));
        } catch (Exception e) {
            System.out.println("Something went wrong. Please try again.");
        }
    }
}
