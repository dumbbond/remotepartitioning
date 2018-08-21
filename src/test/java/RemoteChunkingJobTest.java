//import remote.config.ChunkMasterConfiguration;
////import remote.config.WorkerConfiguration;
//import org.apache.activemq.broker.BrokerService;
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import org.springframework.batch.core.ExitStatus;
//import org.springframework.batch.core.JobExecution;
//import org.springframework.batch.test.JobLauncherTestUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringRunner;
//
//
//@RunWith(SpringRunner.class)
//    @ContextConfiguration(classes = {
//            RemoteChunkingJobTest.JobRunnerConfiguration.class,
//            ChunkMasterConfiguration.class})
//    @PropertySource("classpath:application.yml")
//    public class RemoteChunkingJobTest {
//
//        @Value("${broker.url}")
//        private String brokerUrl;
//
//        @Autowired
//        private JobLauncherTestUtils jobLauncherTestUtils;
//
//
//        private BrokerService brokerService;
//        private AnnotationConfigApplicationContext workerApplicationContext;
//
//
//        @Before
//        public void setUp() throws Exception {
//            this.brokerService = new BrokerService();
//            this.brokerService.addConnector(this.brokerUrl);
//            this.brokerService.start();
//            this.workerApplicationContext = new AnnotationConfigApplicationContext(WorkerConfiguration.class);
//        }
//
//        @After
//        public void tearDown() throws Exception {
//            this.workerApplicationContext.close();
//            this.brokerService.stop();
//        }
//
//        @Test
//        public void testRemoteChunkingJob() throws Exception {
//            // when
//            JobExecution jobExecution = this.jobLauncherTestUtils.launchJob();
//
//            // then
//            Assert.assertEquals(ExitStatus.COMPLETED.getExitCode(), jobExecution.getExitStatus().getExitCode());
//            Assert.assertEquals(
//                    "Waited for 2 results.", // the master sent 2 chunks ({1, 2, 3} and {4, 5, 6}) to workers
//                    jobExecution.getExitStatus().getExitDescription());
//        }
//
//        @Configuration
//        public static class JobRunnerConfiguration {
//
//            @Bean
//            public JobLauncherTestUtils utils() {
//                return new JobLauncherTestUtils();
//            }
//
//        }
//}
