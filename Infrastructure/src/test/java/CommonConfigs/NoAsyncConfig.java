package CommonConfigs;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class NoAsyncConfig {
    @Bean
    public TaskExecutor taskExecutor() {
        return new SyncTaskExecutor();
    }
}
