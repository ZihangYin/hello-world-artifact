package com.pepsi.rest.commons;

import java.util.UUID;

public class UUIDGenerator {

    public static UUID randomUUID() {
        String secureRandom = UUID.randomUUID().toString();
        return UUID.fromString(UUID.nameUUIDFromBytes(secureRandom.getBytes()).toString());
    }
}
