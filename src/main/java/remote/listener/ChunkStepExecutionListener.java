package remote.listener;


import org.springframework.batch.core.*;

public class ChunkStepExecutionListener  implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        Long id = jobExecution.getId();
        JobParameters jobParameters = jobExecution.getJobParameters();
        String topicId = jobParameters.getString("topicId");
        String appId = jobParameters.getString("appId");
        //JobBuilderHelper.CommonJobProperties.put(topicId + appId, id);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }

}
