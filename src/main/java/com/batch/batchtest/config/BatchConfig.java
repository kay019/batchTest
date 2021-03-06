package com.batch.batchtest.config;

import com.batch.batchtest.entity.Market;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job exampleJob() throws Exception{
        return jobBuilderFactory.get("exampleJob")
                .start(exampleStep()).build();
    }

    //example step
    @Bean
    @JobScope
    public Step exampleStep() throws Exception{
        return stepBuilderFactory.get("exampleStep")
                .<Market, Market> chunk(10)
                .reader(reader(null))
                .processor(processor(null))
                .writer(writer(null))
                .build();

    }

    @Bean
    @StepScope
    public JpaPagingItemReader <Market> reader(@Value("#{jobParameters[requestDate]}") String requestDate) throws Exception{
        log.info("=====> reader value :: " + requestDate);
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("price", 1000);

        return new JpaPagingItemReaderBuilder<Market>()
                .pageSize(10)
                .parameterValues(parameterValues)
                .queryString("SELECT m FROM Market m WHERE m.price >= : price")
                .entityManagerFactory(entityManagerFactory)
                .name("JpaPagingItemReader")
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor <Market, Market> processor(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return new ItemProcessor<Market, Market>() {
            @Override
            public Market process(Market market) throws Exception {
                log.info("===> process Market :: " + market);
                log.info("===> process value :: " + requestDate);

                market.setPrice(market.getPrice() + 100);
                return null;
           }
        };
    }

    @Bean
    @StepScope
    public JpaItemWriter<Market> writer(@Value("#{jobParameters[requestDate]}") String requestDate){
        log.info("===> writer value :: " + requestDate);

        return new JpaItemWriterBuilder<Market>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }


}
