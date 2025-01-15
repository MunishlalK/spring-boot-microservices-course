package com.bookstorelabs.orders.domain.models;

import jakarta.validation.constraints.NotBlank;

public record Address(
        @NotBlank(message = "AddressLine1 is required") String addressLine1,
        String addressLine2,
        @NotBlank(message = "City is required") String city,
        @NotBlank(message = "State is required") String state,
        @NotBlank(message = "zipcode is required") String zipCode,
        @NotBlank(message = "country is required") String country) {}
