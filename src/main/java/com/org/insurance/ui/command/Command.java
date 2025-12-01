package com.org.insurance.ui.command;

import com.org.insurance.domain.Derivative;

import java.util.List;
import java.util.Scanner;

public interface Command {
    void execute(Scanner in, List<Derivative> derivatives);
    String getDescription();
}
