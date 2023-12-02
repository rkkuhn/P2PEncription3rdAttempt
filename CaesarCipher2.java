public class CaesarCipher {
    public static final int SHIFT = 3; // Shift value for encryption/decryption

    public static String encrypt(String message) {
        StringBuilder encryptedMessage = new StringBuilder();

        for (char c : message.toCharArray()) {
            if (Character.isLetter(c)) {
                char shiftedChar = (char) (c + SHIFT);
                if ((Character.isLowerCase(c) && shiftedChar > 'z') ||
                    (Character.isUpperCase(c) && shiftedChar > 'Z')) {
                    shiftedChar -= 26; // Wrap around the alphabet
                }
                encryptedMessage.append(shiftedChar);
            } else {
                encryptedMessage.append(c); // Keep non-letter characters unchanged
            }
        }

        return encryptedMessage.toString();
    }

    public static String decryptWithShift(String encryptedMessage, int shift) {
        StringBuilder decryptedMessage = new StringBuilder();

        for (char c : encryptedMessage.toCharArray()) {
            if (Character.isLetter(c)) {
                char shiftedChar = (char) (c - shift);
                if ((Character.isLowerCase(c) && shiftedChar < 'a') ||
                    (Character.isUpperCase(c) && shiftedChar < 'A')) {
                    shiftedChar += 26; // Wrap around the alphabet
                }
                decryptedMessage.append(shiftedChar);
            } else {
                decryptedMessage.append(c); // Keep non-letter characters unchanged
            }
        }

        return decryptedMessage.toString();
    }
}
