package ru.vk.service;

import ru.vk.dto.Notification;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class NotificationQueueImpl implements NotificationQueue {

    PriorityBlockingQueue<Notification> queue = new PriorityBlockingQueue<>();

    @Override
    public void addNotification(Notification notification) {
        queue.add(notification);
    }

    @Override
    public Notification popNotification() {
        if (queue.isEmpty()) {
            throw new IllegalArgumentException("Our queue does not have any element");
        }
        return queue.poll();
    }

    @Override
    public Notification getRandomNotification() {
        if (queue.isEmpty()) {
            throw new IllegalArgumentException("Our queue does not have any element");
        }
        var snapshot = new ArrayList<>(queue);
        int min = 0;
        int max = snapshot.size();
        int randomNumber = (int) (Math.random() * (max - min + 1)) + min;

        return snapshot.get(randomNumber);
    }
}
