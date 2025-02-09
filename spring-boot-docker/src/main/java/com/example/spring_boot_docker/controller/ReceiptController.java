package com.example.spring_boot_docker.controller;

import com.example.spring_boot_docker.entity.ReceiptEntity;
import com.example.spring_boot_docker.service.PointService;
import com.example.spring_boot_docker.service.ReceiptService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@RestController
public class ReceiptController {

    private ReceiptService receiptService;

    private PointService pointService;

    /**
     * On the post request, I calculate the points for a receipt and store them in a
     * map that can be called by the get request.
     */
    private static Map<String, String> receiptMap = new HashMap<>();

    @Autowired
    public ReceiptController(ReceiptService receiptService, PointService pointService){
        this.receiptService = receiptService;
        this.pointService = pointService;
    }

    /**
     *  description: Returns the points awarded for the receipt.
     *  pattern: "^\\S+$"
     *
     * @param id
     * @return pointsAwardedToReceipt
     */
    @GetMapping("receipts/{id}/points")
    public String getPoints(@NonNull @RequestParam String id){
        String regex = "^\\S+$";

        if(id.matches(regex) && receiptMap.get(id) != null && !receiptMap.isEmpty()){
            return "points: " + receiptMap.get(id);
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "#/components/responses/NotFound");
    }

    /**
     *   description: Submits a receipt for processing.
     *     required: retailer, purchaseDate, purchaseTime, items, total
     *
     * @param receiptEntered
     * @return String - Example { "id": "7fb1377b-b223-49d9-a31a-5a02701dd310" }
     */
    @PostMapping("receipts/process")
    public String processReceipt(@RequestBody @NonNull ReceiptEntity receiptEntered, HttpServletRequest request) throws IOException {

        if(receiptEntered.retailer==null||receiptEntered.purchaseDate==null
                || receiptEntered.purchaseTime==null||receiptEntered.items==null||receiptEntered.total==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "#/components/responses/BadRequest");
        }

        String idValue=receiptService.generateValue(); //generates an ID

        Integer pointValue= pointService.countPoints(receiptEntered, receiptEntered.items); //calculates points

        receiptMap.put(idValue, Integer.toString(pointValue)); //stores the values

        return "id: " + idValue; //returns just the ID
    }

}
