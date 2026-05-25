package br.com.gabrielferreira.users.stub.contact;

import br.com.gabrielferreira.users.domain.entities.ContactEntity;

public class ContactEntityStub {

    private ContactEntityStub() {}

    public static ContactEntity createContactEntity() {
        return ContactEntity.builder()
                .id(1L)
                .name("Contact Name")
                .ddiPhone("+55")
                .dddPhone("11")
                .phoneNumber("12345678")
                .ddiMobile("+55")
                .dddMobile("11")
                .mobileNumber("987654321")
                .build();
    }
}
