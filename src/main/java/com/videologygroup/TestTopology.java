package com.videologygroup;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
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
        options.addOption(
                OptionBuilder.withArgName("mode")
                        .hasArgs()
                        .withValueSeparator(' ')
                        .withDescription("local|cluster - local mode or cluster mode")
                        .isRequired(true)
                        .create("m"));
        return options;
    }

    public static void main(final String[] args) throws AlreadyAliveException, InvalidTopologyException {
        final Options options = setupOptions();
        final CommandLine commandLineArgs;
        try {
            commandLineArgs = new GnuParser().parse(options, args);
            // setup spout
            final ZkHosts zkHosts = new ZkHosts("localhost");
            final SpoutConfig spoutConfig = new SpoutConfig(zkHosts,
                    commandLineArgs.getOptionValue("n"),
                    commandLineArgs.getOptionValue("o"),
                    commandLineArgs.getOptionValue("g"));
            final TopologyBuilder builder = new TopologyBuilder();
            final KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);
            final String kafkaSpoutName = "spout";
            builder.setSpout(kafkaSpoutName, kafkaSpout, 5);
            // setup bolt(s)
            final TestBolt testBolt = new TestBolt();
            builder.setBolt("bolt", testBolt).shuffleGrouping(kafkaSpoutName);

            final Config config = new Config();
            if (options.hasOption("m") && "cluster".equals(commandLineArgs.getOptionValue("m"))) {
                StormSubmitter.submitTopology("test-topology", config, builder.createTopology());
            } else {
                final LocalCluster localCluster = new LocalCluster();
                localCluster.submitTopology("test-topology", config, builder.createTopology());
            }
        } catch (final ParseException ex) {
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
