package remote;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableBatchProcessing
@IntegrationComponentScan
@EnableIntegration
@EnableRetry
public class BatchApplication {

    public static void main(String[] args) throws Exception {

        //SpringApplication.exit(SpringApplication.run(BatchApplication.class, args));
        SpringApplication.run(BatchApplication.class, args);
       // Thread.sleep(5 * 1000);

//        AnnotationConfigApplicationContext workerApplicationContext = new AnnotationConfigApplicationContext(WorkerConfiguration .class);
//        Job job = (Job) workerApplicationContext.getBean("remoteChunkingJob");
//        JobLauncher jobLauncher = workerApplicationContext.getBean(JobLauncher.class);
//
//        jobLauncher.run(job, new JobParameters());
//
         //   System.exit(0);
    }
}