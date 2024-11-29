package com.fetch.back_end.servive;
import com.fetch.back_end.model.Receipt;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class ReceieptService {
    private final Map<String, Integer> receiptPointsMap = new ConcurrentHashMap<>();

    public String processReceipt(Receipt receipt) {
        String id = UUID.randomUUID().toString();
        int points = calculatePoints(receipt);
        receiptPointsMap.put(id, points);
        return id;
    }

    public Integer getPoints(String id) {
        return receiptPointsMap.get(id);
    }

    private int calculatePoints(Receipt receipt) {
        int points = 0;

        
        points += receipt.getRetailer().replaceAll("[^a-zA-Z0-9]", "").length();

        
        if (receipt.getTotal().matches("\\d+\\.00")) points += 50;

        
        if (Double.parseDouble(receipt.getTotal()) % 0.25 == 0) points += 25;

        
        points += (receipt.getItems().size() / 2) * 5;


        for (Receipt.Item item : receipt.getItems()) {
            int descLength = item.getShortDescription().trim().length();
            if (descLength % 3 == 0) {
                points += Math.ceil(Double.parseDouble(item.getPrice()) * 0.2);
            }
        }

        
        int day = Integer.parseInt(receipt.getPurchaseDate().split("-")[2]);
        if (day % 2 != 0) points += 6;

        
        String[] timeParts = receipt.getPurchaseTime().split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        if (hour == 14 || (hour == 15 && minute == 0)) points += 10;

        return points;
    }
}
