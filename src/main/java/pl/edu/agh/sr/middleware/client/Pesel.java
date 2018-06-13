package pl.edu.agh.sr.middleware.client;

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
}
