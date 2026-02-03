package com.example.bankcards.util;

public class CardNumberMasker {

    private CardNumberMasker() {
    }

    public static String mask(String fullNumber) {
        if (fullNumber == null || fullNumber.length() < 4) {
            return "****";
        }
        String last4 = fullNumber.substring(fullNumber.length() - 4);
        return "**** **** **** " + last4;
    }
}
