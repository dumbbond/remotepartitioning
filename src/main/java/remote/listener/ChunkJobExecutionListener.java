package remote.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class ChunkJobExecutionListener implements JobExecutionListener {

    private long start = 0;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void beforeJob(JobExecution jobExecution){
        start = System.currentTimeMillis();
        log.info("Job started: {}", start);
    }

    @Override
    public void afterJob(JobExecution jobExecution){
        log.info("Job finished: {}", System.currentTimeMillis() - start);
    }
}
