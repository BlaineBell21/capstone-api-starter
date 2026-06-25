package org.yearup.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yearup.models.Order;
import org.yearup.models.OrderItem;
import org.yearup.utils.DateUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
public class ReceiptService {

    @Transactional
    public String generateReceipt(Order order){
        StringBuilder receipt = new StringBuilder();

        BigDecimal subtotal = BigDecimal.ZERO;

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MMMM dd, yyyy  hh:mm a");

        receipt.append("============================================================\n");
        receipt.append("                    MOONBEAM MARKET\n");
        receipt.append("             Games • Gear • Digital Treasures\n");
        receipt.append("============================================================\n\n");

        receipt.append(String.format("Order #:        MM-%06d%n", order.getOrderId()));
        receipt.append(String.format("Date:           %s%n",
                order.getDate().format(formatter)));
        receipt.append(String.format("Customer ID:    %d%n%n",
                order.getUserId()));

        receipt.append("Shipping Address\n");
        receipt.append("------------------------------------------------------------\n");

        receipt.append(order.getAddress()).append("\n");
        receipt.append(String.format("%s, %s %s%n%n",
                order.getCity(),
                order.getState(),
                order.getZip()));

        receipt.append("Items Purchased\n");
        receipt.append("------------------------------------------------------------\n\n");

        for (OrderItem item : order.getItems()) {

            BigDecimal lineTotal =
                    item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

            subtotal = subtotal.add(lineTotal);

            receipt.append(item.getProductName()).append("\n");

            receipt.append(
                    String.format(
                            "   Qty: %-3d @ $%-8.2f $%8.2f%n%n",
                            item.getQuantity(),
                            item.getPrice(),
                            lineTotal
                    )
            );
        }

        receipt.append("------------------------------------------------------------\n");

        receipt.append(String.format("%-40s $%10.2f%n",
                "Subtotal:",
                subtotal));

        receipt.append(String.format("%-40s $%10.2f%n",
                "Shipping:",
                order.getShippingAmount()));

        receipt.append("------------------------------------------------------------\n");

        receipt.append(String.format("%-40s $%10.2f%n%n",
                "Order Total:",
                order.getTotal()));

        receipt.append("============================================================\n");
        receipt.append("Thank you for shopping at Moonbeam Market!\n");
        receipt.append("May your next adventure be among the stars.\n");
        receipt.append("============================================================");

        return receipt.toString();
    }

    @Transactional
    public void saveReceipt(Order order){
        String receipt = generateReceipt(order);

        // writes new receipts to file
        String fileName = order.getOrderId() + "-" + DateUtils.currentDateAndTimeString() + ".txt";
        // file name gets created in "yyyyMMdd-hhmmss.txt" format
        File folder = new File("receipts");
        // paths to the receipt file in the data file
        if(!folder.exists()){
            // informs user that directory is missing if not found
            System.out.println( "Missing directory: "+ folder.getName());
            if (folder.mkdirs()){
                // creates missing directories
                System.out.println("Directories created");
            } else {
                System.out.println("Failed to create receipts folder.");
            }
        }

        File file = new File(folder, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(receipt);
            // writes order to new receipt txt file

        } catch(IOException e){
            throw new RuntimeException("Unable to write to directory.");
        }
        System.out.println("Receipt created");
    }
}
