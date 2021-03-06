package ru.novikov.library.jsonDiff.data;

import lombok.Builder;
import lombok.Getter;

/**
 * @author novikov_vi on 03.07.2020
 */
@Builder
@Getter
public final class DiffDataDto {
    private final String operation;
    private final String path;
    private final Object value;
    private final Object oldValue;
}
