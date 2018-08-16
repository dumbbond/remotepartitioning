package remote.incrementer;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;

public class KohlsJobIncrementer implements JobParametersIncrementer {

    private static String RUN_ID_KEY = "run.id";
    private String key;

    public KohlsJobIncrementer() {
        this.key = RUN_ID_KEY;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public JobParameters getNext(JobParameters parameters) {
        JobParameters params = parameters == null ? new JobParameters() : parameters;
        long id = params.getLong(this.key, 0L) + 1L;
        return (new JobParametersBuilder(params)).addLong(this.key, id).toJobParameters();
    }
}
