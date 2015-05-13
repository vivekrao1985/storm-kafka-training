package com.videologygroup;

import backtype.storm.topology.TopologyBuilder;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.ZkHosts;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author Vivek Rao
 */
public class TestTopology {

    public static Options setupOptions() {
        final Options options = new Options();
        options.addOption(
                OptionBuilder.withArgName("topic")
                        .hasArgs()
                        .withValueSeparator(' ')
                        .withDescription("name of the topic to listen to")
                        .isRequired(true)
                        .create("n"));
        options.addOption(
                OptionBuilder.withArgName("zk-offset-path")
                        .hasArgs()
                        .withValueSeparator(' ')
                        .withDescription("zookeeper location to store the offsets")
                        .isRequired(true)
                        .create("o"));
        options.addOption(
                OptionBuilder.withArgName("consumer-group-id")
                        .hasArgs()
                        .withValueSeparator(' ')
                        .withDescription("consumer group name to use")
                        .isRequired(true)
                        .create("g"));
        return options;
    }

    public static void main(final String[] args) {
        final Options options = setupOptions();
        final CommandLine commandLineArgs;
        try {
            commandLineArgs = new GnuParser().parse(options, args);
            final TopologyBuilder topologyBuilder = new TopologyBuilder();
            // setup spout
            // setup bolt(s)
        } catch (final ParseException e) {
            final HelpFormatter helpFormatter = new HelpFormatter();
            final StringBuilder line = new StringBuilder("java " + TestTopology.class.getName());

            for (final Object option : options.getOptions()) {
                line.append(" [")
                        .append(((Option) option).getOpt())
                        .append("]")
                        .append(((Option) option).isRequired() ? "(required)" : "");
            }

            helpFormatter.printHelp(line.toString(), options);
        }
    }
}
