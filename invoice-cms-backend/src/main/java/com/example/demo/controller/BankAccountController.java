package com.example.demo.controller;

import com.example.demo.dto.BankAccountDTO;
import com.example.demo.dto.request.BankAccountRequest;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.BankAccountException;
import com.example.demo.model.exception.CompanyException;
import com.example.demo.model.exception.UserException;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.BankAccountService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bank_accounts")
@CrossOrigin("*")
@RequiredArgsConstructor
public class BankAccountController {
    private final UserService userService;
    private final BankAccountService bankAccountService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<BankAccountDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                                    @RequestParam(defaultValue = "15") int perPage) throws UserException {
        return ResponseEntity.ok(
                new PaginatedResponse<>(bankAccountService.get(page, perPage, userService.getCurrentUser()))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<BankAccountDTO>> getById(@PathVariable long id) throws UserException, BankAccountException {
        return ResponseEntity.ok(
                new Response<>(bankAccountService.getById(id, userService.getCurrentUser()))
        );
    }

    @PostMapping
    public ResponseEntity<Response<BankAccountDTO>> add(@RequestBody BankAccountRequest request) throws UserException, CompanyException, BankAccountException {
        return new ResponseEntity<>(
                new Response<>(bankAccountService.add(request, userService.getCurrentUser())),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<BankAccountDTO>> update(@PathVariable long id,
                                                           @RequestBody BankAccountRequest request) throws UserException, CompanyException, BankAccountException {
        request.setId(id);
        return ResponseEntity.ok(
                new Response<>(bankAccountService.update(request, userService.getCurrentUser()))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) throws UserException, BankAccountException {
        bankAccountService.remove(id, userService.getCurrentUser());
        return ResponseEntity.noContent().build();
    }
}
