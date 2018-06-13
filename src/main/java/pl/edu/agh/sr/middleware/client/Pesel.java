package pl.edu.agh.sr.middleware.client;

import com.google.common.base.Objects;

import java.util.Arrays;

public class Pesel {
    private byte PESEL[];

    public Pesel(String pesel) {
        PESEL = new byte[11];
        for (int i = 0; i < pesel.length(); i++) {
            PESEL[i] = Byte.parseByte(String.valueOf(pesel.charAt(i)));
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (byte digit : PESEL) {
            builder.append(digit);
        }

        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pesel)) return false;

        Pesel pesel = (Pesel) o;

        return Arrays.equals(PESEL, pesel.PESEL);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(PESEL);
    }
}
