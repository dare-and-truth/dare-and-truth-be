package PNV.DareAndTruth.controller;

import PNV.DareAndTruth.dto.request.user.UpdateUserRequest;
import PNV.DareAndTruth.dto.response.ApiResponse;
import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.entity.User;
import PNV.DareAndTruth.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.builder().code(1000).status(ApiStatus.SUCCESS).data(allUsers).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getUserById(@PathVariable String id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.builder().code(1000).status(ApiStatus.SUCCESS).data(user).build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updateUser(@PathVariable String id, @RequestBody @Valid UpdateUserRequest request) {
        userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.builder().code(1000).status(ApiStatus.SUCCESS).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.builder().code(1000).status(ApiStatus.SUCCESS).build());
    }
}