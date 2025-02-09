package com.example.spring_boot_docker.service;

import com.example.spring_boot_docker.entity.ItemEntity;
import com.example.spring_boot_docker.entity.ReceiptEntity;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class used to calculate the points attributed to a receipt
 * @author casandra_torres
 */

@Service
public class PointService {

    //BEGIN Point Calculations - can be easily modified here
    public int alphanumeric = 1;
    public int roundDollarNoCents = 50;
    public int multipleOfQuarter = 25;
    public int everyTwoItemsOnReceipt = 5;
    public double trimmedLenMultOfThree = 0.2;
    public int largeLanguageModel = 5;
    public int oddPurchaseDate = 6;
    public int happyHour = 10;
    //END Point Calculations

    //set the amount here to avoid doing the work again
    BigDecimal totalAmount = null;

    /**
     *
     * Method will call all helper methods to calculate the points for a given receipt
     *
     * @param receipt
     * @param items
     * @return total count or 0
     */
    public Integer countPoints(ReceiptEntity receipt, ItemEntity[] items){

        int count = 0;
        count = countAlphanumeric(receipt.retailer) + handleTotal(receipt.total) + handleItems(receipt, items)
        + largeLanguageModelDetected(totalAmount, items) + oddPurchaseDate(receipt.purchaseDate)
                + happyHour(receipt.purchaseTime);

        return count;
    }

    /**
     * Description:
     *  10 points if purchase time is between 2 and 4 pm (inclusive)
     *
     *  Convert to local time to check time
     * @param purchaseTime
     * @return happyHour or 0
     */
    private Integer happyHour(String purchaseTime) {

        try{
            LocalTime purchaseLocalTime = LocalTime.parse(purchaseTime);
            LocalTime two = LocalTime.parse("14:00");
            LocalTime four = LocalTime.parse("16:00");
            if(two.equals(purchaseLocalTime) || four.equals(purchaseLocalTime)
                    || (purchaseLocalTime.isAfter(two) && purchaseLocalTime.isBefore(four))){
                //equals 2 or 4, between 2 or 4
                return happyHour;
            }
            return 0;
        }
        catch (Exception e){
            return 0;
        }
    }

    /**
     * Description:
     *  6 points if the day in the purchase date is odd
     *
     *  Convert to local date and get day of month as int to check if odd
     *
     * @param purchaseDate
     * @return oddPurchaseDate or 0
     */
    private Integer oddPurchaseDate(String purchaseDate) {

        //First check if format is as expected to avoid errors
        try{
            LocalDate date = LocalDate.parse(purchaseDate);

            //get int value of date to check if odd
            if(date.getDayOfMonth() % 2 == 1){
                return oddPurchaseDate;
            }
            return 0;
        }
        catch(Exception e){
            return 0;
        }
    }

    /**
     * Description:
     *
     * Idea is to use the short description, check for repetitive values that may indicate LLM.
     * Based off format of short description, this will flag if someone tries to get same item twice.
     * This is more helpful if the description was in a sentence format. This is more of
     * a probability.
     *
     * @param totalAmount
     * @param items
     * @return largeLanguageModel or 0
     */
    private Integer largeLanguageModelDetected(BigDecimal totalAmount, ItemEntity[] items) {

        if(totalAmount.intValue() > 10){
            Map<String, String> itemMap = new HashMap<>();

            for (ItemEntity item : items) {
                //check if not empty
                if(item.getShortDescription().isEmpty() || item.getPrice().isEmpty()){
                    continue;
                }
                //add short description to map if not there or map empty
                if(itemMap.isEmpty() || !itemMap.containsKey(item.getShortDescription())){
                    itemMap.put(item.getShortDescription(),item.getPrice());
                }
                //if repetitive values found
                else if(!itemMap.isEmpty() && itemMap.containsKey(item.getShortDescription())){
                    return largeLanguageModel;
                }
            }
        }

        return 0;
    }

    /**
     * Description:
     *  1. 5 points for every two items on the receipt
     *  2. If the trimmed length of the item description is a multiple of 3,
     *   multiply the price by 0.2 and round up to the nearest integer. The result is the number of points earned.
     *
     *   Method will handle all point calculations related to items
     *
     * @param receipt
     * @param items
     * @return count or zero (if any errors)
     */
    private Integer handleItems(ReceiptEntity receipt, ItemEntity[] items) {
        int count = 0;
        try{
            //everyTwoItems in array
            if(items.length > 1){
                count = count + everyTwoItemsOnReceipt * (items.length / 2);
            }

            //get trimmed length of description
            for(ItemEntity item: items){
                if(item.getShortDescription() != null || !item.getShortDescription().isEmpty()){
                    int trimmedLength = item.getShortDescription().trim().length();
                    String regex = "^(\\d{0,9}|\\d{0,9}\\.\\d{2})$";
                    double doubleValue = 0d;
                    if(item.getPrice().matches(regex)){
                        doubleValue = Double.parseDouble(item.getPrice());
                    }

                    //is trimmed length multiple of 3
                    if(trimmedLength % 3 == 0 && doubleValue != 0d){
                        doubleValue = doubleValue * trimmedLenMultOfThree;
                        doubleValue = Math.ceil(doubleValue);
                        count = count + (int) doubleValue;
                    }
                }
            }
        }
        catch (Exception e){
            //any errors with this method will return 0
            return count;
        }
        return count;
    }

    /**
     * Description:
     *  1. 50 points if the total is a round dollar amount with no cents.
     *  2. 25 points if the total is a multiple of 0.25
     *
     * To avoid checking the format of total twice, handle both usages of total here
     *
     * @param total
     * @return count or zero (if any errors)
     */
    private Integer handleTotal(String total) {
        int count = 0;
        String regex = "^(\\d{0,9}|\\d{0,9}\\.\\d{2})$";
        try {
            //check if can convert to BigDecimal
            if (total.matches(regex)) {

                //if format is compatible, convert and set totalAmount variable
                totalAmount = new BigDecimal(total).setScale(2);

                //check if round number
                if(totalAmount.scale() <= 0 || totalAmount.stripTrailingZeros().scale() <= 0){
                    count= count + roundDollarNoCents;
                }

                //check if multiple of a quarter
                BigDecimal quarter = new BigDecimal("0.25");
                BigDecimal remainder = totalAmount.remainder(quarter, new MathContext(2, RoundingMode.UNNECESSARY));

                if(remainder.compareTo(BigDecimal.ZERO) == 0){
                    count = count + multipleOfQuarter;
                }
            }
        }
        catch(Exception e){
            //any exceptions will be counted as 0
            return count;
        }
        return count;
    }

    /**
     * Description: One point for every alphanumeric character in the retailer name
     * use character class to calculate this value
     * @param retailer
     * @return count or zero
     */
    public Integer countAlphanumeric(String retailer) {
        Integer count = 0;

        for (char c : retailer.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                count = count + alphanumeric;
            }
        }
        return count;
    }

}
