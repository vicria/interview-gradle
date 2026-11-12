package ru.vk.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.vk.dto.Notification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Описание задачи
 * Нужно реализовать простую систему нотификаций на сайте с методами получения последней и случайной нотификации.
 *
 * Требования к решению
 * Реализуйте класс NotificationQueue, который будет представлять собой очередь из объектов
 * Notification. Каждый Notification имеет свой текст и время создания.
 *
 * Реализуйте метод addNotification(Notification notification), который добавляет нотификацию в конец очереди
 * Реализуйте метод popNotification(), который возвращает и удаляет первый элемент в очереди
 * Реализуйте метод getRandomNotification(), который возвращает случайную нотификацию из очереди.
 * Все созданные методы должны пройти юнит-тесты.
 */
class NotificationQueueImplTest {

    NotificationQueueImpl service;
    Notification notification1 = new Notification("name1", Instant.now());
    Notification notification2 = new Notification("name1", Instant.now().plusSeconds(2));

    @BeforeEach
    void init(){
        service = new NotificationQueueImpl();
    }

    @Test
    void addNotification() {
        service.addNotification(notification1);

        Notification actual = service.popNotification();
        Assertions.assertEquals(notification1, actual);
    }


    @Test
    void popNotification() {
        service.addNotification(notification1);
        service.addNotification(notification2);

        Notification actual = service.popNotification();
        Assertions.assertEquals(notification1, actual);
    }


    @Test
    void popNotificationToEviction() {
        service.addNotification(notification1);
        service.addNotification(notification2);

        Notification actual = service.popNotification();
        Assertions.assertEquals(notification1, actual);

        Notification actual2 = service.popNotification();
        Assertions.assertEquals(notification2, actual2);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.popNotification());
    }

    @Test
    void popNotificationConcurrency() throws InterruptedException {
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        try (ExecutorService pool = Executors.newFixedThreadPool(2)) {
            pool.submit(() ->{
                ready.countDown();
                start.await();
                service.addNotification(notification1);
                return null;
            });

            pool.submit(() ->{
                ready.countDown();
                start.await();
                service.addNotification(notification2);
                return null;
            });

            ready.await();
            start.countDown();
        }

        Notification actual = service.popNotification();
        Assertions.assertEquals(notification1, actual);
    }

    @Test
    void exceptionOnPopEmpty() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.popNotification());
    }

    @Test
    void exceptionOnRandom() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.getRandomNotification());
    }


    @Test
    void getRandomNotification() throws InterruptedException, ExecutionException {
        ArrayList<String> randomElements = new ArrayList<>();
        Runnable addNotification = () -> IntStream.range(0, 100).forEach(idx -> {
            Notification notification = new Notification(String.valueOf(idx), Instant.now());
            service.addNotification(notification);
        });

        Runnable randomNotification = () -> IntStream.range(0, 10).forEach(idx ->{
            try {
                Notification randomNotification1 = service.getRandomNotification();
                System.out.println(randomNotification1.name());
                randomElements.add(randomNotification1.name());
            } catch (IllegalArgumentException ex) {
                System.out.println("Exception");
            }
        });

        ExecutorService pool = Executors.newFixedThreadPool(2);
        Future<?> submit = pool.submit(addNotification);
        Future<?> submit1 = pool.submit(randomNotification);

        submit.get();
        submit1.get();
        Assertions.assertTrue(submit.isDone());
        Assertions.assertTrue(submit1.isDone());

        Assertions.assertTrue(randomElements.size()>0);
    }
}
