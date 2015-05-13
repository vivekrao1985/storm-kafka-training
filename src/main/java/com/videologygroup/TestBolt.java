package com.videologygroup;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vivek Rao
 */
public class TestBolt extends BaseRichBolt {

    private OutputCollector collector;

    private static final Logger LOG = LoggerFactory.getLogger(TestBolt.class);

    public void prepare(final Map map,
                        final TopologyContext topologyContext,
                        final OutputCollector outputCollector) {
        this.collector = outputCollector;
    }

    public void execute(final Tuple tuple) {
        final byte[] bytes = tuple.getBinaryByField("bytes");
        final String message = new String(bytes);
        LOG.info(message);
        this.collector.ack(tuple);
    }

    public void declareOutputFields(final OutputFieldsDeclarer outputFieldsDeclarer) {
    }
}
