package com.example.spring_boot_docker.service;

import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class ReceiptService {

    private PointService pointService;

    /**
     * Created a simple generator that matches the format provided in the
     * example
     *
     * @return Random generated String value
     */
    public String generateValue() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        StringBuilder receiptValue = new StringBuilder();
        Random random = new Random();
        int count = 0;

        while(count < 38){
            if(count == 9 || count == 14 || count == 19 || count == 24){
                receiptValue.append("-");
                count++;
            }
            else if(random.nextInt() % 2 == 0){
                //use random number
                receiptValue.append(random.nextInt(9));
                count++;
            }
            else{
                //use random letter
                char randomLetter = alphabet.charAt(random.nextInt(alphabet.length()));
                receiptValue.append(randomLetter);
                count++;
            }
        }

        return String.valueOf(receiptValue);
    }

}
