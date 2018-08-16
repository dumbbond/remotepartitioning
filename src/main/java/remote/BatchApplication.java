package remote;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.retry.annotation.EnableRetry;
import remote.config.chunk.ChunkMasterConfiguration;

@SpringBootApplication
@IntegrationComponentScan
@EnableIntegration
@EnableRetry
@EnableBatchProcessing
public class BatchApplication {

    public static void main(String[] args) throws Exception {

     //   SpringApplication.run(BatchApplication.class, args).getBean(ChunkMasterConfiguration.class).runBatch();
        SpringApplication.run(BatchApplication.class, args);

//        ChunkMasterConfiguration master = new ChunkMasterConfiguration();
//
//
//        System.out.println("RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR");
//
//        master.runBatch();

        //SpringApplication.exit(SpringApplication.run(BatchApplication.class, args));


        //jobOperator.restart(311);
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