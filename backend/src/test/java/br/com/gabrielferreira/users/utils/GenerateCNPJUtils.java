package br.com.gabrielferreira.users.utils;

import java.util.Random;

public class GenerateCNPJUtils {

    private GenerateCNPJUtils() {}

    public static String generateCNPJ() {
        Random random = new Random();

        int[] cnpj = new int[14];

        // Generate the first 8 digits (company root)
        for (int i = 0; i < 8; i++) {
            cnpj[i] = random.nextInt(10);
        }

        // Default branch = 0001
        cnpj[8] = 0;
        cnpj[9] = 0;
        cnpj[10] = 0;
        cnpj[11] = 1;

        // First check digit
        cnpj[12] = calculateCheckDigit(cnpj, 12);

        // Second check digit
        cnpj[13] = calculateCheckDigit(cnpj, 13);

        StringBuilder result = new StringBuilder();

        for (int digit : cnpj) {
            result.append(digit);
        }

        return result.toString();
    }

    private static int calculateCheckDigit(int[] cnpj, int length) {
        int[] firstWeights = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] secondWeights = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int sum = 0;
        int[] weights = length == 12 ? firstWeights : secondWeights;

        for (int i = 0; i < length; i++) {
            sum += cnpj[i] * weights[i];
        }

        int remainder = sum % 11;

        return remainder < 2 ? 0 : 11 - remainder;
    }
}
