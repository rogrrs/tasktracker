package com.example.tasktracker.service;

import com.example.tasktracker.model.Status;
import com.example.tasktracker.model.Task;
import com.example.tasktracker.model.User;
import com.example.tasktracker.repository.TaskRepository;
import com.example.tasktracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final JavaMailSender mailSender;

    @Scheduled(cron = "0 0 9 * * *")
    public void sendDailyReports() {
        userRepository.findAll().forEach(this::sendReportToUser);
    }

    private void sendReportToUser(User user) {
        List<Task> tasks = taskRepository.findAllByOwnerOrAssignee(user, user);

        long waiting = tasks.stream().filter(t -> t.getStatus() == Status.WAITING).count();
        long done = tasks.stream().filter(t -> t.getStatus() == Status.DONE).count();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Ежедневный отчет по задачам");
        message.setText("Здравствуйте! Ваша сводка по задачам на сегодня:\n" +
                "- В работе (WAITING): " + waiting + "\n" +
                "- Выполнено (DONE): " + done);

        try {
            mailSender.send(message);
        } catch (Exception ignored) {}
    }
}