package io.vanderbeke.glife;

import io.vanderbeke.glife.api.factory.AutomatonFactory;
import io.vanderbeke.glife.api.model.AutomatonProcessor;
import io.vanderbeke.glife.api.repository.AutomatonRepository;
import io.vanderbeke.glife.api.repository.RuleSetRepository;
import io.vanderbeke.glife.api.service.*;
import io.vanderbeke.glife.application.core.ApplicationMode;
import io.vanderbeke.glife.application.core.ApplicationOutput;
import io.vanderbeke.glife.application.core.NioUtil;
import io.vanderbeke.glife.application.server.AdminServer;
import io.vanderbeke.glife.application.server.ConnectionServer;
import io.vanderbeke.glife.business.basic.*;
import io.vanderbeke.glife.client.console.ConsoleClient;
import io.vanderbeke.glife.client.ux.UxClient;
import io.vanderbeke.glife.core.Constants;
import io.vanderbeke.glife.infrastructure.basic.*;
import io.vanderbeke.glife.infrastructure.rng.RngProvider;
import io.vanderbeke.glife.infrastructure.tcp.TcpRenderer;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Stream;

public class Glife {
    public Glife(String[] args) {
        System.out.println("Starting Game of life...\n");

        PropertyLoader loader = new BasicPropertyLoader();

        loader.load(args)
                .onSuccess(this::bindComponents)
                .onFailure(error -> error.printStackTrace());

    }

    private void exitOnError(Throwable error) {
        System.err.println("Sorry... An internal error occurred:");
        System.out.println("");
        printHelp();
        System.out.println("\nBye.");
        error.printStackTrace();
        System.exit(0);
    }

    private void bindComponents(Properties properties) {

        ApplicationMode mode = ApplicationMode.valueOf(properties.get(Constants.CONF_APPLICATION_MODE).toString());
        ApplicationOutput output = ApplicationOutput.valueOf(properties.get(Constants.CONF_APPLICATION_OUTPUT).toString());

        System.out.println("Starting mode: " + mode);

        RuleSetRepository ruleSetRepository = new BasicRuleSetRepository();
        BasicRenderer basicRenderer = new BasicRenderer();
        TcpRenderer tcpRenderer = new TcpRenderer();
        RngProvider rngProvider = new RngProvider();

        CellStateComputer cellStateComputer = new BasicCellStateComputer(ruleSetRepository);
        NeighborhoodComputer circularNeighborhoodComputer = new CircularNeighborhoodComputer();
        NeighborhoodComputer fixedNeighborhoodComputer = new FixedNeighborhoodComputer();
        NeighborhoodComputers neighborhoodComputers = new NeighborhoodComputers(Arrays.asList(circularNeighborhoodComputer, fixedNeighborhoodComputer));
        AutomatonExecutor automatonExecutor = new BasicAutomatonExecutor(cellStateComputer, neighborhoodComputers);
        AutomatonFactory automatonFactory = new BasicAutomatonFactory();
        AutomatonRepository automatonRepository = new BasicAutomatonRepository(basicRenderer, automatonFactory, properties);

        AutomatonProcessor automatonProcessor = new BasicAutomatonProcessor(automatonExecutor, automatonFactory, automatonRepository, rngProvider);
        AutomatonSaver automatonSaver = new BasicAutomatonSaver(automatonRepository, automatonProcessor, properties);

        automatonProcessor.initialize(properties)
                .onSuccess(automaton -> automatonSaver.start())
                .onFailure(this::exitOnError);

        if (mode == ApplicationMode.BASIC && output == ApplicationOutput.CONSOLE) {
            ConsoleClient client = new ConsoleClient(System.in, System.out, automatonProcessor, automatonSaver, basicRenderer);
            Thread clientThread = new Thread(client);
            clientThread.start();
        } else if (mode == ApplicationMode.BASIC && output == ApplicationOutput.UX) {
            UxClient client = new UxClient(automatonProcessor, automatonSaver, basicRenderer);
            client.show();
        } else { // NETWORK mode
            InetSocketAddress connectionAddress = NioUtil.socketAddress(properties, Constants.CONF_CLIENT_SERVER_PORT, Constants.DEFAULT_CLIENT_SERVER_PORT);
            InetSocketAddress adminAddress = NioUtil.socketAddress(properties, Constants.CONF_ADMIN_SERVER_PORT, Constants.DEFAULT_ADMIN_SERVER_PORT);
            ConnectionServer connectionServer = new ConnectionServer(automatonProcessor, tcpRenderer, connectionAddress);
            AdminServer adminServer = new AdminServer(connectionServer, automatonProcessor, automatonSaver, adminAddress);
        }

        System.out.println("Game of life ready.\n\n\n");
    }

    private static void printHelp() {
        String title = "Syntax: start [OPTION...]\n" +
                "\nWhere OPTION is a tuple of key/value, with pattern \"-<key>=<value>\".\n" +
                "Available option keys are:";

        System.out.println(title);

        System.out.printf("\t-%-10s : matrix width (integer only). [MANDATORY]\n", "width");
        System.out.printf("\t-%-10s : matrix height (integer only). [MANDATORY]\n", "height");
        System.out.printf("\t-%-10s : spawning rate percent (float only). [OPTIONAL; default: 0.15]\n", "spawn");
        System.out.printf("\t-%-10s : refresh rate (long, in milliseconds). [OPTIONAL; default: 5000]\n", "refresh");
        System.out.printf("\t-%-10s : application mode (BASIC, NETWORK). [OPTIONAL; default: CONSOLE]\n", "mode");
        System.out.printf("\t-%-10s : output renderer (CONSOLE, UX). [OPTIONAL; default: CONSOLE]\n", "out");
        System.out.printf("\t-%-10s : in NETWORK mode, TCP port for client connection (integer). [OPTIONAL; default: 7777]\n", "port");
        System.out.printf("\t-%-10s : in NETWORK mode, TCP port for admin connection (integer). [OPTIONAL; default: 8888]\n", "admin-port");
    }

    public static void main(String[] args) {
        if (Stream.of(args).anyMatch(arg -> "-h".equals(arg.trim()))) {
            printHelp();
            return;
        }

        Glife glife = new Glife(args);
    }
}
