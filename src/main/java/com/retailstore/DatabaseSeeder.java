package com.retailstore;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.retailstore.entity.Bill;
import com.retailstore.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements ApplicationListener<ApplicationReadyEvent> {

    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        seedDatabase();
    }

    private void seedDatabase() {
        try (InputStream usersStream = new ClassPathResource("mongo-init/users.json").getInputStream();
             InputStream billsStream = new ClassPathResource("mongo-init/bills.json").getInputStream()) {

            List<User> users = objectMapper.readValue(usersStream, new TypeReference<>() {});
            List<Bill> bills = objectMapper.readValue(billsStream, new TypeReference<>() {});

            mongoTemplate.dropCollection(User.class);
            mongoTemplate.dropCollection(Bill.class);

            mongoTemplate.insertAll(users);
            mongoTemplate.insertAll(bills);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
