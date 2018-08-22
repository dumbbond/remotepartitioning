
----------------- Start the Master ------------
java -jar -Dspring.profiles.active=master remote-partitioning-1.0-SNAPSHOT.jar org.springframework.batch.core.launch.support.CommandLineJobRunner remote.config.chunk.ChunkMasterConfiguration remoteChunkingJob -start

----------------- Start Slaves ----------------
java -jar -Dspring.profiles.active=slave remote-partitioning-1.0-SNAPSHOT.jar


java -jar -Dspring.profiles.active=master target/remote-partitioning-1.0-SNAPSHOT.jar org.springframework.batch.core.launch.support.CommandLineJobRunner remote.config.chunk.DBReadConfiguration readFromDbAndSendToQueue

