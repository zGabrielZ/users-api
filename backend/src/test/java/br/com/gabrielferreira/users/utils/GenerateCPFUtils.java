package br.com.gabrielferreira.users.utils;

import java.util.Random;

public class GenerateCPFUtils {

    private GenerateCPFUtils() {}

    public static String generateCPF() {
        Random random = new Random();

        int[] cpf = new int[11];

        // generate the first 9 digits
        for (int i = 0; i < 9; i++) {
            cpf[i] = random.nextInt(10);
        }

        // calculate the first check digit
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += cpf[i] * (10 - i);
        }

        int remainder = sum % 11;
        cpf[9] = (remainder < 2) ? 0 : 11 - remainder;

        // calculate the second check digit
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += cpf[i] * (11 - i);
        }

        remainder = remainder % 11;
        cpf[10] = (remainder < 2) ? 0 : 11 - remainder;

        // build the CPF string
        StringBuilder cpfGenerated = new StringBuilder();
        for (int i = 0; i < 11; i++) {
            cpfGenerated.append(cpf[i]);
        }

        return cpfGenerated.toString();
    }
}
