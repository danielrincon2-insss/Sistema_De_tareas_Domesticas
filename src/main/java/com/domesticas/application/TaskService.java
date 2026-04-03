package com.domesticas.application;

import com.domesticas.application.ports.GroupRepository;
import com.domesticas.application.ports.TaskRepository;
import com.domesticas.application.ports.UserRepository;
import com.domesticas.domain.model.Group;
import com.domesticas.domain.model.Task;
import com.domesticas.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, GroupRepository groupRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    public Task createTask(String title, String description, Task.Priority priority, Task.Difficulty difficulty,
                           LocalDateTime dueDate, Long assignedToId, Long groupId) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        User assignedTo = null;
        if (assignedToId != null) {
            assignedTo = userRepository.findById(assignedToId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (assignedTo.getGroup() == null || !assignedTo.getGroup().getId().equals(groupId)) {
                throw new IllegalArgumentException("User does not belong to the selected group");
            }
        }

        Task task = new Task(title, description, priority, difficulty, dueDate, assignedTo, group);

        if (assignedTo != null) {
            task.setStatus(Task.Status.ASSIGNED);
        }

        return taskRepository.save(task);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> getTasksByGroup(Long groupId) {
        return taskRepository.findByGroupId(groupId);
    }

    public List<Task> getTasksByUser(Long userId) {
        return taskRepository.findByAssignedToId(userId);
    }

    public Task updateTask(Task task) {
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.findById(id).ifPresent(taskRepository::delete);
    }

    public Task updateTaskStatus(Long taskId, Task.Status status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    @Transactional
    public WeeklyAssignmentResult assignWeeklyTasks(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        List<User> users = userRepository.findByGroupId(groupId);
        if (users.isEmpty()) {
            return WeeklyAssignmentResult.error("El hogar no tiene integrantes para asignar tareas.");
        }

        if (wasAssignedThisWeek(group.getLastWeeklyAssignmentDate())) {
            return WeeklyAssignmentResult.error("Solo se puede asignar una vez a la semana.");
        }

        List<Task> pendingTasks = taskRepository.findByGroupIdAndStatusOrderByCreatedAtAsc(groupId, Task.Status.PENDING)
                .stream()
                .filter(task -> task.getAssignedTo() == null)
                .collect(Collectors.toList());

        if (pendingTasks.isEmpty()) {
            return WeeklyAssignmentResult.error("No hay tareas pendientes para asignar.");
        }

        int maxTasksPerUser = pendingTasks.size() / users.size();

        if (maxTasksPerUser == 0) {
            return WeeklyAssignmentResult.success(
                    "No es posible hacer una distribución equitativa con la cantidad actual de tareas. Todas quedan pendientes.",
                    Collections.emptyList(),
                    0,
                    pendingTasks.size()
            );
        }

        int maxAssignable = maxTasksPerUser * users.size();
        List<Task> tasksToAssign = new ArrayList<>(pendingTasks.subList(0, maxAssignable));
        int remainingPending = pendingTasks.size() - tasksToAssign.size();

        List<User> randomOrder = new ArrayList<>(users);
        Collections.shuffle(randomOrder);

        Map<Long, Integer> totalAssignments = new HashMap<>();
        Map<Long, Integer> highAssignments = new HashMap<>();
        Map<Long, Integer> mediumAssignments = new HashMap<>();
        Map<Long, Integer> lowAssignments = new HashMap<>();

        for (User user : randomOrder) {
            totalAssignments.put(user.getId(), 0);
            highAssignments.put(user.getId(), 0);
            mediumAssignments.put(user.getId(), 0);
            lowAssignments.put(user.getId(), 0);
        }

        List<Task> orderedTasks = orderTasksByDifficulty(tasksToAssign);
        List<Task> assignedTasks = new ArrayList<>();

        for (Task task : orderedTasks) {
            User selectedUser = selectBestUser(
                    randomOrder,
                    maxTasksPerUser,
                    totalAssignments,
                    highAssignments,
                    mediumAssignments,
                    lowAssignments,
                    task.getDifficulty()
            );

            if (selectedUser == null) {
                remainingPending++;
                continue;
            }

            task.setAssignedTo(selectedUser);
            task.setStatus(Task.Status.ASSIGNED);
            task.setUpdatedAt(LocalDateTime.now());
            taskRepository.save(task);

            totalAssignments.put(selectedUser.getId(), totalAssignments.get(selectedUser.getId()) + 1);
            incrementDifficultyCounter(
                    selectedUser.getId(),
                    task.getDifficulty(),
                    highAssignments,
                    mediumAssignments,
                    lowAssignments
            );

            assignedTasks.add(task);
        }

        if (!assignedTasks.isEmpty()) {
            group.setLastWeeklyAssignmentDate(LocalDate.now());
            groupRepository.save(group);
        }

        return WeeklyAssignmentResult.success(
                "Asignación semanal realizada exitosamente.",
                assignedTasks,
                assignedTasks.size(),
                remainingPending
        );
    }

    private boolean wasAssignedThisWeek(LocalDate lastWeeklyAssignmentDate) {
        if (lastWeeklyAssignmentDate == null) {
            return false;
        }

        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.ISO;

        int lastWeek = lastWeeklyAssignmentDate.get(weekFields.weekOfWeekBasedYear());
        int currentWeek = today.get(weekFields.weekOfWeekBasedYear());

        int lastWeekYear = lastWeeklyAssignmentDate.get(weekFields.weekBasedYear());
        int currentWeekYear = today.get(weekFields.weekBasedYear());

        return lastWeek == currentWeek && lastWeekYear == currentWeekYear;
    }

    private List<Task> orderTasksByDifficulty(List<Task> tasks) {
        List<Task> ordered = new ArrayList<>();

        tasks.stream()
                .filter(task -> task.getDifficulty() == Task.Difficulty.HIGH)
                .forEach(ordered::add);

        tasks.stream()
                .filter(task -> task.getDifficulty() == Task.Difficulty.MEDIUM)
                .forEach(ordered::add);

        tasks.stream()
                .filter(task -> task.getDifficulty() == Task.Difficulty.LOW)
                .forEach(ordered::add);

        return ordered;
    }

    private User selectBestUser(
            List<User> users,
            int maxTasksPerUser,
            Map<Long, Integer> totalAssignments,
            Map<Long, Integer> highAssignments,
            Map<Long, Integer> mediumAssignments,
            Map<Long, Integer> lowAssignments,
            Task.Difficulty difficulty
    ) {
        return users.stream()
                .filter(user -> totalAssignments.get(user.getId()) < maxTasksPerUser)
                .min(Comparator
                        .comparingInt((User user) -> getDifficultyCounter(user.getId(), difficulty, highAssignments, mediumAssignments, lowAssignments))
                        .thenComparingInt(user -> totalAssignments.get(user.getId())))
                .orElse(null);
    }

    private int getDifficultyCounter(
            Long userId,
            Task.Difficulty difficulty,
            Map<Long, Integer> highAssignments,
            Map<Long, Integer> mediumAssignments,
            Map<Long, Integer> lowAssignments
    ) {
        if (difficulty == Task.Difficulty.HIGH) {
            return highAssignments.get(userId);
        }
        if (difficulty == Task.Difficulty.MEDIUM) {
            return mediumAssignments.get(userId);
        }
        return lowAssignments.get(userId);
    }

    private void incrementDifficultyCounter(
            Long userId,
            Task.Difficulty difficulty,
            Map<Long, Integer> highAssignments,
            Map<Long, Integer> mediumAssignments,
            Map<Long, Integer> lowAssignments
    ) {
        if (difficulty == Task.Difficulty.HIGH) {
            highAssignments.put(userId, highAssignments.get(userId) + 1);
            return;
        }

        if (difficulty == Task.Difficulty.MEDIUM) {
            mediumAssignments.put(userId, mediumAssignments.get(userId) + 1);
            return;
        }

        lowAssignments.put(userId, lowAssignments.get(userId) + 1);
    }

    public static class WeeklyAssignmentResult {
        private final boolean success;
        private final String message;
        private final int assignedCount;
        private final int pendingCount;
        private final List<Task> assignedTasks;

        public WeeklyAssignmentResult(boolean success, String message, int assignedCount, int pendingCount, List<Task> assignedTasks) {
            this.success = success;
            this.message = message;
            this.assignedCount = assignedCount;
            this.pendingCount = pendingCount;
            this.assignedTasks = assignedTasks;
        }

        public static WeeklyAssignmentResult success(String message, List<Task> assignedTasks, int assignedCount, int pendingCount) {
            return new WeeklyAssignmentResult(true, message, assignedCount, pendingCount, assignedTasks);
        }

        public static WeeklyAssignmentResult error(String message) {
            return new WeeklyAssignmentResult(false, message, 0, 0, Collections.emptyList());
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public int getAssignedCount() {
            return assignedCount;
        }

        public int getPendingCount() {
            return pendingCount;
        }

        public List<Task> getAssignedTasks() {
            return assignedTasks;
        }
    }
}