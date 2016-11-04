package com.codewise.voluum;


import java.util.Random;

public class VoluumRequestUtils
{
    public static String generateRandomName(int length){
        String candidateChars = "0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(candidateChars.charAt(random.nextInt(candidateChars.length())));
        }

        return sb.toString();
    }


}
