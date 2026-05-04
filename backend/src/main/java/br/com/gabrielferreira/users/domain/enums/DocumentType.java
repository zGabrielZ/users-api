package br.com.gabrielferreira.users.domain.enums;

public enum DocumentType {
    CPF,
    CNPJ,
    NONE;

    public static boolean isNone(DocumentType documentType) {
        return NONE.equals(documentType);
    }

    public static boolean isCnpj(DocumentType documentType) {
        return CNPJ.equals(documentType);
    }

    public static boolean isCpf(DocumentType documentType) {
        return CPF.equals(documentType);
    }

}
