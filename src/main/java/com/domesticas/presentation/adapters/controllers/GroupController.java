package com.domesticas.presentation.adapters.controllers;

import com.domesticas.application.GroupService;
import com.domesticas.domain.model.Group;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody CreateGroupRequest request) {
        Group group = groupService.createGroup(request.name, request.description);
        return ResponseEntity.ok(group);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Group> getGroup(@PathVariable Long id) {
        return groupService.getGroupById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Group>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Group> updateGroup(@PathVariable Long id, @RequestBody Group group) {
        group.setId(id);
        return ResponseEntity.ok(groupService.updateGroup(group));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }

    public static class CreateGroupRequest {
        public String name;
        public String description;
    }
}