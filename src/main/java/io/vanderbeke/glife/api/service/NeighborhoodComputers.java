package io.vanderbeke.glife.api.service;

import io.vanderbeke.glife.api.model.ExpansionStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class NeighborhoodComputers {

    private final List<NeighborhoodComputer> computers;

    public NeighborhoodComputers(List<NeighborhoodComputer> computers) {
        if (computers == null) {
            this.computers = Collections.EMPTY_LIST;
        } else {
            this.computers = Collections.unmodifiableList(computers);
        }
    }

    public Optional<NeighborhoodComputer> find(ExpansionStrategy strategy) {
        return computers.stream().filter(computer -> computer.manages(strategy)).findAny();
    }
}
