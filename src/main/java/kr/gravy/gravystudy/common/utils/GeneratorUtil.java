package kr.gravy.gravystudy.common.utils;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class GeneratorUtil {

    public UUID generatePublicId() {
        return UUID.randomUUID();
    }
}
