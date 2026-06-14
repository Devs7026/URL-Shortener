package com.dev.urlshortener.util;

import org.springframework.stereotype.Component;

@Component
public class Base62Converter {

    private static final String ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = ALLOWED_CHARACTERS.length();

    //ENCODING : Base10 -> Base62
    public String encode(long input) {
        StringBuilder encodedString = new StringBuilder();

        if (input == 0) {
            return String.valueOf(ALLOWED_CHARACTERS.charAt(0));
        }

        while (input > 0) {
            int remainder = (int) (input % BASE);
            encodedString.append(ALLOWED_CHARACTERS.charAt(remainder));
            input /= BASE;
        }
        return encodedString.reverse().toString();
    }

    //DECODING : Base62 -> Base10
    public long decode(String input) {
        long decoded = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            int value = ALLOWED_CHARACTERS.indexOf(c);
            decoded = (decoded * BASE) + value;
        }

        return decoded;
    }
}
