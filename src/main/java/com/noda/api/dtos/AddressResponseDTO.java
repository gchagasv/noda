package com.noda.api.dtos;

import com.noda.api.models.Address;

public record AddressResponseDTO(
        String cep,
        String street,
        String number,
        String city,
        String state,
        String neighborhood,
        String complement
) {

    public AddressResponseDTO(Address address) {
        this(
                address.getCep(),
                address.getStreet(),
                address.getNumber(),
                address.getCity(),
                address.getState(),
                address.getNeighborhood(),
                address.getComplement()
        );
    }
}