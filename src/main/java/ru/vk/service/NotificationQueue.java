package ru.vk.service;

import ru.vk.dto.Notification;

/**
 *  * Реализуйте метод addNotification(Notification notification), который добавляет нотификацию в конец очереди
 *  * Реализуйте метод popNotification(), который возвращает и удаляет первый элемент в очереди
 *  * Реализуйте метод getRandomNotification(), который возвращает случайную нотификацию из очереди.
 */
public interface NotificationQueue {

    void addNotification (Notification notification);

    Notification popNotification();

    Notification getRandomNotification();

}
