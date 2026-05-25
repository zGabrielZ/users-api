package br.com.gabrielferreira.users.stub.address;

import br.com.gabrielferreira.users.domain.entities.AddressEntity;

public class AddressEntityStub {

    private AddressEntityStub() {}

    public static AddressEntity createAddressEntity() {
        return AddressEntity.builder()
                .id(1L)
                .postalCode("12345-678")
                .street("Street Name")
                .number("123")
                .complement("Apt 456")
                .neighborhood("Neighborhood")
                .city("City")
                .state("State")
                .build();
    }
}
