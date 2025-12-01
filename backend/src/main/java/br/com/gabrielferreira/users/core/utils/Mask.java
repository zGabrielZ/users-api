package br.com.gabrielferreira.users.core.utils;

import br.com.gabrielferreira.users.domain.enums.DocumentType;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.regex.Pattern;

public class Mask {

    private Mask() {}

    public static final Pattern CNPJ_ONLY_NUMERIC = Pattern.compile(
            "^(\\d{14}|(\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}-\\d{2}))$\n"
    );

    public static final Pattern CPF_ONLY_NUMERIC = Pattern.compile(
            "^\\d{11}$|^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$\n"
    );

    public static boolean isOnlyNumeric(DocumentType type, String documentNumber) {
        if (Objects.isNull(type) || StringUtils.isBlank(documentNumber)) return false;

        if (DocumentType.CNPJ.equals(type)) {
            return CNPJ_ONLY_NUMERIC.matcher(documentNumber).matches();
        }

        return CPF_ONLY_NUMERIC.matcher(documentNumber).matches();
    }

    public static boolean isSequential(String documentNumber) {
        if (StringUtils.isBlank(documentNumber)) return false;

        String documentWithoutMask = documentNumber.replaceAll("\\D", "");
        char firstChar = documentWithoutMask.charAt(0);

        for (int i = 1; i < documentWithoutMask.length(); i++) {
            if (documentWithoutMask.charAt(i) != firstChar) {
                return false;
            }
        }
        return true;
    }

    public static String documentWithoutMask(DocumentType type, String documentNumber) {
        return switch (type) {
            case CNPJ -> documentNumber.replaceAll("\\W", "");
            case CPF -> documentNumber.replaceAll("\\D", "");
            default -> documentNumber;
        };
    }

    public static String formatDocument(DocumentType type, String documentNumber) {
        return switch (type) {
            case CPF -> {
                documentNumber = documentWithoutMask(type, documentNumber);
                yield documentNumber.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
            }
            case CNPJ -> {
                documentNumber = documentWithoutMask(type, documentNumber);
                // TODO: precisaria ver uma solucao pra aceitar cnpj alfanumerico
                yield documentNumber;
            }
            default -> documentNumber;
        };
    }
}
