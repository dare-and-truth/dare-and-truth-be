package PNV.DareAndTruth.controller;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import PNV.DareAndTruth.dto.request.message.SendMessageRequest;
import PNV.DareAndTruth.dto.response.ApiStatus;
import PNV.DareAndTruth.dto.response.AppApiResponse;
import PNV.DareAndTruth.service.JwtService;
import PNV.DareAndTruth.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageController {
    MessageService messageService;
    JwtService jwtService;

    @Operation(
            summary = "Send a message",
            description = "Send a message between two users. If no conversation exists, a new one is created.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Message sent successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                "{\"code\": 1000, \"status\": \"success\", \"message\": \"Message sent successfully\"}"))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input provided",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                "{\"code\": 1017, \"status\": \"fail\", \"message\": \"Content cannot be empty\"}")))
            })
    @PostMapping("/send")
    public ResponseEntity<AppApiResponse<Void>> sendMessage(
            @Valid @RequestBody SendMessageRequest request, HttpServletRequest httpServletRequest) {
        // Lấy token từ header
        UUID senderId = jwtService.extractUserIdFromHeader(httpServletRequest);

        messageService.sendMessage(senderId, request);

        return ResponseEntity.status(201)
                .body(AppApiResponse.<Void>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .message("Message sent successfully")
                        .build());
    }
}
