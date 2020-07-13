package io.vanderbeke.glife.infrastructure.basic;

import io.vanderbeke.glife.api.exception.MissingHeaderFileException;
import io.vanderbeke.glife.api.exception.UndefinedPatternFileException;
import io.vanderbeke.glife.api.model.Configuration;
import io.vanderbeke.glife.api.service.Provider;
import io.vanderbeke.glife.core.Constants;
import io.vanderbeke.glife.infrastructure.core.DefaultConfiguration;
import io.vavr.control.Try;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BasicProvider implements Provider {

    private static final Pattern FILE_HEADER_PATTERN = Pattern.compile("\\s*(\\d+)\\s*(\\d+)");

    @Override
    public String name() {
        return "BASIC";
    }

    @Override
    public Try<Configuration> provide(Properties properties) {
        String patternFileName = (String) properties.get(Constants.CONF_PATTERN_FILE_PATH);

        if (Objects.isNull(patternFileName)) {
            return Try.failure(new UndefinedPatternFileException());
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(new File(patternFileName)))) {
            DefaultConfiguration.Builder builder = DefaultConfiguration.aBuilder(properties);
            return provide(builder, reader);
        } catch (Exception e) {
            return Try.failure(e);
        }
    }

    private Try<Configuration> provide(DefaultConfiguration.Builder builder, BufferedReader reader) throws Exception {


        String header = reader.readLine();

        if (Objects.isNull(header)) {
            throw new MissingHeaderFileException();
        }

        Matcher headerMatcher = FILE_HEADER_PATTERN.matcher(header);

        if (!headerMatcher.matches()) {
            throw new MissingHeaderFileException();
        }

        int patternWidth = Integer.parseInt(headerMatcher.group(1));
        int patternHeight = Integer.parseInt(headerMatcher.group(2));
        AtomicInteger cursor = new AtomicInteger(-1);
        // From now, we read cells' states.
        String cells = reader.readLine();

        List<Integer> aliveCells = new ArrayList<>(12);
        while (Objects.nonNull(cells)) {

            aliveCells.addAll(cells.trim().chars()
                    .peek(__ -> cursor.incrementAndGet())
                    .filter(cell -> cell == '*')
                    .map(__ -> cursor.get())
                    .boxed()
                    .collect(Collectors.toList()));

            cells = reader.readLine();
        }

        final int[] space = new int[aliveCells.size()];

        for (int i = 0; i < aliveCells.size(); i++) {
            space[i] = aliveCells.get(i);
        }

        builder.getPattern().setHeight(patternHeight).setWidth(patternWidth).setSpace(space);

        return Try.success(builder.build());
    }
}
