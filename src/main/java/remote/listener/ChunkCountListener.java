package remote.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

import java.text.MessageFormat;

public class ChunkCountListener implements ChunkListener {

    private static final Logger log = LoggerFactory.getLogger(ChunkCountListener.class);

    private MessageFormat fmt = new MessageFormat("{0} items processed");

    private int loggingInterval = 10000;

    @Override
    public void beforeChunk(ChunkContext context) {
        // Nothing to do here
    }

    @Override
    public void afterChunk(ChunkContext context) {

        int count = context.getStepContext().getStepExecution().getReadCount();


        // If the number of records processed so far is a multiple of the logging interval then output a log message.
        if (count > 0 && count % loggingInterval == 0) {
            log.info(fmt.format(new Object[] {new Long(count)})) ;
        }
    }

    @Override
    public void afterChunkError(ChunkContext context) {
;
    }

    public void setItemName(String itemName) {
        this.fmt = new MessageFormat("{0} " + itemName + " processed");
    }

    public void setLoggingInterval(int loggingInterval) {
        this.loggingInterval = loggingInterval;
    }
}
