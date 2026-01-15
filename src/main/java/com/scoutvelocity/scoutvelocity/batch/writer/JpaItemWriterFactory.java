package com.scoutvelocity.scoutvelocity.batch.writer;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.stereotype.Component;

/**
 * JpaItemWriter 팩토리
 * 모든 Entity에 대해 공통으로 사용 가능
 */
@Component
@RequiredArgsConstructor
public class JpaItemWriterFactory {
    
    private final EntityManagerFactory entityManagerFactory;
    
    /**
     * 제네릭 JpaItemWriter 생성
     */
    public <T> JpaItemWriter<T> create() {
        JpaItemWriter<T> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
}
